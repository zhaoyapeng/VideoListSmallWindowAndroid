package com.zhaoyp.video;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * @author zhaoyapeng
 * @version create time:2019-07-0209:27
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class MyApplication extends Application {


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

}
