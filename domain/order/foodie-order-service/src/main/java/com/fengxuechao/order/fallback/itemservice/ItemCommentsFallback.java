package com.fengxuechao.order.fallback.itemservice;

import com.google.common.collect.Lists;
import com.fengxuechao.item.pojo.vo.MyCommentVO;
import com.fengxuechao.pojo.PagedGridResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Component
// 假的requestmapping，把spring糊弄过去
@RequestMapping("JokeJoke")
public class ItemCommentsFallback implements ItemCommentsFeignClient {

    @Override
    // HystrixCommand - 可以实现多级降级
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        MyCommentVO commentVO = new MyCommentVO();
        commentVO.setContent("正在加载中");

        PagedGridResult result = new PagedGridResult();
        result.setRows(Lists.newArrayList(commentVO));
        result.setTotal(1);
        result.setRecords(1);
        return result;
    }

    @Override
    public void saveComments(@RequestBody Map<String, Object> map) {

    }
}
