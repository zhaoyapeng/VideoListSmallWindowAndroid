package com.zhaoyp.video.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhaoyp.video.R;
import com.zhaoyp.video.util.Utils;
import com.zhaoyp.video.media.VideoItemView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author zhaoyapeng
 * @version create time:2019-07-0214:29
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class DetailsActivity extends AppCompatActivity {
    protected VideoItemView topVideo;
    protected WebView webView;
    protected ImageView imageDefult;
    protected ImageView imgPlay;
    protected RelativeLayout normalVideoLayout;
    protected RelativeLayout fullVideoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_details);
        initView();


        topVideo.setStopListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPlay.setVisibility(View.VISIBLE);
                imageDefult.setVisibility(View.VISIBLE);
            }
        });

        imageDefult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topVideo.start("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4");
                imageDefult.setVisibility(View.GONE);
                imgPlay.setVisibility(View.GONE);
            }
        });

        topVideo.setCompletionListener(new VideoItemView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                imgPlay.setVisibility(View.VISIBLE);
                imageDefult.setVisibility(View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView.loadUrl("http://www.baidu.com");
    }

    private void initView() {
        topVideo = (VideoItemView) findViewById(R.id.videoItemView);
        webView = (WebView) findViewById(R.id.webView);
        imageDefult = (ImageView) findViewById(R.id.image_defult);
        imgPlay = (ImageView) findViewById(R.id.img_play);
        normalVideoLayout = (RelativeLayout) findViewById(R.id.layout_video_normal);
        fullVideoLayout = (RelativeLayout) findViewById(R.id.layout_video_full);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            normalVideoLayout.removeAllViews();
            fullVideoLayout.removeAllViews();
            fullVideoLayout.setVisibility(View.GONE);
            normalVideoLayout.addView(topVideo);

        } else {
            normalVideoLayout.removeAllViews();
            fullVideoLayout.addView(topVideo);
            fullVideoLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        topVideo.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Utils.getScreenOrientation(this) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    private void test1(){

    }

    private void test2(){

    }

    private void test(){
        // 测试回滚
    }



}
