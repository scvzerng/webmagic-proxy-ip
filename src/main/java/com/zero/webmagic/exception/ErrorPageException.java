package com.zero.webmagic.exception;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-15:05
 * Project:webmagic-demo
 * Package:com.zero.webmagic.exception
 * To change this template use File | Settings | File Templates.
 */

public class ErrorPageException extends RuntimeException {
    public ErrorPageException() {
    }

    public ErrorPageException(String message) {
        super(message);
    }

    public ErrorPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorPageException(Throwable cause) {
        super(cause);
    }

    public ErrorPageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
