package com.fengxuechao.order.controller.center;

import com.fengxuechao.order.fallback.itemservice.ItemCommentsFeignClient;
import com.fengxuechao.pojo.PagedGridResult;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fengxuechao
 * @date 2022/3/23
 */
@Slf4j
@Service
public class HystrixRequestCacheService {

    @Autowired
    private ItemCommentsFeignClient itemCommentsService;

    @CacheResult
    @HystrixCommand(commandKey = "cacheKey")
    public PagedGridResult requestCache(@CacheKey String userId, Integer page, Integer pageSize) {
        log.info("request cache: userId = {}, page = {}, pageSize = {}", userId, page, pageSize);
        PagedGridResult grid = itemCommentsService.queryMyComments(userId, page, pageSize);
        log.info("after requesting cache: userId = {}, page = {}, pageSize = {}", userId, page, pageSize);
        return grid;
    }
}
