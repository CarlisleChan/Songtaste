package com.carlisle.songtaste.ui.favorite;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.modle.SongInfo;
import com.carlisle.songtaste.ui.local.SongAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/25/15.
 */
public class FavoriteFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private LinearLayoutManager layoutManager;
    public SongAdapter adapter;
    public ArrayList<SongInfo> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        arrayList = new ArrayList();
        SongInfo songInfo = new SongInfo();
        for (int i = 0; i < 7; i++) {
            arrayList.add(songInfo);
        }

        initRecycleView();
        initSwipeRefreshLayout();
        refreshData();

        return view;
    }


    private void initRecycleView() {

        layoutManager = new LinearLayoutManager(getActivity());
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        adapter = new SongAdapter(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void initSwipeRefreshLayout() {
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        refreshData();
                    }
                }, 3000);
            }
        });
    }

    private void refreshData() {
        adapter.refresh(arrayList);
    }
}
