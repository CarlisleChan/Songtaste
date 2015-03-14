package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseAdapter;
import com.carlisle.songtaste.modle.AlbumInfo;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_album, null);
        return new VHItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindView(position);
    }

    class VHItem extends BaseViewHolder {
        public View rootView;

        @InjectView(R.id.album_icon)
        ImageView albumIcon;
        @InjectView(R.id.album_name)
        TextView albumName;

        public VHItem(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        @Override
        public void bindView(int position) {
            albumName.setText(((AlbumInfo) getItem(position)).getAlbum_name());

            Picasso.with(context)
                    .load(((AlbumInfo) getItem(position)).getAlbum_icon())
                    .placeholder(R.drawable.ic_account_circle_grey600_24dp)
                    .into(albumIcon);
        }
    }

}