package com.carlisle.songtaste.ui.offline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.BaseAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

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
        ((DownloadViewHolder) holder).bindView(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownloadViewHolder downloadViewHolder = new DownloadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
        return downloadViewHolder;
    }

    class DownloadViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public DownloadViewHolder(View view) {
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
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.OFFLINE_QUEUE);
                    DataAccessor.SINGLE_INSTANCE.shot(context, QueueHelper.getInstance().getOfflineQueue());
                    DataAccessor.SINGLE_INSTANCE.playSongAtIndex(position);
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
                }
            });
        }
    }
}
