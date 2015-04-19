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

import com.avos.avoscloud.AVAnalytics;
import com.baidao.superrecyclerview.OnMoreListener;
import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.RefreshDataEvent;
import com.carlisle.songtaste.cmpts.modle.FMNewResult;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.NewAdapter;
import com.carlisle.songtaste.ui.view.ProgressWheel;
import com.carlisle.songtaste.utils.QueueHelper;
import com.github.stephanenicolas.loglifecycle.LogLifeCycle;

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
@LogLifeCycle
public class NewFragment extends BaseFragment implements OnMoreListener {
    private static final String TAG = NewFragment.class.getSimpleName();

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;
    ProgressDialog progressDialog;

    private NewAdapter adapter;
    private Subscription subscription;
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    private int currentPage = 1;
    private String songsNumber = "20";
    private String temp = "0";
    private String callback = "dm.st.fmNew";

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
        AVAnalytics.onFragmentEnd(TAG);
        if (adapter.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchData(currentPage, true);
                }
            }, 2000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(TAG);
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
        adapter = new NewAdapter(getActivity());
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
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.FM_NEW_RESULT))
                .recList(String.valueOf(page), songsNumber, temp, callback))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FMNewResult>() {
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
                    public void onNext(FMNewResult songListResult) {
                        if (reset) {
                            currentIndex = 0;
                            QueueHelper.getInstance().getNewQueue().clear();
                            adapter.refresh(songListResult.getData());
                        } else {
                            adapter.add(songListResult.getData());
                        }
                        getQueueDone = false;
                    }
                });
    }

    @Override
    public void onAnalysisCompleted() {
        if (++currentIndex < adapter.getData().size()) {
            setSongtasteQueue(((SongInfo) adapter.getData().get(currentIndex)).getID());
        } else if (currentIndex == adapter.getData().size()){
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
        QueueHelper.getInstance().getNewQueue().add(songDetailInfo);
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