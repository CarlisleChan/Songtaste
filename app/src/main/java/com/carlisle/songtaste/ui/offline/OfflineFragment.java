package com.carlisle.songtaste.ui.offline;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.ui.view.ProgressWheel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/25/15.
 */
public class OfflineFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;

    private LinearLayoutManager layoutManager;
    public OfflineAdapter adapter;
    public ArrayList<SongInfo> songsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        progressBar.setVisibility(View.GONE);
        songsList = new ArrayList();
        SongInfo songInfo = new SongInfo();
        for (int i = 0; i < 7; i++) {
            songsList.add(songInfo);
        }

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new OfflineAdapter(getActivity());

        superRecyclerView.setLayoutManager(layoutManager);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                        refreshData();
                    }
                }, 3000);
            }
        });

    }

    private void refreshData() {
        adapter.refresh(songsList);
    }
}
