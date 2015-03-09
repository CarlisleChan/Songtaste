package com.carlisle.songtaste.ui.local;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.SongInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/7/15.
 */
public class SongAdapter<T> extends BaseAdapter {
    private ArrayList<T> dataList = new ArrayList<>();
    private Context context;

    public SongAdapter(Context context) {
        this.context = context;
//        this.dataList.addAll(dataList);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ((SimpleHolder) holder).upUserName.setText(((SongInfo) getItem(position)).getUpUName());
        Picasso.with(context)
                .load(((SongInfo) getItem(position)).getUpUIcon())
                .placeholder(R.drawable.ic_account_circle_grey600_24dp)
                .into(((SimpleHolder) holder).upUserAvatar);

        Log.i("++",""+ ((SongInfo) getItem(position)).getUpUIcon());

        ((SimpleHolder) holder).songName.setText(((SongInfo) getItem(position)).getName());
        ((SimpleHolder) holder).singerName.setText(((SongInfo) getItem(position)).getSinger());
        ((SimpleHolder) holder).rateDateTime.setText(((SongInfo) getItem(position)).getRateDT());
        ((SimpleHolder) holder).favNum.setText(((SongInfo) getItem(position)).getFavNum());
        ((SimpleHolder) holder).gradeNum.setText(((SongInfo) getItem(position)).getGradeNum());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.song_item, null);
        return new SimpleHolder(view);
    }

    class SimpleHolder extends RecyclerView.ViewHolder {

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

        public SimpleHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);

        }
    }
}
