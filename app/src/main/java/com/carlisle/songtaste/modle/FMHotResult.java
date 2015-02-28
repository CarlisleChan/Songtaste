package com.carlisle.songtaste.modle;

import java.util.ArrayList;

/**
 * Created by chengxin on 2/27/15.
 */
public class FMHotResult {
    public int total;
    public int code;
    public int n;
    public ArrayList<SongInfo> data;

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

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public ArrayList<SongInfo> getData() {
        return data;
    }

    public void setData(ArrayList<SongInfo> data) {
        this.data = data;
    }
}
