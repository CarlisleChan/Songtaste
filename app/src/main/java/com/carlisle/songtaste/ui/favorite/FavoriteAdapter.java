package com.carlisle.songtaste.ui.favorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.SongInfo;
import com.carlisle.songtaste.ui.discover.adapter.BaseViewHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/7/15.
 */
public class FavoriteAdapter extends BaseAdapter {
    private Context context;

    public FavoriteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindView(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_favorite, null);
        return new SimpleHolder(view);
    }

    class SimpleHolder extends BaseViewHolder {
        public View rootView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public SimpleHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        @Override
        public void bindView(int position) {
            songName.setText(((SongInfo) getItem(position)).getName());
            singerName.setText(((SongInfo) getItem(position)).getSinger());
        }
    }
}
