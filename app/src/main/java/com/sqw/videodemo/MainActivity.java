package com.sqw.videodemo;



import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sqw.videodemo.danmuUtils.DanmakuItem;
import com.sqw.videodemo.danmuUtils.DanmakuView;
import com.sqw.videodemo.danmuUtils.IDanmakuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DanmakuView mDanmakuView;
    private Button switcherBtn;
    private Button sendBtn;
    private EditText textEditText;
    private CustomVideoView mVideoView;
    private int screen_width;
    private int screen_height;
    private ImageView screen_image;
    private TextView totally_time_tv;
    private TextView current_time_tv;
    private SeekBar play_seek;
    private LinearLayout controllerLayout;
    private RelativeLayout videoLayout;
    private int currentPosition;
    private AudioManager audioManager;
    private boolean screen_flag = true;
    private SeekBar  volume_seek;//播放进度和音量控制进度
    private ImageView play_controller_image, volume_Image;
    String url="http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4";
    private boolean fullscreen =false;
    public static final int UPDATA_VIDEO_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化音量控制器
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initViewOnClick();
        mVideoView.setVideoURI(Uri.parse("http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4"));
        play_controller_image.setImageResource(R.mipmap.icon_play);
    }

    private void initView(){

        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
//        switcherBtn = (Button) findViewById(R.id.switcher);
        sendBtn = (Button) findViewById(R.id.send);
        textEditText = (EditText) findViewById(R.id.text);
        mVideoView = (CustomVideoView) findViewById(R.id.videoView);
        videoLayout = (RelativeLayout) findViewById(R.id.act_testmovie_videolayout);
        controllerLayout = (LinearLayout) findViewById(R.id.main_controller_liner);
        play_seek = (SeekBar) findViewById(R.id.main_play_seek);
        volume_seek = (SeekBar) findViewById(R.id.main_volume_seek);
        current_time_tv = (TextView) findViewById(R.id.main_current_time);
        totally_time_tv = (TextView) findViewById(R.id.main_totally_time);
        play_controller_image = (ImageView) findViewById(R.id.play_pasue_image);
        screen_image = (ImageView) findViewById(R.id.main_screen_image);
        List<IDanmakuItem> list = initItems();
        Collections.shuffle(list);
        mDanmakuView.addItem(list, true);
//        switcherBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);
        mVideoView.setMediaController(mc);
        mVideoView.requestFocus();

    }

    private List<IDanmakuItem> initItems() {
        List<IDanmakuItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            IDanmakuItem item = new DanmakuItem(this, i + " : 我是文字弹幕", mDanmakuView.getWidth());
            list.add(item);
        }

        String msg = " : 我是图片文字混合弹幕   ";
        for (int i = 0; i < 100; i++) {
            ImageSpan imageSpan = new ImageSpan(this, R.mipmap.ic_launcher);
            SpannableString spannableString = new SpannableString(i + msg);
            spannableString.setSpan(imageSpan, spannableString.length() - 2, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            IDanmakuItem item = new DanmakuItem(this, spannableString, mDanmakuView.getWidth(), 0, 0, 0, 1.5f);
            list.add(item);
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.seekTo(currentPosition);
        }
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
        currentPosition = mVideoView.getCurrentPosition();
        handler.removeMessages(UPDATA_VIDEO_NUM);
    }

    //清空
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
    }

    protected void initData() {
        //获取设置音量的最大值
        int volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume_seek.setMax(volumeMax);
        //获取设置当前音量
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seek.setProgress(currentVolume);
    }



