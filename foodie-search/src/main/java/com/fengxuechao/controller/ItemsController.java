package com.fengxuechao.controller;

import com.fengxuechao.service.ItemsESService;
import com.fengxuechao.utils.PagedGridResult;
import com.fengxuechao.utils.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fengxuechao
 */
@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsESService itemsEsService;

    @GetMapping("/hello")
    public Object hello() {
        return "Hello Elasticsearch~";
    }

    @GetMapping("/es/search")
    public ResultBean<?> search(
                            String keywords,
                            String sort,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "20")Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return ResultBean.errorMsg("keywords is required");
        }

        page --;

        PagedGridResult grid = itemsEsService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return ResultBean.ok(grid);
    }

}
