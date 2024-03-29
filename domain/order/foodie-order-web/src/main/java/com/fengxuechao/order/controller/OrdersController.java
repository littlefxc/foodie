package com.fengxuechao.order.controller;

import com.fengxuechao.cart.pojo.ShopcartBO;
import com.fengxuechao.controller.BaseController;
import com.fengxuechao.enums.OrderStatusEnum;
import com.fengxuechao.enums.PayMethod;
import com.fengxuechao.order.pojo.OrderStatus;
import com.fengxuechao.order.pojo.bo.PlaceOrderBO;
import com.fengxuechao.order.pojo.bo.SubmitOrderBO;
import com.fengxuechao.order.pojo.vo.MerchantOrdersVO;
import com.fengxuechao.order.pojo.vo.OrderVO;
import com.fengxuechao.order.service.OrderService;
import com.fengxuechao.pojo.ResultBean;
import com.fengxuechao.utils.CookieUtils;
import com.fengxuechao.utils.JsonUtils;
import com.fengxuechao.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 幂等性
     *
     * @param session
     * @return
     */
    @ApiOperation(value = "获取订单Token", notes = "获取订单Token", httpMethod = "POST")
    @PostMapping("getOrderToken")
    public ResultBean getOrderToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        redisOperator.set("ORDER_TOKEN" + session.getId(), token, 600);
        return ResultBean.ok(token);
    }

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ResultBean create(
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 幂等性
        String orderTokenKey = "ORDER_TOKEN" + request.getSession().getId();
        String orderToken = redisOperator.get(orderTokenKey);
        // 分布式锁
        String lockKey = "LOCK_KEY_" + orderTokenKey;
        RLock orderTokenLock = redissonClient.getLock(lockKey);
        orderTokenLock.lock(5, TimeUnit.SECONDS);
        try {
            if (StringUtils.isBlank(orderToken)) {
                throw new RuntimeException("orderToken 不存在");
            }
            boolean correctOrderToken = orderToken.equals(submitOrderBO.getOrderToken());
            if (!correctOrderToken) {
                throw new RuntimeException("orderToken 不正确");
            }
            redisOperator.del(orderTokenKey);
        } finally {
            try {
                orderTokenLock.unlock();
            } catch (Exception e) {
            }
        }

        if (!submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)
                && !submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.type)) {
            return ResultBean.errorMsg("支付方式不支持！");
        }

//        System.out.println(submitOrderBO.toString());

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return ResultBean.errorMsg("购物数据不正确");
        }

        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        // 1. 创建订单
        PlaceOrderBO orderBO = new PlaceOrderBO(submitOrderBO, shopcartList);
        OrderVO orderVO = orderService.createOrder(orderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        /**
         * 1001
         * 2002 -> 用户购买
         * 3003 -> 用户购买
         * 4004
         */
        // 清理覆盖现有的redis汇总的购物数据
        shopcartList.removeAll(orderVO.getToBeRemovedShopcatdList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        // 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartList), true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("foodieUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<ResultBean> responseEntity = restTemplate.postForEntity(paymentUrl, entity, ResultBean.class);
        ResultBean paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            logger.error("发送错误：{}", paymentResult.getMsg());
            return ResultBean.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        return ResultBean.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public ResultBean getPaidOrderInfo(String orderId) {

        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return ResultBean.ok(orderStatus);
    }
}
