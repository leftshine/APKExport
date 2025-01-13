package cn.leftshine.apkexport.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取手机上apk文件信息类，主要是判断是否安装再手机上了，安装的版本比较现有apk版本信息
 * @author  Dylan
 */
public class LocalApkSearchUtils {
    private final static String TAG = "LocalApkSearchUtils";
    private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
    private static int UNINSTALLED = 1; // 表示未安装
    private static int INSTALLED_UPDATE =2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新

    private Context context;
    private List<AppInfo> appInfoList = new ArrayList<>();

    public LocalApkSearchUtils(Context context) {
        super();
        this.context = context;
    }
    public List<AppInfo> getAppInfo() {
        return appInfoList;
    }

    public void setAppInfo(List<AppInfo> appInfo) {
        this.appInfoList = appInfo;
    }

    /**
     * @param args
     *            运用递归的思想，递归去找每个目录下面的apk文件
     */
    /*public void FindAllAPKFile(File file) {

        // 手机上的文件,目前只判断SD卡上的APK文件
        // file = Environment.getDataDirectory();
        // SD卡上的文件目录
        //Log.i(TAG, "FindAllAPKFile: file path="+file);
        if (file.isFile()) {
            String name_s = file.getName();
            //AppInfo myFile = new AppInfo();
            String apk_path = null;
            // MimeTypeMap.getSingleton()
            if (name_s.toLowerCase().endsWith(".apk")) {
                apk_path = file.getAbsolutePath();// apk文件的绝对路劲
                Log.i(TAG, "FindAllAPKFile: APK Path= "+apk_path);
                // System.out.println("----" + file.getAbsolutePath() + "" +
                // name_s);
                PackageManager pm = context.getPackageManager();
                PackageInfo localPackageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);

                //ApplicationInfo appInfo = packageInfo.applicationInfo;
                final AppInfo localAppInfo = new AppInfo();
                //localAppInfo.appName = localPackageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                //localAppInfo.appSourcDir = localPackageInfo.applicationInfo.publicSourceDir;
                localAppInfo.appName = name_s;
                localAppInfo.appSourcDir = apk_path;
                localAppInfo.appSize = 0;
                File localFile1 = new File(localAppInfo.appSourcDir);
                if (localFile1 != null)
                    localAppInfo.appSize = (int) (localFile1.length());
                localAppInfo.appCache = 0;
                //String packageName = localPackageInfo.packageName;
                localAppInfo.isSelected = Boolean.valueOf(false);

                *//*localAppInfo.packageName = localPackageInfo.packageName;
                localAppInfo.versionName = localPackageInfo.versionName;
                localAppInfo.versionCode = localPackageInfo.versionCode;*//*
                localAppInfo.packageName="";
                localAppInfo.versionName="";
                //localAppInfo.versionCode=00;
                //获取最近修改时间
                long time = localFile1.lastModified();//返回文件最后修改时间，是一个long型毫秒数
                localAppInfo.setLastUpdateTime(time);
                try {
                    localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(context.getPackageManager());
                } catch (Exception e) {
                    Log.e(TAG, "FindAllAPKFile: 图标获取失败，设为默认图标");
                    Drawable defaultAppIcon = context.getResources().getDrawable(R.mipmap.default_app_icon);
                    localAppInfo.appIcon = defaultAppIcon;
                }

                //getRunningAppProcessInfo(localAppInfo);
               *//* int type = doType(pm, packageName, versionCode);
                localAppInfo.setInstalled(type);
                Log.i(TAG, "getAppOf TYPE_LOCAL " + localAppInfo.getAppName());*//*
                    appInfoList.add(localAppInfo);

                    *//**获取apk的图标 *//**//*
                appInfo.sourceDir = apk_path;
                appInfo.publicSourceDir = apk_path;
                Drawable apk_icon = appInfo.loadIcon(pm);
                myFile.setApk_icon(apk_icon);
                *//**//** 得到包名 *//**//*
                String packageName = packageInfo.packageName;
                myFile.setPackageName(packageName);
                *//**//** apk的绝对路劲 *//**//*
                myFile.setFilePath(file.getAbsolutePath());
                *//**//** apk的版本名称 String *//**//*
                String versionName = packageInfo.versionName;
                myFile.setVersionName(versionName);
                *//**//** apk的版本号码 int *//**//*
                int versionCode = packageInfo.versionCode;
                myFile.setVersionCode(versionCode);
                *//**//**安装处理类型*//**//*
                int type = doType(pm, packageName, versionCode);
                myFile.setInstalled(type);

                Log.i("ok", "处理类型:"+String.valueOf(type)+"\n" + "------------------我是纯洁的分割线-------------------");
                myFiles.add(myFile);*//*
            }
            // String apk_app = name_s.substring(name_s.lastIndexOf("."));
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file_str : files) {
                    FindAllAPKFile(file_str);
                }
            }
        }
    }*/

