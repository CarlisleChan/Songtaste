package com.carlisle.songtaste.provider.exception;

import android.text.TextUtils;

import com.google.common.base.Optional;

import org.apache.http.HttpStatus;


/**
 * Created by carlisle on 15/1/14.
 */
public class ResponseError extends Throwable{
    public int statusCode;
    public Optional<String> msg;

    public ResponseError(int statusCode, String msg) {
        this.statusCode = statusCode > 0 ? statusCode : HttpStatus.SC_INTERNAL_SERVER_ERROR;
        if (statusCode == HttpStatus.SC_BAD_REQUEST
                || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_REQUEST_TIMEOUT
                || statusCode == HttpStatus.SC_BAD_GATEWAY
                || statusCode == HttpStatus.SC_GATEWAY_TIMEOUT) {
            msg = null;
        }

        if (TextUtils.isEmpty(msg)) {
            this.msg = Optional.absent();
        } else {
            this.msg = Optional.of(msg);
        }
    }

}
