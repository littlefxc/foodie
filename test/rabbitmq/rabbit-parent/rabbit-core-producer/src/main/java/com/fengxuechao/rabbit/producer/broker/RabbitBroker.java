package com.fengxuechao.rabbit.producer.broker;

import com.fengxuechao.rabbit.api.Message;

/**
 * $RabbitBroker 具体发送不同种类型消息的接口
 *
 * @author fengxuechao
 */
public interface RabbitBroker {

    /**
     * 发送迅速消息
     *
     * @param message
     */
    void rapidSend(Message message);

    /**
     * 发送确认消息
     *
     * @param message
     */
    void confirmSend(Message message);

    /**
     * 发送可靠性消息
     *
     * @param message
     */
    void reliantSend(Message message);

    /**
     * 发送批量消息
     */
    void sendMessages();

}
