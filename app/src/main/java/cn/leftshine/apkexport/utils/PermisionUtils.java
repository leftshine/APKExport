package cn.leftshine.apkexport.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class PermisionUtils {

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_SHOWLOCALAPK = 2;
    public static final int REQUEST_EXTERNAL_STORAGE_AUTOCLEAN = 3;
    public static final int REQUEST_EXTERNAL_STORAGE_CLEAN_EXPORT_DIR = 4;
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
}