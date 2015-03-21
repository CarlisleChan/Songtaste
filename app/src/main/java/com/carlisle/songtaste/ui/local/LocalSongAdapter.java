package com.carlisle.songtaste.ui.local;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.services.MusicService;
import com.carlisle.songtaste.ui.discover.adapter.BaseViewHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/7/15.
 */
public class LocalSongAdapter extends BaseAdapter {
    private Context context;

    public LocalSongAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindView(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_local_song, null);
        return new SimpleHolder(view);
    }

    class SimpleHolder extends BaseViewHolder {
        public View itemView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public SimpleHolder(View view) {
            super(view);
            itemView = view;
            ButterKnife.inject(this, view);
        }

        @Override
        public void bindView(final int position) {
            songName.setText(((SongDetailInfo) getItem(position)).getSong_name());
            singerName.setText(((SongDetailInfo) getItem(position)).getSinger_name());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, MusicService.class);
                    i.putExtra(MusicService.LAUNCH_NOW_PLAYING_ACTION, (SongDetailInfo)dataList.get(position));
                    context.startService(i);
                }
            });
        }
    }
}
