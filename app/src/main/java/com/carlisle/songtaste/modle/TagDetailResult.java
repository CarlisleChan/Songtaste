package com.carlisle.songtaste.modle;

import java.util.ArrayList;

/**
 * Created by carlisle on 3/14/15.
 */
public class TagDetailResult {
    public int code;
    public String total;
    public int n;
    public ArrayList<SongDetailInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public ArrayList<SongDetailInfo> getData() {
        return data;
    }

    public void setData(ArrayList<SongDetailInfo> data) {
        this.data = data;
    }
}
