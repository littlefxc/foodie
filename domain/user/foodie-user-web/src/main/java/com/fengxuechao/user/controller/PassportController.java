package com.fengxuechao.user.controller;

import com.fengxuechao.auth.service.AuthService;
import com.fengxuechao.auth.service.pojo.Account;
import com.fengxuechao.auth.service.pojo.AuthCode;
import com.fengxuechao.auth.service.pojo.AuthResponse;
import com.fengxuechao.cart.pojo.ShopcartBO;
import com.fengxuechao.controller.BaseController;
import com.fengxuechao.pojo.ResultBean;
import com.fengxuechao.user.pojo.Users;
import com.fengxuechao.user.pojo.bo.UserBO;
import com.fengxuechao.user.pojo.vo.UsersVO;
import com.fengxuechao.user.service.UserService;
import com.fengxuechao.utils.CookieUtils;
import com.fengxuechao.utils.JsonUtils;
import com.fengxuechao.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * @author fengxuechao
 */
@Slf4j
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    /**
     * @see # com.fengxuechao.gateway.filter.AuthFilter
     */
    private static final String AUTH_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "refresh-token";
    private static final String UID_HEADER = "foodie-user-id";
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RedisOperator redisOperator;
    private final AuthService authService;

    public PassportController(UserService userService, PasswordEncoder passwordEncoder, RedisOperator redisOperator, AuthService authService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.redisOperator = redisOperator;
        this.authService = authService;
    }

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public ResultBean<Object> usernameIsExist(@RequestParam String username) {

        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return ResultBean.errorMsg("用户名不能为空");
        }

        // 2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return ResultBean.errorMsg("用户名已经存在");
        }

        // 3. 请求成功，用户名没有重复
        return ResultBean.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public ResultBean<Object> regist(@RequestBody UserBO userBO,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return ResultBean.errorMsg("用户名或密码不能为空");
        }

        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return ResultBean.errorMsg("用户名已经存在");
        }

        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return ResultBean.errorMsg("密码长度不能少于6");
        }

        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return ResultBean.errorMsg("两次密码输入不一致");
        }

        // 4. 实现注册
        Users userResult = userService.createUser(userBO);

        userResult = setNullProperty(userResult);

        // 生成用户token，存入redis会话
        UsersVO usersVO = conventUsersVO(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        // 同步购物车数据
        synchShopcartData(userResult.getId(), request, response);

        return ResultBean.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    /*@HystrixCommand(
            commandKey = "loginFail",                            // 全局唯一的标识符
            groupKey = "password",                               // 全局服务分组，用于组织仪表盘，统计信息
            fallbackMethod = "loginFail",                        // 同一个类里，public private 都可以
            ignoreExceptions = {IllegalArgumentException.class}, // 在列表中的 exception, 不会触发降级
            threadPoolKey = "threadPoolA",                       // 线程组，多个服务可以共用一个线程组
            threadPoolProperties = {
                    // 核心线程数
                    @HystrixProperty(name = "coreSize", value = "20"),
                    // size > 0, LinkedBlockingQueue -> 请求等待队列;
                    // 默认 size = -1, SynchronousQueue -> 不储存元素的阻塞队列;
                    @HystrixProperty(name = "maxQueueSize", value = "40"),
                    // 在 maxQueueSize = -1 时无效, 队列没有达到 maxQueueSize 依然拒绝
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    // (线程池)统计窗口持续时间
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "3000"),
                    // (线程池)统计窗口持续时间内桶子的数量
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "18")
            },
            commandProperties = {
                    // 熔断降级相关属性也可以放在这里了
            }
    )*/
    public ResultBean<Object> login(@RequestBody UserBO userBO,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return ResultBean.errorMsg("用户名或密码不能为空");
        }

        // 1. 实现登录
        Users userResult = userService.findByUsername(username);

        if (userResult == null || !passwordEncoder.matches(password, userResult.getPassword())) {
            return ResultBean.errorMsg("用户名或密码不正确");
        }

        AuthResponse token = authService.tokenize(userResult.getId());
        if (!AuthCode.SUCCESS.getCode().equals(token.getCode())) {
            log.error("Token error - uid={}", userResult.getId());
            return ResultBean.errorMsg("Token error");
        }
        // 将 token 添加到 Header 当中
        addAuth2Header(response, token);

        userResult = setNullProperty(userResult);
        // 生成用户token，存入redis会话
        UsersVO usersVO = conventUsersVO(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        // 同步购物车数据
        synchShopcartData(userResult.getId(), request, response);

        return ResultBean.ok(userResult);
    }

    // TODO 修改前端 JS 代码
    // 在前端页面里拿到 Authorization、refresh-token、foodie-user-id
    // 前端每次请求服务，都把这几个参数带上
    private void addAuth2Header(HttpServletResponse response, AuthResponse token) {
        response.setHeader(AUTH_HEADER, token.getAccount().getToken());
        response.setHeader(REFRESH_TOKEN_HEADER, token.getAccount().getRefreshToken());
        response.setHeader(UID_HEADER, token.getAccount().getUserId());

        // 让前端感知到，过期时间一天，这样就可以在临近过期的时间 refresh token
        Calendar expireTime = Calendar.getInstance();
        expireTime.add(Calendar.DAY_OF_MONTH, 1);
        response.setHeader("token-exp-time", expireTime.getTimeInMillis() + "");
    }

    /**
     * 熔断服务
     *
     * @param userBO
     * @param request
     * @param response
     * @param throwable
     * @return
     * @throws Exception
     */
    private ResultBean<Object> loginFail(@RequestBody UserBO userBO,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         Throwable throwable) throws Exception {
        return ResultBean.errorMsg("验证码错误（模仿 12306）");
    }


    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     * TODO 放到购物车模块
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }


    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ResultBean<Object> logout(@RequestParam String userId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        Account account = Account.builder()
                .token(request.getHeader(AUTH_HEADER))
                .refreshToken(request.getHeader(REFRESH_TOKEN_HEADER))
                .userId(userId)
                .build();
        AuthResponse authResponse = authService.delete(account);
        if (!AuthCode.SUCCESS.getCode().equals(authResponse.getCode())) {
            log.error("Token error - uid={}", userId);
            return ResultBean.errorMsg("Token error");
        }

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录，清除redis中user的会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        // 分布式会话中需要清除用户数据
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return ResultBean.ok();
    }

    public UsersVO conventUsersVO(Users user) {
        // 实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(),
                uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }

}
