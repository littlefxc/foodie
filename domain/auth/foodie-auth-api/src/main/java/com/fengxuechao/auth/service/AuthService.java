package com.fengxuechao.auth.service;

import com.fengxuechao.auth.service.pojo.Account;
import com.fengxuechao.auth.service.pojo.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengxuechao
 * @date 2021/10/8
 */
@FeignClient("foodie-auth-service")
@RequestMapping("auth-service")
public interface AuthService {

    @PostMapping("token")
    public AuthResponse tokenize(@RequestParam("userId") String userId);

    @PostMapping("verify")
    public AuthResponse verify(@RequestBody Account account);

    @PostMapping("refresh")
    public AuthResponse refresh(@RequestParam("refresh") String refresh);

    @DeleteMapping("delete")
    public AuthResponse delete(@RequestBody Account account);

}
