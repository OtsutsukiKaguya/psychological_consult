package com.example.demo.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 捕获所有运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        e.printStackTrace(); // 开发调试阶段建议保留，生产环境可以去掉或用日志记录
        return Result.error(500, "系统运行异常：" + e.getMessage());
    }

    // 可以添加更多异常类型，比如参数异常等
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(400, "参数错误：" + e.getMessage());
    }

    // 兜底异常处理
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error(500, "系统未知错误：" + e.getMessage());
    }
}
