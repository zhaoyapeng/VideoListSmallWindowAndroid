package com.zhaoyp.video.media;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhaoyp.video.R;
import com.zhaoyp.video.util.Utils;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author zhaoyapeng
 * @version create time:16/5/10下午4:46
 * @Email zyp@jusfoun.com
 * @Description ${控制器}
 */
public class CustomMediaContoller implements IMediaController {


    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 3000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private static final int PAUSE_IMAGE_HIDE = 3;

    private static final int SET_VIEW_HIDE_SHORT = 4;

    private static final int MESSAGE_SEEK_NEW_POSITION = 5;
    private static final int MESSAGE_HIDE_CONTOLL = 6;
    private View itemView;
    private View view;
    private boolean isShow;
    private IjkVideoView videoView;

    private MySeekBar seekBar, timeSeek;
    AudioManager audioManager;
    private ProgressBar progressBar;

    private boolean isDragging;


    private boolean isShowContoller;
    private ImageView sound, full, play;
    private TextView time, allTime, seekTxt;
    private Context context;
    private ImageView pauseImage;
    private Bitmap bitmap;
    private GestureDetector detector;
    private RelativeLayout layout;
    private boolean isclickToPause = true;

    private ImageView seekTimeImg;


    private LinearLayout seekTimeLayout;




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_HIDE:
                    isShow = false;
                    itemView.setVisibility(View.GONE);
                    break;
                case SET_VIEW_HIDE_SHORT:
                    isShow = false;
                    itemView.setVisibility(View.GONE);
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PAUSE_IMAGE_HIDE:
                    if (pauseImage != null) {
                        pauseImage.setVisibility(View.GONE);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                    }
                    break;

                case MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_HIDE_CONTOLL:
                    hideSeekLayout();
                    break;
            }
        }
    };

    public CustomMediaContoller(Context context, View view) {
        this.view = view;
        itemView = view.findViewById(R.id.media_contoller);
        layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        this.videoView = (IjkVideoView) view.findViewById(R.id.main_video);


        itemView.setVisibility(View.GONE);
        isShow = false;
        isDragging = false;

        isShowContoller = true;
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            mMaxVolume = audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        initView();
        initAction();
    }

    public void hideSeekBar() {
        if (seekBar != null)
            seekBar.setVisibility(View.GONE);
        if (time != null)
            time.setVisibility(View.GONE);
        if (allTime != null)
            allTime.setVisibility(View.GONE);
    }

    public void showHideSeekBar() {
        if (seekBar != null)
            seekBar.setVisibility(View.VISIBLE);
        if (time != null)
            time.setVisibility(View.VISIBLE);
        if (allTime != null)
            allTime.setVisibility(View.VISIBLE);
    }


    public void initView() {
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        seekBar = (MySeekBar) itemView.findViewById(R.id.seekbar);


        allTime = (TextView) itemView.findViewById(R.id.all_time);
        time = (TextView) itemView.findViewById(R.id.time);
        full = (ImageView) itemView.findViewById(R.id.full);
//        sound = (ImageView) itemView.findViewById(R.id.sound);
        play = (ImageView) itemView.findViewById(R.id.player_btn);
        pauseImage = (ImageView) view.findViewById(R.id.pause_image);
        seekTxt = (TextView) view.findViewById(R.id.seekTxt);
        seekTimeLayout = (LinearLayout) view.findViewById(R.id.layout_seek_time);
        timeSeek = (MySeekBar) view.findViewById(R.id.seek_time);
        seekTimeImg = (ImageView) view.findViewById(R.id.img_jiantou);

    }

    public void start() {
        pauseImage.setVisibility(View.GONE);
        itemView.setVisibility(View.GONE);
        play.setImageResource(R.drawable.video_stop_btn);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void pause() {
        play.setImageResource(R.drawable.video_play_btn);
        videoView.pause();
        handler.removeMessages(SET_VIEW_HIDE);
        if (isShowContoller) {
            itemView.setVisibility(View.VISIBLE);
        } else
            itemView.setVisibility(View.GONE);
        bitmap = videoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            pauseImage.setVisibility(View.VISIBLE);

        }
        if (callBack != null) {
            callBack.pause();
        }
    }


    public void reStart() {
        play.setImageResource(R.drawable.video_stop_btn);
        videoView.start();
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, 300);
        handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);

        if (callBack != null) {
            callBack.play();
        }
    }

    private long duration;

    private void initAction() {

        detector = new GestureDetector(context, new PlayGestureListener());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                time.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.requestFocusFromTouch();
                setProgress();
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                videoView.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                videoView.start();
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect seekRect = new Rect();
                seekBar.getHitRect(seekRect);
                if ((event.getY() >= (seekRect.top - 100)) && (event.getY() <= (seekRect.bottom + 100)) && event.getX() >= seekRect.left
                        && event.getX() <= seekRect.right) {
                    if (itemView.getVisibility() == View.GONE) {
                        itemView.setVisibility(View.VISIBLE);
                    }
                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return seekBar.onTouchEvent(me);

                }
                return false;
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        if (progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        progressBar.setVisibility(View.GONE);
                        pauseImage.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                        statusChange(STATUS_PLAYING);
                        progressBar.setVisibility(View.GONE);
                        pauseImage.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        pauseImage.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    setIsclickToPause(true);
                    pause();
                } else {
                    reStart();
                }
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isCanFull()) {
                    if (Utils.getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        full.setImageResource(R.drawable.expand_screen_icon);

                    } else {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        full.setImageResource(R.drawable.full_screen_icon);
                    }
                } else {
                    if (full.getTag() == null || !(boolean) full.getTag()) {
                        full.setTag(true);
                        verticalScreenCallBack.mFull();
                    } else {
                        full.setTag(false);
                        verticalScreenCallBack.mHide();
                    }
                }
            }

        });


        allTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isCanFull()) {
                    if (Utils.getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        full.setImageResource(R.drawable.expand_screen_icon);
                    } else {

                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        full.setImageResource(R.drawable.full_screen_icon);
                    }
                } else {
                    if (full.getTag() == null || !(boolean) full.getTag()) {
                        full.setTag(true);
                        verticalScreenCallBack.mFull();
                    } else {
                        full.setTag(false);
                        verticalScreenCallBack.mHide();
                    }
                }

            }
        });


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (detector.onTouchEvent(event)) {
                    return true;
                }

                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });


    }

    public void setIsclickToPause(boolean isclickToPause) {
        this.isclickToPause = isclickToPause;
    }

    public boolean isIsclickToPause() {
        return isclickToPause;
    }

    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(SET_VIEW_HIDE);
        itemView.setVisibility(View.GONE);
    }


    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(SET_VIEW_HIDE);

            itemView.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        if (!isShowContoller)
            return;
        Rect rect = new Rect();
        full.getHitRect(rect);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
        isShow = true;
        itemView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOnce(View view) {
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);

    }


    private long setProgress() {
        if (isDragging) {
            return 0;
        }
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        this.duration = duration;
        if (duration == 0) {
            allTime.setText("--:--");
        } else if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime(position);
        time.setText(string);
        return position;
    }


    public void setPauseImageHide() {
        pauseImage.setVisibility(View.GONE);
        handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);

    }



    private int volume = -1;
    private float brightness = -1;
    private long newPosition = -1;
    private int mMaxVolume;


    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
