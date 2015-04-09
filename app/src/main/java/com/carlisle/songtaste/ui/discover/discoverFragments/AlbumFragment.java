package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.content.Intent;
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
import com.carlisle.songtaste.cmpts.events.RefreshDataEvent;
import com.carlisle.songtaste.cmpts.modle.FMAlbumResult;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.AlbumDetailActivity;
import com.carlisle.songtaste.ui.discover.adapter.AlbumAdapter;
import com.carlisle.songtaste.ui.discover.listener.RecyclerItemClickListener;
import com.carlisle.songtaste.ui.view.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by chengxin on 2/25/15.
 */
public class AlbumFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.blank)
    View blank;
    @InjectView(R.id.progressBar)
    ProgressWheel progressBar;

    private android.support.v7.widget.GridLayoutManager layoutManager;
    private AlbumAdapter adapter;
    private Subscription subscription;

    private String temp = "0";
    private String callback = "dm.st.fmAlbum";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        blank.setVisibility(View.VISIBLE);
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.isEmpty()) {
            fetchData();
        }
    }

    public void onEvent(RefreshDataEvent event) {
        if (event.position == 2) {
            superRecyclerView.getSwipeToRefresh().setRefreshing(true);
            superRecyclerView.getRecyclerView().smoothScrollToPosition(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                    fetchData();
                }
            }, 3000);
        }
    }

    private void initRecyclerView() {
        adapter = new AlbumAdapter(getActivity());
        layoutManager = new GridLayoutManager(getActivity(), 2);

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

        superRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                        AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString(AlbumDetailFragment.ALBUM_ID, ((AlbumInfo) adapter.getData().get(position)).getAid());
//                        albumDetailFragment.setArguments(bundle);
//                        ((BaseActivity) getActivity()).pushFragment(albumDetailFragment, AlbumDetailFragment.class.getSimpleName());

                        startActivity(new Intent(getActivity(), AlbumDetailActivity.class));
                    }
                })
        );

    }

    private void fetchData() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.FM_ALBUM_RESULT))
                .hotAlbums(temp, callback))
                .subscribe(new Observer<FMAlbumResult>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
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
        EventBus.getDefault().unregister(this);
    }

}
