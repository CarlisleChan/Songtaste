package com.carlisle.songtaste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.SongInfo;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by carlisle on 3/7/15.
 */
public class AlbumAdapter extends BaseAdapter {
    private ArrayList<SongInfo> dataList = new ArrayList<>();

    public AlbumAdapter(ArrayList<SongInfo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.album_item, null);
        return new SimpleHolder(view);
    }

    class SimpleHolder extends RecyclerView.ViewHolder {

        public View rootView;

        @InjectView(R.id.album_icon)
        ImageView albumImageView;
        @InjectView(R.id.album_name)
        TextView albumName;

        public SimpleHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);

        }
    }
}
