package com.carlisle.songtaste.ui.offline;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.adapter.SimpleAdapter;
import com.carlisle.songtaste.base.BaseFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/25/15.
 */
public class OffLineFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private LinearLayoutManager layoutManager;
    public SimpleAdapter adapter;
    public ArrayList arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器

        arrayList = new ArrayList();

        for (int i = 0; i < 7; i++) {
            arrayList.add(i, "item" + i);
        }

        adapter = new SimpleAdapter(arrayList);

        initRecycleView(recyclerView);
        initSwipeRefreshLayout(swipeLayout);

        return view;
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
}
