package com.carlisle.songtaste.ui.favorite;

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
import com.carlisle.songtaste.ui.local.SongAdapter;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.modle.CollectionResult;
import com.carlisle.songtaste.modle.SongDetailInfo;
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
    private SongAdapter adapter;
    private ArrayList arrayList;
    private Subscription subscription;

    private int currentPage = 1;
    private String songsNumber = "20";
    private String temp = "0";
    private String callback = "";
    private String code = "utf-8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover_new, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
//      layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器

        adapter = new SongAdapter(getActivity());
//        adapter = new SongAdapter(getData("6973651", "1", "20", "0", "dm.st.getDetailBackUser", "utf-8"));

        fetchData("6973651", 1);
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

    private void fetchData(String uid, int page) {

        final ArrayList<SongDetailInfo> data = new ArrayList<>();

        subscription = AndroidObservable.bindFragment(this, new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.COLLECTION_RESULT))
                .collectionSong(uid, String.valueOf(page), songsNumber, temp, callback, code))
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
