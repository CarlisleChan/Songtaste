package com.carlisle.songtaste.ui.offline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.BaseAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.modle.SongInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/7/15.
 */
public class OfflineAdapter extends BaseAdapter {
    private Context context;

    public OfflineAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((OfflineViewHloder) holder).bindView(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OfflineViewHloder offlineViewHloder = new OfflineViewHloder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline, parent, false));
        return offlineViewHloder;
    }

    class OfflineViewHloder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public OfflineViewHloder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(int position) {
            songName.setText(((SongInfo) getItem(position)).getName());
            singerName.setText(((SongInfo) getItem(position)).getSinger());
        }
    }
}
