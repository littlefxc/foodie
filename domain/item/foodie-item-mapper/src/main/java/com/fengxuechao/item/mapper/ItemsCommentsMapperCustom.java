package com.fengxuechao.item.mapper;

import com.fengxuechao.my.mapper.MyMapper;
import com.fengxuechao.item.pojo.ItemsComments;
import com.fengxuechao.item.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}