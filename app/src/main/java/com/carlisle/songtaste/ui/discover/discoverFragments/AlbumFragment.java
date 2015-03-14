package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.modle.FMAlbumResult;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.GsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.AlbumAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by chengxin on 2/25/15.
 */
public class AlbumFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private android.support.v7.widget.GridLayoutManager layoutManager;
    private AlbumAdapter adapter;
    private Subscription subscription;

    private String temp = "0";
    private String callback = "dm.st.fmAlbum";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new GridLayoutManager(getActivity(), 3);
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        adapter = new AlbumAdapter(getActivity());
        refreshData();

        initRecycleView(recyclerView);
        initSwipeRefreshLayout(swipeLayout);

        return view;
    }

    private void initRecycleView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
                        refreshData();
                    }
                }, 3000);
            }
        });
    }

    private void refreshData() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.FM_ALBUM_RESULT))
                .hotAlbums(temp, callback))
                .subscribe(new Observer<FMAlbumResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FMAlbumResult fmAlbumResult) {
                        adapter.refresh(fmAlbumResult.getData());
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }
}
