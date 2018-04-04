package cn.leftshine.apkexport.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.adapter.ContentPagerAdapter;
import cn.leftshine.apkexport.fragment.AppFragment;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.utils.ToolUtils;
import cn.leftshine.apkexport.view.ZoomOutPageTransformer;

import static cn.leftshine.apkexport.utils.PermisionUtils.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int STARTED = 0;
    private static final int FINISHED = 1;
    AppFragment fragmentUserApp;
    AppFragment fragmentSystemApp;
    public AppFragment fragmentLocalApp;
    AppFragment currentFragment;
    public FloatingActionButton fab;
    boolean isExitSnackbarShown = false;
    private TabLayout mTabTl;
    private ViewPager mContentVp;

    private List<String> tabIndicators;
    private List<AppFragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
    int sortType = 0;
    private ScanSdFilesReceiver scanReceiver;
    private FileUtils fileUtils;
    private Toolbar toolbar;
    private AppBarLayout ly_app_bar;
    private ObjectAnimator animtor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                anim.setDuration(500);
                anim.start();
                //((AppFragment)fragmentManager.findFragmentById(R.id.layout_fragment)).refresh();
                //currentFragment.refresh(true);
                currentFragment.loadWaitUI(true,true);
                FileUtils.notifyMediaScan();
                Snackbar.make(view, R.string.Refreshing, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        //getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(Settings.isAutoClean()) {
            if(verifyStoragePermissions(this)) {
                //Snackbar.make(fab, deleteCache() ? getString(R.string.auto_clean_success) : getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
                deleteCache();
            }else{
                Settings.setAutoClean(false);
                Toast.makeText(this,R.string.tip_auto_clean_closed,Toast.LENGTH_SHORT).show();
            }
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        scanReceiver = new ScanSdFilesReceiver();
        registerReceiver(scanReceiver, intentFilter);
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

    private void initTab(){
        mTabTl.setTabMode(TabLayout.MODE_FIXED);
        //mTabTl.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.white));
        //mTabTl.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent(){
        tabIndicators = new ArrayList<>();
        tabFragments = new ArrayList<>();

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
                Toast.makeText(this,R.string.tip_local_apk_closed,Toast.LENGTH_SHORT).show();
                //requestStoragePermissions(this);
            }
        }

        currentFragment = fragmentUserApp;

        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),tabFragments,tabIndicators);
        mContentVp.setAdapter(contentAdapter);
        mContentVp.setOffscreenPageLimit(2);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mContentVp.setCurrentItem(position);
                currentFragment = (AppFragment)contentAdapter.getItem(position);
                //currentFragment.load(true,false);
                //currentFragment.loadWaitUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //清除缓存
    private void deleteCache() {
        //File mFolder = new File( Environment.getExternalStorageDirectory()+File.separator+"APKExport");
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
        }catch (Exception e){
            isDeleteSucecss = false;
        }
        final boolean finalIsDeleteSucecss = isDeleteSucecss;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(fab, finalIsDeleteSucecss ? getString(R.string.auto_clean_success) : getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
            }
        });
        //return false;
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
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
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
    protected void onResume() {
        super.onResume();
        if(Settings.isIsNeedLoad()){
            //currentFragment.refresh(true);
            currentFragment.loadWaitUI(true,false);
            //allFragmentReload();
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(!cm.hasPrimaryClip())
        {
            ClipData clipData = ClipData.newPlainText(null, ToolUtils.DEFAULT_COPY_DATA);
            cm.setPrimaryClip(clipData);
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(scanReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                currentFragment.doSearch(queryText);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }

        if(id == R.id.action_help)
        {
            Uri uri = Uri.parse("http://leftshine.gitee.io/apkexport/pages/help/index.html");
            Intent it = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(it);
            return true;
        }

        if(id == R.id.action_about)
        {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
                            //currentFragment.refresh(true);
                            //allFragmentReload();
                            currentFragment.loadWaitUI(true,false);
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
                            //currentFragment.refresh(true);
                            currentFragment.loadWaitUI(true,false);
                            //allFragmentReload();
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*    private void allFragmentReload() {
        for(AppFragment fragment:tabFragments){
            fragment.loadWaitUI(true,false);
        }

    }*/

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
                    if(currentFragment == fragmentLocalApp)
                        fragmentLocalApp.loadWaitUI(false,true);
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
