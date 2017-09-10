package com.example.administrator.push1.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;

import api.PushRequest;
import api.RequestCallback;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/5.
 */

public class LoginActivity extends Activity {

    Button mRefisterButton = null;
    Button mLoginButton = null;
    EditText userEdit = null;
    EditText userPassword = null;
    Handler mHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Uri uri = Uri.parse("http://ww3.sinaimg.cn/large/610dc034jw1f6m4aj83g9j20zk1hcww3.jpg");
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.logo);
        draweeView.setImageURI(uri);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String str = (String) msg.obj;
                        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Intent intent = new Intent(LoginActivity.this, CouponActivity.class);

                        Bundle bundle=new Bundle();
                        bundle.putSerializable("user", (UserInfo) msg.obj);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }

            }
        };
        mRefisterButton = findViewById(R.id.login_btn_register);
        mLoginButton = findViewById(R.id.login_btn_login);

        userEdit = findViewById(R.id.login_edit_account);
        userPassword = findViewById(R.id.login_edit_pwd);

        mRefisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RegularUtil.isPhoneNumber(userEdit.getText().toString())
                        && RegularUtil.isPasswordValid(userPassword.getText().toString())) {
                    UserInfo info = new UserInfo();
                    info.setTag("login");
                    info.setAccount(userEdit.getText().toString());
                    info.setPassword(userPassword.getText().toString());
                    PushRequest request = PushRequest.newBuilder(Constant.BaseUrl + "/login")
                            .setMethod("get")
                            .setContentType("json")
                            .setBody(RequestBody.create(Constant.JSON, GsonUtil.GsonString(info)))
                            .build();
                    request.excuteAsyncRequest(new RequestCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            Message m = Message.obtain();
                            m.what = 1;
                            try {
                                String result = response.body().string();
                                UserInfo userInfo = null;
                                try {
                                    userInfo = (UserInfo) GsonUtil.GsonToBean(result, UserInfo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (userInfo != null) {
                                    if (DbUtil.queryUser(userInfo) != null) {
                                        DbUtil.updateUser(userInfo);
                                    } else {
                                        DbUtil.insertUser(userInfo);
                                    }
                                    m.what = 3;
                                    m.obj = userInfo;
                                    mHandler.sendMessage(m);
                                    return;
                                }
                                m.obj = result;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mHandler.sendMessage(m);
                        }

                        @Override
                        public void onFailure(int statuscode) {
                            Message m = Message.obtain();
                            m.what = 2;
                            mHandler.sendMessage(m);
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "请确保输入手机号和密码合法", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
