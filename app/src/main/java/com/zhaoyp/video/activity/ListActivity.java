package com.zhaoyp.video.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zhaoyp.video.R;
import com.zhaoyp.video.media.IEvent;
import com.zhaoyp.video.media.IjkVideoView;
import com.zhaoyp.video.media.VideoAdapter;
import com.zhaoyp.video.media.VideoEvent;
import com.zhaoyp.video.media.VideoModel;
import com.zhaoyp.video.util.Utils;
import com.zhaoyp.video.media.VideoItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author zhaoyapeng
 * @version create time:2019-07-0209:41
 * @Email zyp@jusfoun.com
 * @Description ${列表视频}
 */
public class ListActivity extends AppCompatActivity {

    protected RecyclerView mXRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VideoAdapter videoAdapter;
    private VideoItemView videoItemView;
    ImageView closeVideoImg;
    FrameLayout fullScreen;
    private int position = -1;

    private int lastPlayPostion = -1;//用以记录 上次播放的position ，
    // 比如 此时播放的是列表中的position=1位置，点击position=3 处的视频，此时用lastPlayPostion重置状态为完成状态

    FrameLayout videoLayout;
    RelativeLayout rootVideoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_list);
        EventBus.getDefault().register(this);
        initView();



        mLayoutManager = new LinearLayoutManager(this);
        mXRecyclerView.setLayoutManager(mLayoutManager);
        videoAdapter = new VideoAdapter(this);
        mXRecyclerView.setAdapter(videoAdapter);

        VideoModel videoModel = new Gson().fromJson(Utils.readTextFileFromRawResourceId(this, R.raw.video), VideoModel.class);
        videoAdapter.refresh(videoModel.list);

        videoItemView = new VideoItemView(this);

        videoItemView.setStopListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideo();
            }
        });

        videoItemView.setCompletionListener(new VideoItemView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                stopVideo();
            }
        });

        closeVideoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVideo();
            }
        });

        mXRecyclerView.addOnChildAttachStateChangeListener(
                new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View view) {
                        if (view.findViewById(R.id.showview) != null) {
                            view.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                        }
                        if (position == -1) {
                            return;
                        }
                        int index = mXRecyclerView.getChildAdapterPosition(view);
                        if (index == position) {
                            FrameLayout frameLayout = (FrameLayout) view
                                    .findViewById(R.id.layout_video);
                            frameLayout.removeAllViews();
                            if (rootVideoLayout.getVisibility() == View.VISIBLE
                                    && videoItemView != null
                                    && (videoItemView.isPlayOrBuffer()
                                    || videoItemView.getCurrentStatus()
                                    == IjkVideoView.STATE_PAUSED)) {
                                rootVideoLayout.setVisibility(View.GONE);
                                videoLayout.removeAllViews();
                                videoItemView.setClickable(true);
                                frameLayout.addView(videoItemView);
                                videoItemView.setShowContoller(true);
                            }

                            if (rootVideoLayout.getVisibility() == View.GONE
                                    && videoItemView != null
                                    && videoItemView.getCurrentStatus()
                                    == IjkVideoView.STATE_PAUSED) {
                                if (videoItemView.getParent() != null) {
                                    ((ViewGroup) videoItemView.getParent()).removeAllViews();
                                }
                                videoItemView.setClickable(true);
                                frameLayout.addView(videoItemView);

                            }

                        }
                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View view) {
                        try {
                            int index = mXRecyclerView.getChildAdapterPosition(view);
                            if (index == position) {
                                FrameLayout frameLayout = (FrameLayout) view
                                        .findViewById(R.id.layout_video);
                                if (frameLayout != null) {
                                    frameLayout.removeAllViews();
                                    if (videoItemView != null
                                            && rootVideoLayout.getVisibility() == View.GONE
                                            && (videoItemView.isPlay()
                                            || videoItemView.getCurrentStatus()
                                            == IjkVideoView.STATE_PREPARING)) {
                                        videoLayout.removeAllViews();
                                        videoItemView.setClickable(true);
                                        videoLayout.addView(videoItemView);
                                        rootVideoLayout.setVisibility(View.VISIBLE);
                                        videoItemView.setShowContoller(false);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }

                    }
                });

    }

    private void initView() {
        mXRecyclerView = findViewById(R.id.recyclerView);
        videoLayout = findViewById(R.id.layout_video);
        rootVideoLayout = findViewById(R.id.layout_video_root);
        fullScreen = findViewById(R.id.full_screen);
        closeVideoImg = findViewById(R.id.img_close_video);

    }

    private void stopVideo() {
        try {
            //播放完还原播放界面
            if (videoItemView == null) {
                return;
            }
            videoItemView.release();
            lastPlayPostion = -1;
            position = -1;
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            if (rootVideoLayout.getVisibility() == View.VISIBLE) {
                rootVideoLayout.setVisibility(View.GONE);
                videoLayout.removeAllViews();
                return;
            }
            FrameLayout frameLayout = (FrameLayout) videoItemView.getParent();
            if (frameLayout != null && frameLayout.getChildCount() > 0) {
                frameLayout.removeAllViews();
                View itemView = (View) frameLayout.getParent();

                if (itemView != null) {
                    itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(IEvent event) {

        if (event instanceof VideoEvent) {
            position = ((VideoEvent) event).getPostion();
            if (videoItemView == null) {
                videoItemView = new VideoItemView(this);
            }

            if (rootVideoLayout.getVisibility() == View.VISIBLE) {
                rootVideoLayout.setVisibility(View.GONE);
                videoLayout.removeAllViews();
                videoItemView.setShowContoller(true);
            }

            if (lastPlayPostion != -1 && lastPlayPostion != position) {
                ViewGroup last = (ViewGroup) videoItemView.getParent();//找到videoitemview的父类，然后remove
                if (last != null) {
                    last.removeAllViews();
                    View itemView = (View) last.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                    }
                }
            }

            if (videoItemView.getCurrentStatus() == IjkVideoView.STATE_PAUSED) {
                videoItemView.stop();
                videoItemView.release();
                videoItemView = null;
                videoItemView = new VideoItemView(this);
            }



            if (videoItemView.getParent() != null) {
                ((ViewGroup) videoItemView.getParent()).removeAllViews();
            }
            View view = mXRecyclerView.findViewHolderForAdapterPosition(position).itemView;
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
            frameLayout.removeAllViews();
            videoItemView.setClickable(true);
            frameLayout.addView(videoItemView);
            videoItemView.start(((VideoEvent) event).getVideoUrl());
            lastPlayPostion = ((VideoEvent) event).getPostion();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoItemView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (fullScreen != null) {
                    fullScreen.setVisibility(View.GONE);
                    fullScreen.removeAllViews();
                }
                mXRecyclerView.setVisibility(View.VISIBLE);

                if (position == -1) {
                    return;
                }
                if (position
                        < mLayoutManager.findLastVisibleItemPosition()
                        && position
                        >= mLayoutManager.findFirstVisibleItemPosition()) {
                    View view = mXRecyclerView.findViewHolderForAdapterPosition(
                            position).itemView;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
                    frameLayout.removeAllViews();
                    videoItemView.setShowContoller(true);
                    videoItemView.setClickable(true);
                    frameLayout.addView(videoItemView);
                } else {
                    if (videoLayout != null) {
                        videoLayout.removeAllViews();
                        videoItemView.setClickable(true);
                        videoLayout.addView(videoItemView);
                    }
                    videoItemView.setShowContoller(false);
                    rootVideoLayout.setVisibility(View.VISIBLE);
                }

            } else {
                ViewGroup viewGroup = (ViewGroup) videoItemView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                fullScreen.addView(videoItemView);
                mXRecyclerView.setVisibility(View.GONE);
                rootVideoLayout.setVisibility(View.GONE);
                videoItemView.setShowContoller(true);
                fullScreen.setVisibility(View.VISIBLE);
            }
        } else {
            mXRecyclerView.setVisibility(View.VISIBLE);
            fullScreen.setVisibility(View.GONE);
        }
    }

    private void closeVideo() {
        if (videoItemView != null && videoItemView.isPlay()) {
            videoLayout.removeAllViews();
            rootVideoLayout.setVisibility(View.GONE);
            videoItemView.setShowContoller(true);
            videoItemView.stop();
            videoItemView = null;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        closeVideo();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return false;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
