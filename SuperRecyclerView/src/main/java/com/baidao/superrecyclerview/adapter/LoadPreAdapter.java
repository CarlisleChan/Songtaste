package com.baidao.superrecyclerview.adapter;

import java.util.List;

/**
 * Created by hexi on 15/3/3.
 */
public abstract class LoadPreAdapter<T> extends BaseAdapter<T>{

    public void insert(List<T> data) {
        this.data.addAll(0, data);
        notifyItemRangeInserted(0, data.size());
    }
}
