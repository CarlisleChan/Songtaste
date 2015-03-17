package com.carlisle.songtaste.ui.discover;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.modle.TagDetailResult;
import com.carlisle.songtaste.provider.ApiFactory;
import com.carlisle.songtaste.provider.converter.GsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.LoadMoreAdapter;
import com.carlisle.songtaste.ui.discover.adapter.TagDetailAdapter;
import com.carlisle.songtaste.ui.view.BottomScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by carlisle on 3/14/15.
 */
public class TagDetailActivity extends BaseActivity {
    public static final String TAG_KEY = "tag_key";

    @InjectView(R.id.toolbar_container)
    RelativeLayout toolbarContainer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.album_bg)
    ImageView albumBg;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.scroll_view)
    BottomScrollView scrollView;
    @InjectView(R.id.linear_layout)
    LinearLayout linearLayout;

    private MyLayoutManager layoutManager;
    private TagDetailAdapter adapter;
    private Subscription subscription;
    private int lastY = -1;

    private String key = "纯音乐";
    private int currentPage = 1;
    private int songsNumber = 20;
    private String t = "1";
    private String tmp = "0";
    private String callback = "dm.st.getDetailBackTag";
    private String code = "utf8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_detail);
        ButterKnife.inject(this);

//        initStatusBar();
        toolbar.setBackgroundColor(this.getResources().getColor(android.R.color.transparent));
        toolbarContainer.setBackgroundColor(this.getResources().getColor(android.R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new TagDetailAdapter(this);
        fetchData(key, currentPage, songsNumber);

        layoutManager = new MyLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        scrollView.setOnScrollToBottomLintener(new BottomScrollView.OnScrollToBottomListener() {

            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom && adapter.loadStatus != LoadMoreAdapter.LoadStatus.LOADING) {
                    fetchData(key, ++currentPage, songsNumber);
                }
            }
        });

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

    private void fetchData(String key, int currentPage, int songsNumber) {
        subscription = AndroidObservable.bindActivity(this, new ApiFactory().getSongtasteApi(new GsonConverter(GsonConverter.ConverterType.TAG_DETAIL))
                .tag(key, t, String.valueOf(currentPage), String.valueOf(songsNumber), tmp, callback, code))
                .subscribe(new Observer<TagDetailResult>() {
                    @Override
                    public void onCompleted() {
                        adapter.loadStatus = LoadMoreAdapter.LoadStatus.LOAD_CIMPLETE;
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.loadStatus = LoadMoreAdapter.LoadStatus.LOAD_FAILED;
                    }

                    @Override
                    public void onNext(TagDetailResult tagDetailResult) {
                        adapter.insert2Bottom(tagDetailResult.getData());
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
