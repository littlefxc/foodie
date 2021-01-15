package com.fengxuechao.service.impl;

import com.fengxuechao.es.pojo.Items;
import com.fengxuechao.service.ItemsESService;
import com.fengxuechao.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {
        // 使用默认标签 em，允许前端自定义样式
        // String preTag = "<font color='red'>";
        // String postTag = "</font>";

        Pageable pageable = PageRequest.of(page, pageSize);

        FieldSortBuilder sortBuilder;
        if (StringUtils.equals("c", sort)) {
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
        } else if (StringUtils.equals("p", sort)) {
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
        } else {
            // itemName 在 es 中的数据类型是 text， 所以要用 itemName.keyword
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
        }

        String itemNameFiled = "itemName";

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameFiled, keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameFiled)
//                        .preTags(preTag)
//                        .postTags(postTag)
                )
                .withSort(sortBuilder)
//                .withSort(sortBuilderAge)
                .withPageable(pageable)
                .build();

        SearchHits<Items> hits = esTemplate.search(query, Items.class);
        Page<Items> itemsPage = SearchHitSupport.searchPageFor(hits, pageable)
                .map(itemsSearchHit -> {
                    itemsSearchHit.getContent().setItemName(itemsSearchHit.getHighlightField(itemNameFiled).get(0));
                    return itemsSearchHit.getContent();
                });

//        System.out.println("检索后的总分页数目为：" + pagedStu.getTotalPages());
//        List<Stu> stuList = pagedStu.getContent();
//        for (Stu s : stuList) {
//            System.out.println(s);
//        }

        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(itemsPage.getContent());
        gridResult.setPage(page + 1);
        // 两数相除，向上取整
        gridResult.setTotal(itemsPage.getTotalPages());
        gridResult.setRecords(itemsPage.getTotalElements());

        return gridResult;
    }
}
