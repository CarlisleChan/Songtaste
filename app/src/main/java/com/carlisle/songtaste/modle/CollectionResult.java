package com.carlisle.songtaste.modle;

import java.util.ArrayList;

/**
 * Created by chengxin on 2/26/15.
 */
public class CollectionResult {
    public int code;
    public int nextpage;
    public String collection_total;
    public String support_total;
    public int num;
    public int page;
    public User userinfo;
    public ArrayList<SongDetailInfo> data;

    public CollectionResult() {
        data = new ArrayList<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getNextpage() {
        return nextpage;
    }

    public void setNextpage(int nextpage) {
        this.nextpage = nextpage;
    }

    public String getCollection_total() {
        return collection_total;
    }

    public void setCollection_total(String collection_total) {
        this.collection_total = collection_total;
    }

    public String getSupport_total() {
        return support_total;
    }

    public void setSupport_total(String support_total) {
        this.support_total = support_total;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public User getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(User userinfo) {
        this.userinfo = userinfo;
    }

    public ArrayList<SongDetailInfo> getData() {
        return data;
    }

    public void setData(ArrayList<SongDetailInfo> data) {
        this.data = data;
    }
}
