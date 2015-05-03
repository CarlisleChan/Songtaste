package com.baidao.superrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.baidao.superrecyclerview.adapter.LoadMoreAdapter;
import com.baidao.superrecyclerview.adapter.LoadMoreInterface;

public class SuperRecyclerView extends FrameLayout {

    protected RecyclerView recyclerView;
    protected ViewStub     progress;
    protected ViewStub     empty;
    protected View         progressView;
    protected View         emptyView;

    protected boolean clipToPadding;
    protected int     padding;
    protected int     paddingTop;
    protected int     paddingBottom;
    protected int     paddingLeft;
    protected int     paddingRight;
    protected int     scrollbarStyle;
    protected int     emptyId;

    protected RecyclerView.OnScrollListener internalOnScrollListener;
    protected RecyclerView.OnScrollListener externalOnScrollListener;

    protected OnMoreListener     onMoreListener;
    protected boolean            isLoadingMore;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected int superRecyclerViewMainLayout;
    private   int progressId;

    private RecyclerView.Adapter adapter;
    private LoadMoreInterface loadMoreInterface;

    public SwipeRefreshLayout getSwipeToRefresh() {
        return swipeRefreshLayout;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SuperRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            superRecyclerViewMainLayout = a.getResourceId(R.styleable.superrecyclerview_mainLayoutId, R.layout.layout_progress_recyclerview);
            clipToPadding = a.getBoolean(R.styleable.superrecyclerview_recyclerClipToPadding, false);
            padding = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPadding, -1.0f);
            paddingTop = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingTop, 0.0f);
            paddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingBottom, 0.0f);
            paddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingLeft, 0.0f);
            paddingRight = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingRight, 0.0f);
            scrollbarStyle = a.getInt(R.styleable.superrecyclerview_scrollbarStyle, -1);
            emptyId = a.getResourceId(R.styleable.superrecyclerview_layout_empty, 0);
            progressId = a.getResourceId(R.styleable.superrecyclerview_layout_progress, R.layout.layout_progress);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View v = LayoutInflater.from(getContext()).inflate(superRecyclerViewMainLayout, this);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        progress = (ViewStub) v.findViewById(android.R.id.progress);

        progress.setLayoutResource(progressId);
        progressView = progress.inflate();

        empty = (ViewStub) v.findViewById(R.id.empty);
        empty.setLayoutResource(emptyId);
        if (emptyId != 0)
            emptyView = empty.inflate();
        empty.setVisibility(View.GONE);

        initRecyclerView(v);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        View recyclerView = view.findViewById(android.R.id.list);

        if (recyclerView instanceof RecyclerView)
            this.recyclerView = (RecyclerView) recyclerView;
        else
            throw new IllegalArgumentException("SuperRecyclerView works with a RecyclerView!");


        if (this.recyclerView != null) {
            this.recyclerView.setClipToPadding(clipToPadding);
            internalOnScrollListener = new RecyclerView.OnScrollListener() {
                private int[] lastPositions;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();

                    int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

//                    Log.d("SuperRecyclerView", "totalItemCount: " + totalItemCount + ", firstVisibleItemPosition: " + firstVisibleItemPosition + ", visibleItemCount: " + visibleItemCount);
                    boolean isScrollToLast = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
                    if (onMoreListener != null
                            && isScrollToLast
                            && totalItemCount >= visibleItemCount
                            && loadMoreInterface != null
                            && !loadMoreInterface.isLoading()){
                        isLoadingMore = true;
                        loadMoreInterface.showLoading();
                        onMoreListener.onMoreAsked(SuperRecyclerView.this.recyclerView.getAdapter().getItemCount(), lastVisibleItemPosition);
                    }

                    if (externalOnScrollListener != null)
                        externalOnScrollListener.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (externalOnScrollListener != null)
                        externalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            };
            this.recyclerView.setOnScrollListener(internalOnScrollListener);

            if (padding != -1.0f) {
                this.recyclerView.setPadding(padding, padding, padding, padding);
            } else {
                this.recyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }

            if (scrollbarStyle != -1) {
                this.recyclerView.setScrollBarStyle(scrollbarStyle);
            }
        }
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param layoutManager
     */
    public void setLayoutManager(LinearLayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set the adapter to the recycler
     * Automatically hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyview is shown
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        if (adapter instanceof LoadMoreInterface) {
            loadMoreInterface = (LoadMoreInterface) adapter;
        }
        recyclerView.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            private void update() {
                progress.setVisibility(View.GONE);
                isLoadingMore = false;
                swipeRefreshLayout.setRefreshing(false);
                if (recyclerView.getAdapter().getItemCount() == 0 && emptyId != 0) {
                    empty.setVisibility(View.VISIBLE);
                } else if (emptyId != 0) {
                    empty.setVisibility(View.GONE);
                }
            }
        });
        if ((adapter == null || adapter.getItemCount() == 0) && emptyId != 0) {
            empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        recyclerView.setAdapter(null);
    }

    /**
     * Show the progressbar
     */
    public void showProgress() {
        hideRecycler();
        if (emptyId != 0) empty.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the progressbar and show the recycler
     */
    public void showRecycler() {
        hideProgress();
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     *
     * @param listener
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(listener);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param colRes1
     * @param colRes2
     * @param colRes3
     * @param colRes4
     */
    public void setRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        swipeRefreshLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param col1
     * @param col2
     * @param col3
     * @param col4
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        swipeRefreshLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Hide the progressbar
     */
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    /**
     * Hide the recycler
     */
    public void hideRecycler() {
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * Set the scroll listener for the recycler
     *
     * @param listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.externalOnScrollListener = listener;
    }

    /**
     * Add the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        recyclerView.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        recyclerView.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return recyclerView.getAdapter();
    }

    /**
     * Sets the More listener
     *
     * @param onMoreListener
     */
    public void setMoreListener(OnMoreListener onMoreListener) {
        this.onMoreListener = onMoreListener;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * Enable/Disable the More event
     *
     * @param isLoadingMore
     */
    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    /**
     * Remove the moreListener
     */
    public void removeMoreListener() {
        onMoreListener = null;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        recyclerView.setOnTouchListener(listener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        recyclerView.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration);
    }

    /**
     *
     * @return inflated progress view or null
     */
    public View getProgressView() {
        return progressView;
    }

    /**
     *
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        return emptyView;
    }

    public void setRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }
}
