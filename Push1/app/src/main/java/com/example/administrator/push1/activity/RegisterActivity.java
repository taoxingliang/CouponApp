package com.example.administrator.push1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.push1.R;
import com.example.administrator.push1.bean.UserInfo;
import com.example.administrator.push1.util.Constant;
import com.example.administrator.push1.util.DbUtil;
import com.example.administrator.push1.util.GsonUtil;
import com.example.administrator.push1.util.RegularUtil;

import java.io.IOException;

import api.PushRequest;
import api.RequestCallback;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RegisterActivity extends Activity {

    EventHandler mEventHandler = null;
    Button mRegisterButton = null;
    Button mSureRegisterButton = null;
    EditText mPassWordEdit1 = null;
    EditText mPassWordEdit2 = null;
    EditText mPhoneNumEdit = null;
    EditText mMmsCodeEdit = null;
    Handler mHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mRegisterButton = findViewById(R.id.mms_code_button);
        mPhoneNumEdit = findViewById(R.id.phone_number_edit);
        mSureRegisterButton = findViewById(R.id.register_btn_sure);
        mMmsCodeEdit = findViewById(R.id.mms_code_edit);
        mPassWordEdit1 = findViewById(R.id.resetpwd_edit_pwd_old);
        mRegisterButton.setBackgroundColor(Color.parseColor("#614DB3"));

        mPassWordEdit2 = findViewById(R.id.resetpwd_edit_pwd_new);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if(msg.arg2 == SMSSDK.RESULT_COMPLETE){//发送成功的情况
                            if(msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//验证成功通过
                                Toast.makeText(RegisterActivity.this, "验证成功通过", Toast.LENGTH_SHORT).show();
                                UserInfo userInfo = new UserInfo();
                                userInfo.setAccount(mPhoneNumEdit.getText().toString());
                                userInfo.setPassword(mPassWordEdit1.getText().toString());
                                userInfo.setTag("register");
                                final PushRequest request = PushRequest.newBuilder(Constant.BaseUrl + "/register")
                                        .setMethod("get")
                                        .setContentType("json")
                                        .setBody(RequestBody.create(Constant.JSON, GsonUtil.GsonString(userInfo)))
                                        .build();
                                request.excuteAsyncRequest(new RequestCallback() {
                                    @Override
                                    public void onSuccess(Response response) {
                                        String result = null;
                                        try {
                                            result = response.body().string();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (result == null || result.equals("register fail")) {
                                            Message m = Message.obtain();
                                            m.what = 3;
                                            mHandler.sendMessage(m);
                                        } else {

                                            UserInfo info = (UserInfo) GsonUtil.GsonToBean(result, UserInfo.class);
                                            DbUtil.insertUser(info);
                                            Message m = Message.obtain();
                                            m.what = 2;
                                            m.obj = info;
                                            mHandler.sendMessage(m);
                                        }

                                    }

                                    @Override
                                    public void onFailure(int statuscode) {
                                        Message m = Message.obtain();
                                        m.what = 3;
                                        mHandler.sendMessage(m);
                                    }
                                });
                            }else if(msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE){//验证码已经从服务器发出
                                Toast.makeText(RegisterActivity.this, "验证码已发出,请注意查收", Toast.LENGTH_SHORT).show();
                                mRegisterButton.setText("获取成功");
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            mRegisterButton.setBackgroundColor(Color.parseColor("#614DB3"));
                            mRegisterButton.setText("点击获取");
                        }
                        break;
                    case 2:
                        Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, CouponActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("user", (UserInfo)msg.obj);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 3:
                        Toast.makeText(RegisterActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(RegisterActivity.this, "此号码已注册，请直接登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        mRegisterButton.setBackgroundColor(Color.parseColor("#ff888888"));
                        mRegisterButton.setText("获取中");
                        SMSSDK.getVerificationCode("86",mPhoneNumEdit.getText().toString());
                        break;
                    case 6:

                        Toast.makeText(RegisterActivity.this, "手机连接服务器异常", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Toast.makeText(RegisterActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mEventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message m = Message.obtain();
                m.what = 1;
                m.arg1 = event;//event
                m.arg2 = result;//result
                mHandler.sendMessage(m);
            }
        };
        SMSSDK.registerEventHandler(mEventHandler);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordOk(mPassWordEdit1, mPassWordEdit2)) {
                    Toast.makeText(RegisterActivity.this, "请确保密码输入合法", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (RegularUtil.isPhoneNumber(mPhoneNumEdit.getText().toString())) {
                    UserInfo info = new UserInfo();
                    info.setAccount(mPhoneNumEdit.getText().toString());
                    info.setPassword(mPassWordEdit1.getText().toString());
                    info.setTag("test exsit");
                    final PushRequest request = PushRequest.newBuilder(Constant.BaseUrl + "/register")
                            .setMethod("get")
                            .setContentType("json")
                            .setBody(RequestBody.create(Constant.JSON, GsonUtil.GsonString(info)))
                            .build();
                    request.excuteAsyncRequest(new RequestCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            String result = null;
                            try {
                                result = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (result.equals("user exsit")) {
                                Message m = Message.obtain();
                                m.what = 4;
                                mHandler.sendMessage(m);
                            } else if (result.equals("user not exsit")) {
                                Message m = Message.obtain();
                                m.what = 5;
                                mHandler.sendMessage(m);
                            } else if (request != null) {
                                Message m = Message.obtain();
                                m.what = 7;
                                m.obj = result;
                                mHandler.sendMessage(m);
                            }
                        }

                        @Override
                        public void onFailure(int statuscode) {
                            Message m = Message.obtain();
                            m.what = 6;
                            mHandler.sendMessage(m);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "请确保输入手机号合法", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSureRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RegularUtil.isPhoneNumber(mPhoneNumEdit.getText().toString()) && isPasswordOk(mPassWordEdit1, mPassWordEdit2)) {
                    SMSSDK.submitVerificationCode("86", mPhoneNumEdit.getText().toString(), mMmsCodeEdit.getText().toString());
                    Toast.makeText(RegisterActivity.this, "注册中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "手机号或密码不合法", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPasswordOk(EditText edit1, EditText edit2) {
        if (edit1 == null || edit2 == null) {
            return false;
        }
        if (RegularUtil.isPasswordValid(edit1.getText().toString())
                && RegularUtil.isPasswordValid(edit2.getText().toString())
                && edit1.getText().toString().equals(edit2.getText().toString())) {

            return true;
        }
        return false;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
    }
}
