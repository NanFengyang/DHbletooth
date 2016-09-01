package com.nanfeng.linphonelibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

/**
 * 蓝牙通话开启通道 和关闭蓝牙通道。
 * Created by yangyoutao on 2016/9/1.
 */
public class AudioManagerUtils {

    private Context mContext;
    private AudioManager mAudioManager;

    public AudioManagerUtils(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);//不能改，千万不能改
    }

    /**
     * 关闭蓝牙SCO通道
     */
    public void stopAudio() {
        if (null != mAudioManager) {
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }

    /**
     * 启动蓝牙通道
     */
    public void openAudioWay() {
        mAudioManager.setSpeakerphoneOn(true);
        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.d("bluetooh_myapp", "系统不支持蓝牙录音");
            return;
        }
        mAudioManager.stopBluetoothSco();
        //蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。o
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    context.unregisterReceiver(this);  //别遗漏
                } else {//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }

}
