package com.fengxuechao.rabbit.common.serializer.impl;

import com.fengxuechao.rabbit.api.Message;
import com.fengxuechao.rabbit.common.serializer.Serializer;
import com.fengxuechao.rabbit.common.serializer.SerializerFactory;

public class JacksonSerializerFactory implements SerializerFactory {

	public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();
	
	@Override
	public Serializer create() {
		return JacksonSerializer.createParametricType(Message.class);
	}

}
