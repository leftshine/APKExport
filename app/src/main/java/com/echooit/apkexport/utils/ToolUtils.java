package com.echooit.apkexport.utils;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ToolUtils {
    private final static String TAG = "ToolUtils";
	/**
     * 返回byte的数据大小对应的文本
     * 
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        if (size < 0) {
            size = 0;
        }
        DecimalFormat formater = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return formater.format(kbsize) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return formater.format(mbsize) + " MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + " GB";
        } else {
            return "0 KB";
        }
    }

    public static List<AppInfo> getApp(Fragment fragment, Handler mHandler) {

        List<?> localList = fragment.getActivity().getPackageManager().getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo = (PackageInfo) localList.get(i);
            if ((ApplicationInfo.FLAG_SYSTEM &
                    localPackageInfo.applicationInfo.flags) == 0) {
                final AppInfo localAppInfo = new AppInfo();
                localAppInfo.appName = localPackageInfo.applicationInfo.loadLabel(fragment.getActivity().getPackageManager()).toString();
                localAppInfo.appSourcDir = localPackageInfo.applicationInfo.publicSourceDir;
                localAppInfo.appSize = 0;
                //File localFile1 = new File(localPackageInfo.applicationInfo.publicSourceDir);
                File localFile1 = new File(localAppInfo.appSourcDir);
                if (localFile1 != null)
                    localAppInfo.appSize = (int) (localFile1.length());
                localAppInfo.appCache = 0;
                String packageName = localPackageInfo.packageName;
                localAppInfo.isSelected = Boolean.valueOf(false);
                localAppInfo.packageName = localPackageInfo.packageName;
                Log.i(TAG, "getApp: "+localAppInfo.packageName);
                localAppInfo.versionName = localPackageInfo.versionName;
                localAppInfo.versionCode = localPackageInfo.versionCode;
                localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(fragment.getActivity().getPackageManager());
                //getRunningAppProcessInfo(localAppInfo);
                appInfoList.add(localAppInfo);
                Message msg = mHandler.obtainMessage(MessageCode.MSG_GET_APP, localAppInfo);
                // mHandler.sendEmptyMessage(0);
                msg.sendToTarget();
            }
        }
        Message msg = Message.obtain();
        Log.i(TAG, "getApp:"+"completed");
        //msg.obj = copiedSize * 100 / totalSize ;//progress的值为0到100，因此得到的百分数要乘以100;
        msg.what =MessageCode.MSG_GET_APP_COMPLETED;
        mHandler.sendMessage(msg);
        return appInfoList;
    }
}
