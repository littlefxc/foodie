package com.fengxuechao.asynccallback;

/**
 * 这个listener用来做为回调，将worker的执行结果，放到result的参数里。
 * @author fengxuechao
 * @date 2021/9/3
 */
public interface Listener {

    void result(Object result);
}
