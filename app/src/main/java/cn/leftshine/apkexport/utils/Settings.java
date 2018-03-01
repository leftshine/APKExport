package cn.leftshine.apkexport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

import cn.leftshine.apkexport.R;

/**
 * Created by Administrator on 2018/1/25.
 */

public class Settings {
    //public String SETTING_PREF_NAME="pref_settings";
    private static String DEFAULT_EXPORT_PATH = Environment.getExternalStorageDirectory()+ File.separator+"APKExport";
    private static Context mContext;
    private static SharedPreferences sharedPreferences;
    private static boolean isNeedLoad = false;

    public static boolean isIsNeedLoad() {
        return isNeedLoad;
    }

    public static void setIsNeedLoad(boolean isNeedLoad) {
        Settings.isNeedLoad = isNeedLoad;
    }


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

    public static boolean isShowLocalApk() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_show_local_apk), true);
    }

    public static void setShowLocalApk(boolean autoClean) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_show_local_apk), autoClean).apply();
    }

    public static String getCustomFileNameFormat() {
        return sharedPreferences.getString(mContext.getString(R.string.key_custom_filename_format),"#N-#P-#V");
    }

    public static void setCustomFileNameFormat(String customFileNameFormat) {
        sharedPreferences.edit().putString(mContext.getString(R.string.key_custom_filename_format), customFileNameFormat).apply();
    }
    public static String getCustomExportPath() {
        return sharedPreferences.getString(mContext.getString(R.string.key_custom_export_path),DEFAULT_EXPORT_PATH);
    }

    public static void setCustomExportPath(String customFileNameFormat) {
        sharedPreferences.edit().putString(mContext.getString(R.string.key_custom_export_path), customFileNameFormat).apply();
    }

    public static String getLongPressAction() {
        return sharedPreferences.getString(mContext.getString(R.string.key_long_press_action),"103");
    }

    public static String getSortType() {
        return sharedPreferences.getString(mContext.getString(R.string.key_sort_type),"200");
    }
    public static void setSortType(String sortType) {
        //return sharedPreferences.getString(mContext.getString(R.string.key_sort_type),"200");
        sharedPreferences.edit().putString(mContext.getString(R.string.key_sort_type),sortType).apply();
    }
    public static String getSortOrder() {
        return sharedPreferences.getString(mContext.getString(R.string.key_sort_order),"300");
    }
    public static void setSortOrder(String sortOrder) {
        //return sharedPreferences.getString(mContext.getString(R.string.key_sort_order),"300");
        sharedPreferences.edit().putString(mContext.getString(R.string.key_sort_order),sortOrder).apply();
    }

}
