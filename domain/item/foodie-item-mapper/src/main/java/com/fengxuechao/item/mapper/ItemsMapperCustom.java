package com.fengxuechao.item.mapper;

import com.fengxuechao.item.pojo.vo.ItemCommentVO;
import com.fengxuechao.item.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ItemsMapperCustom {

    List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    // TODO 迁移到 foodie-search 模块
    // List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);
    // List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);

    int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);
}