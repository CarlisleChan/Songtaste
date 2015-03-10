package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
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
        View itemView = View.inflate(parent.getContext(), R.layout.tag_item, null);
        return new VHItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindView(position);
    }

    class VHItem extends BaseViewHolder {
        public View rootView;

        @InjectView(R.id.tag_name)
        TextView tagName;

        public VHItem(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        @Override
        void bindView(int position) {
            tagName.setText(((TagInfo) getItem(position)).getKey());
        }
    }

}