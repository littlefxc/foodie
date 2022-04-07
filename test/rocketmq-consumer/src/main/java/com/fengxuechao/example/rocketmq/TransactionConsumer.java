package com.fengxuechao.example.rocketmq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author fengxuechao
 * @date 2022/4/7
 */
public class TransactionConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_order_trans_group");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("transaction-topic", "trans-order");
        consumer.setMaxReconsumeTimes(3);
        TransactionUtil.startTransaction();
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    for (MessageExt msg : msgs) {
                        // 多次消费消息处理仍然失败后，发送邮件，人工处理
                        if (msg.getReconsumeTimes() >= 3) {
                            // 发送邮件，人工处理
                            sendMail();
                        }

                        String orderStr = new String(msg.getBody(), StandardCharsets.UTF_8);
                        Order order = JSON.parseObject(orderStr, Order.class);
                        // 幂等性保持
                        String sql1 = "select * from order_credits where order_id = ?";
                        ResultSet rs = TransactionUtil.select(sql1, order.getId());
                        if (rs != null && rs.next()) {
                            System.out.println("积分已添加，订单已处理！");
                        } else {
                            // 增加积分
                            String sql2 = "insert into order_credits(user_id,order_id,total) values(?,?,?)";
                            TransactionUtil.execute(sql2, order.getUserId(), order.getId(), order.getTotal() * 2);
                            System.out.printf("订单（id=%s）添加积分%n", order.getId());
                            TransactionUtil.commit();
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    TransactionUtil.rollback();
                    e.printStackTrace();
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            private void sendMail() { }
        });

        consumer.start();

        System.out.println("Consumer Started.");
    }
}
