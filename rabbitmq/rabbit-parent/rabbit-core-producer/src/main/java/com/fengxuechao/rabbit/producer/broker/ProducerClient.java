package com.fengxuechao.rabbit.producer.broker;

import com.fengxuechao.rabbit.api.Message;
import com.fengxuechao.rabbit.api.MessageProducer;
import com.fengxuechao.rabbit.api.MessageType;
import com.fengxuechao.rabbit.api.SendCallback;
import com.fengxuechao.rabbit.api.exception.MessageRunTimeException;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * $ProducerClient 发送消息的实际实现类
 *
 * @author fengxuechao
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) throws MessageRunTimeException {
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    /**
     * $send Messagetype
     */
    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {
        messages.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
        });
        rabbitBroker.sendMessages();
    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {
        // TODO Auto-generated method stub

    }

}
