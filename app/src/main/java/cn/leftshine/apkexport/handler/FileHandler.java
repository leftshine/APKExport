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
    private int mode;

    public FileHandler(Context context) {
        this.context = context;
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        fileUtils = new FileUtils(context);
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MessageCode.MSG_COPY_START:
                mode = msg.arg1;
                String message = (String)msg.obj;
                showProgressDialog(context,message,mode);
                break;
            case MessageCode.MSG_COPY_COMPLETE:
                closeProgressDialog();
                String shareFilePath = msg.obj.toString();
                mode = msg.arg1;

                if(mode == FileUtils.MODE_EXPORT_SHARE||mode == FileUtils.MODE_EXPORT_RENAME_SHARE) {
                    if(Settings.isShareWithExport()){
                        showToast(context.getString(R.string.tip_export_success) + shareFilePath);
                    }
                    fileUtils.startShare(shareFilePath);
                }else {
                    showToast(context.getString(R.string.tip_export_success) + shareFilePath);
                }
                if(context instanceof SystemShareActivity&&Settings.isExportDerect()){
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

            case MessageCode.MSG_COPY_ERROR:
                String path = (String)msg.obj;
                Toast.makeText(context,context.getString(R.string.tip_copy_failed)+path,Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        super.handleMessage(msg);
    }
    public static void showProgressDialog(Context context, String msg, int mode) {
        progressDialog = new ProgressDialog(context);
        if(Settings.isShareWithExport()||mode == FileUtils.MODE_ONLY_EXPORT||mode == FileUtils.MODE_ONLY_EXPORT_RENAME) {
            progressDialog.setTitle(R.string.exporting);
        }else{
            progressDialog.setTitle(R.string.prepare_for_share);
        }
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        progressDialog.setCancelable(false);
        progressDialog.show();

    }
    public static void setProgressDialogProgress(int i) {
        progressDialog.setProgress(i);
    }
    public static void setSecondaryProgressDialogProgress(int i) {
        progressDialog.setSecondaryProgress(i);
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
