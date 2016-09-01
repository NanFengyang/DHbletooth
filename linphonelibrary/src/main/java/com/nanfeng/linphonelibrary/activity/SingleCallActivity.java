package com.nanfeng.linphonelibrary.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nanfeng.linphonelibrary.R;
import com.nanfeng.linphonelibrary.linphone.LinphoneManager;
import com.nanfeng.linphonelibrary.linphone.PhoneBean;
import com.nanfeng.linphonelibrary.linphone.PhoneServiceCallBack;
import com.nanfeng.linphonelibrary.linphone.PhoneVoiceUtils;
import com.nanfeng.linphonelibrary.linphone.Utility;
import com.nanfeng.linphonelibrary.utils.AudioManagerUtils;
import com.nanfeng.linphonelibrary.utils.ToastUtils;


public class SingleCallActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username;
    private TextView mic, bange, speark;
    private Boolean isCall = true;//来源是打电话还是接电话，true为打电话，false为接电话
    private String userName = "莫少雪";
    private Boolean isCallConnected = false;
    private AudioManagerUtils mAudioManagerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_call);
        isCall = this.getIntent().getBooleanExtra("isCall", true);
        if (!isCall) {
            userName = this.getIntent().getStringExtra("userName");
        }
        mAudioManagerUtils = new AudioManagerUtils(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAudioManagerUtils.openAudioWay();
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        mic = (TextView) findViewById(R.id.mic);
        bange = (TextView) findViewById(R.id.hangu);
        speark = (TextView) findViewById(R.id.speark);
        mic.setTag(false);
        speark.setTag(false);
        bange.setTag(false);
        mic.setOnClickListener(this);
        bange.setOnClickListener(this);
        speark.setOnClickListener(this);
        if (!isCall) {//如果是接电话，用户名不可编辑
            username.setEnabled(false);
            username.setText(userName);
            bange.setText("挂断");
            isCallConnected = true;
        } else {
            isCallConnected = false;
            username.setEnabled(true);
            bange.setText("拨打");
        }
        LinphoneManager.addCallBack(new PhoneServiceCallBack() {
            @Override
            public void callConnected() {
                bange.setText("挂断");
                isCallConnected = true;
            }

            @Override
            public void callReleased() {
                ToastUtils.showShort("通话结束");
                finish();
            }
        });
    }

    /**
     * 图片设置
     *
     * @param tv
     * @param id
     */
    private void setTextViewPic(TextView tv, int id) {
        Drawable drawable = getResources().getDrawable(id);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void onClick(View view) {
        boolean ispress = (boolean) view.getTag();
        int i = view.getId();
        if (i == R.id.mic) {
            if (ispress) {
                setTextViewPic(mic, R.mipmap.icon_call_mute);
                mic.setTag(false);
            } else {
                setTextViewPic(mic, R.mipmap.icon_call_mute_press);
                mic.setTag(true);
            }
            PhoneVoiceUtils.getInstance().toggleMicro((boolean) mic.getTag());

        } else if (i == R.id.hangu) {
            if (!isCallConnected) {
                PhoneBean bean = new PhoneBean();
                bean.userName = username.getText().toString();
                bean.host = Utility.getHost();
                PhoneVoiceUtils.getInstance().startSingleCallingTo(bean);
                isCallConnected = true;
                bange.setText("挂断");
            } else {
                PhoneVoiceUtils.getInstance().hangUp();
                finish();
            }

        } else if (i == R.id.speark) {
            if (ispress) {
                setTextViewPic(speark, R.mipmap.icon_call_speak);
                speark.setTag(false);
            } else {
                setTextViewPic(speark, R.mipmap.icon_call_speak_press);
                speark.setTag(true);
            }
            PhoneVoiceUtils.getInstance().toggleSpeaker((boolean) speark.getTag());

        }

    }
}
