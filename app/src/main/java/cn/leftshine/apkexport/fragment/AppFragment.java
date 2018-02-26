package cn.leftshine.apkexport.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.aware.PublishConfig;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.adapter.AppInfoAdapter;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.MessageCode;
import cn.leftshine.apkexport.utils.ToolUtils;

//import android.support.v4.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppFragment extends Fragment {
    private static final String TAG = "AppFragment";

    private View root = null;
    //private Toast mToast = null;
    private TextView mAppStatus;

    private ListView mAppListView;

    //private PackageManager packageManager;
    //private ActivityManager mActivityManager = null;
    private static List<AppInfo> mLists = new ArrayList<AppInfo>();
    //private HandlerThread mThread;
    //private Handler mThreadHanlder;
    private AppReceiver mAppReceiver;
    //private SearchView searchBox;
    private RelativeLayout cloud,ly_list_show;
    private int type = ToolUtils.TYPE_USER;
    boolean isUIReady = false;
    //boolean isLoading = false;
    //public boolean isFirstLoad = true;
    //ToolUtils toolUtils;
    private AppInfoAdapter mAdapter;
    ToolUtils  toolUtilsLoad;
    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                //region
                case MessageCode.MSG_LOAD_START:
                    Log.i(TAG, "MSG_LOAD_START");
                    /*//if(!isLoading) {
                        showLoadUI();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                toolUtils.loadApp(AppFragment.this, mHandler, type);
                            }
                        }).start();
                    //  isLoading=true;
                    //}*/
                    load();
                    break;
                /*
                case MessageCode.MSG_REFRESH_START:
                    Log.i(TAG, "MSG_REFRESH_START");
                    //if(!isLoading) {
                        showLoadUI();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                toolUtils.getApp(AppFragment.this, mHandler, type);
                            }
                        }).start();
                    //    isLoading=true;
                    //}
                    break;
                case MessageCode.MSG_GET_APP:
                    *//*if (View.GONE == mAppListView.getVisibility()) {
                        mAppStatus.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                    AppInfo info = (AppInfo) msg.obj;
                    //mLists.add(info);
                    mAdapter.setDataList(mLists);
                    mAdapter.add(info);
                    mAdapter.notifyDataSetChanged();*//*
                    break;*/
                //endregion
                case MessageCode.MSG_INSTALLED_APP_DETAILS:
                    mAppListView.setVisibility(View.GONE);
                    mAppStatus.setVisibility(View.VISIBLE);
                    break;
               /* case MessageCode.MSG_PACKAGE_ADDED:
                    if (isAdded() && isResumed()) {
                        //if(!isLoading) {
                            showLoadUI();
                            //mLists.clear();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    toolUtils.getApp(mHandler, type);
                                }
                            }).start();
                        //   isLoading = true;
                        //}
                    }
                    break;*/
                case MessageCode.MSG_GET_APP_COMPLETED:
                    Log.i(TAG, "MSG_GET_APP_COMPLETED ");
                    hideLoadUI();
                    //isLoading = false;
                    //isFirstLoad = false;
                    if (View.GONE == mAppListView.getVisibility()) {
                        mAppStatus.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                    if(!mAppListView.isEnabled())
                        mAppListView.setEnabled(true);
                    List<AppInfo> appInfoList = (List<AppInfo>)msg.obj;
                    mLists.clear();
                    mLists.addAll(appInfoList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MessageCode.MSG_SHOW_LOAD_UI:
                    showLoadUI();
                    break;
                default:
                    break;
            }
        }
    };
       //region
/*    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            List<AppInfo> list = ToolUtils.getApp(AppFragment.this,mHandler, type);
            if (list.size() <= 0) {
                mHandler.sendEmptyMessage(MessageCode.MSG_INSTALLED_APP_DETAILS);
            }
        }
    };*/
