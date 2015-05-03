package com.baidao.superrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.R;

public class StringListAdapter extends LoadMoreAdapter<String> {

    public StringListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getMyItemViewType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, parent, false);
        return new ViewHolder1(view);
    }

    @Override
    protected void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder1)holder).textView.setText(getItem(position));
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder1(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
