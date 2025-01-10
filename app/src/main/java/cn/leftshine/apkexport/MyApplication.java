package cn.leftshine.apkexport;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import cn.leftshine.apkexport.utils.Settings;

/**
 * Created by Administrator on 2018/1/25.
 */

public class MyApplication extends Application {
        public void onCreate() {
        super.onCreate();
        Settings.init(this);
        String darkMode = Settings.getDarkMode();
        setDarkMode(darkMode);
    }

    public static void setDarkMode(String mode){
        switch (mode){
            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_TIME);
                break;
            case "3":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "4":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "1":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
