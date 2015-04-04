package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.LoadMoreAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.utils.Common;
import com.carlisle.songtaste.utils.QueueHelper;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 12/25/14.
 */
public class NewAdapter extends LoadMoreAdapter {

    private Context context;

    public NewAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType) {
        NewViewHolder newViewHolder = new NewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songtaste_song, parent, false));
        return newViewHolder;
    }

    @Override
    protected void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NewViewHolder) holder).bindView(position);
//        bindView(RecyclerView.ViewHolder holder, int position);
    }

    @Override
    protected int getMyItemViewType(int position) {
        return 0;
    }

    class NewViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.iv_up_user_avatar)
        RoundedImageView upUserAvatar;
        @InjectView(R.id.tv_up_user_name)
        TextView upUserName;
        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_rate_date_time)
        TextView rateDateTime;
        @InjectView(R.id.dot)
        ImageView dot;

        public NewViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(final int position) {
            Log.d("bindView===>", "" + position);
            upUserName.setText(((SongInfo) getItem(position)).getUpUName());
            songName.setText(((SongInfo) getItem(position)).getName());
            rateDateTime.setText(((SongInfo) getItem(position)).getRateDT());

            Picasso.with(context)
                    .load(((SongInfo) getItem(position)).getUpUIcon())
                    .placeholder(R.drawable.default_artist)
                    .into(upUserAvatar);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dot.setVisibility(View.VISIBLE);
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.NEW_QUEUE);
                    EventBus.getDefault().post(new PlayEvent(position));
                }
            });

            if (Common.CURRENT_POSITION == position) {
                dot.setVisibility(View.VISIBLE);
            } else {
                dot.setVisibility(View.INVISIBLE);
                Common.CURRENT_POSITION = position;
            }

        }
    }
}