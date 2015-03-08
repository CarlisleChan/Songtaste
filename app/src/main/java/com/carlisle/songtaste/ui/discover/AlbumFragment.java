package com.carlisle.songtaste.ui.discover;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.adapter.AlbumAdapter;
import com.carlisle.songtaste.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 2/25/15.
 */
public class AlbumFragment extends BaseFragment {

    @InjectView(R.id.gridView)
    GridView gridView;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    public AlbumAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        ButterKnife.inject(this, view);

        initGridView();
        initSwipeRefreshLayout(swipeLayout);

        return view;
    }

    private void initGridView() {
        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.album);
//            map.put("ItemText", "NO."+String.valueOf(i));
            lstImageItem.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(), //没什么解释
                lstImageItem,//数据来源
                R.layout.album_item,//night_item的XML实现
                new String[] {"ItemImage","ItemText"},
                new int[] {R.id.album_icon,R.id.album_name});

        gridView.setAdapter(saImageItems);
    }

    private void initSwipeRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
//                        adapter.insert2Top(arrayList);
                    }
                }, 3000);
            }
        });
    }
}
