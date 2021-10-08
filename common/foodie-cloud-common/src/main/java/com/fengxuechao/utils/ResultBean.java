package com.fengxuechao.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 自定义响应数据结构
 * 本类可提供给 H5/ios/安卓/公众号/小程序 使用
 * 前端接受此类数据（json object)后，可自行根据业务去实现相关功能
 * <ul>
 *  <li>200：表示成功</li>
 *  <li>500：表示错误，错误信息在msg字段中</li>
 *  <li>501：bean验证错误，不管多少个错误都以map形式返回</li>
 *  <li>502：拦截器拦截到用户token出错</li>
 *  <li>555：异常抛出信息</li>
 *  <li>556: 用户qq校验异常</li>
 *  <li>557: 校验用户是否在CAS登录，用户门票的校验</li>
 * </ul>
 *
 * @author 冯雪超
 * @version V1.0
 */
public class ResultBean<T> {

    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     *  响应中的数据
     */
    private T data;

    @JsonIgnore
    private String ok;    // 不使用

    public ResultBean() {

    }

    public ResultBean(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ResultBean(Integer status, String msg, T data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public ResultBean(T data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public static ResultBean<Object> build(Integer status, String msg, Object data) {
        return new ResultBean<>(status, msg, data);
    }

    public static ResultBean<Object> build(Integer status, String msg, Object data, String ok) {
        return new ResultBean<Object>(status, msg, data, ok);
    }

    public static ResultBean<Object> ok(Object data) {
        return new ResultBean<Object>(data);
    }

    public static ResultBean<Object> ok() {
        return new ResultBean<Object>(null);
    }

    public static ResultBean<Object> errorMsg(String msg) {
        return new ResultBean<Object>(500, msg, null);
    }

    public static ResultBean<Object> errorUserTicket(String msg) {
        return new ResultBean<Object>(557, msg, null);
    }

    public static ResultBean<Object> errorMap(Object data) {
        return new ResultBean<Object>(501, "error", data);
    }

    public static ResultBean<Object> errorTokenMsg(String msg) {
        return new ResultBean<Object>(502, msg, null);
    }

    public static ResultBean<Object> errorException(String msg) {
        return new ResultBean<Object>(555, msg, null);
    }

    public static ResultBean<Object> errorUserQQ(String msg) {
        return new ResultBean<Object>(556, msg, null);
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

}
