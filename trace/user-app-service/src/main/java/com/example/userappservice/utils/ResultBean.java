package com.example.userappservice.utils;

import com.example.userappservice.entity.User;
import lombok.Data;

/**
 * @author fengxuechao
 * @date 2021/7/27
 */
@Data
public class ResultBean<T> {

    public Integer code;

    public String msg;

    public T data;

    public ResultBean() {
    }

    public ResultBean(Integer code, T data) {
        this(code, "ok", data);
    }

    public ResultBean(Integer code, String msg) {
        this(code, msg, null);
    }

    public ResultBean(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultBean<Object> ok(Object obj) {
        return new ResultBean<>(0, obj);
    }
}
