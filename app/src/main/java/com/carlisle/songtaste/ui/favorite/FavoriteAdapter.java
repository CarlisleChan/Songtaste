package com.carlisle.songtaste.ui.favorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.PlayerReceivingEvent;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.services.DataAccessor;
import com.carlisle.songtaste.ui.discover.adapter.SongtasteLoadMoreAdapter;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by carlisle on 3/7/15.
 */
public class FavoriteAdapter extends SongtasteLoadMoreAdapter {
    private Context context;

    public FavoriteAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getMyItemViewType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType) {
        FavoriteViewHolder favoriteViewHolder = new FavoriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
        return favoriteViewHolder;
    }

    @Override
    protected void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FavoriteViewHolder)holder).bindView(position);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public FavoriteViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(final int position) {
            songName.setText(((SongInfo) getItem(position)).getSongname());
            singerName.setText(((SongInfo) getItem(position)).getSingername());

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.FAVORITE_QUEUE);
                    DataAccessor.SINGLE_INSTANCE.shot(context, QueueHelper.getInstance().getFavoriteQueue());
                    DataAccessor.SINGLE_INSTANCE.playSongAtIndex(position);
                    EventBus.getDefault().post(new PlayerReceivingEvent(PlayerReceivingEvent.PLAYER_RECEIVING_BROADCAST_CATEGORY_PLAY));
                }
            });
        }
    }
}
