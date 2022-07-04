package com.github.niefy.common.exception;

import com.github.niefy.common.utils.Result;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 * @author Mark sunlightcs@gmail.com
 */
@RestControllerAdvice
public class RRExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public Result handleRrException(RRException e) {
        Result result = new Result();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());

        return result;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return Result.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return Result.error("数据库中已存在该记录");
    }

    @ExceptionHandler(AuthorizationException.class)
    public Result handleAuthorizationException(AuthorizationException e) {
        logger.error(e.getMessage(), e);
        return Result.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler({WxErrorException.class})
    public Result handleWxErrorException(WxErrorException e) {
        logger.error(e.getMessage(), e);
        return Result.error("微信公众平台接口错误:" + e.getError().getErrorMsg());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return Result.error();
    }
}
