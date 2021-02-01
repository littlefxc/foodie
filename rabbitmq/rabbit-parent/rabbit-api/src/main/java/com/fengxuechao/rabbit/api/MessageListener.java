package com.fengxuechao.rabbit.api;

/**
 * 	$MessageListener 消费者监听消息
 * @author fengxuechao
 *
 */
public interface MessageListener {

	void onMessage(Message message);
	
}
