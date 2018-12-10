package com.ctftek.controlmanager.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ctftek.controlmanager.R;


/**
 * Created by renrui on 2017/7/13.
 */

public class MyButton extends android.support.v7.widget.AppCompatButton {
    private static final String TAG = "MyButton";

    private SoundPool soundPool;//声明一个SoundPool
    private int soundID;//创建某个声音对应的音频ID
    private Context mContext;
    private GradientDrawable gradientDrawable;

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "MyButton: 222");
        this.mContext = context;
        initSound();
        setBackgroundColor(Color.parseColor("#e0e0e0"));
    }

    public MyButton(Context context) {
        super(context);
    }

    private void getGradientDrawable() {
        if (gradientDrawable == null){
            gradientDrawable = new GradientDrawable();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(Color.parseColor("#ffd180"));
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.parseColor("#e0e0e0"));
                playSound();
                break;
        }


        return super.onTouchEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initSound() {
        if (Build.VERSION.SDK_INT >= 21) {
            soundPool = new SoundPool.Builder().build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            Log.i(TAG, "initSound: " + soundPool);
        }

        soundID = soundPool.load(mContext, R.raw.button, 1);
    }

    private void playSound() {
        Log.i(TAG, "playSound: 0000000000");
        soundPool.play(
                soundID,
                0.1f,      //左耳道音量【0~1】
                0.5f,      //右耳道音量【0~1】
                0,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }
}
