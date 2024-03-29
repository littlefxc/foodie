package com.fengxuechao.service.center;

import com.fengxuechao.pojo.Orders;
import com.fengxuechao.pojo.ResultBean;
import com.fengxuechao.pojo.vo.OrderStatusCountsVO;
import com.fengxuechao.pojo.PagedGridResult;

public interface MyOrdersService {

    /**
     * 查询我的订单列表
     *
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize);

    /**
     * @Description: 订单状态 --> 商家发货
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 —> 确认收货
     *
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     *
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);

    /**
     * 查询用户订单数
     *
     * @param userId
     */
    public OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 获得分页的订单动向
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getOrdersTrend(String userId,
                                          Integer page,
                                          Integer pageSize);

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    ResultBean checkUserOrder(String userId, String orderId);
}