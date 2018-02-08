package cn.leftshine.apkexport.utils;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ToolUtils {
    private final static String TAG = "ToolUtils";
    public static final String TYPE = "type";
    public final static int TYPE_USER = 0;
    public final static int TYPE_SYSTEM = 1;
    public final static int TYPE_LOCAL = 2;
    //public final static String SORT_TYPE_DEFALUT = 0;
    public final static String SORT_TYPE_APPNAME = "200";
    public final static String SORT_TYPE_PACKAGENAME = "201";
    public final static String SORT_TYPE_SIZE = "202";
    public final static String SORT_TYPE_INSTALL = "203";
    public final static String SORT_TYPE_UPDATE = "204";
    public final static String SORT_TYPE_RECENTUSE = "205";
    public final static String SORT_ORDER_ASC = "300";
    public final static String SORT_ORDER_DES = "301";

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

    public static List<AppInfo> getApp(Fragment fragment, Handler mHandler, int type) {

        List<?> localList = fragment.getActivity().getPackageManager().getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        Log.i(TAG, "getApp: type="+type);
        switch (type){
            case TYPE_USER:
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

                        localAppInfo.versionName = localPackageInfo.versionName;
                        localAppInfo.versionCode = localPackageInfo.versionCode;
                        localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(fragment.getActivity().getPackageManager());
                        localAppInfo.setFirstInstallTime(localPackageInfo.firstInstallTime);
                        localAppInfo.setLastUpdateTime(localPackageInfo.lastUpdateTime);
                        //getRunningAppProcessInfo(localAppInfo);
                        Log.i(TAG, "getAppOf TYPE_USER "+localAppInfo.getAppName());
                        appInfoList.add(localAppInfo);
                        /*Message msg = mHandler.obtainMessage(MessageCode.MSG_GET_APP, localAppInfo);
                        // mHandler.sendEmptyMessage(0);
                        msg.sendToTarget();*/
                    }
                }
                break;
            case TYPE_SYSTEM:
                for (int i = 0; i < localList.size(); i++) {
                    PackageInfo localPackageInfo = (PackageInfo) localList.get(i);
                    if ((ApplicationInfo.FLAG_SYSTEM &
                            localPackageInfo.applicationInfo.flags) > 0) {
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

                        localAppInfo.versionName = localPackageInfo.versionName;
                        localAppInfo.versionCode = localPackageInfo.versionCode;
                        localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(fragment.getActivity().getPackageManager());
                        //getRunningAppProcessInfo(localAppInfo);
                        Log.i(TAG, "getAppOf TYPE_SYSTEM "+localAppInfo.getAppName());
                        appInfoList.add(localAppInfo);
                        /*Message msg = mHandler.obtainMessage(MessageCode.MSG_GET_APP, localAppInfo);
                        // mHandler.sendEmptyMessage(0);
                        msg.sendToTarget();*/
                    }
                }
                break;
        }
        Message msg = Message.obtain();
        Log.i(TAG, "getApp:"+"completed");
        msg.what =MessageCode.MSG_GET_APP_COMPLETED;
        msg.obj = sort(appInfoList);
        mHandler.sendMessage(msg);
        return appInfoList;
    }

    public static List<AppInfo> sort(List<AppInfo> list) {
        //修改为从设置读取排序方式
        String sortType = Settings.getSortType();
        String sortOrder = Settings.getSortOrder();
        Log.i(TAG, "sort: sortType="+sortType+"---sortOrder="+sortOrder);
        if(sortOrder.equals(SORT_ORDER_ASC)){
            //升序
            switch (sortType){
                case SORT_TYPE_APPNAME:
                    Collections.sort(list,SORT_BY_APPNAME);
                    break;
                case SORT_TYPE_PACKAGENAME:
                    Collections.sort(list,SORT_BY_PACKAGENAME);
                    break;
                case SORT_TYPE_SIZE:
                    Collections.sort(list,SORT_BY_SIZE);
                    break;
                case SORT_TYPE_INSTALL:
                    Collections.sort(list,SORT_BY_INSTALL);
                    break;
                case SORT_TYPE_UPDATE:
                    Collections.sort(list,SORT_BY_UPDATE);
                    break;
                case SORT_TYPE_RECENTUSE:
                    //Collections.sort(list,SORT_BY_RECENTUSE);
                    break;
            }
        }else{
            //降序
            switch (sortType){
                case SORT_TYPE_APPNAME:
                    Collections.sort(list,SORT_BY_APPNAME_DES);
                    break;
                case SORT_TYPE_PACKAGENAME:
                    Collections.sort(list,SORT_BY_PACKAGENAME_DES);
                    break;
                case SORT_TYPE_SIZE:
                    Collections.sort(list,SORT_BY_SIZE_DES);
                    break;
                case SORT_TYPE_INSTALL:
                    Collections.sort(list,SORT_BY_INSTALL_DES);
                    break;
                case SORT_TYPE_UPDATE:
                    Collections.sort(list,SORT_BY_UPDATE_DES);
                    break;
                case SORT_TYPE_RECENTUSE:
                    //Collections.sort(list,SORT_BY_RECENTUSE_DES);
                    break;
            }
        }


        return list;
    }

    //升序比较器
    public static final Comparator<AppInfo> SORT_BY_APPNAME = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.appName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.appName).toUpperCase();
            Log.i(TAG, "compare: " + compare1);
            return (compare1.compareTo(compare2));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.packageName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.packageName).toUpperCase();
            Log.i(TAG, "compare: " + compare1);
            return (compare1.compareTo(compare2));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            Log.i(TAG, "compare: " + compare1);
          if(compare2<compare1)
              return 1;
          else
              return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            Log.i(TAG, "compare: " + compare1);
            if(compare2<compare1)
                return 1;
            else
                return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_UPDATE = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getLastUpdateTime();
            Long  compare2 = appInfo2.getLastUpdateTime();
            Log.i(TAG, "compare: " + compare1);
            if(compare2<compare1)
                return 1;
            else
                return -1;
        }
    };

    //降序比较器
    public static final Comparator<AppInfo> SORT_BY_APPNAME_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.appName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.appName).toUpperCase();
            Log.i(TAG, "compare: " + compare1);
            return (compare2.compareTo(compare1));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.packageName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.packageName).toUpperCase();
            Log.i(TAG, "compare: " + compare1);
            return (compare2.compareTo(compare1));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            else
                return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            else
                return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_UPDATE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getLastUpdateTime();
            Long  compare2 = appInfo2.getLastUpdateTime();
            Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            else
                return -1;
        }
    };

}
