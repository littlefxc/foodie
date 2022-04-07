package com.fengxuechao.example.rocketmq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息事务生产者
 *
 * @author fengxuechao
 * @date 2022/4/6
 */
public class TransactionProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {

        // 生产者事务监听器
        TransactionListener transactionListener = new OrderTransactionListener();
        TransactionMQProducer producer = new TransactionMQProducer();
        ExecutorService executorService = new ThreadPoolExecutor(
                2, 5, 100,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000),
                r -> new Thread(r, "client-transaction-msg-check-thread"));

        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setProducerGroup("producer_order_trans_group");
        producer.start();

        // 发送消息
        String topic = "transaction-topic";
        String tags = "trans-order";
        Order order = new Order();
        order.setId(1);
        order.setUserId(1);
        order.setGoodsName("小脆面");
        order.setTotal(2);
        String orderJson = JSON.toJSONString(order);
        try {
            byte[] orderBytes = orderJson.getBytes(RemotingHelper.DEFAULT_CHARSET);
            Message msg = new Message(topic, tags, "order", orderBytes);
            producer.sendMessageInTransaction(msg, null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Thread.sleep(5000);
        producer.shutdown();
    }
}
