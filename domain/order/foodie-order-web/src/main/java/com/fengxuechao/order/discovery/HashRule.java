package com.fengxuechao.order.discovery;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 基于哈希一致性实现的负载均衡策略
 *
 * @author fengxuechao
 * @date 2022/2/11
 */
public class HashRule extends AbstractLoadBalancerRule {

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .getRequest();

        String uri = request.getServletPath() + "?" + request.getQueryString();
        return route(uri.hashCode(), getLoadBalancer().getAllServers());
    }

    private Server route(int hashId, List<Server> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        // 这个可以换成更好的算法
        TreeMap<Long, Server> serverMap = new TreeMap<>();
        servers.forEach(itemServer -> {
            // 虚化若干个服务节点到环上
            for (int i = 0; i < 8; i++) {
                long hash = hash(itemServer.getId() + i);
                serverMap.put(hash, itemServer);
            }
        });

        long hash = hash(String.valueOf(hashId));
        SortedMap<Long, Server> sortedMap = serverMap.tailMap(hash);

        // request 的 URL 的 hash 值大于任意一个服务器对应的一个 HashKey, 取 servers 中的第一个节点
        if (sortedMap.isEmpty()) {
            return serverMap.firstEntry().getValue();
        }

        return sortedMap.get(sortedMap.firstKey());
    }

    private long hash(String key) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        md5.update(keyBytes);
        byte[] digest = md5.digest();

        long hashcode = ((long) (digest[2] & 0xFF << 16))
                | ((long) (digest[1] & 0xFF << 8))
                | ((long) (digest[1] & 0xFF));

        return hashcode & 0xffffffffL;
    }
}
