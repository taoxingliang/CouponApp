package com.example.administrator.push1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.push1.activity.CouponActivity;
import com.example.administrator.push1.activity.LoginActivity;
import com.example.administrator.push1.activity.UpLoadActivity;
import com.example.administrator.push1.bean.UserInfo;
import com.example.administrator.push1.util.DbUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        Fresco.initialize(this);
        DbUtil.initDb(this);
        UserInfo info = DbUtil.getLocalUser();
//        if (info != null) {
//            Intent intent =new Intent(MainActivity.this,CouponActivity.class);
//            Bundle bundle=new Bundle();
//            bundle.putSerializable("user", info);
//            intent.putExtras(bundle);
//            startActivity(intent);
//
//        } else {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UpLoadActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
