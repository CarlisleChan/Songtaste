package com.carlisle.songtaste.ui.discover.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/9/15.
 */
public class LoadMoreAdapter extends BaseAdapter {

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    public enum LoadStatus {
        LOADING, LOAD_CIMPLETE, LOAD_FAILED
    }

    private OnLoadMoreListener loadMoreListener;
    private RecyclerView.ViewHolder viewHolder;

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View footerView = View.inflate(parent.getContext(), R.layout.recyclerview_footer, null);
        return new VHFooter(footerView);
    }

    public void resetProgressBarStatus(LoadStatus loadStatus) {
        switch (loadStatus) {
            case LOADING:
                ((VHFooter) viewHolder).loading();
                break;
            case LOAD_CIMPLETE:
                ((VHFooter) viewHolder).loadComplete();
                break;
            case LOAD_FAILED:
                ((VHFooter) viewHolder).loadFailed();
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    class VHFooter extends BaseViewHolder {
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

        @Override
        void bindView(int position) {
            progressLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loadMoreListener != null) {
                        loadMoreListener.onLoadMoreClick(v);
                    }
                }
            });
        }

        public void loading() {
            progressBar.setVisibility(View.VISIBLE);
            progressTip.setText("Loading");
            progressLayout.setClickable(false);
        }

        public void loadComplete() {
            progressBar.setVisibility(View.INVISIBLE);
            progressTip.setText("load complete");
            progressLayout.setClickable(false);
        }

        public void loadFailed() {
            progressBar.setVisibility(View.INVISIBLE);
            progressTip.setText("Click here to reload");
            progressLayout.setClickable(true);
        }
    }
}
