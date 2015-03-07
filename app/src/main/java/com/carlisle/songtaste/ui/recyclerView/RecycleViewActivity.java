package com.carlisle.songtaste.ui.recyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.adapter.LoadMoreAdapter;
import com.carlisle.songtaste.adapter.OnLoadMoreListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RecycleViewActivity extends ActionBarActivity implements OnLoadMoreListener{
    private static final String TAG = RecycleViewActivity.class.getSimpleName();

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager layoutManager;
    public LoadMoreAdapter adapter;
    public ArrayList arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview);
        ButterKnife.inject(this);
        layoutManager = new LinearLayoutManager(this);
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器

        arrayList = new ArrayList();

        for (int i = 0; i < 7; i++) {
            arrayList.add(i, "item" + i);
        }

        adapter = new LoadMoreAdapter(arrayList);

        initRecycleView(recyclerView);
        initSwipeRefreshLayout(swipeRefreshLayout);

    }

    private void initRecycleView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                // dy > 0 表示下滑
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    loadMoreData();
                }
            }
        });
    }


    @Override
    public void onLoadMoreClick(View view) {
        loadMoreData();
    }

    private void loadMoreData() {

        adapter.resetProgressBarStatus(LoadMoreAdapter.LoadStatus.LOADING);

        if (true) { // request success
            adapter.resetProgressBarStatus(LoadMoreAdapter.LoadStatus.LOAD_CIMPLETE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.insert2Bottom(arrayList);
                }
            }, 1000);

        } else {    // request fail
            adapter.resetProgressBarStatus(LoadMoreAdapter.LoadStatus.LOAD_FAILED);
        }

    }

    private void initSwipeRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.insert2Top(arrayList);
                    }
                }, 3000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
