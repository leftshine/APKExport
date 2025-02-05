package cn.leftshine.apkexport.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.leftshine.apkexport.BuildConfig;
import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.adapter.AppInfoAdapter;
import cn.leftshine.apkexport.adapter.ContentPagerAdapter;
import cn.leftshine.apkexport.fragment.AppFragment;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.GlobalData;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.utils.ToolUtils;
import cn.leftshine.apkexport.view.ZoomOutPageTransformer;

import static cn.leftshine.apkexport.utils.PermisionUtils.REQUEST_CODE_GET_INSTALLED_APPS;
import static cn.leftshine.apkexport.utils.PermisionUtils.REQUEST_CODE_GET_INSTALLED_APPS_CURRENT;
import static cn.leftshine.apkexport.utils.PermisionUtils.REQUEST_EXTERNAL_STORAGE;
import static cn.leftshine.apkexport.utils.PermisionUtils.isNeverAskInstalledPermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.isNeverAskStoragePermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.requestInstalledAppsPermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.requestStoragePermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.verifyInstalledAppsPermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.verifyStoragePermissions;

public class MainActivity extends BaseActivity {

    private static final boolean DBG = Settings.isDebug();
    private static final String TAG = "MainActivity";
    private static final int STARTED = 0;
    private static final int FINISHED = 1;
    private static final int CLEAN_CACHE_DIR = 0;
    private static final int CLEAN_EXPORT_DIR = 1;
    private static final int REQUEST_CODE_SETTING = 101;

    private static final String BUNDLE_KEY_FRAGMENT_INDEX= "FRAGMENT_INDEX";
    private static final String BUNDLE_KEY_MULTIPLE_MODE = "MULTIPLE_MODE";
    private static final String BUNDLE_KEY_SELECTED_COUNT= "SELECTED_COUNT";
    private static final String BUNDLE_KEY_ALL_COUNT= "ALL_COUNT";

    AppFragment fragmentUserApp;
    AppFragment fragmentSystemApp;
    AppFragment fragmentLocalApp;

    public FloatingActionButton fab;
    boolean isExitSnackbarShown = false;
    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private ContentPagerAdapter mContentAdapter;
    int sortType = 0;
    private ScanSdFilesReceiver scanReceiver;
    private FileUtils fileUtils;
    private Toolbar toolbar;
    private AppBarLayout ly_app_bar;
    private ObjectAnimator animtor;
    private ActionMode mActionMode;
    private Context mContext;
    private boolean mLastShowLocalApk;
    private String mLastCustomLanguage;
    private boolean mLastFixedToolbar;
    private ArrayList<ArrayList<String>> mRestoreSelectPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(DBG) Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        //verifyStoragePermissions(this);
        fileUtils =new FileUtils(this);
        setContentView(R.layout.activity_main);
        ly_app_bar = findViewById(R.id.ly_app_bar);
        mTabTl = (TabLayout) findViewById(R.id.tl_tab);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mContentVp.setPageTransformer(true, new ZoomOutPageTransformer());
        initContent();
        initTab();
        //FragmentManager fragmentManager = getSupportFragmentManager();
        initToolbar();
        initFloatingActionButton();


