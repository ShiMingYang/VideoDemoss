package com.sqw.videodemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {



    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽高
        int defaultWidth = getDefaultSize(0, widthMeasureSpec);
        int defaultHeight = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(defaultWidth, defaultHeight);
    }
}
