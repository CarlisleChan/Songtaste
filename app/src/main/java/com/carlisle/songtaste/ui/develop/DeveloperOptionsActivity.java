package com.carlisle.songtaste.ui.develop;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SendCallback;
import com.carlisle.songtaste.R;
import com.carlisle.songtaste.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by chengxin on 4/15/15.
 */
public class DeveloperOptionsActivity extends BaseActivity {

    @InjectView(R.id.ed_type)
    EditText channelEdit;
    @InjectView(R.id.btn_send)
    Button sendButton;
    @InjectView(R.id.et_message)
    EditText msgEdit;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_options);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("开发者选项");

    }

    @OnClick(R.id.btn_send)
    public void onSendClick() {
        AVPush push = new AVPush();
        push.setChannel(channelEdit.getText().toString().trim());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "com.songtaste.push.action");
        jsonObject.put("alert", msgEdit.getText().toString().trim());

        push.setData(jsonObject);
        push.setPushToAndroid(true);
        push.sendInBackground(new SendCallback() {

            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "send successfully", Toast.LENGTH_SHORT);
                }   else {
                    Toast.makeText(getApplicationContext(), "send failed", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
