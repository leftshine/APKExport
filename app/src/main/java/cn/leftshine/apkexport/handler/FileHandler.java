package cn.leftshine.apkexport.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.activity.SystemShareActivity;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.MessageCode;
import cn.leftshine.apkexport.utils.Settings;

/**
 * Created by Administrator on 2018/1/30.
 */

public class FileHandler extends Handler {
    private final static String TAG = "FileHandler";
    private Context context;
    private static ProgressDialog progressDialog;
    private static Toast mToast;
    private FileUtils fileUtils;

    public FileHandler(Context context) {
        this.context = context;
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        fileUtils = new FileUtils(context);
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MessageCode.MSG_COPY_START:
                String message = (String)msg.obj;
                showProgressDialog(context,message);
                break;
            case MessageCode.MSG_COPY_COMPLETE:
                closeProgressDialog();
                String shareFilePath = msg.obj.toString();
                showToast("导出完成，文件保存在:"+shareFilePath);
                int mode = msg.arg1;
                if(mode == FileUtils.MODE_EXPORT_SHARE||mode == FileUtils.MODE_EXPORT_RENAME_SHARE) {
                    fileUtils.startShare(shareFilePath);
                }
                if(Settings.isExportDerect()){
                    //System.exit(0);
                    SystemShareActivity systemShareActivity = (SystemShareActivity)context;
                    systemShareActivity.finish();
                }
                break;
            case MessageCode.MSG_COPY_PROGRESS:
                Long lprogress = (Long) msg.obj;
                Log.i(TAG, "MSG_COPY_PROGRESS: "+lprogress.intValue());
                setProgressDialogProgress(lprogress.intValue());
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }
    public static void showProgressDialog(Context context, String msg) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.exporting);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public static void setProgressDialogProgress(int i) {
        progressDialog.setProgress(i);
    }
    public static void closeProgressDialog() {
        progressDialog.dismiss();
    }
    public static void showToast(String toastText) {
        // TODO Auto-generated method stub

        mToast.setText(toastText);
       /* if (code == 1) {
            mToast.setText(getActivity().getResources().getString(R.string.app_uninstall_success));
        } else {
            mToast.setText(getActivity().getResources().getString(R.string.app_uninstall_failed));
        }*/
        mToast.show();
    }


}
