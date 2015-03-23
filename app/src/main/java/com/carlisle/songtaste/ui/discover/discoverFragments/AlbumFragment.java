package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.modle.FMAlbumResult;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.JsonConverter;
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
    SuperRecyclerView superRecyclerView;

    private android.support.v7.widget.GridLayoutManager layoutManager;
    private AlbumAdapter adapter;
    private Subscription subscription;

    private String temp = "0";
    private String callback = "dm.st.fmAlbum";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        initRecyclerView();
        fetchData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.isEmpty()) {
            fetchData();
        }
    }

    private void initRecyclerView() {
        adapter = new AlbumAdapter(getActivity());
        layoutManager = new GridLayoutManager(getActivity(), 3);

        superRecyclerView.setLayoutManager(layoutManager);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                        fetchData();
                    }
                }, 3000);
            }
        });
    }

    private void fetchData() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.FM_ALBUM_RESULT))
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
