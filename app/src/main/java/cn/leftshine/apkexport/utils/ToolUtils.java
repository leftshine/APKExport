package cn.leftshine.apkexport.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ToolUtils {
    final static boolean DBG = false;
    public static final String HOME_PAGE_URL = "https://leftshine.gitlab.io/apkexport/";
    public static final String DEFAULT_COPY_DATA = HOME_PAGE_URL;
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

    private Context mContext;

    public ToolUtils(Context context) {
        mContext = context;
    }
    List<AppInfo> userAppInfoList = new ArrayList<AppInfo>();
    List<AppInfo> systemAppInfoList = new ArrayList<AppInfo>();
    List<AppInfo> localAppInfoList = new ArrayList<AppInfo>();

    public void loadApp(Handler mHandler, int type, boolean isRefresh) {
/*        Message msg = Message.obtain();
        msg.what =MessageCode.MSG_SHOW_LOAD_UI;
        mHandler.sendMessage(msg);*/
        Log.i(TAG, "loadApp-isRefresh: "+isRefresh);
        Message msg = Message.obtain();
        msg.what =MessageCode.MSG_GET_APP_COMPLETED;
        switch (type) {
            case TYPE_USER:
                if(isRefresh) {
                    userAppInfoList.clear();
                }
                if(userAppInfoList==null||userAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }
                Log.i(TAG, "TYPE_USER getApp:"+"completed");
                msg.obj = userAppInfoList;
                //Log.i(TAG, "loadApp-userAppInfoList: "+userAppInfoList);
                break;
            case TYPE_SYSTEM:
                if(isRefresh) {
                    systemAppInfoList.clear();
                }
                if(systemAppInfoList==null||systemAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }
                Log.i(TAG, "TYPE_SYSTEM getApp:"+"completed");
                msg.obj = systemAppInfoList;
                break;
            case TYPE_LOCAL:
                if(isRefresh) {
                    localAppInfoList.clear();
                }
                if(localAppInfoList==null||localAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }
                Log.i(TAG, "TYPE_LOCAL getApp:"+"completed");
                msg.obj = localAppInfoList;
                break;
        }
        mHandler.sendMessage(msg);

    }
    public  void getApp(Handler mHandler, int type) {
        /*Message msg = Message.obtain();
        Log.i(TAG, "TYPE_USER getApp:"+"completed");
        msg.what =MessageCode.MSG_SHOW_LOAD_UI;
        mHandler.sendMessage(msg);*/
        List<?> localList = mContext.getPackageManager().getInstalledPackages(0);
        Log.i(TAG, "getApp: type="+type);
        switch (type){
            case TYPE_USER:
                for (int i = 0; i < localList.size(); i++) {
                    try {
                        PackageInfo localPackageInfo = (PackageInfo) localList.get(i);
                        if ((ApplicationInfo.FLAG_SYSTEM &
                                localPackageInfo.applicationInfo.flags) == 0) {
                            final AppInfo localAppInfo = new AppInfo();
                            localAppInfo.appName = localPackageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                            localAppInfo.appSourcDir = localPackageInfo.applicationInfo.publicSourceDir;
                            localAppInfo.appSize = 0;
                            //File localFile1 = new File(localPackageInfo.applicationInfo.publicSourceDir);
                            File localFile1 = new File(localAppInfo.appSourcDir);
                            if (localFile1 != null)
                                localAppInfo.appSize = (int) (localFile1.length());
                            localAppInfo.appCache = 0;
                            //String packageName = localPackageInfo.packageName;
                            localAppInfo.isSelected = Boolean.valueOf(false);
                            localAppInfo.packageName = localPackageInfo.packageName;

                            localAppInfo.versionName = localPackageInfo.versionName;
                            localAppInfo.versionCode = localPackageInfo.versionCode;
                            localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                            localAppInfo.setFirstInstallTime(localPackageInfo.firstInstallTime);
                            localAppInfo.setLastUpdateTime(localPackageInfo.lastUpdateTime);
                            //getRunningAppProcessInfo(localAppInfo);
                            if(DBG) Log.d(TAG, "getAppOf TYPE_USER "+localAppInfo.getAppName());
                            userAppInfoList.add(localAppInfo);
                        /*Message msg = mHandler.obtainMessage(MessageCode.MSG_GET_APP, localAppInfo);
                        // mHandler.sendEmptyMessage(0);
                        msg.sendToTarget();*/
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "getApp: failed i="+i );
                    }
                }
                userAppInfoList = sort(userAppInfoList);
                break;
            case TYPE_SYSTEM:
                for (int i = 0; i < localList.size(); i++) {
                    try {
                        PackageInfo localPackageInfo = (PackageInfo) localList.get(i);
                        if ((ApplicationInfo.FLAG_SYSTEM &
                                localPackageInfo.applicationInfo.flags) > 0) {
                            final AppInfo localAppInfo = new AppInfo();
                            localAppInfo.appName = localPackageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                            localAppInfo.appSourcDir = localPackageInfo.applicationInfo.publicSourceDir;
                            localAppInfo.appSize = 0;
                            //File localFile1 = new File(localPackageInfo.applicationInfo.publicSourceDir);
                            File localFile1 = new File(localAppInfo.appSourcDir);
                            if (localFile1 != null)
                                localAppInfo.appSize = (int) (localFile1.length());
                            localAppInfo.appCache = 0;
                            //String packageName = localPackageInfo.packageName;
                            localAppInfo.isSelected = Boolean.valueOf(false);
                            localAppInfo.packageName = localPackageInfo.packageName;

                            localAppInfo.versionName = localPackageInfo.versionName;
                            localAppInfo.versionCode = localPackageInfo.versionCode;
                            localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                            //getRunningAppProcessInfo(localAppInfo);
                            if(DBG) Log.d(TAG, "getAppOf TYPE_SYSTEM " + localAppInfo.getAppName());
                            systemAppInfoList.add(localAppInfo);
                        /*Message msg = mHandler.obtainMessage(MessageCode.MSG_GET_APP, localAppInfo);
                        // mHandler.sendEmptyMessage(0);
                        msg.sendToTarget();*/
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "getApp: failed i=" + i);
                    }
                }
                systemAppInfoList = sort(systemAppInfoList);
                break;
            case TYPE_LOCAL:
                Log.i(TAG, "getApp:TYPE_LOCAL ");
                LocalApkSearchUtils localApkSearchUtils = new LocalApkSearchUtils(mContext);
                //localApkSearchUtils.FindAllAPKFile(Environment.getDataDirectory());
                localApkSearchUtils.FindAllAPKFile(Environment.getExternalStorageDirectory());
                localAppInfoList = localApkSearchUtils.getAppInfo();
                localAppInfoList = sort(localAppInfoList);
                break;
        }

        //return appInfoList;
    }

    public  List<AppInfo> sort(List<AppInfo> list) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        //修改为从设置读取排序方式
        String sortType = Settings.getSortType();
        String sortOrder = Settings.getSortOrder();
        Log.i(TAG, "sort: sortType="+sortType+"---sortOrder="+sortOrder);
        if(sortOrder.equals(SORT_ORDER_ASC)){
            //升序
            switch (sortType){
                case SORT_TYPE_APPNAME:
                    Collections.sort(list, SORT_BY_APPNAME);
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
            Collator collator = Collator.getInstance(Settings.getCustomLocale());
            if(DBG) Log.d(TAG, "appName: " + appInfo1.appName+" VS "+appInfo2.appName);
            return collator.compare(appInfo1.appName, appInfo2.appName);
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Collator collator = Collator.getInstance(Settings.getCustomLocale());
            return collator.compare(appInfo1.packageName, appInfo2.packageName);
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            if(compare1.compareTo(compare2)>0)
                return 1;
            else if(compare1.compareTo(compare2)<0)
                return -1;
            else
                return 0;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            if(compare1.compareTo(compare2)>0)
                return 1;
            else if(compare1.compareTo(compare2)<0)
                return -1;
            else
                return 0;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_UPDATE = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getLastUpdateTime();
            Long  compare2 = appInfo2.getLastUpdateTime();
            Log.i(TAG, "compare: " + compare1);
            if(compare1.compareTo(compare2)>0)
                return 1;
            else if(compare1.compareTo(compare2)<0)
                return -1;
            else
                return 0;
        }
    };

    //降序比较器
    public static final Comparator<AppInfo> SORT_BY_APPNAME_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Collator collator = Collator.getInstance(Settings.getCustomLocale());
            return -collator.compare(appInfo1.appName, appInfo2.appName);
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Collator collator = Collator.getInstance(Settings.getCustomLocale());
            return -collator.compare(appInfo1.packageName, appInfo2.packageName);
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            if(compare1.compareTo(compare2)<0)
                return 1;
            else if(compare1.compareTo(compare2)>0)
                return -1;
            else
                return 0;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            if(compare1.compareTo(compare2)<0)
                return 1;
            else if(compare1.compareTo(compare2)>0)
                return -1;
            else
                return 0;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_UPDATE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getLastUpdateTime();
            Long  compare2 = appInfo2.getLastUpdateTime();
            if(compare1.compareTo(compare2)<0)
                return 1;
            else if(compare1.compareTo(compare2)>0)
                return -1;
            else
                return 0;
        }
    };
}
