package com.zhaoyp.video.media;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhaoyp.video.R;
import com.zhaoyp.video.util.Utils;


/**
 * @author zhaoyapeng
 * @version create time:16/5/10下午4:46
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class ShowNetDialog extends Dialog {
    private Context context;
    private TextView stop,start;
    public ShowNetDialog(Context context) {
        super(context);
        initView(context);
        initAction();
    }

    public ShowNetDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
        initAction();
    }

    protected ShowNetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
        initAction();
    }

    private void initView(Context context){
        this.context=context;
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (Utils.getDisplayWidth(context) * 0.7);
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        setContentView(R.layout.dialog_show_net);

        start= (TextView) findViewById(R.id.start);
        stop= (TextView) findViewById(R.id.stop);
    }

    private void initAction(){

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.onClick(v);
                dismiss();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopListener!=null)
                    stopListener.onClick(v);
                dismiss();
            }
        });

    }

    private View.OnClickListener listener;

    private View.OnClickListener stopListener;

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setStopListener(View.OnClickListener stopListener) {
        this.stopListener = stopListener;
    }
}
