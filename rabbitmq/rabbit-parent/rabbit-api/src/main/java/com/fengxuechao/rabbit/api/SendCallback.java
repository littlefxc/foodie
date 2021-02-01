package com.fengxuechao.rabbit.api;

/**
 * 	$SendCallback 回调函数处理
 * @author fengxuechao
 *
 */
public interface SendCallback {

	void onSuccess();
	
	void onFailure();
	
}
