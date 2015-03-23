package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.BaseAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.modle.TagInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 12/25/14.
 */
public class TagAdapter extends BaseAdapter {

    private Context context;
    public TagAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TagViewHolder tagViewHolder = new TagViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false));
        return tagViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TagViewHolder) holder).bindView(position);
    }



    class TagViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.tag_name)
        TextView tagName;

        public TagViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(int position) {
            tagName.setText(((TagInfo) getItem(position)).getKey());
        }
    }

}