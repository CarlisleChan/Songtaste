package com.carlisle.songtaste.cmpts.modle;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by carlisle on 3/9/15.
 */
public class AlbumDetailInfo {
    public int code;
    @SerializedName("p")
    public int p;
    public String total;
    public int n;
    public int page;
    public ArrayList<SongInfo> data;
    public AlbumInfo albuminfo;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<SongInfo> getData() {
        return data;
    }

    public void setData(ArrayList<SongInfo> data) {
        this.data = data;
    }

    public AlbumInfo getAlbuminfo() {
        return albuminfo;
    }

    public void setAlbuminfo(AlbumInfo albuminfo) {
        this.albuminfo = albuminfo;
    }
}
