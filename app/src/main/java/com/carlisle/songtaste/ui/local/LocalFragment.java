package com.carlisle.songtaste.ui.local;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVAnalytics;
import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.ui.view.progress.ProgressWheel;
import com.carlisle.songtaste.utils.LocalSongHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/25/15.
 */
public class LocalFragment extends BaseFragment {
    private static final String TAG = LocalFragment.class.getSimpleName();

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;

    private LinearLayoutManager layoutManager;
    public LocalSongAdapter adapter;
    public ArrayList<SongInfo> songsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentEnd(TAG);
        progressBar.setVisibility(View.GONE);
        if (adapter.isEmpty()) {
            refreshData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(TAG);
    }

    private void initRecyclerView() {

        adapter = new LocalSongAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        superRecyclerView.setLayoutManager(layoutManager);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       refreshData();
                    }
                }, 3000);
            }
        });

    }

    private void refreshData() {
        adapter.refresh(LocalSongHelper.getSongList(getActivity()));
    }
}
