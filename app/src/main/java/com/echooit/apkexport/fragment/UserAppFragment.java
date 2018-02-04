package com.echooit.apkexport.fragment;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
import android.widget.Toast;

import com.echooit.apkexport.R;
import com.echooit.apkexport.adapter.AppInfoAdapter;
import com.echooit.apkexport.utils.AppInfo;
import com.echooit.apkexport.utils.MessageCode;
import com.echooit.apkexport.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;

//import android.support.v4.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserAppFragment extends Fragment {
    private static final String TAG = "UserAppFragment";

    private View root = null;
    private Toast mToast = null;
    private TextView mAppStatus;

    private ListView mAppListView;

    private PackageManager packageManager;
    private ActivityManager mActivityManager = null;
    private AppInfoAdapter mAdapter;
    private static List<AppInfo> mLists = new ArrayList<AppInfo>();
    private HandlerThread mThread;
    private Handler mThreadHanlder;
    private AppReceiver mAppReceiver;
    //private SearchView searchBox;
    private RelativeLayout cloud;
    private int type = ToolUtils.TYPE_USER;
    boolean isReady = false;

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MessageCode.MSG_LOAD_START:
                    showLoadUI();
                    ToolUtils.getApp(UserAppFragment.this,mHandler, type);
                    break;
                case MessageCode.MSG_GET_APP:
                    /*if (View.GONE == mAppListView.getVisibility()) {
                        mAppStatus.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                    AppInfo info = (AppInfo) msg.obj;
                    //mLists.add(info);
                    mAdapter.setDataList(mLists);
                    mAdapter.add(info);
                    mAdapter.notifyDataSetChanged();*/
                    break;
                case MessageCode.MSG_INSTALLED_APP_DETAILS:
                    mAppListView.setVisibility(View.GONE);
                    mAppStatus.setVisibility(View.VISIBLE);
                    break;
                case MessageCode.MSG_PACKAGE_ADDED:
                    if (isAdded() && isResumed()) {
                        mLists.clear();
                        ToolUtils.getApp(UserAppFragment.this,mHandler,type);
                    }
                    break;
                case MessageCode.MSG_GET_APP_COMPLETED:
                    Log.i(TAG, "MSG_GET_APP_COMPLETED ");
                    if (View.GONE == mAppListView.getVisibility()) {
                        mAppStatus.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                    hideLoadUI();
                    if(!mAppListView.isEnabled())
                        mAppListView.setEnabled(true);
                    List<AppInfo> appInfoList = (List<AppInfo>)msg.obj;
                    mLists.clear();
                    mLists.addAll(appInfoList);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
/*    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            List<AppInfo> list = ToolUtils.getApp(UserAppFragment.this,mHandler, type);
            if (list.size() <= 0) {
                mHandler.sendEmptyMessage(MessageCode.MSG_INSTALLED_APP_DETAILS);
            }
        }
    };*/

    public static UserAppFragment newInstance(int type){
        Bundle arguments = new Bundle();
        arguments.putInt(ToolUtils.TYPE, type);
        UserAppFragment userAppFragment = new UserAppFragment();
        userAppFragment.setArguments(arguments);
        return userAppFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        initView(root);
        initData();
        //refresh();
        isReady = true;
        return root;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        packageManager = getActivity().getPackageManager();
        mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        type = getArguments().getInt(ToolUtils.TYPE);
        Log.i(TAG, "onCreate: type="+type);
       /* mThread = new HandlerThread("scan_app");
        mThread.start();
        mThreadHanlder = new Handler(mThread.getLooper());*/

        registerAppChangedReceiver();

    }
    private void initView(View root) {
        cloud = root.findViewById(R.id.cloud);
        mAppStatus = (TextView) root.findViewById(R.id.have_app);
        mAppListView = (ListView) root.findViewById(R.id.app_listView);
        mAppListView.setTextFilterEnabled(true);
    }


    private void initData() {
        mAdapter = new AppInfoAdapter(getActivity(), mLists);
        mAppListView.setAdapter(mAdapter);
        //mAppListView.requestFocus();
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
        mThread.quit();
        super.onDestroy();
    }

    public void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    }
    public void doSearch(String queryText) {
        mAdapter.doSearch(queryText);
    }

    private void showLoadUI() {
        cloud.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,0.7f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        cloud.startAnimation(alphaAnimation);
        mAppListView.setEnabled(false);
    }

    private void hideLoadUI() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.7f,0);
        alphaAnimation.setDuration(500);
        //alphaAnimation.setFillAfter(true);
        cloud.startAnimation(alphaAnimation);
        cloud.setVisibility(View.GONE);
        mAppListView.setEnabled(false);
    }

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
