package com.fengxuechao.demo.dubbbo;

import com.fengxuechao.demo.dubbo.Product;
import com.fengxuechao.demo.dubbo.service.IDubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengxuechao
 * @date 2022/6/8
 */
@RestController
public class DubboController {

    @Reference(loadbalance = "roundrobin")
    private IDubboService dubboService;

    @PostMapping("/publish")
    public Product publish(@RequestParam String name) {
        Product product = new Product();
        product.setName(name);
        return dubboService.publish(product);
    }
}
