package cn.leftshine.apkexport;

import android.app.Application;

import cn.leftshine.apkexport.utils.Settings;

/**
 * Created by Administrator on 2018/1/25.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Settings.init(this);
    }
}
