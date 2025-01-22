package cn.leftshine.apkexport.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermisionUtils {
    private static final String TAG = "PermisionUtils";

    public static final int REQUEST_CODE_BASE = 1000;
    public static final int REQUEST_EXTERNAL_STORAGE = REQUEST_CODE_BASE + 1;
    public static final int REQUEST_EXTERNAL_STORAGE_SHOWLOCALAPK = REQUEST_CODE_BASE + 2;
    public static final int REQUEST_EXTERNAL_STORAGE_AUTOCLEAN = REQUEST_CODE_BASE + 3;
    public static final int REQUEST_EXTERNAL_STORAGE_CLEAN_EXPORT_DIR = REQUEST_CODE_BASE + 4;
    public static final int REQUEST_CODE_GET_INSTALLED_APPS = REQUEST_CODE_BASE + 5;
    public static final int REQUEST_CODE_GET_INSTALLED_APPS_CURRENT = REQUEST_CODE_BASE + 6;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }else{
            return true;
        }
    }
    public static void requestStoragePermissions(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
    public static void requestStoragePermissions(Fragment fragment,int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment.requestPermissions(PERMISSIONS_STORAGE, requestCode);
        }
    }

    public static void requestStoragePermissions(Activity activity,int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, requestCode);
        }
    }

    public static void requestStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }

    public static boolean isNeverAskStoragePermissions(Fragment fragment){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }
    public static boolean isNeverAskStoragePermissions(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

    public static boolean isNeverAskInstalledPermissions(Activity activity){
        if (isInstalledAppsPermissionsEnable(activity.getBaseContext())) {
            return !ActivityCompat.shouldShowRequestPermissionRationale(activity, "com.android.permission.GET_INSTALLED_APPS");
        }
        return false;
    }

    // todo：当前target API不需要该权限
    // Android_R版本上谷歌新增特性，限制第三方应用直接获取系统安装应用列表:android.permission.QUERY_ALL_PACKAGES
    // QUERY_ALL_PACKAGES是谷歌权限要求，GET_INSTALLED_APPS为国内工信部要求
    // 原生安卓不需要 GET_INSTALLED_APPS 权限, 不用请求该权限
    // 国内部分厂商比如小米、vivo,他们将”获取用户已安装应用列表”的权限暴露给了用户，让用户可以自由决定允许或者禁止应用访问该信息
    // https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1619
    // https://dev.vivo.com.cn/documentCenter/doc/747
    private static boolean isInstalledAppsPermissionsEnable(Context context) {
        if (context == null) {
            return false;
        }

//        if (Build.VERSION.SDK_INT < 30 /*Android R 11.0*/) {
//            return false;
//        }
//        if (android.provider.Settings.Secure.getInt(context.getContentResolver(),"oem_installed_apps_runtime_permission_enable", 0) > 0) {
//            Log.d(TAG,"vivo 系统支持动态申请 GET_INSTALLED_APPS 权限");
//            return true;
//        }
//
//        try {
//            PermissionInfo permissionInfo =  context.getPackageManager().getPermissionInfo("com.android.permission.GET_INSTALLED_APPS", 0);
//            if (permissionInfo != null && permissionInfo.packageName.equals("com.lbe.security.miui")) {
//                Log.d(TAG,"xiaomi 系统支持动态申请 GET_INSTALLED_APPS 权限");
//                return true;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    public static boolean verifyInstalledAppsPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, "com.android.permission.GET_INSTALLED_APPS");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }
    }

    public static void requestInstalledAppsPermissions(Activity activity, boolean all) {
        if (!isInstalledAppsPermissionsEnable(activity.getBaseContext())) {
            return;
        }
        if (all) {
            ActivityCompat.requestPermissions(activity, new String[]{"com.android.permission.GET_INSTALLED_APPS"}, REQUEST_CODE_GET_INSTALLED_APPS);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{"com.android.permission.GET_INSTALLED_APPS"}, REQUEST_CODE_GET_INSTALLED_APPS_CURRENT);
        }
    }

}