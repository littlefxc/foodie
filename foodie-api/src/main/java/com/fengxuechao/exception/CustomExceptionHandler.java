package com.fengxuechao.exception;

import com.fengxuechao.pojo.ResultBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author fengxuechao
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 上传文件超过500k，捕获异常：MaxUploadSizeExceededException
     *
     * @param ex MaxUploadSizeExceededException
     * @return ResultBean
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultBean<Object> handlerMaxUploadFile(MaxUploadSizeExceededException ex) {
        return ResultBean.errorMsg("文件上传大小不能超过500k，请压缩图片或者降低图片质量再上传！");
    }
}
