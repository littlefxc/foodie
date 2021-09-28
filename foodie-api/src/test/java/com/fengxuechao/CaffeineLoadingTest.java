package com.fengxuechao;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 同步加载数据
 *
 * @author fengxuechao
 * @date 2021/8/11
 */
public class CaffeineLoadingTest {

    /**
     * 模拟从数据库中读取key
     *
     * @param key
     * @return
     */
    private int getInDb(int key) {
        return key + 1;
    }

    @Test
    public void test() {
        // 初始化缓存，设置了1分钟的写过期，100的缓存最大个数
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        return getInDb(key);
                    }
                });
        int key1 = 1;
        // get数据，取不到则从数据库中读取相关数据，该值也会插入缓存中：
        Integer value1 = cache.get(key1);
        System.out.println(value1);

        // 支持直接get一组值，支持批量查找
        Map<Integer, Integer> dataMap = cache.getAll(Arrays.asList(1, 2, 3));
        System.out.println(dataMap);
    }
}
