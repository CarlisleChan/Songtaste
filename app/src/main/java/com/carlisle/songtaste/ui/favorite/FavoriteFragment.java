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
import com.carlisle.songtaste.modle.CollectionResult;
import com.carlisle.songtaste.modle.SongInfo;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.GsonConverter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by chengxin on 2/25/15.
 */
public class FavoriteFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private LinearLayoutManager layoutManager;
    private FavoriteAdapter adapter;
    private ArrayList<SongInfo> songsList;
    private Subscription subscription;

    private String uid = "6973651";
    private int currentPage = 1;
    private int songsNumber = 20;
    private String tmp = "0";
    private String callBack = "dm.st.getDetailBackUser";
    private String code = "utf8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);

        initRecyclerView();
        initSwipeRefreshLayout();
        fetchData(uid, currentPage, songsNumber);
        return view;
    }


    private void initRecyclerView() {
        adapter = new FavoriteAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
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
                        fetchData(uid, currentPage, songsNumber);
                    }
                }, 3000);
            }
        });
    }

    private void fetchData(String uid, int currentPage, int songsNumber) {
        subscription = AndroidObservable.bindActivity(getActivity(), new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.COLLECTION_RESULT))
                .collectionSong(uid, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callBack, code))
                .subscribe(new Observer<CollectionResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CollectionResult collectionResult) {
                        adapter.refresh(collectionResult.getData());
                    }
                });
    }

}
