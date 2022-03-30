package com.fengxuechao.order.controller.center;

import com.fengxuechao.controller.BaseController;
import com.fengxuechao.enums.YesOrNo;
import com.fengxuechao.order.fallback.itemservice.ItemCommentsFeignClient;
import com.fengxuechao.order.pojo.OrderItems;
import com.fengxuechao.order.pojo.Orders;
import com.fengxuechao.order.pojo.bo.center.OrderItemsCommentBO;
import com.fengxuechao.order.service.center.MyCommentsService;
import com.fengxuechao.order.service.center.MyOrdersService;
import com.fengxuechao.pojo.PagedGridResult;
import com.fengxuechao.pojo.ResultBean;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @Autowired
//    private ItemCommentsService itemCommentsService;
    private ItemCommentsFeignClient itemCommentsService;

    @Autowired
    private HystrixRequestCacheService hystrixRequestCacheService;

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public ResultBean pending(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        // 判断用户和订单是否关联
        ResultBean checkResult = myOrdersService.checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断该笔订单是否已经评价过，评价过了就不再继续
        Orders myOrder = (Orders) checkResult.getData();
        if (myOrder.getIsComment() == YesOrNo.YES.type) {
            return ResultBean.errorMsg("该笔订单已经评价");
        }

        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);

        return ResultBean.ok(list);
    }


    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public ResultBean saveList(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList) {

        System.out.println(commentList);

        // 判断用户和订单是否关联
        ResultBean checkResult = myOrdersService.checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断评论内容list不能为空
        if (commentList == null || commentList.isEmpty() || commentList.size() == 0) {
            return ResultBean.errorMsg("评论内容不能为空！");
        }

        myCommentsService.saveComments(orderId, userId, commentList);
        return ResultBean.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public ResultBean query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return ResultBean.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = itemCommentsService.queryMyComments(userId, page, pageSize);
        return ResultBean.ok(grid);

        /*HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            // 测试 Hystrix 的 RequestCache 的减压，RequestCache 的具体表现就是客户端连续调用两次，实际只会调用后段远程服务一次。
            // RequestCacheService 上下文可以让方法只被调用一次
            // RequestCache 需要在配置文件中开启：hystrix.command.default.requestCache.enabled=true
            PagedGridResult grid = hystrixRequestCacheService.requestCache(userId, page, pageSize);
            grid = hystrixRequestCacheService.requestCache(userId, page, pageSize);
            return ResultBean.ok(grid);
        } finally {
            context.close();
        }*/

    }


}
