package com.carlisle.songtaste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;

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

    private RecyclerView.ViewHolder viewHolder;
    private ArrayList<T> dataList = new ArrayList<>();
    private OnLoadMoreListener loadMoreListener;

    public void setOnLoadMoreClickListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public LoadMoreAdapter(ArrayList<T> data) {
        this.dataList = data;
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
            ((VHItem) viewHolder).rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), getItem(position).toString(), Toast.LENGTH_SHORT).show();
                }
            });

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