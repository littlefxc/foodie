package com.fengxuechao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 手动填充数据
 *
 * @author fengxuechao
 * @date 2021/8/11
 */
public class CaffeineManualTest {

    /**
     * expireAfterWrite：写入间隔多久淘汰；
     * expireAfterAccess：最后访问后间隔多久淘汰；
     * refreshAfterWrite：写入后间隔多久刷新，该刷新是基于访问被动触发的，支持异步刷新和同步刷新，如果和 expireAfterWrite 组合使用，能够保证即使该缓存访问不到、也能在固定时间间隔后被淘汰，否则如果单独使用容易造成OOM；
     * expireAfter：自定义淘汰策略，该策略下 Caffeine 通过时间轮算法来实现不同key 的不同过期时间；
     * maximumSize：缓存 key 的最大个数；
     * weakKeys：key设置为弱引用，在 GC 时可以直接淘汰；
     * weakValues：value设置为弱引用，在 GC 时可以直接淘汰；
     * softValues：value设置为软引用，在内存溢出前可以直接淘汰；
     * executor：选择自定义的线程池，默认的线程池实现是 ForkJoinPool.commonPool()；
     * maximumWeight：设置缓存最大权重；
     * weigher：设置具体key权重；
     * recordStats：缓存的统计数据，比如命中率等；
     * removalListener：缓存淘汰监听器；
     * writer：缓存写入、更新、淘汰的监听器。
     */
    @Test
    public void test() {
        // 初始化缓存，设置了1分钟的写过期，100的缓存最大个数
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        int key1 = 1;
        // 使用getIfPresent方法从缓存中获取值。如果缓存中不存指定的值，则方法将返回 null：
        System.out.println("1. " + cache.getIfPresent(key1));

        // 也可以使用 get 方法获取值，该方法将一个参数为 key 的 Function 作为参数传入。如果缓存中不存在该 key
        // 则该函数将用于提供默认值，该值在计算后插入缓存中：
        System.out.println("3. " + cache.get(key1, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println("2. " + integer);
                return 2;
            }
        }));

        // 校验key1对应的value是否插入缓存中
        System.out.println("4. " + cache.getIfPresent(key1));

        // 手动put数据填充缓存中
        int value1 = 2;
        cache.put(key1, value1);

        // 使用getIfPresent方法从缓存中获取值。如果缓存中不存指定的值，则方法将返回 null：
        System.out.println("5. " + cache.getIfPresent(1));

        // 移除数据，让数据失效
        cache.invalidate(1);
        System.out.println("6. " + cache.getIfPresent(1));
    }
}
