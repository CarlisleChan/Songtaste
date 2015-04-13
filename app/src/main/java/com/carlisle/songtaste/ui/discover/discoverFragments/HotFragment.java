package com.carlisle.songtaste.ui.discover.discoverFragments;

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
import com.carlisle.songtaste.cmpts.events.RefreshDataEvent;
import com.carlisle.songtaste.cmpts.modle.FMHotResult;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.HotAdapter;
import com.carlisle.songtaste.ui.view.ProgressWheel;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chengxin on 2/25/15.
 */
public class HotFragment extends BaseFragment implements OnMoreListener {
    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;
    ProgressDialog progressDialog;

    private HotAdapter adapter;
    private Subscription subscription;
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    private int currentPage = 0;
    private String songsNumber = "20";
    private String temp = "0";
    private String callback = "dm.st.fmHot";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        setupSuperRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.isEmpty()) {
            fetchData(currentPage, true);
        }
    }

    public void onEvent(RefreshDataEvent event) {
        if (getUserVisibleHint()) {
            superRecyclerView.getSwipeToRefresh().setRefreshing(true);
            superRecyclerView.getRecyclerView().smoothScrollToPosition(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                    fetchData(currentPage, true);
                }
            }, 3000);
        }
    }

    private void setupSuperRecyclerView() {
        adapter = new HotAdapter(getActivity());
        adapter.setOnLoadMoreClickListener(this);

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
                        fetchData(currentPage, true);
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
        if (!isVisibleToUser && superRecyclerView != null && subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onMoreAsked(int totalCount, int currentPosition) {
        if (getQueueDone) {
            fetchData(++currentPage, false);
        }
    }

    private void fetchData(int page, final boolean reset) {
        if (reset) {
            currentPage = page = 1;
        }

        subscription = AndroidObservable.bindFragment(this, new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.FM_HOT_RESULT))
                .hotSong(String.valueOf(page), songsNumber, temp, callback))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FMHotResult>() {
                    @Override
                    public void onCompleted() {
                        SongInfo songInfo = (SongInfo) adapter.getData().get(currentIndex);
                        setSongtasteQueue(songInfo.getID());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onLoadingFinished(false);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(FMHotResult fmHotResult) {
                        if (reset) {
                            currentIndex = 0;
                            QueueHelper.getInstance().getHotQueue().clear();
                            adapter.refresh(fmHotResult.getData());
                        } else {
                            adapter.add(fmHotResult.getData());
                        }
                        getQueueDone = false;
                    }
                });
    }

    @Override
    public void onAnalysisCompleted() {
        if (++currentIndex < adapter.getData().size()) {
            setSongtasteQueue(((SongInfo) adapter.getData().get(currentIndex)).getID());
        } else if (currentIndex == adapter.getData().size()) {
            getQueueDone = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onLoadingFinished(true);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onAnalysisNext(SongDetailInfo songDetailInfo) {
        SongDetailInfo songDetailInfo1 = songDetailInfo;
        songDetailInfo1.setAlbumArt(((SongInfo) adapter.getData().get(currentIndex)).getUpUIcon());
        songDetailInfo1.setMediaId(((SongInfo) adapter.getData().get(currentIndex)).getID());
        QueueHelper.getInstance().getHotQueue().add(songDetailInfo);
    }

    @Override
    public void onAnalysisError(Throwable e) {
        e.printStackTrace();
        onLoadingFinished(false);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        EventBus.getDefault().unregister(this);
    }

}