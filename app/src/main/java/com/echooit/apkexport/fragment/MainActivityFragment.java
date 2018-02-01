package com.echooit.apkexport.fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
public class MainActivityFragment extends Fragment{
    private static final String TAG = "MainActivityFragment";
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
    private SearchView searchBox;
    private RelativeLayout cloud;

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MessageCode.MSG_GET_APP:
                    if (View.GONE == mAppListView.getVisibility()) {
                        mAppStatus.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                    AppInfo info = (AppInfo) msg.obj;
                    //mLists.add(info);
                    mAdapter.setDataList(mLists);
                    mAdapter.add(info);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MessageCode.MSG_INSTALLED_APP_DETAILS:
                    mAppListView.setVisibility(View.GONE);
                    mAppStatus.setVisibility(View.VISIBLE);
                    break;
                case MessageCode.MSG_PACKAGE_ADDED:
                    if (isAdded() && isResumed()) {
                        mLists.clear();
                        ToolUtils.getApp(MainActivityFragment.this,mHandler);
                    }
                    break;
                case MessageCode.MSG_GET_APP_COMPLETED:
                    Log.i(TAG, "MSG_GET_APP_COMPLETED ");
                    hideLoadUI();
                    if(!mAppListView.isEnabled())
                    mAppListView.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            List<AppInfo> list = ToolUtils.getApp(MainActivityFragment.this,mHandler);
            if (list.size() <= 0) {
                mHandler.sendEmptyMessage(1);
            }
        }
    };

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        initView(root);
        initData();
        refresh();
        return root;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        packageManager = getActivity().getPackageManager();
        mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        mThread = new HandlerThread("scan_app");
        mThread.start();
        mThreadHanlder = new Handler(mThread.getLooper());

        registerAppChangedReceiver();

    }
    private void initView(View root) {
        cloud = root.findViewById(R.id.cloud);
        searchBox = (SearchView)root.findViewById(R.id.searchbox);
        mAppStatus = (TextView) root.findViewById(R.id.have_app);
        mAppListView = (ListView) root.findViewById(R.id.app_listView);
        mAppListView.setTextFilterEnabled(true);
        searchBox.setIconifiedByDefault(false);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                if (TextUtils.isEmpty(queryText)) {
                    //mAppListView.clearTextFilter();//搜索文本为空时，清除ListView的过滤
                    Log.i(TAG, "onQueryTextChange: isEmpty");
                    mAdapter.getFilter().filter(null);
                } else {
                    //mAppListView.setFilterText(queryText);//设置过滤关键字
                    //通过Adapter设置可以避免弹出提示区域
                    mAdapter.getFilter().filter(queryText);
                }
                return true;
            }
        });
        //mAppListView.setOnItemClickListener(this);
    }

    private void initData() {
        mAdapter = new AppInfoAdapter(getActivity(), mLists);
        mAppListView.setAdapter(mAdapter);
        mAppListView.requestFocus();
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
        showLoadUI();
        mLists.clear();
        mThreadHanlder.removeCallbacks(mRunnable);
        mThreadHanlder.postDelayed(mRunnable, 500);
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
