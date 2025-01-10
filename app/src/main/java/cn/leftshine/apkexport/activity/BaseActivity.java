package cn.leftshine.apkexport.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

import cn.leftshine.apkexport.utils.Settings;

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
    }

    public void setLanguage() {
        // reference https://juejin.cn/post/6844904137226878984
        // 获得res资源对象
        Resources resources = getResources();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics metrics = resources.getDisplayMetrics();
        // 获得配置对象
        Configuration config = resources.getConfiguration();
        Locale locale = null;
        String custom_language = Settings.getLanguage();
        switch (custom_language) {
            case "2":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "3":
                locale = Locale.ENGLISH;
                break;
            case "1":
            default:
                locale = Locale.getDefault();
        }
        Log.d(TAG, "setLanguage: locale= "+locale);
        Log.d(TAG, "setLanguage: getAppLocale= "+getAppLocale(this));
        if (locale.equals(getAppLocale(this))) {
            Log.i(TAG, "setLanguage: local not change");
            return;
        }
        Log.i(TAG, "setLanguage to " + locale);
        //区别17版本（其实在17以上版本通过 config.locale设置也是有效的，不知道为什么还要区别）
        //在这里设置需要转换成的语言，也就是选择用哪个values目录下的strings.xml文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        resources.updateConfiguration(config, metrics);
    }
    
    /**
     * 获取应用语言
     */
    public static Locale getAppLocale(Context context){
        Locale local;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            local =context.getResources().getConfiguration().getLocales().get(0);
        } else {
            local =context.getResources().getConfiguration().locale;
        }
        return local;
    }
    

}
