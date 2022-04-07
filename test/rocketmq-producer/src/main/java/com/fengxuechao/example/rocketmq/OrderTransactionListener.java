package com.fengxuechao.example.rocketmq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fengxuechao
 * @date 2022/4/7
 */
public class OrderTransactionListener implements TransactionListener {
    /**
     * When send transactional prepare(half) message succeed, this method will be invoked to execute local transaction.
     *
     * @param msg Half(prepare) message
     * @param arg Custom business parameter
     * @return Transaction state
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // RocketMQ 半消息发送成功，开始执行本地事务
        System.out.println("执行本地事务");
        TransactionUtil.startTransaction();
        LocalTransactionState state;
        try {
            // 创建订单
            System.out.println("创建订单");
            String orderStr = new String(msg.getBody());
            Order order = JSON.parseObject(orderStr, Order.class);
            String sql = "insert into orders(id, user_id, goods_name, total) values(?, ?, ?, ?)";
            int executeUpdates = TransactionUtil.execute(sql, order.getId(), order.getUserId(),
                    order.getGoodsName(), order.getTotal());
            if (executeUpdates > 0) {
                // 写入本地事务日志
                System.out.println("写入本地事务日志");
                String logSql = "insert into transaction_log(id, business, foreign_key) values(?, ?, ?)";
                String business = msg.getKeys();
                TransactionUtil.execute(logSql, msg.getTransactionId(), business, order.getId());
            }
            TransactionUtil.commit();
            state = LocalTransactionState.COMMIT_MESSAGE;
        } catch (SQLException e) {
            TransactionUtil.rollback();
            state = LocalTransactionState.ROLLBACK_MESSAGE;
            System.out.println("本地事务异常，回滚");
            e.printStackTrace();
        }
        return state;
    }

    /**
     * When no response to prepare(half) message. broker will send check message to check the transaction status, and this
     * method will be invoked to get local transaction status.
     *
     * @param msg Check message
     * @return Transaction state
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        // 回查本地事务
        System.out.printf("回查本地事务, transactionId = %s%n", msg.getTransactionId());
        TransactionUtil.startTransaction();
        String sql = "select id, business, foreign_key from transaction_log where id = ?";
        try (ResultSet transactionLog = TransactionUtil.select(sql, msg.getTransactionId())) {
            if (transactionLog == null) {
                return LocalTransactionState.UNKNOW;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
