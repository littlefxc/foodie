package com.fengxuechao.service.impl;

import com.fengxuechao.service.ItemsESService;
import com.fengxuechao.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {
/*
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(page, pageSize);

        SortBuilder sortBuilder = null;
        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts")
                    .order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price")
                    .order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword")
                    .order(SortOrder.ASC);
        }

        String itemNameFiled = "itemName";

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameFiled, keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameFiled)
//                        .preTags(preTag)
//                        .postTags(postTag)
                )
                .withSort(sortBuilder)
//                .withSort(sortBuilderAge)
                .withPageable(pageable)
                .build();
        AggregatedPage<Items> pagedItems = esTemplate.queryForPage(query, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

                List<Items> itemHighLightList = new ArrayList<>();

                SearchHits hits = response.getHits();
                for (SearchHit h : hits) {
                    HighlightField highlightField = h.getHighlightFields().get(itemNameFiled);
                    String itemName = highlightField.getFragments()[0].toString();

                    String itemId = (String)h.getSourceAsMap().get("itemId");
                    String imgUrl = (String)h.getSourceAsMap().get("imgUrl");
                    Integer price = (Integer)h.getSourceAsMap().get("price");
                    Integer sellCounts = (Integer)h.getSourceAsMap().get("sellCounts");

                    Items item = new Items();
                    item.setItemId(itemId);
                    item.setItemName(itemName);
                    item.setImgUrl(imgUrl);
                    item.setPrice(price);
                    item.setSellCounts(sellCounts);

                    itemHighLightList.add(item);
                }

                return new AggregatedPageImpl<>((List<T>)itemHighLightList,
                                                pageable,
                                                response.getHits().totalHits);
            }
        });
//        System.out.println("检索后的总分页数目为：" + pagedStu.getTotalPages());
//        List<Stu> stuList = pagedStu.getContent();
//        for (Stu s : stuList) {
//            System.out.println(s);
//        }

        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(pagedItems.getContent());
        gridResult.setPage(page + 1);
        gridResult.setTotal(pagedItems.getTotalPages());
        gridResult.setRecords(pagedItems.getTotalElements());

        return gridResult;*/
        return null;
    }
}
