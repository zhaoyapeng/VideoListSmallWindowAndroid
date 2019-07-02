package com.zhaoyp.video.media;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhaoyp.video.R;
import com.zhaoyp.video.util.Utils;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * @author zhaoyapeng
 * @version create time:16/5/10下午4:46
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class VideoItemView extends RelativeLayout
        implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private Context mContext;
    /**
     * 视频相关
     */
    CustomMediaContoller mediaController;
    private View contollerView;
    IjkVideoView mVideoView;
    private View rview;
    private Handler handler = new Handler();
    private NetworkInfo networkInfo;
    private ShowNetDialog dialog;
    private Uri uri;
    private CustomReceiver registerReceiver;

    private boolean isLooping = false;//是否为 循环播放
    private ConnectivityManager connectMgr;
    private ImageView errorImg;
    private RelativeLayout errorLayout;
    private TextView errorText;

    public VideoItemView(Context context) {
        super(context);
        mContext = context;
        initData();
        initViews();
        initActions();

    }

    public VideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
        initViews();
        initActions();
    }

    public VideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initViews();
        initActions();
    }


    private void initData() {
        connectMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectMgr.getActiveNetworkInfo();

        dialog = new ShowNetDialog(mContext, R.style.my_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    private void initViews() {
        rview = LayoutInflater.from(mContext).inflate(R.layout.view_video_item, this, true);
        contollerView = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        errorImg = (ImageView) findViewById(R.id.img_error);
        errorText = (TextView) findViewById(R.id.text_error);
        mediaController = new CustomMediaContoller(mContext, rview);
        mVideoView.setMediaController(mediaController);
        errorLayout = (RelativeLayout) findViewById(R.id.layout_error);

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (!isLooping) {//非循环播放时 清除屏幕常亮
                    clearScreenOn();
                }else{
                    //循环播放 重置进度为0
                    mVideoView.seekTo(0);
                    mVideoView.start();
                    return;
                }

                contollerView.setVisibility(View.GONE);

                if (!mVideoView.isCanFull()) {
                    animHideVideo();
                }
                if (completionListener != null) {
                    //播放完成时的回调
                    completionListener.completion(mp);
                }


            }
        });
    }

    public void hideSeekBar() {
        if (mediaController != null) {
            mediaController.hideSeekBar();
        }
    }

    public void showSeekBar() {
        if (mediaController != null) {
            mediaController.showHideSeekBar();
        }
    }


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (registerReceiver == null) {
            registerReceiver = new CustomReceiver();
        }
        mContext.registerReceiver(registerReceiver, filter);
    }

    private void unRegisterReceiver() {
        if (registerReceiver != null) {
            mContext.unregisterReceiver(registerReceiver);
            registerReceiver = null;
        }
    }

    public void setShowContoller(boolean isShowContoller) {
        mediaController.setShowContoller(isShowContoller);
    }


    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public boolean isPlayOrBuffer() {
        return mVideoView.isPlaying() || mVideoView.getCurrentStatue()
                == IjkVideoView.STATE_PREPARING || mVideoView.getCurrentStatue() ==
                IjkVideoView.STATE_PREPARED;
    }

    public void pause() {
        if (mediaController == null) {
            return;
        }
        if (mVideoView.isPlaying()) {
            mediaController.pause();
            mediaController.setIsclickToPause(false);
        }
    }


    public void reStart() {
        if (mediaController == null) {
            return;
        }
        mediaController.reStart();
    }

    public void stop() {
        clearScreenOn();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterReceiver();
    }

    private void initActions() {

        dialog.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null) {
                    return;
                }

                errorLayout.setVisibility(GONE);
                if (mVideoView.isPause()) {
                    reStart();
                } else if (!mVideoView.isPlaying()) {
                    mVideoView.setVideoURI(uri);
                    mVideoView.start();
                } else {
                    mVideoView.stopPlayback();
                    mVideoView.setVideoURI(uri);
                    mVideoView.start();
                }
            }
        });

        dialog.setStopListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopListener != null) {
                    stopListener.onClick(v);
                }
            }
        });

        mediaController.setCallBack(new CustomMediaContoller.CallBack() {
            @Override
            public void play() {
                keeyScreenOn();
            }

            @Override
            public void pause() {
                clearScreenOn();
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int i1) {
                Log.e("tag", "onError=" + framework_err);
                if (!isLooping) {
                    clearScreenOn();
                }
                ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null) {
                    return false;
                }

                // TODO 修改了超时时间  此处 错误是视频比较慢或者视频有问题，暂时屏蔽掉
//                else if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
//                    return false;
//                }
                else {
                    errorLayout.setVisibility(VISIBLE);
                }
                return true;
            }
        });

        errorImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start(path);
            }
        });

        mediaController.setVerticalScreenCallBack(new CustomMediaContoller.VerticalScreenCallBack() {
            @Override
            public void mFull() {
                animShowVideo();
            }

            @Override
            public void mHide() {
                animHideVideo();
            }
        });


    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }


    public void release() {
        unRegisterReceiver();
        mVideoView.release(true);
        contollerView.setVisibility(GONE);

    }

    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }


    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }


    /**
     *  获取播放状态
     * */
    public int getCurrentStatus() {
        if (mVideoView == null) {
            return -1;
        }
        return mVideoView.getCurrentStatue();
    }


    /**
     *
     *  网络监听广播
     * */
    class CustomReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null && (isPlay()
                    || getCurrentStatus() == IjkVideoView.STATE_PAUSED
                    || getCurrentStatus() == IjkVideoView.STATE_PREPARING)) {
                pause();
                errorText.setText(mContext.getString(R.string.no_network));
                errorLayout.setVisibility(VISIBLE);
                return;
            }
            if (networkInfo != null && networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                    && (isPlay()
                    || getCurrentStatus() == IjkVideoView.STATE_PAUSED
                    || getCurrentStatus() == IjkVideoView.STATE_PREPARING)) {

                pause();
                showNetDialog();
            }
        }
    }


    private OnClickListener stopListener;

    /**
     *  停止播放回调
     * */
    public void setStopListener(OnClickListener stopListener) {
        this.stopListener = stopListener;
    }

    String path;

    public void start(String path) {
        if (TextUtils.isEmpty(path))
            return;

        path = path.trim();
        this.path = path;

        uri = Uri.parse(path);
        registerReceiver();

        if (isLive(path)) {
            // TODO 当为直播时 隐藏进度条
            hideSeekBar();
        } else {
            showSeekBar();
        }
        contollerView.setVisibility(GONE);
        stop();
        mVideoView.setRender(1);
        if (mediaController != null) {
            mediaController.start();
            mediaController.setPauseImageHide();
        }
        networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            errorText.setText(mContext.getString(R.string.no_network));
            errorLayout.setVisibility(VISIBLE);
            return;
        }
        if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
            showNetDialog();
            return;

        }
        errorLayout.setVisibility(GONE);
        mVideoView.reset();


