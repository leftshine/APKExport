package cn.leftshine.apkexport.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.Settings;

public class SystemShareActivity extends AppCompatActivity {

    private Context mContext;
    private AppInfo appInfo;
    private final static String TAG = "SystemShareActivity";
    private ImageView share_appInfo_icon;
    private TextView share_appInfo_appName,share_appInfo_packageName,share_appInfo_appSize,share_appInfo_appVersionName,share_appInfo_appVersionCode,txt_share_filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_system_share);
        bindView();
        getAppInfo();
        initView();
        //Log.i(TAG, "getCallingPackage: "+getCallingPackage());
    }


    private void bindView() {
        share_appInfo_icon = findViewById(R.id.share_appInfo_icon);
        share_appInfo_appName = findViewById(R.id.share_appInfo_appName);
        share_appInfo_packageName =findViewById(R.id.share_appInfo_packageName);
        share_appInfo_appSize =findViewById(R.id.share_appInfo_appSize);
        share_appInfo_appVersionName =findViewById(R.id.share_appInfo_appVersionName);
        share_appInfo_appVersionCode =findViewById(R.id.share_appInfo_appVersionCode);
        txt_share_filename =findViewById(R.id.txt_share_filename);
    }
    private void initView() {
        share_appInfo_icon.setImageDrawable(appInfo.appIcon);
        share_appInfo_appName.setText(appInfo.appName);
        share_appInfo_packageName.setText(appInfo.packageName);
        share_appInfo_appSize.setText(String.valueOf(appInfo.appSize));
        share_appInfo_appVersionName.setText(appInfo.versionName);
        share_appInfo_appVersionCode.setText(String.valueOf(appInfo.versionCode));
        String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",appInfo.appName).replace("#P",appInfo.packageName).replace("#V",appInfo.versionName).replace("#C",String.valueOf(appInfo.versionCode));
        txt_share_filename.setText(customFileName+".apk");
    }

    private void getAppInfo() {
        String callingPackageName = "";
        //getCallerProcessName();
        try{
            callingPackageName = (reflectGetReferrer()==null||"".equals(reflectGetReferrer())) ? getCallingPackage() : reflectGetReferrer();
        }catch (Exception e){
            callingPackageName = "";
        }
        if(callingPackageName==null||"".equals(callingPackageName)){
            Toast.makeText(mContext,"运气不好，获取应用信息失败",Toast.LENGTH_SHORT).show();
        }else {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(callingPackageName, 0);
                appInfo = new AppInfo();
                appInfo.appSourcDir = applicationInfo.publicSourceDir;
                appInfo.appIcon = applicationInfo.loadIcon(pm);
                appInfo.appName = applicationInfo.loadLabel(pm).toString();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(appInfo.appSourcDir, PackageManager.GET_ACTIVITIES);
                appInfo.packageName = packageInfo.packageName;

                appInfo.versionName = packageInfo.versionName;
                appInfo.versionCode = packageInfo.versionCode;
                File localFile1 = new File(appInfo.appSourcDir);
                if (localFile1 != null)
                    appInfo.appSize = (int) (localFile1.length());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //不能在调用者startActivity()的时候获取到调用者的包名，只能用于Activity用到的Binder同步调用的地方。
    /*private String getCallerProcessName() {
        int uid = Binder.getCallingUid();
        String callingApp = mContext.getPackageManager().getNameForUid(uid);
        Log.i(TAG, "callingApp: " + callingApp);
        if(callingApp != null){
            return callingApp;
        }
        return "";
    }*/
    //Activity的getReferrer()是不可靠的，因为调用者可以自己设置referrer的值。
    //反射的方式获取mReferrer
    //是getReferrer()的改进，消除getReferrer()可能返回的不可靠的值，直接获取可靠的mReferrer值（目前来看是可靠的）
    private String reflectGetReferrer() {
        try {
            Class activityClass = Class.forName("android.app.Activity");

            Field refererField = activityClass.getDeclaredField("mReferrer");
            refererField.setAccessible(true);
            String referrer = (String) refererField.get(mContext);
            Log.i(TAG, "referrer: " + referrer);
            return referrer;
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "reflectGetReferrer: fail");
        }
        return "";
    }
}
