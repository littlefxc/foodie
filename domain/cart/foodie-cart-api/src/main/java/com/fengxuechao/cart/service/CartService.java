package com.fengxuechao.cart.service;

import com.fengxuechao.pojo.ShopcartBO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("cart-api")
public interface CartService {

    @PostMapping("addItem")
    public boolean addItemToCart(@RequestParam("userId") String userId,
                                 @RequestBody ShopcartBO shopcartBO);

    @PostMapping("removeItem")
    public boolean removeItemFromCart(@RequestParam("userId") String userId,
                                      @RequestParam("itemSpecId") String itemSpecId);

    @PostMapping("clearCart")
    public boolean clearCart(@RequestParam("userId") String userId);

}