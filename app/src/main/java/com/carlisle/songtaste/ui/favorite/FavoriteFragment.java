package com.carlisle.songtaste.ui.favorite;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidao.superrecyclerview.OnMoreListener;
import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.modle.CollectionResult;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.utils.Common;
import com.carlisle.songtaste.utils.QueueHelper;
import com.carlisle.songtaste.utils.UserHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by chengxin on 2/25/15.
 */
public class FavoriteFragment extends BaseFragment implements OnMoreListener {

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    ProgressDialog progressDialog;

    private FavoriteAdapter adapter;
    private Subscription subscription;

    private int currentPage = 1;
    private int songsNumber = 20;
    private String tmp = "0";
    private String callBack = "dm.st.getDetailBackUser";
    private String code = "utf8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);
        setupSuperRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.isEmpty()) {
            fetchData(UserHelper.getInstance().getUID(), currentPage, songsNumber);
        }
    }

    private void setupSuperRecyclerView() {
        adapter = new FavoriteAdapter(getActivity());
//        adapter.setOnLoadMoreClickListener(this);

        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setMoreListener(this);
        superRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        });

        superRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                        fetchData(UserHelper.getInstance().getUID(), currentPage, songsNumber);
                    }
                }, 3000);
            }
        });

    }

    protected final void onLoadingFinished(boolean success) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (getActivity() == null) return;
        if (!success) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "刷新失败，请再试一次", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && superRecyclerView != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onMoreAsked(int totalCount, int currentPosition) {
        fetchData(UserHelper.getInstance().getUID(), currentPage, songsNumber);
    }

    private void fetchData(String uid, int currentPage, int songsNumber) {
        subscription = AndroidObservable.bindActivity(getActivity(), new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.COLLECTION_RESULT))
                .collectionSong(uid, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callBack, code))
                .subscribe(new Observer<CollectionResult>() {
                    @Override
                    public void onCompleted() {
                        onLoadingFinished(true);
                        Common.SONG_NUMBER = 0;
                        SongInfo songInfo = (SongInfo) adapter.getData().get(Common.SONG_NUMBER);
                        QueueHelper.getInstance().getFavoriteQueue().clear();
                        setSongtasteQueue(songInfo.getSongid());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onLoadingFinished(false);
                    }

                    @Override
                    public void onNext(CollectionResult collectionResult) {
                        adapter.refresh(collectionResult.getData());
                    }
                });
    }

    public void setSongtasteQueue(String songId) {
        new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.SONG))
                .songUrl(songId, "")
                .subscribe(new Observer<SongDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        if ((++Common.SONG_NUMBER) < adapter.getData().size()) {
                            setSongtasteQueue(((SongInfo) adapter.getData().get(Common.SONG_NUMBER)).getSongid());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SongDetailInfo songDetailInfo) {
                        QueueHelper.getInstance().getFavoriteQueue().add(songDetailInfo);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }
}