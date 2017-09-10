package com.example.administrator.push1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.push1.R;
import com.example.administrator.push1.bean.UserInfo;
import com.example.administrator.push1.util.DbUtil;


/**
 * Created by Administrator on 2017/7/9.
 */

public class CouponActivity extends Activity {

    private Button test_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon);
        Bundle bundle = this.getIntent().getExtras();
        final UserInfo info = (UserInfo) bundle.getSerializable("user");
        test_button = findViewById(R.id.test_button);
        test_button.setText("注销：" + info.getAccount());
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbUtil.deleteUser(info);
                Intent intent = new Intent(CouponActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
