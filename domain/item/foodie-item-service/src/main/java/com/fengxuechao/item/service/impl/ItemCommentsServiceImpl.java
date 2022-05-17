package com.fengxuechao.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.fengxuechao.item.mapper.ItemsCommentsMapperCustom;
import com.fengxuechao.item.pojo.vo.MyCommentVO;
import com.fengxuechao.item.service.ItemCommentsService;
import com.fengxuechao.pojo.PagedGridResult;
import com.fengxuechao.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengxuechao.
 */
@Slf4j
@RestController
public class ItemCommentsServiceImpl extends BaseService implements ItemCommentsService {

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(@RequestParam("userId") String userId,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        log.info("feign - 查询我的评价 - {}, {}, {}", userId, page, pageSize);
        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedGrid(list, page);
    }

    @Override
    public void saveComments(@RequestBody Map<String, Object> map) {
        itemsCommentsMapperCustom.saveComments(map);
    }
}
