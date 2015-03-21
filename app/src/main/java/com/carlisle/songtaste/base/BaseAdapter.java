package com.carlisle.songtaste.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlisle on 3/7/15.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<T> dataList = new ArrayList();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public void insert2Top(ArrayList<T> data) {
        int itemCount = data.size();
        this.dataList.addAll(0, data);
        notifyItemRangeChanged(0, itemCount);
    }

    public void insert2Bottom(ArrayList<T> data) {
        int itemCount = data.size();
        this.dataList.addAll(data);
        notifyItemRangeChanged(dataList.size() - itemCount, itemCount);
    }

    public void removeItem(int position) {
        this.dataList.remove(position - 1);
        notifyItemRemoved(getItemCount() - 1);
    }

    public void refresh(List<T> dataList) {
        int itemCount = dataList.size();
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

}
