package com.fengxuechao;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 查看缓存命中率
 *
 * @author fengxuechao
 * @date 2021/8/12
 */
public class CaffeineRecordTest {

    private int getInDb(int key) {
        return key + 1;
    }

    @Test
    public void test() throws InterruptedException {
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .maximumSize(50)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        return getInDb(key);
                    }
                });
        for (int i = 0; i < 5000; i++) {
            if (i == 1000) {
                Thread.sleep(1000);
            }
            System.out.print(i % 100 +" ");
            cache.get(i % 100);
        }

        System.out.println();
        // 命中率
        System.out.println(cache.stats().hitRate());
        // 被剔除的数量
        System.out.println(cache.stats().evictionCount());
        // 加载新值所花费的平均时间（纳秒）
        System.out.println(cache.stats().averageLoadPenalty());
    }
}
