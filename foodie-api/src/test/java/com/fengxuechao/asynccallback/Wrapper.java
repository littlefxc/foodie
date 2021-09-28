package com.fengxuechao.asynccallback;

import lombok.Getter;
import lombok.Setter;

/**
 * 包装器Wrapper，来将worker和回调器包装一下
 * @author fengxuechao
 * @date 2021/9/3
 */
public class Wrapper {

    @Setter
    @Getter
    private Object param;

    @Setter
    @Getter
    private Worker worker;

    @Getter
    private Listener listener;

    public void addListener(Listener listener) {
        this.listener = listener;
    }
}
