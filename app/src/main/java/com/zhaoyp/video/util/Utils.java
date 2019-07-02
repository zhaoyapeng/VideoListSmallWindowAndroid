package com.zhaoyp.video.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;


public class Utils {


    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }



    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }





    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     *
     * @return
     */
    private static int getHasVirtualKey(Context context) {
        int dpi = 0;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * 这是用来 获取raw下文件 String。
     * <p/>
     * 比如json。
     */
    public static String readTextFileFromRawResourceId(Context context, int resourceId) {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(
                resourceId)));

        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    // 将Bitmap存入SD卡
    public static File saveBitmapToSD(Bitmap mBitmap, String imagePath, String name) {
        File f = new File(imagePath, name + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mBitmap != null && !mBitmap.isRecycled() && fOut != null)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            if (fOut != null)
                fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fOut != null)
                fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    // 图片路径
    public static String getImagePath(Context context) {
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = basePath + "/zhaoyp/video/";
        if (!isDirExist(path)) {
            boolean isMakeSucc = makeDirs(path);
            if (!isMakeSucc) {
                return null;
            }
        }
        return path;
    }

    public static boolean isDirExist(String path) {
        File file = new File(path);

        return file.exists() && file.isDirectory();
    }

    public static boolean makeDirs(String path) {
        File file = new File(path);
        return file.mkdirs();
    }
}