//        hide();

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }


    public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean seek;

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;


            //横屏下拦截事件
            if ((Utils.getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || (!videoView.isCanFull() && Utils.getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))) {
                // 竖屏状态下，如果视频分辨率 宽<高 则拦截事件，可进行快进操作
                //横屏下拦截事件
                return true;
            } else {
                return super.onDown(e);
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = e1.getX() - e2.getX();
            float y = e1.getY() - e2.getY();
            if (firstTouch) {
                seek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = e1.getX() < view.getMeasuredWidth() * 0.5;
                firstTouch = false;
            }
            if (seek) {
                onProgressSlide(-x / view.getWidth(), e1.getX() / view.getWidth());
            } else {
                float percent = y / view.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }


    /**
     * 手势结束
     */
    private void endGesture() {


        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(MESSAGE_HIDE_CONTOLL);
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CONTOLL, 500);

    }


    private long oldTimePosition = 0;//滑动快进快推时 上一次的时间，用于比较

    /**
     * 滑动跳转
     *
     * @param percent 移动比例
     * @param downPer 按下比例
     */
    private void onProgressSlide(float percent, float downPer) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            seekTimeLayout.setAlpha(1f);
            seekTimeLayout.setVisibility(View.VISIBLE);

            String current = generateTime(newPosition);

            if (newPosition > oldTimePosition) {
                seekTimeImg.setImageResource(R.drawable.img_video_jin);
            } else {
                seekTimeImg.setImageResource(R.drawable.img_video_tui);
            }

            oldTimePosition = newPosition;
            seekTxt.setText(current + "/" + allTime.getText());
            if (duration != 0) {
                long pos = 100L * newPosition / duration;
                timeSeek.setProgress((int) pos);
            }
        }
    }


    private CallBack callBack;

    public interface CallBack {
        void play();

        void pause();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void setFullOpen() {
        full.setImageResource(R.drawable.expand_screen_icon);
    }

    public void setFullClose() {
        full.setImageResource(R.drawable.full_screen_icon);
    }



    private void hideSeekLayout() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(seekTimeLayout, "alpha", 1f, 0f);
        objectAnimator.setDuration(300);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                seekTimeLayout.setVisibility(View.GONE);
            }
        });
        objectAnimator.start();
    }


    public interface VerticalScreenCallBack {
        void mFull();

        void mHide();

    }

    public VerticalScreenCallBack verticalScreenCallBack;

    // 竖屏视频（高度大于宽度的视频）回调
    public void setVerticalScreenCallBack(VerticalScreenCallBack callBack) {
        this.verticalScreenCallBack = callBack;
    }
}
