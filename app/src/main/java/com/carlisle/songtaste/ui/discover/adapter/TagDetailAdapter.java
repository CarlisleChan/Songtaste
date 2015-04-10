package com.carlisle.songtaste.ui.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidao.superrecyclerview.adapter.LoadMoreAdapter;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.cmpts.events.PlayEvent;
import com.carlisle.songtaste.cmpts.modle.SongInfo;
import com.carlisle.songtaste.utils.QueueHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by chengxin on 12/25/14.
 */
public class TagDetailAdapter extends LoadMoreAdapter {

    private Context context;
    public TagDetailAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateMyHolder(ViewGroup parent, int viewType) {
        TagDetailVH tagDetailVH = new TagDetailVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
        return tagDetailVH;
    }

    @Override
    protected void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TagDetailVH) holder).bindView(position);
    }

    @Override
    protected int getMyItemViewType(int position) {
        return 0;
    }

    class TagDetailVH extends RecyclerView.ViewHolder {
        public View rootView;

        @InjectView(R.id.tv_song_name)
        TextView songName;
        @InjectView(R.id.tv_singer_name)
        TextView singerName;

        public TagDetailVH(View view) {
            super(view);
            rootView = view;
            ButterKnife.inject(this, view);
        }

        public void bindView(final int position) {
            songName.setText(((SongInfo) getItem(position)).getSongname());
            singerName.setText(((SongInfo) getItem(position)).getSingername());

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QueueHelper.getInstance().setCurrentQueue(QueueHelper.QueueType.TAG_DEAIL_QUEUE);
                    EventBus.getDefault().post(new PlayEvent(position));
                }
            });
        }
    }
}