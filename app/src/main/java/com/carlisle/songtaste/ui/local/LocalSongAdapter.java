package com.carlisle.songtaste.ui.local;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.BaseAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.events.PlayEvent;
import com.carlisle.songtaste.modle.SongDetailInfo;
import com.carlisle.songtaste.utils.LocalSongHelper;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

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
        ((LocalSongVH) holder).bindView(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LocalSongVH localSongVH = new LocalSongVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_song, parent, false));
        return localSongVH;
    }

    class LocalSongVH extends RecyclerView.ViewHolder {
        public View itemView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public LocalSongVH(View view) {
            super(view);
            itemView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(final int position) {
            songName.setText(((SongDetailInfo) getItem(position)).getSong_name());
            singerName.setText(((SongDetailInfo) getItem(position)).getSinger_name());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QueueHelper.getInstance().setLocalSongQueue(LocalSongHelper.getSongList(context));
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.LOCAL_QUEUE);
                    EventBus.getDefault().post(new PlayEvent(position));
                }
            });
        }
    }
}
