package cn.leftshine.apkexport.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.handler.FileHandler;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.MessageCode;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.utils.ToolUtils;
import cn.leftshine.apkexport.view.ClearEditText;

public class SystemShareActivity extends AppCompatActivity {

    private Context mContext;
    private AppInfo appInfo;
    private final static String TAG = "SystemShareActivity";
    private ImageView share_appInfo_icon;
    private TextView share_appInfo_appName,share_appInfo_packageName,share_appInfo_appSize,share_appInfo_appVersionName,share_appInfo_appVersionCode;
    private ClearEditText txt_share_filename;
    private Button btn_export,btn_export_share;
    private Button btn_insert_divider,btn_insert_N, btn_insert_P, btn_insert_V, btn_insert_C, btn_insert_default;

    private Handler mHandler;
    private FileUtils fileUtils;
    private boolean isDirectShare=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new FileHandler(mContext);
        fileUtils = new FileUtils(mContext);
        isDirectShare = Settings.isExportDerect();
        setContentView(R.layout.activity_system_share);
        getAppInfo();
        bindView();
        initView();
        if (isDirectShare) {
            //setTheme(android.R.style.Theme_Translucent);
            //moveTaskToBack(isTaskRoot());
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    shareDirect();
                }
            }).start();*/
            shareDirect();
            //finish();
        }
        //Log.i(TAG, "getCallingPackage: "+getCallingPackage());
    }

    private void shareDirect() {
        String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",appInfo.appName).replace("#P",appInfo.packageName).replace("#V",appInfo.versionName).replace("#C",String.valueOf(appInfo.versionCode))+".apk";
        fileUtils.doExport(mHandler, FileUtils.MODE_EXPORT_SHARE, appInfo.appSourcDir,customFileName);
    }


    private void bindView() {
        share_appInfo_icon = findViewById(R.id.share_appInfo_icon);
        share_appInfo_appName = findViewById(R.id.share_appInfo_appName);
        share_appInfo_packageName =findViewById(R.id.share_appInfo_packageName);
        share_appInfo_appSize =findViewById(R.id.share_appInfo_appSize);
        share_appInfo_appVersionName =findViewById(R.id.share_appInfo_appVersionName);
        share_appInfo_appVersionCode =findViewById(R.id.share_appInfo_appVersionCode);
        txt_share_filename =findViewById(R.id.txt_share_filename);
        btn_export = findViewById(R.id.btn_export);
        btn_export_share = findViewById(R.id.btn_export_share);
        btn_insert_divider = findViewById((R.id.btn_share_insert_divider));
        btn_insert_N = findViewById(R.id.btn_share_insert_N);
        btn_insert_P = findViewById(R.id.btn_share_insert_P);
        btn_insert_V = findViewById(R.id.btn_share_insert_V);
        btn_insert_C = findViewById(R.id.btn_share_insert_C);
        btn_insert_default = findViewById(R.id.btn_share_insert_default);
    }
    private void initView() {
        share_appInfo_icon.setImageDrawable(appInfo.appIcon);
        share_appInfo_appName.setText(appInfo.appName);
        share_appInfo_packageName.setText(appInfo.packageName);
        share_appInfo_appSize.setText(ToolUtils.getDataSize(appInfo.appSize));
        share_appInfo_appVersionName.setText(appInfo.versionName);
        share_appInfo_appVersionCode.setText(String.valueOf(appInfo.versionCode));
        String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",appInfo.appName).replace("#P",appInfo.packageName).replace("#V",appInfo.versionName).replace("#C",String.valueOf(appInfo.versionCode))+".apk";
        txt_share_filename.setText(customFileName);

        btn_export.setOnClickListener(onClickListener);
        btn_export_share.setOnClickListener(onClickListener);
        btn_insert_divider.setOnClickListener(onClickListener);
        btn_insert_N.setOnClickListener(onClickListener);
        btn_insert_P.setOnClickListener(onClickListener);
        btn_insert_V.setOnClickListener(onClickListener);
        btn_insert_C.setOnClickListener(onClickListener);
        btn_insert_default.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_export:
                    fileUtils.doExport(mHandler, FileUtils.MODE_ONLY_EXPORT, appInfo.appSourcDir,txt_share_filename.getText().toString());
                    break;
                case R.id.btn_export_share:
                    fileUtils.doExport(mHandler, FileUtils.MODE_EXPORT_SHARE, appInfo.appSourcDir,txt_share_filename.getText().toString());
                    break;

                case R.id.btn_share_insert_divider:
                    insertText(txt_share_filename,"-");
                    break;
                case R.id.btn_share_insert_N:
                    insertText(txt_share_filename,appInfo.appName);
                    break;
                case R.id.btn_share_insert_P:
                    insertText(txt_share_filename,appInfo.packageName);
                    break;
                case R.id.btn_share_insert_V:
                    insertText(txt_share_filename,appInfo.versionName);
                    break;
                case R.id.btn_share_insert_C:
                    insertText(txt_share_filename,String.valueOf(appInfo.versionCode));
                    break;
                case R.id.btn_share_insert_default:
                    insertText(txt_share_filename,appInfo.appName+"-"+appInfo.packageName+"-"+appInfo.versionName);
                    break;
            }
        }
    };
    /**获取EditText光标所在的位置*/
    private int getEditTextCursorIndex(EditText mEditText){
        return mEditText.getSelectionStart();
    }
    /**向EditText指定光标位置插入字符串*/
    private void insertText(EditText mEditText, String mText){
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share_settings) {
            startActivity(new Intent(SystemShareActivity.this, SettingActivity.class));
            return true;
        }
        if(id == R.id.action_share_main)
        {
            startActivity(new Intent(SystemShareActivity.this, MainActivity.class));
            return true;
        }
        if(id == android.R.id.home){
            this.finish(); // back button
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
