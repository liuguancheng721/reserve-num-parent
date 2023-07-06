package com.fengzhu.yygh.exception;

import com.fengzhu.yygh.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @RestControllerAdvice 用于定义全局的异常处理和全局数据绑定的通知（advice）类。
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler 捕获并处理全局范围内的异常。
     */
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法。需要手动throw
     */
    @ExceptionHandler(YyghException.class)
    public Result error(YyghException e) {
        return Result.build(e.getCode(),e.getMessage());
    }
}