//endregion
    public static AppFragment newInstance(int type){
        Bundle arguments = new Bundle();
        arguments.putInt(ToolUtils.TYPE, type);
        AppFragment appFragment = new AppFragment();
        appFragment.setArguments(arguments);
        return appFragment;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        type = getArguments().getInt(ToolUtils.TYPE);
        Log.i(TAG, "onCreate: type="+type);
        //toolUtils = new ToolUtils(getActivity());
        toolUtilsLoad = new ToolUtils(getActivity());
        registerAppChangedReceiver();
        //initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        initView(root);
        //initData();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initData();
        isUIReady = true;
    }

    private void initView(View root) {
        cloud = root.findViewById(R.id.cloud);
        ly_list_show = root.findViewById(R.id.ly_list_show);
        mAppStatus = (TextView) root.findViewById(R.id.have_app);
        mAppListView = (ListView) root.findViewById(R.id.app_listView);
        mAppListView.setTextFilterEnabled(true);
        mAdapter = new AppInfoAdapter(getActivity(), mLists,type);
        mAppListView.setAdapter(mAdapter);
    }
    private void initData() {
        load();
    }

    private void registerAppChangedReceiver() {
        if (null != mAppReceiver) {
            getActivity().unregisterReceiver(mAppReceiver);
            mAppReceiver = null;
        }
        mAppReceiver = new AppReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(mAppReceiver,filter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mAppReceiver);
        //mThread.quit();
        super.onDestroy();
    }
public  void loadWaitUI(){
    new Thread(new Runnable() {
        @Override
        public void run() {
           /* try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            while (true) {
                if (isUIReady)
                    break;
                Log.i(TAG, "refresh: 等待界面初始化");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg = mHandler.obtainMessage();
            msg.what = MessageCode.MSG_LOAD_START;
            mHandler.sendMessage(msg);
        }
    }).start();
}
    public void load() {
        showLoadUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                toolUtilsLoad.loadApp(mHandler, type);
            }
        }).start();
    }
    public void refresh(boolean isShowHideUI){
        if(isShowHideUI) {
            showLoadUI();
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ToolUtils  toolUtilsGet = new ToolUtils(getActivity());
                    toolUtilsGet.getApp(mHandler, type);
                }
            }).start();
        }catch (Exception e){
            Log.e(TAG, "refresh: failed"+e );
        }
    }
//region
    /*public void load() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isReady)
                        break;
                    Log.i(TAG, "refresh: 等待界面初始化");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MessageCode.MSG_LOAD_START;
                mHandler.sendMessage(msg);
            }
        }).start();
    }*/
    /*public void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isReady)
                        break;
                    Log.i(TAG, "refresh: 等待界面初始化");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MessageCode.MSG_REFRESH_START;
                mHandler.sendMessage(msg);
            }
        }).start();

    }*/
    //endregion
    public void doSearch(String queryText) {
        mAdapter.doSearch(queryText);
    }

    private void showLoadUI() {
        ly_list_show.setVisibility(View.INVISIBLE);
        cloud.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1f);
        alphaAnimation.setDuration(200);
        alphaAnimation.setFillAfter(true);
        cloud.startAnimation(alphaAnimation);
        mAppListView.setEnabled(false);
    }
    private void hideLoadUI() {
        if(cloud.getVisibility() == View.VISIBLE) {
            ly_list_show.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0);
            alphaAnimation.setDuration(200);
            //alphaAnimation.setFillAfter(true);
            cloud.startAnimation(alphaAnimation);
            cloud.setVisibility(View.INVISIBLE);
            mAppListView.setEnabled(true);
        }
    }

    /*public void mediaScan(){
        String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
        //String[] paths = new String[]{path};
        String[] mimeTypes = new String[]{"application/vnd.android.package-archive"};
        MediaScannerConnection.scanFile(getActivity(),paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.i(TAG, "onScanCompleted: "+s+uri);
                mediaScanfinish();
            }
        });
    }
    void mediaScanfinish(){
        if(type==ToolUtils.TYPE_LOCAL)
            refresh(false);
    }*/
    class AppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);

            if (TextUtils.equals(action, Intent.ACTION_PACKAGE_ADDED)
                    || TextUtils.equals(action, Intent.ACTION_PACKAGE_REPLACED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (null != mHandler) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = MessageCode.MSG_PACKAGE_ADDED;
                    msg.obj = packageName;
                    mHandler.sendMessage(msg);
                }
            } else {
                Log.d(TAG, "action = " + action);
            }
        }
    }
}
