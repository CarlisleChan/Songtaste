package com.carlisle.songtaste.modle;

import java.util.ArrayList;

/**
 * Created by chengxin on 2/27/15.
 */
public class FMAlbumResult {
    public int code;
    public ArrayList<AlbumInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<AlbumInfo> getData() {
        return data;
    }

    public void setData(ArrayList<AlbumInfo> data) {
        this.data = data;
    }
}
