package com.test;

import com.fengxuechao.FoodieSearchApp;
import com.fengxuechao.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodieSearchApp.class)
public class ESTest {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    /**
     * 不建议使用 ElasticsearchRestTemplate 对索引进行管理（创建索引，更新映射，删除索引）
     * 索引就像是数据库或者数据库中的表，我们平时是不会是通过java代码频繁的去创建修改删除数据库或者表的
     * 我们只会针对数据做CRUD的操作
     * 在es中也是同理，我们尽量使用 ElasticsearchRestTemplate 对文档数据做CRUD的操作
     * 1. 属性（FieldType）类型不灵活
     * 2. 主分片与副本分片数无法设置
     */

    @Test
    public void createIndexStu() {

        Stu stu = new Stu();
        stu.setStuId(1007L);
        stu.setName("iron man");
        stu.setAge(22);
        stu.setMoney(1000.8f);
        stu.setSign("I am iron man");
        stu.setDescription("I have a spider man");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery, esTemplate.getIndexCoordinatesFor(stu.getClass()));
    }

    @Test
    public void deleteIndexStu() {
        esTemplate.indexOps(Stu.class).delete();
    }

//    ------------------------- 我是分割线 --------------------------------

    @Test
    public void updateStuDoc() {

        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("name", "spider man");
        sourceMap.put("money", 99.8f);
        sourceMap.put("age", 33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = UpdateQuery.builder("1004")
                .withDocument(Document.from(sourceMap))
                .build();

//        update stu set sign='abc',age=33,money=88.6 where docId='1002'

        esTemplate.update(updateQuery, IndexCoordinates.of("stu"));
    }


    @Test
    public void getStuDoc() {
        Stu stu = esTemplate.get("1004", Stu.class);

        System.out.println(stu);
    }

    @Test
    public void deleteStuDoc() {
        esTemplate.delete("1002", IndexCoordinates.of("stu"));
    }


//    ------------------------- 我是分割线 --------------------------------

    @Test
    public void searchStuDoc() {

        Pageable pageable = PageRequest.of(0, 2);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "spider man"))
                .withPageable(pageable)
                .build();
        SearchHits<Stu> hits = esTemplate.search(query, Stu.class, esTemplate.getIndexCoordinatesFor(Stu.class));
        System.out.println("检索后的总分页数目为：" + hits.getTotalHits());
        for (SearchHit<Stu> hit : hits) {
            System.out.println(hit.getContent());
        }

    }

    @Test
    public void highlightStuDoc() {

        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(0, 10);

        FieldSortBuilder sortBuilder = new FieldSortBuilder("money")
                .order(SortOrder.DESC);
        FieldSortBuilder sortBuilderAge = new FieldSortBuilder("age")
                .order(SortOrder.ASC);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "spider man"))
                .withHighlightFields(new HighlightBuilder.Field("description")
                        .preTags(preTag)
                        .postTags(postTag))
                .withSort(sortBuilder)
                .withSort(sortBuilderAge)
                .withPageable(pageable)
                .build();

        SearchHits<Stu> hits = esTemplate.search(query, Stu.class);

        System.out.println("检索后的总分页数目为：" + hits.getTotalHits());
        // 将
        List<SearchHit<Stu>> list = hits.get()
                .peek(stuSearchHit -> stuSearchHit.getContent().setDescription(stuSearchHit.getHighlightField("description").get(0))).collect(Collectors.toList());
        for (SearchHit<Stu> hit : list) {
            System.out.println(hit.getContent());
        }

    }
}
