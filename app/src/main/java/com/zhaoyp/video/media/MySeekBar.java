package com.zhaoyp.video.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * @author zhaoyapeng
 * @version create time:16/7/2022:25
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class MySeekBar extends SeekBar {
    private ViewGroup rootLayout;
    Context mContext;

    public MySeekBar(Context context) {
        super(context);
        mContext = context;
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (rootLayout != null) {
                    rootLayout.requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (rootLayout != null) {
                    rootLayout.requestDisallowInterceptTouchEvent(false);
                }
                break;

        }
        return super.onTouchEvent(event);

    }

//    @Override
//    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
//        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
//        if (gainFocus) {
//            if (mContext instanceof BaseTintActivity)
//                ((BaseTintActivity) mContext).isFail = true;
//        } else {
//            if (mContext instanceof BaseTintActivity)
//                ((BaseTintActivity) mContext).isFail = false;
//        }
//    }

    public void setLayout(ViewGroup rootLayout) {
        this.rootLayout = rootLayout;
    }
}