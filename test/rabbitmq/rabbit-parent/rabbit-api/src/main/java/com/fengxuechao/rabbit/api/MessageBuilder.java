package com.fengxuechao.rabbit.api;

import com.fengxuechao.rabbit.api.exception.MessageRunTimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * $MessageBuilder 建造者模式
 *
 * @author fengxuechao
 */
public class MessageBuilder {

    /**
     * 消息的唯一ID
     */
    private String messageId;

    /**
     * 消息的主题
     */
    private String topic;

    /**
     * 消息的路由规则
     */
    private String routingKey = "";

    /**
     * 消息的附加属性
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * 延迟消息的参数配置
     */
    private int delayMills;

    /**
     * 消息类型：默认为confirm消息类型
     */
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder(String topic) {
        this.topic = topic;
        this.messageId = UUID.randomUUID().toString();
    }

    private MessageBuilder(String messageId, String topic) {
        this.messageId = messageId;
        this.topic = topic;
    }

    public static MessageBuilder create(String topic) {
        return new MessageBuilder(topic);
    }

    public static MessageBuilder create(String messageId, String topic) {
        return new MessageBuilder(messageId, topic);
    }

    public MessageBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MessageBuilder withAttribute(String key, Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public MessageBuilder withDelayMills(int delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message build() {

        // 1. check messageId
        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }
        // 2. topic is null
        if (topic == null) {
            throw new MessageRunTimeException("this topic is null");
        }
        return new Message(messageId, topic, routingKey, attributes, delayMills, messageType);
    }

}
