package com.example.ballrebound;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

public class MyHandler extends Handler {
    private Activity activity;
    private ImageView imageView;

    public MyHandler(Activity activity,ImageView outImageView) {
        this.activity = activity;
        this.imageView = outImageView;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
//        imageView = (ImageView) activity.findViewById(R.id.activity_main_image);
        if (msg.what == MainActivity.MOVE_IMAGE) {
            android.widget.RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            // 利用Margin改變小球的位置
            lp.setMargins(msg.getData().getInt("moveX"),
                    msg.getData().getInt("moveY"), 0, 0);
            imageView.setLayoutParams(lp);
        }
    }
}