//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // 判断当前屏幕的横竖屏状态
//        int screenOritentation = getResources().getConfiguration().orientation;
//
//        if (screenOritentation == Configuration.ORIENTATION_LANDSCAPE) {
//            //横屏时处理
//            setVideoScreenSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            volume_seek.setVisibility(View.VISIBLE);
//            volume_Image.setVisibility(View.VISIBLE);
//            screen_flag = false;
//            //清除全屏标记，重新添加
//            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
//            getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
//        } else {
//            //竖屏时处理
//            setVideoScreenSize(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(MainActivity2.this, 240));
//            screen_flag = true;
//            volume_seek.setVisibility(View.GONE);
//            volume_Image.setVisibility(View.GONE);
//            //清除全屏标记，重新添加
//            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
//            getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
//        }
//    }

    /**
     * 通过handler对播放进度和时间进行更新
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATA_VIDEO_NUM) {
                //获取视频播放的当前时间
                int currentTime = mVideoView.getCurrentPosition();
                //获取视频的总时间
                int totally = mVideoView.getDuration();
                //格式化显示时间
                updataTimeFormat(totally_time_tv, totally);
                updataTimeFormat(current_time_tv, currentTime);
                //设置播放进度
                play_seek.setMax(totally);
                play_seek.setProgress(currentTime);
                //自己通知自己更新
                handler.sendEmptyMessageDelayed(UPDATA_VIDEO_NUM, 500);//500毫秒刷新
            }
        }
    };

    /**
     * 设置横竖屏时的视频大小
     *
     * @param width
     * @param height
     */
    private void setVideoScreenSize(int width, int height) {
        //获取视频控件的布局参数
        ViewGroup.LayoutParams videoViewLayoutParams = mVideoView.getLayoutParams();
        //设置视频范围
        videoViewLayoutParams.width = width;
        videoViewLayoutParams.height = height;
        mVideoView.setLayoutParams(videoViewLayoutParams);
        //设置视频和控制组件的layout
        ViewGroup.LayoutParams videoLayoutLayoutParams = videoLayout.getLayoutParams();
        videoLayoutLayoutParams.width = width;
        videoLayoutLayoutParams.height = height;
        videoLayout.setLayoutParams(videoLayoutLayoutParams);
    }

    /**
     * 时间格式化
     *
     * @param textView    时间控件
     * @param millisecond 总时间 毫秒
     */
    private void updataTimeFormat(TextView textView, int millisecond) {
        //将毫秒转换为秒
        int second = millisecond / 1000;
        //计算小时
        int hh = second / 3600;
        //计算分钟
        int mm = second % 3600 / 60;
        //计算秒
        int ss = second % 60;
        //判断时间单位的位数
        String str = null;
        if (hh != 0) {//表示时间单位为三位
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        //将时间赋值给控件
        textView.setText(str);
    }

    /**
     * 按钮点击事件
     */
    private void initViewOnClick() {
        //播放按钮事件
        play_controller_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断播放按钮的状态
                if (mVideoView.isPlaying()) {
                    play_controller_image.setImageResource(R.mipmap.icon_play);
                    //视频暂停
                    mVideoView.pause();
                    //当视频处于暂停状态，停止handler的刷新
                    handler.removeMessages(UPDATA_VIDEO_NUM);
                } else {
                    play_controller_image.setImageResource(R.mipmap.icon_pause);
                    mVideoView.start();
                    //当视频播放时，通知刷新
                    handler.sendEmptyMessage(UPDATA_VIDEO_NUM);
                }
            }
        });
        //播放进度条事件
        play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前的播放时间
                updataTimeFormat(current_time_tv, progress);
                if (mVideoView.getDuration() == progress) {
                    play_controller_image.setImageResource(R.mipmap.icon_play);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //拖动视频进度时，停止刷新
                handler.removeMessages(UPDATA_VIDEO_NUM);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动后，获取总进度
                int totall = seekBar.getProgress();
                //设置VideoView的播放进度
                mVideoView.seekTo(totall);
                //重新handler刷新
                handler.sendEmptyMessage(UPDATA_VIDEO_NUM);

            }
        });
        //音量控制条事件
        volume_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置音量变动后系统的值
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.switcher: 设置弹幕隐藏 显示的  需要自己去布局加控件
//                if (mDanmakuView.isPaused()) {
//                    switcherBtn.setText(R.string.hide);
//                    mDanmakuView.show();
//                } else {
//                    switcherBtn.setText(R.string.show);
//                    mDanmakuView.hide();
//                }
//                break;
            case R.id.send:
                textEditText.setVisibility(View.VISIBLE);
                String input = textEditText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(MainActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
                } else {
                    IDanmakuItem item = new DanmakuItem(this, new SpannableString(input), mDanmakuView.getWidth(),0,R.color.my_item_color,0,1);
//                    IDanmakuItem item = new DanmakuItem(this, input, mDanmakuView.getWidth());
//                    item.setTextColor(getResources().getColor(R.color.my_item_color));
//                    item.setTextSize(14);
//                    item.setTextColor(textColor);
                    mDanmakuView.addItemToHead(item);
                    textEditText.setVisibility(View.INVISIBLE);
                }
                textEditText.setText("");
                break;
               case  R.id.main_screen_image:
            if(!fullscreen){//设置RelativeLayout的全e68a84e8a2ad3231313335323631343130323136353331333337373566屏模式
                RelativeLayout.LayoutParams layoutParams=
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mVideoView.setLayoutParams(layoutParams);
                fullscreen = true;//改变全屏/窗口的标记
            }else{//设置RelativeLayout的窗口模式
                RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(640,420);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                mVideoView.setLayoutParams(lp);
                fullscreen = false;//改变全屏/窗口的标记
            }
                break;
        }
    }


    /**
     * 将dp转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
