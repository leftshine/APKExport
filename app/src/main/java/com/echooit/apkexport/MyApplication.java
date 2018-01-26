package com.echooit.apkexport;

import android.app.Application;

import com.echooit.apkexport.utils.Settings;

/**
 * Created by Administrator on 2018/1/25.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Settings.init(this);
    }
}
