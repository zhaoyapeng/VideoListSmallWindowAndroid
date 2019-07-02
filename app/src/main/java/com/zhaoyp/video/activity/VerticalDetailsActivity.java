package com.zhaoyp.video.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhaoyp.video.R;
import com.zhaoyp.video.media.VideoItemView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author zhaoyapeng
 * @version create time:2019-07-0214:29
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class VerticalDetailsActivity extends AppCompatActivity {
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


        topVideo.setLooping(true);
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
                topVideo.start("http://mp42.china.com.cn/video_tide/video/2019/6/25/20196251561456898291_431_2.mp4");
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
    protected void onDestroy() {
        super.onDestroy();
        topVideo.release();
    }


}
