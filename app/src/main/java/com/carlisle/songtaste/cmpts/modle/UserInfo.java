package com.carlisle.songtaste.cmpts.modle;

/**
 * Created by chengxin on 2/26/15.
 */
public class UserInfo {
    public long uid;
    public String name;
    public String Duomiid;
    public String avatar_small;
    public String avatar_big;

    public String getAvatar_big() {
        return avatar_big;
    }

    public void setAvatar_big(String avatar_big) {
        this.avatar_big = avatar_big;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuomiid() {
        return Duomiid;
    }

    public void setDuomiid(String duomiid) {
        Duomiid = duomiid;
    }

    public String getAvatar_small() {
        return avatar_small;
    }

    public void setAvatar_small(String avatar_small) {
        this.avatar_small = avatar_small;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
