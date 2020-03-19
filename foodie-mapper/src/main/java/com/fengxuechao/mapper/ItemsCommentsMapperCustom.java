package com.fengxuechao.mapper;

import com.fengxuechao.my.mapper.MyMapper;
import com.fengxuechao.pojo.ItemsComments;
import com.fengxuechao.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}