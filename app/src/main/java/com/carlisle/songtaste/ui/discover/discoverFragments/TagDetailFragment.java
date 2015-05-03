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
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.modle.TagDetailResult;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.TagDetailAdapter;
import com.carlisle.songtaste.ui.main.MainActivity;
import com.carlisle.songtaste.ui.view.progress.ProgressWheel;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by carlisle on 4/9/15.
 */
public class TagDetailFragment extends BaseFragment implements OnMoreListener {
    private static final String TAG = TagDetailFragment.class.getSimpleName();
    public static String TAG_KEY = "tag_key";

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;
    ProgressDialog progressDialog;

    private TagDetailAdapter adapter;
    private Subscription subscription;
    private String tagKey;
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    private int currentPage = 1;
    private int songsNumber = 20;
    private String t = "1";
    private String tmp = "0";
    private String callback = "dm.st.getDetailBackTag";
    private String code = "utf8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tagKey = bundle.getString(TAG_KEY);
        }

        ((MainActivity) getActivity()).resetToolbarTitleAndIcon(tagKey, R.drawable.ic_btn_left);
        setupSuperRecyclerView();

        return view;
    }

    public void onEvent(RefreshDataEvent event) {
        if (getUserVisibleHint()) {
            superRecyclerView.getSwipeToRefresh().setRefreshing(true);
            superRecyclerView.getRecyclerView().smoothScrollToPosition(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                    fetchData(tagKey, currentPage, songsNumber, true);
                }
            }, 3000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentEnd(TAG);
        if (adapter.isEmpty()) {
            fetchData(tagKey, currentPage, songsNumber, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(TAG);
    }

    private void setupSuperRecyclerView() {
        adapter = new TagDetailAdapter(getActivity());
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
                        fetchData(tagKey, currentPage, songsNumber, true);
                    }
                }, 3000);
            }
        });

    }

    @Override
    public void onMoreAsked(int totalCount, int currentPosition) {
        if (getQueueDone) {
            fetchData(tagKey, ++currentPage, songsNumber, false);
        }
    }

    protected final void onLoadingFinished(boolean success) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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
    public void onAnalysisCompleted() {
        if (++currentIndex < adapter.getData().size()) {
            setSongtasteQueue(((SongInfo) adapter.getData().get(currentIndex)).getSongid());
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
        QueueHelper.getInstance().getTagDetailQueue().add(songDetailInfo);
    }

    @Override
    public void onAnalysisError(Throwable e) {
        e.printStackTrace();
        onLoadingFinished(false);
        progressBar.setVisibility(View.GONE);
    }

    private void fetchData(String key, int page, int songsNumber, final boolean reset) {
        if (reset) {
            currentPage = page = 1;
        }

        subscription = AndroidObservable.bindActivity(getActivity(), new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.TAG_DETAIL))
                .tag(key, t, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callback, code))
                .subscribe(new Observer<TagDetailResult>() {
                    @Override
                    public void onCompleted() {
                        SongInfo songInfo = (SongInfo) adapter.getData().get(currentIndex);
                        setSongtasteQueue(songInfo.getSongid());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onLoadingFinished(false);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(TagDetailResult tagDetailResult) {
                        if (reset) {
                            currentIndex = 0;
                            QueueHelper.getInstance().getTagDetailQueue().clear();
                            adapter.refresh(tagDetailResult.getData());
                        } else {
                            adapter.add(tagDetailResult.getData());
                        }
                        getQueueDone = false;
                    }
                });
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
