package com.carlisle.songtaste.ui.discover.discoverFragments;

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
import com.carlisle.songtaste.modle.FMNewResult;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.GsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.NewAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chengxin on 2/25/15.
 */
public class NewFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private LinearLayoutManager layoutManager;
    private NewAdapter adapter;
    private Subscription subscription;

    private int currentPage = 1;
    private String songsNumber = "20";
    private String temp = "0";
    private String callback = "dm.st.fmNew";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        initRecyclerView();
        initSwipeRefreshLayout();
        refreshData();
        return view;
    }

    private void initRecyclerView() {

        layoutManager = new LinearLayoutManager(getActivity());
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        adapter = new NewAdapter(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                // dy > 0 表示下滑
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    loadMoreData(++currentPage);
                }
            }
        });


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

    private void loadMoreData(int page) {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.FM_NEW_RESULT))
                .recList(String.valueOf(page), songsNumber, temp, callback))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FMNewResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(FMNewResult songListResult) {
                        adapter.insert2Bottom(songListResult.getData());
                    }
                });
    }

    private void refreshData() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.FM_NEW_RESULT))
                .recList("1", songsNumber, temp, callback))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FMNewResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(FMNewResult songListResult) {
                        adapter.refresh(songListResult.getData());
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }
}
