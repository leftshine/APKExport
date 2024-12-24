package cn.leftshine.apkexport.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.handler.FileHandler;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.view.ClearEditText;

import static cn.leftshine.apkexport.utils.PermisionUtils.REQUEST_EXTERNAL_STORAGE;
import static cn.leftshine.apkexport.utils.PermisionUtils.isNeverAskStoragePermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.requestStoragePermissions;
import static cn.leftshine.apkexport.utils.ToolUtils.DEFAULT_COPY_DATA;

public class SystemShareActivity extends AppCompatActivity {

    private Context mContext;
    private AppInfo appInfo;
    private final static String TAG = "SystemShareActivity";
    private ImageView share_appInfo_icon;
    private TextView share_appInfo_appName,share_appInfo_packageName,share_appInfo_appSize,share_appInfo_appVersionName,share_appInfo_appVersionCode,txt_tip_direct_share;
    private ClearEditText txt_share_filename;
    private Button btn_export,btn_export_share;
    private Button btn_insert_divider,btn_insert_N, btn_insert_P, btn_insert_V, btn_insert_C, btn_insert_default;
    private RelativeLayout share_appInfo, ly_export_filename;
    private LinearLayout ly_insert;
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
        getAppInfo();
        setContentView(R.layout.activity_system_share);
        bindView();
        initView();
        if (isDirectShare) {
            ly_export_filename.setVisibility(View.INVISIBLE);
            ly_insert.setVisibility(View.INVISIBLE);
            btn_export.setVisibility(View.GONE);
            btn_export_share.setVisibility(View.GONE);
            txt_tip_direct_share.setVisibility(View.VISIBLE);
            shareDirect();
        }
        //Log.i(TAG, "getCallingPackage: "+getCallingPackage());
    }

    private void shareDirect() {
        String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",appInfo.appName).replace("#P",appInfo.packageName).replace("#V",appInfo.versionName).replace("#C",String.valueOf(appInfo.versionCode))+".apk";
        fileUtils.doExport(mHandler, FileUtils.MODE_EXPORT_SHARE, appInfo.appSourcDir,customFileName);
    }


    private void bindView() {
        share_appInfo = findViewById(R.id.share_appInfo);
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
        ly_insert = findViewById(R.id.ly_insert);
        ly_export_filename = findViewById(R.id.ly_export_filename);
        txt_tip_direct_share = findViewById(R.id.txt_tip_direct_share);
    }
    private void initView() {
        share_appInfo_icon.setImageDrawable(appInfo.appIcon);
        share_appInfo_appName.setText(appInfo.appName);
        share_appInfo_packageName.setText(appInfo.packageName);
        share_appInfo_appSize.setText(FileUtils.FormatFileSize(appInfo.appSize));
        share_appInfo_appVersionName.setText(appInfo.versionName);
        share_appInfo_appVersionCode.setText(String.valueOf(appInfo.versionCode));
        String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",appInfo.appName).replace("#P",appInfo.packageName).replace("#V",appInfo.versionName).replace("#C",String.valueOf(appInfo.versionCode))+".apk";
        txt_share_filename.setText(customFileName);

        share_appInfo.setOnLongClickListener(onLongClickListener);
        btn_export.setOnClickListener(onClickListener);
        btn_export_share.setOnClickListener(onClickListener);
        btn_insert_divider.setOnClickListener(onClickListener);
        btn_insert_N.setOnClickListener(onClickListener);
        btn_insert_P.setOnClickListener(onClickListener);
        btn_insert_V.setOnClickListener(onClickListener);
        btn_insert_C.setOnClickListener(onClickListener);
        btn_insert_default.setOnClickListener(onClickListener);
        txt_tip_direct_share.setVisibility(View.GONE);
    }
    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.share_appInfo:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.choose_next_action)
                            .setItems(R.array.copy_info_actions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
								/*
								<item>复制应用名称</item>
        						<item>复制包名</item>
        						<item>复制版本号</item>
        						<item>复制内部版本号</item>
								 */
                                    //ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    //ClipData clipData = ClipData.newPlainText(null, "");
                                    String copy_str = DEFAULT_COPY_DATA;
                                    switch (i) {
                                        case 0:
                                            copy_str = appInfo.getAppName();
                                            //clipData = ClipData.newPlainText(null, info.getAppName());
                                            break;
                                        case 1:
                                            copy_str = appInfo.getPackageName();
                                            //clipData = ClipData.newPlainText(null, info.getPackageName());
                                            break;
                                        case 2:
                                            copy_str = appInfo.getVersionName();
                                            //clipData = ClipData.newPlainText(null, info.getVersionName());
                                            break;
                                        case 3:
                                            copy_str = String.valueOf(appInfo.getVersionCode());
                                            break;
                                    }
                                    ClipData clipData = ClipData.newPlainText(null, copy_str);
                                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (cm != null) {
                                        cm.setPrimaryClip(clipData);
                                        Toast.makeText(mContext,mContext.getString(R.string.copy_success, copy_str),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .show();
                    break;
            }
            return false;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_export:
                    String filename = txt_share_filename.getText().toString();
                    if(!filename.endsWith(".apk"))
                        filename = filename+".apk";
                    fileUtils.doExport(mHandler, FileUtils.MODE_ONLY_EXPORT, appInfo.appSourcDir,filename);
                    break;
                case R.id.btn_export_share:
                    String filename2 = txt_share_filename.getText().toString();
                    if(!filename2.endsWith(".apk"))
                        filename2 = filename2+".apk";
                    fileUtils.doExport(mHandler, FileUtils.MODE_EXPORT_SHARE, appInfo.appSourcDir,filename2);
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
                    insertText(txt_share_filename,appInfo.appName+"-"+appInfo.packageName+"-"+appInfo.versionName+".apk");
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
            callingPackageName = (reflectGetReferrer() == null || "".equals(reflectGetReferrer())) ? getCallingPackage() : reflectGetReferrer();
        }catch (Exception e){
            e.printStackTrace();
            callingPackageName = null;
        }
        Log.i(TAG, "callingPackageName: " + callingPackageName);
        if(callingPackageName == null || "".equals(callingPackageName)){
            Toast.makeText(mContext,"运气不好，获取应用信息失败",Toast.LENGTH_SHORT).show();
            callingPackageName = this.getPackageName();
        }
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
        return null;
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

    //运行时权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final Activity activity = this;
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                Toast.makeText(this,R.string.storage_permission_obtain_toast,Toast.LENGTH_SHORT).show();
            } else {
                if(isNeverAskStoragePermissions(activity)){
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_copy_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_go_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //isOnPermission = true;
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //finish();
                                }
                            }).show();
                }else {
                    //权限被拒绝
                    //Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_copy_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestStoragePermissions(activity);
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //finish();
                                }
                            })
                            .show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
