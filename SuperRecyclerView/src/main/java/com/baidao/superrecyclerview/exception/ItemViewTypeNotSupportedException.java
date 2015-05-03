package com.baidao.superrecyclerview.exception;

/**
 * Created by hexi on 15/3/4.
 */
public class ItemViewTypeNotSupportedException extends RuntimeException{

    public ItemViewTypeNotSupportedException() {
    }

    public ItemViewTypeNotSupportedException(String detailMessage) {
        super(detailMessage);
    }

    public ItemViewTypeNotSupportedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public ItemViewTypeNotSupportedException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
