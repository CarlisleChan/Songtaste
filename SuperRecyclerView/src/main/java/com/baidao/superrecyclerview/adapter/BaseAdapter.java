package com.baidao.superrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexi on 15/3/3.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected ArrayList<T> data = new ArrayList<>();

    public T getItem(int position) {
        return data.get(position);
    }

    public ArrayList<T> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void refresh(List<T> ts) {
        data.clear();
        data.addAll(ts);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

}
