package com.zhaoyp.video.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhaoyp.video.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyapeng
 * @version create time:2019-07-0209:44
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class VideoAdapter extends RecyclerView.Adapter {

    private List<VideoModel.VideoitemModel> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public VideoAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VedioViewHolder(
                mInflater.inflate(R.layout.item__video, viewGroup, false), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((VedioViewHolder) viewHolder)
                .updateView(list.get(position).videoUrl, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VedioViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView playImg;
        private Context mContext;

        public VedioViewHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            image = itemView.findViewById(R.id.image);
            playImg = itemView.findViewById(R.id.img_play);
        }

        public void updateView(final String videoUrl,
                               final int position) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoEvent event = new VideoEvent();
                    event.setPostion(position);
                    event.setVideoUrl(videoUrl);
                    event.setArticleId(position + "");
                    EventBus.getDefault().post(event);
                }
            });
        }
    }

    public void refresh(List<VideoModel.VideoitemModel> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}

