package com.northmeter.camerameterwithblue.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.northmeter.camerameterwithblue.MainActivity;
import com.northmeter.camerameterwithblue.R;
import com.northmeter.camerameterwithblue.base.BaseActivity;


/**
 *欢迎页
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_we;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, LocationSet_NBDevice.class));
                finish();
            }
        }, 2000);
        
    }
    

}
