package com.fengxuechao.rabbit.common.serializer;

/**
 * 	序列化和反序列化的接口
 * @author fengxuechao
 */
public interface Serializer {

	byte[] serializeRaw(Object data);
	
	String serialize(Object data);
	
	<T> T deserialize(String content);
	
	<T> T deserialize(byte[] content);
	
}
