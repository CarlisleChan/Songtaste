package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.carlisle.songtaste.cmpts.modle.AlbumDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.AlbumDetailAdapter;
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
public class AlbumDetailFragment extends BaseFragment implements OnMoreListener {
    private static final String TAG = AlbumDetailFragment.class.getSimpleName();
    public static String ALBUM_ID = "album_id";
    public static String ALBUM_NAME = "album_name";

    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressWheel progressBar;
    ProgressDialog progressDialog;

    private AlbumDetailAdapter adapter;
    private Subscription subscription;
    private String albumId;
    private String albumName;
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    private int currentPage = 1;
    private int songsNumber = 20;
    private String tmp = "0";
    private String callback = "dm.st.getDetailBcakAl";
    private String code = "utf8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_with_swipe, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            albumId = bundle.getString(ALBUM_ID);
            albumName = bundle.getString(ALBUM_NAME);
        }

        ((MainActivity) getActivity()).resetToolbarTitleAndIcon(albumName, R.drawable.ic_btn_left);
        setupSuperRecyclerView();

        return view;
    }

    public void onEvent(RefreshDataEvent event) {
        Log.d("toolbar==>", "AlbumDetailFragment");
        if (getUserVisibleHint()) {
            superRecyclerView.getSwipeToRefresh().setRefreshing(true);
            superRecyclerView.getRecyclerView().smoothScrollToPosition(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    superRecyclerView.getSwipeToRefresh().setRefreshing(false);
                    fetchData(albumId, currentPage, songsNumber, true);
                }
            }, 3000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(TAG);
        if (adapter.isEmpty()) {
            fetchData(albumId, currentPage, songsNumber, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(TAG);
    }

    private void setupSuperRecyclerView() {
        adapter = new AlbumDetailAdapter(getActivity());
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
                        fetchData(albumId, currentPage, songsNumber, true);
                    }
                }, 3000);
            }
        });

    }

    @Override
    public void onMoreAsked(int totalCount, int currentPosition) {
        Log.d("total===>", "" + totalCount);
        if (totalCount < 20) {
            onLoadingFinished(true);
        } else if (getQueueDone) {
            fetchData(albumId, ++currentPage, songsNumber, false);
        }
    }

    public class MyLayoutManager extends LinearLayoutManager {

        public MyLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            if (adapter.getItemCount() <= 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }

            View view = recycler.getViewForPosition(0);
            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                int measuredHeight = view.getMeasuredHeight();
                setMeasuredDimension(measuredWidth, measuredHeight * adapter.getItemCount());
            }
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
        QueueHelper.getInstance().getAlbumDetailQueue().add(songDetailInfo);
    }

    @Override
    public void onAnalysisError(Throwable e) {
        e.printStackTrace();
        onLoadingFinished(false);
        progressBar.setVisibility(View.GONE);
    }

    private void fetchData(String aid, int page, int songsNumber, final boolean reset) {
        if (reset) {
            currentPage = page = 1;
        }

        subscription = AndroidObservable.bindActivity(getActivity(), new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.ALBUM_DETAIL))
                .albumSong(aid, String.valueOf(page), String.valueOf(songsNumber), tmp, callback, code))
                .subscribe(new Observer<AlbumDetailInfo>() {
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
                    public void onNext(AlbumDetailInfo albumDetailInfo) {
                        currentIndex = 0;
                        if (reset) {
                            currentIndex = 0;
                            QueueHelper.getInstance().getAlbumDetailQueue().clear();
                            adapter.refresh(albumDetailInfo.getData());
                        } else {
                            adapter.add(albumDetailInfo.getData());
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
