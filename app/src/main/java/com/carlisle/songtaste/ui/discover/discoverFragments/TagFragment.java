package com.carlisle.songtaste.ui.discover.discoverFragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;
import com.carlisle.songtaste.base.BaseFragment;
import com.carlisle.songtaste.cmpts.events.RefreshDataEvent;
import com.carlisle.songtaste.cmpts.modle.FMTagResult;
import com.carlisle.songtaste.cmpts.modle.TagInfo;
import com.carlisle.songtaste.cmpts.provider.ApiFactory;
import com.carlisle.songtaste.cmpts.provider.converter.JsonConverter;
import com.carlisle.songtaste.ui.discover.adapter.TagAdapter;
import com.carlisle.songtaste.ui.view.ProgressWheel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import me.gujun.android.taggroup.OnTagGroupCLickListener;
import me.gujun.android.taggroup.TagGroup;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chengxin on 2/25/15.
 */
public class TagFragment extends BaseFragment {
    @InjectView(R.id.tag_group)
    TagGroup tagGroup;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.scroll_view)
    ScrollView scrollView;
    @InjectView(R.id.progressBar)
    ProgressWheel progressBar;

    private LinearLayoutManager layoutManager;
    private TagAdapter adapter;
    private Subscription subscription;
    private ArrayList<String> tags = new ArrayList();

    private String songsNumber = "40";
    private String temp = "0";
    private String callback = "dm.st.fmTag";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initTagGroup();
        initSwipeRefreshLayout();
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
        if (event.position == 3) {
            swipeLayout.setRefreshing(true);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                    fetchData();
                }
            }, 3000);
        }
    }

    private void initTagGroup() {
        adapter = new TagAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        tagGroup.setOnTagGroupListener(new OnTagGroupCLickListener() {
            @Override
            public void onClick(View view) {
                String tagKey = ((TagGroup.TagView) view).getText().toString();

                TagDetailFragment tagDetailFragment = new TagDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(TagDetailFragment.TAG_KEY, tagKey);
                tagDetailFragment.setArguments(bundle);
                ((BaseActivity) getActivity()).pushFragment(tagDetailFragment, AlbumDetailFragment.class.getSimpleName());
            }
        });

        tagGroup.setBrightColor(getActivity().getResources().getColor(android.R.color.black));
    }

    private void initSwipeRefreshLayout() {
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        fetchData();
                    }
                }, 3000);
            }
        });
    }

    private void fetchData() {
        subscription = AndroidObservable.bindFragment(this, new ApiFactory()
                .getSongtasteApi(new JsonConverter(JsonConverter.ConverterType.FM_TAG_RESULT))
                .tagList(temp, songsNumber))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FMTagResult>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(FMTagResult fmTagResult) {
                        tags.clear();
                        for (TagInfo tagInfo : fmTagResult.getData()) {
                            tags.add(tagInfo.getKey());
                        }
                        tagGroup.setTags(tags);
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
