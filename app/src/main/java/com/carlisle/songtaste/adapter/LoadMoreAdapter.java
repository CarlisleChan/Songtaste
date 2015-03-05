package com.carlisle.songtaste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlisle.songtaste.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 12/25/14.
 */
public class LoadMoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    public enum LoadStatus {
        LOADING, LOAD_CIMPLETE, LOAD_FAILED
    }

    private RecyclerView.ViewHolder viewHolder;

    private ArrayList<T> dataList = new ArrayList<>();
    private OnLoadMoreClickListener loadMoreClickListener;

    public void setOnLoadMoreClickListener(OnLoadMoreClickListener loadMoreClickListener) {
        this.loadMoreClickListener = loadMoreClickListener;
    }

    public LoadMoreAdapter(ArrayList<T> data) {
        dataList = data;
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
                View itemView = View.inflate(parent.getContext(), R.layout.recyclerview_item, null);
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
            ((VHItem) viewHolder).mTextView.setText(getItem(position).toString());
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
                    if (loadMoreClickListener != null) {
                        loadMoreClickListener.onLoadMoreClick(v);
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

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private Object getItem(int position) {
        return dataList.get(position);
    }

    public void insert2Top(ArrayList<T> data) {
        int itemCount = data.size();

        dataList.addAll(0, data);
        notifyItemRangeChanged(0, itemCount);
    }

    public void insert2Bottom(ArrayList<T> data) {
        int itemCount = data.size();
        dataList.addAll(data);
        notifyItemRangeChanged(dataList.size() - itemCount, itemCount);
    }

    public void removeItem() {
        dataList.remove(getItemCount() - 1);
        notifyItemRemoved(getItemCount() - 1);
    }

    public interface OnLoadMoreClickListener {
        public void onLoadMoreClick(View view);
    }


    class VHItem<T> extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView mTextView;


        public VHItem(View view) {
            super(view);
            rootView = view;
            mTextView = (TextView) view;

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