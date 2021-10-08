package com.fengxuechao.service;

import com.fengxuechao.pojo.PagedGridResult;

public interface ItemsESService {

    PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize);

}
