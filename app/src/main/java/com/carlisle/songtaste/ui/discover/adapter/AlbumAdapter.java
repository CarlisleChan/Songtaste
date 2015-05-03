package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.BaseAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.modle.AlbumInfo;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chengxin on 12/25/14.
 */
public class AlbumAdapter extends BaseAdapter {

    private Context context;

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false));
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AlbumViewHolder) holder).bindView(position);
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.album_icon)
        ImageView albumIcon;
        @InjectView(R.id.album_name)
        TextView albumName;

        public AlbumViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(int position) {
            albumName.setText(((AlbumInfo) getItem(position)).getAlbumName());

            Picasso.with(context)
                    .load(((AlbumInfo) getItem(position)).getAlbumIcon())
                    .placeholder(R.drawable.default_artist)
                    .into(albumIcon);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}