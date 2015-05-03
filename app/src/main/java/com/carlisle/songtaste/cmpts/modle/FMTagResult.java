package com.carlisle.songtaste.cmpts.modle;

import java.util.ArrayList;

/**
 * Created by chengxin on 2/27/15.
 */
public class FMTagResult {
    public int total;
    public int code;
    public ArrayList<TagInfo> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<TagInfo> getData() {
        return data;
    }

    public void setData(ArrayList<TagInfo> data) {
        this.data = data;
    }
}
