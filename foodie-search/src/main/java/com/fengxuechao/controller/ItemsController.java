package com.fengxuechao.controller;

import com.fengxuechao.service.ItemsESService;
import com.fengxuechao.utils.PagedGridResult;
import com.fengxuechao.utils.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/hello")
    public Object hello() {
        return "Hello Elasticsearch~";
    }

    @GetMapping("/es/search")
    public ResultBean<?> search(
                            String keywords,
                            String sort,
                            Integer page,
                            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return ResultBean.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page --;

        PagedGridResult grid = itemsESService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return ResultBean.ok(grid);
    }

}
