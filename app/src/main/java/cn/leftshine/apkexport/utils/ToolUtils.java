package cn.leftshine.apkexport.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ToolUtils {
    public static final String DEFAULT_COPY_DATA = "gUC65D40b9";
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
    List<AppInfo> userAppInfoList = new ArrayList<AppInfo>();
    List<AppInfo> systemAppInfoList = new ArrayList<AppInfo>();
    List<AppInfo> localAppInfoList = new ArrayList<AppInfo>();

    public void loadApp(Handler mHandler, int type) {
/*        Message msg = Message.obtain();
        msg.what =MessageCode.MSG_SHOW_LOAD_UI;
        mHandler.sendMessage(msg);*/
        switch (type) {
            case TYPE_USER:
                if(userAppInfoList==null||userAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }else {
                    Message msg1 = Message.obtain();
                    Log.i(TAG, "TYPE_USER getApp:"+"completed");
                    msg1.what =MessageCode.MSG_GET_APP_COMPLETED;
                    msg1.obj = userAppInfoList;
                    mHandler.sendMessage(msg1);
                }
                break;
            case TYPE_SYSTEM:
                if(systemAppInfoList==null||systemAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }else{

                    Message msg2 = Message.obtain();
                    Log.i(TAG, "TYPE_SYSTEM getApp:"+"completed");
                    msg2.what =MessageCode.MSG_GET_APP_COMPLETED;
                    msg2.obj = systemAppInfoList;
                    mHandler.sendMessage(msg2);
                }
                break;
            case TYPE_LOCAL:
                if(localAppInfoList==null||localAppInfoList.isEmpty()) {
                    getApp(mHandler, type);
                }else{
                    Message msg3 = Message.obtain();
                    Log.i(TAG, "TYPE_LOCAL getApp:"+"completed");
                    msg3.what =MessageCode.MSG_GET_APP_COMPLETED;
                    msg3.obj = localAppInfoList;
                    mHandler.sendMessage(msg3);
                }
                break;
        }

    }
    public  void getApp(Handler mHandler, int type) {
        List<?> localList = mContext.getPackageManager().getInstalledPackages(0);
        Log.i(TAG, "getApp: type="+type);
        switch (type){
            case TYPE_USER:
                userAppInfoList.clear();
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
                            //Log.i(TAG, "getAppOf TYPE_USER "+localAppInfo.getAppName());
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
                Message msg1 = Message.obtain();
                Log.i(TAG, "TYPE_USER getApp:"+"completed");
                msg1.what =MessageCode.MSG_GET_APP_COMPLETED;
                msg1.obj = userAppInfoList;
                mHandler.sendMessage(msg1);
                break;
            case TYPE_SYSTEM:
                systemAppInfoList.clear();
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
                            Log.i(TAG, "getAppOf TYPE_SYSTEM " + localAppInfo.getAppName());
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
                Message msg2 = Message.obtain();
                Log.i(TAG, "TYPE_SYSTEM getApp:"+"completed");
                msg2.what =MessageCode.MSG_GET_APP_COMPLETED;
                msg2.obj = systemAppInfoList;
                mHandler.sendMessage(msg2);
                break;
            case TYPE_LOCAL:
                localAppInfoList.clear();
                Log.i(TAG, "getApp:TYPE_LOCAL ");
                LocalApkSearchUtils localApkSearchUtils = new LocalApkSearchUtils(mContext);
                //localApkSearchUtils.FindAllAPKFile(Environment.getDataDirectory());
                localApkSearchUtils.FindAllAPKFile(Environment.getExternalStorageDirectory());
                localAppInfoList = localApkSearchUtils.getAppInfo();
                localAppInfoList = sort(localAppInfoList);
                Message msg3 = Message.obtain();
                Log.i(TAG, "TYPE_LOCAL getApp:"+"completed");
                msg3.what =MessageCode.MSG_GET_APP_COMPLETED;
                msg3.obj = localAppInfoList;
                mHandler.sendMessage(msg3);
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
            //Log.i(TAG, "compare: " + compare1+":"+compare2);
            if(compare1==compare2)
                return 0;
            else
                return (compare1.compareTo(compare2));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.packageName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.packageName).toUpperCase();
            //Log.i(TAG, "compare: " + compare1+":"+compare2);
            if(compare1==compare2)
                return 0;
            else
                return (compare1.compareTo(compare2));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            //Log.i(TAG, "compare: " + compare1);
          if(compare2<compare1)
              return 1;
          if(compare2==compare1)
              return 0;
          else
              return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            //Log.i(TAG, "compare: " + compare1);
            if(compare2<compare1)
                return 1;
            if(compare2==compare1)
                return 0;
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
            if(compare2==compare1)
                return 0;
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
            //Log.i(TAG, "compare: " + compare1+":"+compare2);
            if(compare1==compare2)
                return 0;
            else
                return (compare2.compareTo(compare1));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_PACKAGENAME_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            CharacterParser characterParser = CharacterParser.getInstance();
            String compare1 = characterParser.getSelling(appInfo1.packageName).toUpperCase();
            String compare2 = characterParser.getSelling(appInfo2.packageName).toUpperCase();
            //Log.i(TAG, "compare: " + compare1+":"+compare2);
            if(compare1==compare2)
                return 0;
            else
                return (compare2.compareTo(compare1));
        }
    };
    public static final Comparator<AppInfo> SORT_BY_SIZE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.appSize;
            Long  compare2 = appInfo2.appSize;
            //Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            if(compare2==compare1)
                return 0;
            else
                return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_INSTALL_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getFirstInstallTime();
            Long  compare2 = appInfo2.getFirstInstallTime();
            //Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            if(compare2==compare1)
                return 0;
            else
                return -1;
        }
    };
    public static final Comparator<AppInfo> SORT_BY_UPDATE_DES = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo1, AppInfo appInfo2) {
            Long  compare1 = appInfo1.getLastUpdateTime();
            Long  compare2 = appInfo2.getLastUpdateTime();
            //Log.i(TAG, "compare: " + compare1);
            if(compare2>compare1)
                return 1;
            if(compare2==compare1)
                return 0;
            else
                return -1;
        }
    };


}