//        设置视频缓存
//        HttpProxyCacheServer proxy = MyApplication.getProxy(mContext);
//        String proxyUrl = proxy.getProxyUrl(path);
//        mVideoView.setVideoPath(proxyUrl);

        if (mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
        keeyScreenOn();

    }


    //是否为直播流
    public static boolean isLive(String url) {
        if (!TextUtils.isEmpty(url) && (url.startsWith("rtmp://") || url.startsWith("rtsp://") || url.endsWith(".m3u8"))) {
            return true;
        }
        return false;

    }


    /**
     * 保持屏幕常亮（视频播放时）
     */
    private void keeyScreenOn() {
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 移除屏幕常亮（视频停止、暂停）
     */
    private void clearScreenOn() {
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * 展示非wifi dialog
     */
    private void showNetDialog() {
        if (dialog != null && !dialog.isShowing() && !((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 竖屏视频中收缩视频动画
     */
    private void animHideVideo() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(Utils.getDisplayHeight(mContext), Utils.dip2px(mContext, 200));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int values = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = rview.getLayoutParams();
                ViewGroup.LayoutParams rootLayoutParmas = null;

                if (rview.getParent() != null) {
                    rootLayoutParmas = ((ViewGroup) rview.getParent()).getLayoutParams();
                }

                if (layoutParams != null) {
                    layoutParams.height = values;
                    rview.setLayoutParams(layoutParams);

                    if (rootLayoutParmas != null) {
                        rootLayoutParmas.height = values;
                        ((ViewGroup) rview.getParent()).setLayoutParams(layoutParams);
                    }
                }
            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    /**
     * 竖屏视频中展开视频动画
     */
    private void animShowVideo() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(Utils.dip2px(mContext, 200), Utils.getDisplayHeight(mContext));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    int values = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = rview.getLayoutParams();
                    ViewGroup.LayoutParams rootLayoutParmas = null;
                    if (rview.getParent() != null) {
                        rootLayoutParmas = ((ViewGroup) rview.getParent()).getLayoutParams();
                    }

                    if (layoutParams != null) {
                        layoutParams.height = values;
                        rview.setLayoutParams(layoutParams);
                        if (rootLayoutParmas != null) {
                            rootLayoutParmas.height = values;
                            ((ViewGroup) rview.getParent()).setLayoutParams(layoutParams);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }


    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}

