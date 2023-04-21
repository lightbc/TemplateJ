package com.lightbc.templatej.exception;

/**
 * 不存在异常
 */
public class NotExistException extends RuntimeException {

    public NotExistException(String msg) {
        super(msg);
    }
}
