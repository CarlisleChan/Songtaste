package com.carlisle.songtaste.modle;

/**
 * Created by chengxin on 2/26/15.
 */
public class User {
    public int code;
    public UserInfo data;

    public User() {
        data = new UserInfo();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
