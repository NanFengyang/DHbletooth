package com.nanfeng.linphonelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nanfeng.linphonelibrary.activity.MoreCallActivity;
import com.nanfeng.linphonelibrary.activity.SingleCallActivity;
import com.nanfeng.linphonelibrary.linphone.Utility;

public class LinphoneActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linphone);
        findViewById(R.id.signle_phone).setOnClickListener(this);
        findViewById(R.id.more_phone).setOnClickListener(this);
        TextView username = (TextView) findViewById(R.id.showusernaem);
        String showtext = "用户名：" + Utility.getUsername() + " 密码：" + Utility.getPassword() + " HOST:" + Utility.getPassword();
        username.setText(showtext);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signle_phone) {
            Intent intent = new Intent(this, SingleCallActivity.class);
            intent.putExtra("isCall", true);
            startActivity(intent);

        } else if (i == R.id.more_phone) {
            Intent intent1 = new Intent(this, MoreCallActivity.class);
            startActivity(intent1);

        }

    }
}

