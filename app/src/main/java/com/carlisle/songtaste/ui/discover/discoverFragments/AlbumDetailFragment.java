package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.modle.AlbumDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.ui.discover.adapter.AlbumDetailAdapter;
import com.carlisle.songtaste.utils.PreferencesHelper;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by carlisle on 4/9/15.
 */
public class AlbumDetailFragment extends BaseFragment {
    private static final String TAG = AlbumDetailFragment.class.getSimpleName();
    public static String ALBUM_ID = "position";

    @InjectView(R.id.toolbar_container)
    RelativeLayout toolbarContainer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.album_bg)
    ImageView albumBg;
    @InjectView(R.id.recyclerView)
    SuperRecyclerView superRecyclerView;
    ProgressDialog progressDialog;

    private MyLayoutManager layoutManager;
    private AlbumDetailAdapter adapter;
    private Subscription subscription;
    private String albumId;
    private boolean getQueueDone = true;
    private int currentIndex = -1;

    private String aid = "31926";
    private int currentPage = 1;
    private int songsNumber = 20;
    private String tmp = "0";
    private String callback = "dm.st.getDetailBcakAl";
    private String code = "utf8";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_detail, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            albumId = bundle.getString(ALBUM_ID);
        }

        adapter = new AlbumDetailAdapter(getActivity());
        layoutManager = new MyLayoutManager(getActivity());
        superRecyclerView.setLayoutManager(layoutManager);
        superRecyclerView.setAdapter(adapter);
        fetchData(albumId, currentPage, songsNumber);

        return view;
    }

    public class MyLayoutManager extends LinearLayoutManager {

        public MyLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,int heightSpec) {
            if (adapter.getItemCount() <= 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }

            View view = recycler.getViewForPosition(0);
            if(view != null){
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

    public void setSongtasteQueue(String songId) {
        new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.SONG))
                .songUrl(songId, PreferencesHelper.getInstance(getActivity()).getUID(), "")
                .subscribe(new Observer<SongDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        if (++currentIndex < adapter.getData().size()) {
                            setSongtasteQueue(((SongInfo) adapter.getData().get(currentIndex)).getID());
                        } else if (currentIndex == adapter.getData().size()){
                            getQueueDone = true;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onLoadingFinished(true);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SongDetailInfo songDetailInfo) {
                        QueueHelper.getInstance().getAlbumDetailQueue().add(songDetailInfo);
                    }
                });
    }

    private void fetchData(String aid, int currentPage, int songsNumber) {
        subscription = AndroidObservable.bindActivity(getActivity(), new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.ALBUM_DETAIL))
                .albumSong(aid, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callback, code))
                .subscribe(new Observer<AlbumDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        SongInfo songInfo = (SongInfo) adapter.getData().get(currentIndex);
                        QueueHelper.getInstance().getAlbumDetailQueue().clear();
                        setSongtasteQueue(songInfo.getID());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onLoadingFinished(false);
                    }

                    @Override
                    public void onNext(AlbumDetailInfo albumDetailInfo) {
                        currentIndex = 0;
                        adapter.refresh(albumDetailInfo.getData());
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
