package com.carlisle.songtaste.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.SongInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 12/25/14.
 */
public class LoadMoreAdapter<T> extends BaseAdapter {

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    public enum LoadStatus {
        LOADING, LOAD_CIMPLETE, LOAD_FAILED
    }

    private Context context;
    private RecyclerView.ViewHolder viewHolder;
    private ArrayList<T> dataList = new ArrayList<>();
    private OnLoadMoreListener loadMoreListener;

    public void setOnLoadMoreClickListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public LoadMoreAdapter(Context context) {
        this.context = context;
    }

    public void resetProgressBarStatus(LoadStatus loadStatus) {
        switch (loadStatus) {
            case LOADING:
                ((VHFooter) viewHolder).progressBar.setVisibility(View.VISIBLE);
                ((VHFooter) viewHolder).progressTip.setText("Loading");
                ((VHFooter) viewHolder).progressLayout.setClickable(false);
                break;
            case LOAD_CIMPLETE:
                ((VHFooter) viewHolder).progressBar.setVisibility(View.INVISIBLE);
                ((VHFooter) viewHolder).progressTip.setText("load complete");
                ((VHFooter) viewHolder).progressLayout.setClickable(false);
                break;
            case LOAD_FAILED:
                ((VHFooter) viewHolder).progressBar.setVisibility(View.INVISIBLE);
                ((VHFooter) viewHolder).progressTip.setText("Click here to reload");
                ((VHFooter) viewHolder).progressLayout.setClickable(true);
                break;
            default:
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_ITEM:
                View itemView = View.inflate(parent.getContext(), R.layout.song_item, null);
                return new VHItem(itemView);
            case TYPE_FOOTER:
                View footerView = View.inflate(parent.getContext(), R.layout.recyclerview_footer, null);
                return new VHFooter(footerView);
            default:
                break;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        viewHolder = holder;

        if (viewHolder instanceof VHItem) {
            ((VHItem) holder).upUserName.setText(((SongInfo)getItem(position)).getUpUName());
            ((VHItem) holder).songName.setText(((SongInfo)getItem(position)).getName());
            ((VHItem) holder).singerName.setText(((SongInfo)getItem(position)).getSinger());
            ((VHItem) holder).rateDateTime.setText(((SongInfo)getItem(position)).getRateDT());
            ((VHItem) holder).favNum.setText(((SongInfo)getItem(position)).getFavNum());
            ((VHItem) holder).gradeNum.setText(((SongInfo) getItem(position)).getGradeNum());
            Picasso.with(context)
                    .load(((SongInfo) getItem(position)).getUserIcon())
                    .placeholder(R.drawable.ic_account_circle_grey600_24dp)
                    .into(((VHItem) holder).upUserAvatar);

        } else if (viewHolder instanceof VHFooter) {
            ((VHFooter) viewHolder).progressLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loadMoreListener != null) {
                        loadMoreListener.onLoadMoreClick(v);
                    }
                }
            });
        }

    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    class VHItem<T> extends RecyclerView.ViewHolder {

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

        public VHItem(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);

        }
    }

    class VHFooter<T> extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.ll_progress)
        LinearLayout progressLayout;
        @InjectView(R.id.progress_bar)
        ProgressBar progressBar;
        @InjectView(R.id.progress_tip)
        TextView progressTip;

        public VHFooter(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

    }

}