package com.fengxuechao.rabbit.producer.service;

import java.util.Date;
import java.util.List;

import com.fengxuechao.rabbit.producer.constant.BrokerMessageStatus;
import com.fengxuechao.rabbit.producer.entity.BrokerMessage;
import com.fengxuechao.rabbit.producer.mapper.BrokerMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageStoreService {

	@Autowired
	private BrokerMessageMapper brokerMessageMapper;
	
	public int insert(BrokerMessage brokerMessage) {
		return this.brokerMessageMapper.insert(brokerMessage);
	}
	
	public BrokerMessage selectByMessageId(String messageId) {
		return this.brokerMessageMapper.selectByPrimaryKey(messageId);
	}

	public void succuess(String messageId) {
		this.brokerMessageMapper.changeBrokerMessageStatus(messageId,
				BrokerMessageStatus.SEND_OK.getCode(),
				new Date());
	}
	
	public void failure(String messageId) {
		this.brokerMessageMapper.changeBrokerMessageStatus(messageId,
				BrokerMessageStatus.SEND_FAIL.getCode(),
				new Date());
	}
	
	public List<BrokerMessage> fetchTimeOutMessage4Retry(BrokerMessageStatus brokerMessageStatus){
		return this.brokerMessageMapper.queryBrokerMessageStatus4Timeout(brokerMessageStatus.getCode());
	}
	
	public int updateTryCount(String brokerMessageId) {
		return this.brokerMessageMapper.update4TryCount(brokerMessageId, new Date());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