        deleteCache();
        //getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*if(Settings.isAutoCleanExportDir()) {
            if(verifyStoragePermissions(this)) {
                //Snackbar.make(fab, deleteCache() ? getString(R.string.auto_clean_success) : getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
                deleteCache(CLEAN_EXPORT_DIR);
            }else{
                Settings.setAutoCleanExportDir(false);
                Toast.makeText(this,R.string.tip_auto_clean_closed,Toast.LENGTH_SHORT).show();
            }
        }
        if(Settings.isAutoCleanCacheDir()){
            deleteCache(CLEAN_CACHE_DIR);
        }*/

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        scanReceiver = new ScanSdFilesReceiver();
        registerReceiver(scanReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        if(DBG) Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(DBG) Log.d(TAG, "onResume: ");
        super.onResume();
        if(Settings.isIsNeedLoad()){
            //mContentAdapter.getCurrentFragment().refresh(true);
            mContentAdapter.getCurrentFragment().loadWaitUI(true,false);
            //allFragmentReload();
        }
        /*
        //神秘代码复制到剪贴板
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(!cm.hasPrimaryClip())
        {
            ClipData clipData = ClipData.newPlainText(null, ToolUtils.DEFAULT_COPY_DATA);
            cm.setPrimaryClip(clipData);
        }
        */
    }

    @Override
    protected void onPause() {
        if(DBG) Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(DBG) Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(DBG) Log.d(TAG, "onDestroy: ");
        if (scanReceiver != null) {
            unregisterReceiver(scanReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Boolean isMultipleMode = GlobalData.isMultipleMode();
        int fragmentIndex = mContentVp.getCurrentItem();
        if(DBG) Log.d(TAG, "onSaveInstanceState: isMultipleMode=" + isMultipleMode
                + ", fragmentIndex=" + fragmentIndex);
        savedInstanceState.putBoolean(BUNDLE_KEY_MULTIPLE_MODE,isMultipleMode);
        if (isMultipleMode){
            savedInstanceState.putInt(BUNDLE_KEY_SELECTED_COUNT, mContentAdapter.getCurrentFragment().getmAdapter().getSelecteditemCount());
            savedInstanceState.putInt(BUNDLE_KEY_ALL_COUNT, mContentAdapter.getCurrentFragment().getmAdapter().getCount());
        }
        savedInstanceState.putInt(BUNDLE_KEY_FRAGMENT_INDEX, fragmentIndex);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(DBG) Log.d(TAG, "onRestoreInstanceState: GlobalData.isMultipleMode()="+GlobalData.isMultipleMode());
        Boolean isMultipleMode = savedInstanceState.getBoolean(BUNDLE_KEY_MULTIPLE_MODE);
        int fragmentIndex = savedInstanceState.getInt(BUNDLE_KEY_FRAGMENT_INDEX);
        if(DBG) Log.d(TAG, "onRestoreInstanceState: isMultipleMode=" + isMultipleMode
                + ", fragmentIndex=" + fragmentIndex);
        mContentVp.setCurrentItem(fragmentIndex);
        if (isMultipleMode && mActionMode == null) {
            switchMultipleMode(true);
            int selectItemCount = savedInstanceState.getInt(BUNDLE_KEY_SELECTED_COUNT);
            int allItemCount = savedInstanceState.getInt(BUNDLE_KEY_ALL_COUNT);
            updateSelectedCount(selectItemCount, allItemCount);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*private void mediaScan(){
        String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
        String[] mimeTypes = new String[]{"application/vnd.android.package-archive"};
        MediaScannerConnection.scanFile(this,paths, mimeTypes, null);
        *//*MediaScannerConnection.scanFile(this,paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.i(TAG, "onScanCompleted: "+s+uri);
               fragmentLocalApp.refresh(false);
            }
        });*//*

        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }*/

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        if(Settings.isToolbarFixed()) {
            params.setScrollFlags(0);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);
        try {
            // for update title when change language in time
            String label = getResources().getString(getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
            getSupportActionBar().setTitle(label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTab(){
        mTabTl.setTabMode(TabLayout.MODE_FIXED);
        //mTabTl.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.white));
        //mTabTl.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent(){
        if (!verifyInstalledAppsPermissions((Activity) this)) {
            requestInstalledAppsPermissions((Activity) this, true);
        }

        List<String> tabIndicators = new ArrayList<>();
        List<AppFragment> tabFragments = new ArrayList<>();

        tabIndicators.add(getString(R.string.user_app));
        fragmentUserApp = AppFragment.newInstance(ToolUtils.TYPE_USER);
        tabFragments.add(fragmentUserApp);
        //fragmentUserApp.loadWaitUI(true,false);

        tabIndicators.add(getString(R.string.system_app));
        fragmentSystemApp = AppFragment.newInstance(ToolUtils.TYPE_SYSTEM);
        tabFragments.add(fragmentSystemApp);

        if(Settings.isShowLocalApk()) {
            if(verifyStoragePermissions(this)){
                //已获得权限
                tabIndicators.add(getString(R.string.local_apk));
                fragmentLocalApp = AppFragment.newInstance(ToolUtils.TYPE_LOCAL);
                tabFragments.add(fragmentLocalApp);
                FileUtils.notifyMediaScan();
            }else{
                // 未获得权限
                Settings.setShowLocalApk(false);
                //Toast.makeText(this,R.string.tip_local_apk_closed,Toast.LENGTH_SHORT).show();
                //requestStoragePermissions(this);
            }
        }
        ContentPagerAdapterCallback callback = new ContentPagerAdapterCallback();
        mContentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),tabFragments,tabIndicators, callback);
        mContentVp.setAdapter(mContentAdapter);
        mContentVp.setOffscreenPageLimit(mContentVp.getAdapter().getCount() - 1);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position="+position);
                //mContentVp.setCurrentItem(position);
                AppFragment currentFragment = mContentAdapter.getFragment(position);
                if(currentFragment ==null) return;
                Log.d(TAG, "onPageSelected: GlobalData.isMultipleMode="+GlobalData.isMultipleMode + "mActionMode=" +mActionMode);
                if(GlobalData.isMultipleMode && mActionMode != null) {
                    Log.d(TAG, "onPageSelected: type="+currentFragment.getType());
                    updateSelectedCount(currentFragment.getmAdapter().getSelecteditemCount(), currentFragment.getmAdapter().getCount());
                    mActionMode.invalidate();
                }
                //currentFragment.getmAdapter().notifyDataSetChanged();
                //currentFragment.load(true,false);
                //currentFragment.loadWaitUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initFloatingActionButton(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!verifyInstalledAppsPermissions((Activity) mContext)) {
                    requestInstalledAppsPermissions((Activity) mContext, false);
                }
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                anim.setDuration(500);
                anim.start();
                //((AppFragment)fragmentManager.findFragmentById(R.id.layout_fragment)).refresh();
                //mContentAdapter.getCurrentFragment().refresh(true);
                mContentAdapter.getCurrentFragment().loadWaitUI(true,true);
                FileUtils.notifyMediaScan();
                Snackbar.make(view, R.string.Refreshing, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            Log.i(TAG, "initFloatingActionButton: no FEATURE_TOUCHSCREEN");
            fab.hide();
        }
    }

    //清除缓存
    private void deleteCache() {
        //Snackbar.make(fab, getString(R.string.auto_clean_running), Snackbar.LENGTH_LONG).show();

        if(Settings.isAutoCleanExportDir()) {
            if(verifyStoragePermissions(this)) {
                //Snackbar.make(fab, deleteCache() ? getString(R.string.auto_clean_success) : getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
                Snackbar.make(fab, getString(R.string.auto_clean_running), Snackbar.LENGTH_LONG).show();
                FileUtils.cleanExportDir(this);
            }else{
                Settings.setAutoCleanExportDir(false);
                Toast.makeText(this,R.string.tip_auto_clean_closed,Toast.LENGTH_SHORT).show();
            }
        }
        if(Settings.isAutoCleanCacheDir()){
            FileUtils.cleanCacheDir(this);
        }

        /*//File mFolder = new File( Environment.getExternalStorageDirectory()+File.separator+"APKExport");
        boolean isDeleteSucecss = false;
        try {
            File mFolder = new File(Settings.getCustomExportPath());
            if (mFolder.exists()) {
                if (mFolder.isDirectory() && mFolder.listFiles() != null) {
                    for (File file : mFolder.listFiles()) {
                        file.delete();
                    }
                    //return true;
                    isDeleteSucecss = true;
                }
            }
        } catch (Exception e) {
            isDeleteSucecss = false;
        }
        final boolean finalIsDeleteSucecss = isDeleteSucecss;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(fab, finalIsDeleteSucecss ? getString(R.string.auto_clean_success) : getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
            }
        });*/



        //return false;
    }

    //运行时权限请求结果
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final Activity activity = this;
        Log.i(TAG, "onRequestPermissionsResult: requestCode=" + requestCode + ", permissions:" + permissions +", grantResults:" + grantResults);
        if (requestCode == REQUEST_CODE_GET_INSTALLED_APPS || requestCode == REQUEST_CODE_GET_INSTALLED_APPS_CURRENT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                //Toast.makeText(this,R.string.storage_permission_obtain_toast,Toast.LENGTH_SHORT).show();
                if (requestCode == REQUEST_CODE_GET_INSTALLED_APPS_CURRENT) {
                    mContentAdapter.getCurrentFragment().loadWaitUI(true, true);
                } else {
                    for (AppFragment fragment : mContentAdapter.getAllFragment()){
                        fragment.loadWaitUI(true, true);
                    }
                }
            } else {
                if(isNeverAskInstalledPermissions(activity)){
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(R.string.installed_permission_dialog_content)
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
                            .setMessage(R.string.installed_permission_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestInstalledAppsPermissions(activity, requestCode == REQUEST_CODE_GET_INSTALLED_APPS);
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
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+resultCode);
        switch (requestCode) {
            case REQUEST_CODE_SETTING:
                if (resultCode == RESULT_OK) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                    if (mLastShowLocalApk != Settings.isShowLocalApk()) {
                        recreate();
                    }
                    if (mLastCustomLanguage !=null && !mLastCustomLanguage.equals(Settings.getLanguage())) {
                        recreate();
                    }
                    if (mLastFixedToolbar != Settings.isToolbarFixed()) {
                        recreate();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void switchMultipleMode(boolean on) {
        Log.d(TAG, "switchMultipleMode: "+on);
        if (on) {
            //进入多选模式
            ActionModeCallbackMultiple callback = new ActionModeCallbackMultiple();
            mActionMode = startSupportActionMode(callback);
        } else {
            //退出多选模式
            Log.d(TAG, "switchMultipleMode: 退出多选模式mActionMode="+mActionMode);
            if(mActionMode != null) {
                mActionMode.finish();
                Log.d(TAG, "switchMultipleMode: mActionMode="+mActionMode);
            }
        }
    }

    private void updateSelectedCount(int count, int all) {
        if (mActionMode!= null) {
            View actionBarView =mActionMode.getCustomView();
            TextView selectedNum = (TextView) actionBarView.findViewById(R.id.selected_num);
            selectedNum.setText(count + "/" +all);
        }
    }

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(GlobalData.isMultipleMode){
                mActionMode.finish();
            }else{
                exit();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
       /* if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Snackbar.make(findViewById(R.id.layout_fragment), "再按一次退出", Snackbar.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }*/
        if(!isExitSnackbarShown) {
            Snackbar.make(fab, getString(R.string.one_more_click_to_exit), Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    isExitSnackbarShown = false;
                    super.onDismissed(snackbar, event);
                }

                @Override
                public void onShown(Snackbar snackbar) {
                    isExitSnackbarShown = true;
                    super.onShown(snackbar);
                }
            }).show();
        }else{
            finish();
            System.exit(0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_no_touchscreen, menu);
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                mContentAdapter.getCurrentFragment().doSearch(queryText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i(TAG, "onOptionsItemSelected: id="+id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivity(new Intent(MainActivity.this, SettingActivity.class));
            mLastShowLocalApk = Settings.isShowLocalApk();
            mLastCustomLanguage = Settings.getLanguage();
            mLastFixedToolbar = Settings.isToolbarFixed();
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTING);
            return true;
        }

        if(id == R.id.action_help)
        {
            Uri uri = Uri.parse("https://leftshine.gitlab.io/apkexport/help/index.html");
            Intent it = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(it);
            return true;
        }

        if(id == R.id.action_about)
        {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        if (id == R.id.action_multi_select) {
            if(GlobalData.isMultipleMode){
                //退出多选模式
                switchMultipleMode(false);
            }else {
                //进入多选模式
                Toast.makeText(mContext, R.string.enter_multiple_mode,Toast.LENGTH_SHORT).show();
                switchMultipleMode(true);
                if (mContentAdapter.getCurrentFragment() !=null && mContentAdapter.getCurrentFragment().getmAdapter() != null) {
                    AppInfoAdapter adapter = mContentAdapter.getCurrentFragment().getmAdapter();
                    updateSelectedCount(adapter.getSelecteditemCount(), adapter.getCount());
                }
                for (AppFragment fragment : mContentAdapter.getAllFragment()){
                    fragment.changeMultiSelectMode(true);
                }
            }

            /*if(fragmentLocalApp!=null)
                fragmentLocalApp.changeMultiSelectMode();*/
            return true;
        }
        if(id == R.id.action_sort){
            switch (Settings.getSortType()){
                case "200":
                    sortType = 0;
                    break;
                case "201":
                    sortType = 1;
                    break;
                case "202":
                    sortType = 2;
                    break;
                case "203":
                    sortType = 3;
                    break;
                case "204":
                    sortType = 4;
                    break;
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_sort_type)
                    /*.setItems(R.array.sort_type_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sortType= i;
                        }
                    })*/
                    .setSingleChoiceItems(R.array.sort_type_name, sortType, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sortType = i;
                        }
                    })
                    .setNeutralButton(R.string.cancel,null)
                    .setNegativeButton(R.string.ASC, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "ASC onClick: sortType="+sortType);
                            switch (sortType){
                                case 0:
                                    //按应用名称
                                    Settings.setSortType("200");
                                    break;
                                case 1:
                                    //按包名
                                    Settings.setSortType("201");
                                    break;
                                case 2:
                                    //按大小
                                    Settings.setSortType("202");
                                    break;
                                case 3:
                                    //最近安装
                                    Settings.setSortType("203");
                                    break;
                                case 4:
                                    //最近更新
                                    Settings.setSortType("204");
                                    break;
                            }
                            Settings.setSortOrder("300");
                            //mContentAdapter.getCurrentFragment().refresh(true);
                            //allFragmentReload();
                            mContentAdapter.getCurrentFragment().loadWaitUI(true,true);
                        }
                    })
                    .setPositiveButton(R.string.DES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "DES onClick: sortType="+sortType);
                            switch (sortType){
                                case 0:
                                    //按应用名称
                                    Settings.setSortType("200");
                                    break;
                                case 1:
                                    //按包名
                                    Settings.setSortType("201");
                                    break;
                                case 2:
                                    //按大小
                                    Settings.setSortType("202");
                                    break;
                                case 3:
                                    //最近安装
                                    Settings.setSortType("203");
                                    break;
                                case 4:
                                    //最近更新
                                    Settings.setSortType("204");
                                    break;
                            }
                            Settings.setSortOrder("301");
                            //mContentAdapter.getCurrentFragment().refresh(true);
                            mContentAdapter.getCurrentFragment().loadWaitUI(true,true);
                            //allFragmentReload();
                        }
                    })
                    .show();
            return true;
        }
        if(id == R.id.action_refresh) {
            mContentAdapter.getCurrentFragment().loadWaitUI(true,true);
            FileUtils.notifyMediaScan();
            Snackbar.make(fab, R.string.Refreshing, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScanSdFilesReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                Log.i(TAG, "ACTION_MEDIA_SCANNER_STARTED");
                scanHandler.sendEmptyMessage(STARTED);
            }
            if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                Log.i(TAG, "ACTION_MEDIA_SCANNER_FINISHED");
                scanHandler.sendEmptyMessage(FINISHED);
            }
        }
    }

    private class ContentPagerAdapterCallback implements cn.leftshine.apkexport.adapter.ContentPagerAdapter.Callback {

        @Override
        public void onInstantiateItem(int position, AppFragment appFragment) {
            Log.d(TAG, "onInstantiateItem: "+position);
        }

        @Override
        public void onSetPrimaryItem(int position, AppFragment curFragment, List<AppFragment> allFragments) {
            Log.d(TAG, "onSetPrimaryItem: "+position);
        }
    }

    private class ActionModeCallbackMultiple implements ActionMode.Callback{
        View actionBarView;
        TextView selectedNum;

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            getSupportActionBar().hide();
            GlobalData.setMultipleMode(true);

            if(mContentVp.getCurrentItem() == 2){
                menu.findItem(R.id.multi_export).setVisible(false);
            }else{
                menu.findItem(R.id.multi_export).setVisible(true);
            }
            return true;
        }


        //退出多选模式时调用
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            Toast.makeText(mContext, R.string.exit_multiple_mode,Toast.LENGTH_SHORT).show();
            GlobalData.setMultipleMode(false);
            for (AppFragment fragment : mContentAdapter.getAllFragment()){
                fragment.changeMultiSelectMode(false);
            }
            getSupportActionBar().show();
        }

        //进入多选模式调用，初始化ActionBar的菜单和布局
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            ((Activity)mContext).getMenuInflater().inflate(R.menu.menu_multiple_mode, menu);
            if(actionBarView == null) {
                actionBarView = LayoutInflater.from(mContext).inflate(R.layout.actionbar_view_multiple, null);
                selectedNum = (TextView)actionBarView.findViewById(R.id.selected_num);
            }
            mode.setCustomView(actionBarView);
            GlobalData.setActionmode(mode);
            return true;
        }

        //ActionBar上的菜单项被点击时调用
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            AppInfoAdapter adapter = mContentAdapter.getCurrentFragment().getmAdapter();
            switch(item.getItemId()) {
                case R.id.multi_export:
                    adapter.multiExport(0);
                    break;
                case R.id.multi_share:
                    if(ToolUtils.TYPE_LOCAL==adapter.getmType()) {
                        adapter.multiShare();
                    }else {
                        adapter.multiExport(1);
                    }
                    break;
                case R.id.select_all:
                    if (adapter.getSelecteditemCount() == adapter.getCount()) {
                        adapter.unSelectAll();
                    } else {
                        adapter.selectAll();
                    }
                    break;
                case R.id.unselect_all:
                    adapter.unSelectAll();
                    break;
                case R.id.exit_multi_mode:
                    mode.finish();
                    break;
            }
            return true;
        }


    }

    private Handler scanHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STARTED:
                    Log.i(TAG, "media scan start");
                    break;
                case FINISHED:
                    Log.i(TAG, "media scan finished");
                    //fragmentLocalApp.refresh(false);
                    AppFragment currentFragment = mContentAdapter.getCurrentFragment();
                    if(currentFragment.getmAdapter().getmType() == ToolUtils.TYPE_LOCAL);
                        currentFragment.loadWaitUI(false,true);
                default:
                    break;
            }
        }
    };
    public int getAppBarHeight(){
        return ly_app_bar.getHeight();
    }
    /**
     * ToolBar显示隐藏动画
     * @param direction
     */
    public void toobarAnim(int direction) {
        //开始新的动画之前要先取消以前的动画
        /*if (animtor != null && animtor.isRunning()) {
            animtor.cancel();
        }
        //toolbar.getTranslationY()获取的是Toolbar距离自己顶部的距离
        float translationY=ly_app_bar.getTranslationY();*/
        if (direction == 0) {
            //animtor = ObjectAnimator.ofFloat(ly_app_bar, "translationY", translationY, 0);
            //fab.show();
            ViewCompat.animate(fab).translationY(0).setInterpolator(new DecelerateInterpolator(3));

        } else if (direction == 1) {
            //animtor = ObjectAnimator.ofFloat(ly_app_bar, "translationY", translationY, -ly_app_bar.getHeight());
            //fab.hide();
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            ViewCompat.animate(fab).translationY(fab.getHeight() + layoutParams.bottomMargin + layoutParams.topMargin).setInterpolator(new AccelerateInterpolator(3));
        }
        //animtor.start();
    }
}
