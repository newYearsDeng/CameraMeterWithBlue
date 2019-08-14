package com.northmeter.camerameterwithblue.base;

import android.app.Application;
import android.content.Context;

import com.jmesh.blebase.base.BleManager;

/**
 * Created by dyd on 2018/11/28.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        new InitOkGo().initOkGo(this);
        BleManager.getInstance().init(this);
    }
    public static Context getContext() {
        return context;
    }
}
