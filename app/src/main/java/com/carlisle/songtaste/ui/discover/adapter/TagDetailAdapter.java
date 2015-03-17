package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.modle.SongDetailInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 12/25/14.
 */
public class TagDetailAdapter extends LoadMoreAdapter {

    private Context context;

    public TagDetailAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = View.inflate(parent.getContext(), R.layout.item_tag_detail, null);
            return new VHItem(itemView);
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindView(position);
    }

    class VHItem extends BaseViewHolder {
        public View rootView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public VHItem(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        @Override
        public void bindView(int position) {
            songName.setText(((SongDetailInfo) getItem(position)).getSongname());
            singerName.setText(((SongDetailInfo) getItem(position)).getSingername());
        }
    }



}