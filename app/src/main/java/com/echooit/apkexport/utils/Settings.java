package com.echooit.apkexport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.echooit.apkexport.R;

/**
 * Created by Administrator on 2018/1/25.
 */

public class Settings {
    //public String SETTING_PREF_NAME="pref_settings";
    private static Context mContext;
    private static SharedPreferences sharedPreferences;
    public static void init(Context context) {
        mContext = context;
        //context.getPreferenceManager().getSharedPreferencesName();
        //sharedPreferences = mContext.getSharedPreferences(Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static boolean isAutoClean() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_auto_clean), false);
    }

    public static void setAutoClean(boolean autoClean) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_auto_clean), autoClean).apply();
    }

    public static String getCustomFileNameFormat() {
        return sharedPreferences.getString(mContext.getString(R.string.key_custom_filename_format),"#N-#P-#V");
    }

    public static void setCustomFileNameFormat(String customFileNameFormat) {
        sharedPreferences.edit().putString(mContext.getString(R.string.key_custom_filename_format), customFileNameFormat).apply();
    }

    public static String getLongPressAction() {
        return sharedPreferences.getString(mContext.getString(R.string.key_long_press_action),"103");
    }
}
