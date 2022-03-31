package com.fengxuechao.example.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author fengxuechao
 * @date 2022/3/30
 */
public class SyncProducer {

    private final static String namesrvAddr = "127.0.0.1:9876";
    private final static String producerGroup = "test-producer-group";

    private final static String topic = "TopicTest";
    private final static String tags = "TagA";

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer mqProducer = new DefaultMQProducer();
        mqProducer.setNamesrvAddr(namesrvAddr);
        mqProducer.setProducerGroup(producerGroup);
        mqProducer.start();

        SyncProducer syncProducer = new SyncProducer();

        syncProducer.sendSyncMessage(mqProducer);
        syncProducer.sendAsyncMessage(mqProducer);
        syncProducer.sendOnewayMessage(mqProducer);

        mqProducer.shutdown();
    }

    /**
     * 同步发送消息，这种可靠性同步地发送方式使用的比较广泛，比如：重要的消息通知，短信通知。
     */
    private void sendSyncMessage(DefaultMQProducer mqProducer) {
        try {
            for (int i = 0; i < 100; i++) {
                byte[] body = String.format("%d. Sync Message!", i)
                        .getBytes(RemotingHelper.DEFAULT_CHARSET);
                Message message = new Message(topic, tags, body);
                SendResult sendResult = mqProducer.send(message);
                System.out.printf("消息发送结果: %s\n", sendResult);
            }
        } catch (MQClientException | UnsupportedEncodingException | MQBrokerException | RemotingException
                | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送异步消息
     * 异步消息通常用在对响应时间敏感的业务场景，即发送端不能容忍长时间地等待Broker的响应。
     */
    private void sendAsyncMessage(DefaultMQProducer mqProducer) {
        try {
            mqProducer.setRetryTimesWhenSendAsyncFailed(0);
            int messageCount = 100;
            final CountDownLatch2 countDownLatch2 = new CountDownLatch2(messageCount);
            for (int i = 0; i < messageCount; i++) {
                final int index = i;
                byte[] body = String.format("%d. Async Message!", i)
                        .getBytes(RemotingHelper.DEFAULT_CHARSET);
                Message message = new Message(topic, tags, body);
                // SendCallback 接受异步返回结果的回调
                mqProducer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch2.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        countDownLatch2.countDown();
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            }
            countDownLatch2.await(5, TimeUnit.SECONDS);
        } catch (MQClientException | UnsupportedEncodingException | RemotingException
                | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单向发送消息
     * 这种方式主要用在不特别关心发送结果的场景，例如日志发送。
     */
    private void sendOnewayMessage(DefaultMQProducer mqProducer) {
        try {
        for (int i = 0; i < 100; i++) {
            byte[] body = String.format("%d. Oneway Message.", i).getBytes(StandardCharsets.UTF_8);
            Message message = new Message(topic, tags, body);
            mqProducer.sendOneway(message);
        }
        } catch (MQClientException | RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
