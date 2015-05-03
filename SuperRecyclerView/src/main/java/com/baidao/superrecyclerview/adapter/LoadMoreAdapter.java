package com.baidao.superrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidao.superrecyclerview.OnMoreListener;
import com.baidao.superrecyclerview.R;
import com.baidao.superrecyclerview.exception.ItemViewTypeNotSupportedException;

import java.util.List;

/**
 * Created by hexi on 15/3/3.
 */
public abstract class LoadMoreAdapter<T> extends BaseAdapter<T> implements LoadMoreInterface{

    private static final int LOAD_MORE_ITEM_TYPE = -1;
    private Context context;
    private OnMoreListener onLoadMoreClickListener;

    private boolean isLoadedAll = false;
    public boolean isLoading = false;

    protected LoadMoreAdapter(Context context) {
        this.context = context;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    public void setOnLoadMoreClickListener(OnMoreListener onMoreListener) {
        this.onLoadMoreClickListener = onMoreListener;
    }

    @Override
    public void showLoading() {
        this.isLoading = true;
        notifyItemRangeChanged(getItemCount() - 1, 1);
    }

    public void showError() {
        this.isLoading = false;
        notifyItemRangeChanged(getItemCount() - 1, 1);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public void add(List<T> ts) {
        this.isLoading = false;
        if (ts.isEmpty()) {
            isLoadedAll = true;
            notifyItemRangeChanged(getItemCount() - 1, 1);
        } else {
            isLoadedAll = false;
        }
        data.addAll(ts);
        notifyItemRangeInserted(data.size() - ts.size(), ts.size());
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public void refresh(List<T> ts) {
        this.isLoading = false;
        isLoadedAll = false;
        super.refresh(ts);
    }

    @Override
    public void clear() {
        this.isLoading = false;
        isLoadedAll = false;
        super.clear();
    }

    @Override
    public final int getItemViewType(int position) {
        if (getItemCount() -1 == position) {
            return LOAD_MORE_ITEM_TYPE;
        }
        int itemType =  getMyItemViewType(position);
        if (itemType == LOAD_MORE_ITEM_TYPE) {
            throw new ItemViewTypeNotSupportedException();
        }
        return itemType;
    }

    protected abstract int getMyItemViewType(int position);

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_ITEM_TYPE) {
            return getLoadMoreViewHolder(context, parent);
        }
        return onCreateMyHolder(parent, viewType);
    }

    private LoadMoreViewHolder getLoadMoreViewHolder(Context context, ViewGroup parent) {
        return new LoadMoreViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_more_progress, parent, false));
    }

    protected abstract RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            bindLoadMoreView(holder);
        } else {
            onBindMyViewHolder(holder, position);
        }
    }

    protected abstract void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position);

    private void bindLoadMoreView(RecyclerView.ViewHolder holder) {
        LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
        if (data.isEmpty()) {
            loadMoreViewHolder.progressBar.setVisibility(View.GONE);
            loadMoreViewHolder.contentView.setVisibility(View.GONE);
        } else if (isLoadedAll) {
            loadMoreViewHolder.progressBar.setVisibility(View.GONE);
            loadMoreViewHolder.contentView.setVisibility(View.VISIBLE);
            loadMoreViewHolder.contentView.setText("已加载完所有数据");
        } else if (isLoading) {
            loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
            loadMoreViewHolder.contentView.setVisibility(View.VISIBLE);
            loadMoreViewHolder.contentView.setText("数据正在加载中");
        } else {
            loadMoreViewHolder.progressBar.setVisibility(View.GONE);
            loadMoreViewHolder.contentView.setVisibility(View.VISIBLE);
            loadMoreViewHolder.contentView.setText("点击加载更多");
            loadMoreViewHolder.loadMoreContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (onLoadMoreClickListener != null && !isLoading && !isLoadedAll) {
                    showLoading();
                    onLoadMoreClickListener.onMoreAsked(getItemCount(), getItemCount() - 1);
                }
                }
            });
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 1;
    }

    static class LoadMoreViewHolder extends RecyclerView.ViewHolder{

        LinearLayout loadMoreContainer;
        ProgressBar progressBar;
        TextView contentView;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            this.loadMoreContainer = (LinearLayout) itemView.findViewById(R.id.ll_load_more_container);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.pb_loadding);
            this.contentView = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
