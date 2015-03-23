package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.LoadMoreAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.events.PlayEvent;
import com.carlisle.songtaste.modle.SongInfo;
import com.carlisle.songtaste.utils.QueueHelper;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 12/25/14.
 */
public class HotAdapter extends LoadMoreAdapter {

    private Context context;
    public HotAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getMyItemViewType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType) {
        HotViewHolder hotViewHolder = new HotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songtaste_song, parent, false));
        return hotViewHolder;
    }

    @Override
    protected void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HotViewHolder) holder).bindView(position);
    }

    class HotViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.iv_up_user_avatar)
        ImageView upUserAvatar;
        @InjectView(R.id.tv_up_user_name)
        TextView upUserName;
        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;
        @InjectView(R.id.tv_rate_date_time)
        TextView rateDateTime;
        @InjectView(R.id.tv_grade_num)
        TextView gradeNum;
        @InjectView(R.id.tv_fav_num)
        TextView favNum;

        public HotViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(final int position) {
            upUserName.setText(((SongInfo) getItem(position)).getUserName());
            songName.setText(((SongInfo) getItem(position)).getName());
            singerName.setText(((SongInfo) getItem(position)).getSinger());
            rateDateTime.setText(((SongInfo) getItem(position)).getUpDT());
            favNum.setText(((SongInfo) getItem(position)).getFavNum());
            gradeNum.setText(((SongInfo) getItem(position)).getGradeNum());

            Picasso.with(context)
                    .load(((SongInfo) getItem(position)).getUserIcon())
                    .placeholder(R.drawable.ic_account_circle_grey600_24dp)
                    .into(upUserAvatar);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.HOT_QUEUE);
                    EventBus.getDefault().post(new PlayEvent(position));
                }
            });
        }
    }

}