    public void FindAllAPKFile(File file){
        String[] projection = new String[] { MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE
        };
        String selection = MediaStore.Files.FileColumns.DATA + " like ?"
                + " or " + MediaStore.Files.FileColumns.DATA + " like ?";
        String[] selectionArgs = new String[]{"%.apk", "%.apk.1"};
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/file"),
                //Uri.parse(Environment.getExternalStorageDirectory().toString()),
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int idindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns._ID);
                int dataindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int sizeindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                do {
                    //String id = cursor.getString(idindex);
                    String path = cursor.getString(dataindex);
                    Log.i(TAG, "FindAllAPKFile: \npath="+path+"\nExternalStorageDirectory="+Environment.getExternalStorageDirectory().toString());
                    //排除一些重复的
                    /*if(!path.startsWith(Environment.getExternalStorageDirectory().toString()))
                        continue;*/
                    if(path.startsWith("/storage/emulated/legacy/"))
                        continue;
                   // String size = cursor.getString(sizeindex);
                    PackageManager pm = context.getPackageManager();
                    PackageInfo localPackageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                    File localFile1 = new File(path);
                    final AppInfo localAppInfo = new AppInfo();
                    try {
                        localAppInfo.appSourcDir = path;
                        localAppInfo.appName = localFile1.getName();
                        Log.i(TAG, "FindAllAPKFile: path="+path);
                        if (localFile1 != null)
                            localAppInfo.appSize = (int) (localFile1.length());
                        //localAppInfo.appCache = 0;
                        //localAppInfo.isSelected = Boolean.valueOf(false);
                        localAppInfo.packageName = localPackageInfo.packageName;
                        localAppInfo.versionName = localPackageInfo.versionName;
                        localAppInfo.versionCode = localPackageInfo.versionCode;
                        Log.i(TAG, "FindAllAPKFile: " +
                                "\nappSourcDir"+localAppInfo.appSourcDir+
                                "\nappName"+localAppInfo.appName+
                                "\npackageName"+localAppInfo.packageName+
                                "\nversionName"+localAppInfo.versionName+
                                "\nversionCode"+localAppInfo.versionCode

                        );
                        //获取最近修改时间
                        long time = localFile1.lastModified();//返回文件最后修改时间，是一个long型毫秒数
                        localAppInfo.setLastUpdateTime(time);
                        localAppInfo.appIcon = localPackageInfo.applicationInfo.loadIcon(context.getPackageManager());

                        //Log.e(TAG, "FindAllAPKFile: 图标获取失败，设为默认图标");
                        //Drawable defaultAppIcon = context.getResources().getDrawable(R.mipmap.default_app_icon);
                        //localAppInfo.appIcon = defaultAppIcon;
                        appInfoList.add(localAppInfo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

	/*
	 * 判断该应用是否在手机上已经安装过，有以下集中情况出现
	 * 1.未安装，这个时候按钮应该是“安装”点击按钮进行安装
	 * 2.已安装，按钮显示“已安装” 可以卸载该应用
	 * 3.已安装，但是版本有更新，按钮显示“更新” 点击按钮就安装应用
	 *//*

    *//**
     * 判断该应用在手机中的安装情况
     * @param pm                   PackageManager
     * @param packageName  要判断应用的包名
     * @param versionCode     要判断应用的版本号
     *//*
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.endsWith(pi_packageName)){
                //Log.i("test","此应用安装过了");
                if(versionCode==pi_versionCode){
                    Log.i("test","已经安装，不用更新，可以卸载该应用");
                    return INSTALLED;
                }else if(versionCode>pi_versionCode){
                    Log.i("test","已经安装，有更新");
                    return INSTALLED_UPDATE;
                }
            }
        }
        Log.i("test","未安装该应用，可以安装");
        return UNINSTALLED;
    }*/

}
