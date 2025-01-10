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
    private static String mDefaultExportPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
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
        // /storage/emulated/0/Android/data/cn.leftshine.apkexport/cache/
        mDefaultExportPath = FileUtils.getDiskCacheDir(mContext);
    }

    public static boolean isAutoCleanCacheDir() {
        //return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_auto_clean_cacheDir), true);
        return true;
    }

    public static void setAutoCleanCacheDir(boolean autoClean) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_auto_clean_cacheDir), autoClean).apply();
    }

    public static boolean isAutoCleanExportDir() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_auto_clean_exportDir), false);
    }

    public static void setAutoCleanExportDir(boolean autoClean) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_auto_clean_exportDir), autoClean).apply();
    }

    public static boolean isExportDerect() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_export_direct), false);
    }

    public static void setExportDerect(boolean exportDirect) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_export_direct), exportDirect).apply();
    }

    public static boolean isShowLocalApk() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_show_local_apk), true);
    }

    public static void setShowLocalApk(boolean showLocalApk) {
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_show_local_apk), showLocalApk).apply();
    }

    public static String getCustomFileNameFormat() {
        return sharedPreferences.getString(mContext.getString(R.string.key_custom_filename_format),"#N-#P-#V");
    }

    public static void setCustomFileNameFormat(String customFileNameFormat) {
        sharedPreferences.edit().putString(mContext.getString(R.string.key_custom_filename_format), customFileNameFormat).apply();
    }
    public static String getCustomExportPath() {
        return sharedPreferences.getString(mContext.getString(R.string.key_custom_export_path), mDefaultExportPath);
    }

    public static void setCustomExportPath(String customFileNameFormat) {
        sharedPreferences.edit().putString(mContext.getString(R.string.key_custom_export_path), customFileNameFormat).apply();
    }

    public static String getLongPressAction() {
        return sharedPreferences.getString(mContext.getString(R.string.key_long_press_action),"103");
    }

    public static String getDarkMode() {
        return sharedPreferences.getString(mContext.getString(R.string.key_dark_mode),"1");
    }

    public static String getLanguage() {
        return sharedPreferences.getString(mContext.getString(R.string.key_language),"1");
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

    public static Boolean isShareWithExport() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_is_share_with_export),false);
    }
    public static void setShareWithExport(Boolean b) {
        //return sharedPreferences.getString(mContext.getString(R.string.key_sort_order),"300");
        sharedPreferences.edit().putBoolean(mContext.getString(R.string.key_is_share_with_export),b).apply();
    }

}
