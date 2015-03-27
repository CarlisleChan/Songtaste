package com.carlisle.songtaste.ui.discover;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidao.superrecyclerview.SuperRecyclerView;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.cmpts.modle.AlbumDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongDetailInfo;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.cmpts.provider.converter.XmlConverter;
import com.carlisle.songtaste.ui.discover.adapter.AlbumDetailAdapter;
import com.carlisle.songtaste.utils.Common;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by carlisle on 3/14/15.
 */
public class AlbumDetailActivity extends BaseActivity {

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

    private String aid = "31926";
    private int currentPage = 1;
    private int songsNumber = 20;
    private String tmp = "0";
    private String callback = "dm.st.getDetailBcakAl";
    private String code = "utf8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.inject(this);

//        initStatusBar();
        toolbar.setBackgroundColor(this.getResources().getColor(android.R.color.transparent));
        toolbarContainer.setBackgroundColor(this.getResources().getColor(android.R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new AlbumDetailAdapter(this);
        fetchData(aid, currentPage, songsNumber);

        layoutManager = new MyLayoutManager(this);
        superRecyclerView.setLayoutManager(layoutManager);
        superRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AlbumDetailActivity.this, "刷新失败，请再试一次", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void setSongtasteQueue(String songId) {
        new ApiFactory().getSongtasteApi(new XmlConverter(XmlConverter.ConvterType.SONG))
                .songUrl(songId, "")
                .subscribe(new Observer<SongDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        if (++Common.SONG_NUMBER < adapter.getData().size()) {
                            setSongtasteQueue(((SongInfo) adapter.getData().get(Common.SONG_NUMBER)).getID());
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
        subscription = AndroidObservable.bindActivity(this, new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.ALBUM_DETAIL))
                .albumSong(aid, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callback, code))
                .subscribe(new Observer<AlbumDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        onLoadingFinished(true);
                        Common.SONG_NUMBER = 0;
                        SongInfo songInfo = (SongInfo) adapter.getData().get(Common.SONG_NUMBER);
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
                        adapter.refresh(albumDetailInfo.getData());
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

}
