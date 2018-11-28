package cn.leftshine.apkexport.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.activity.SystemShareActivity;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.MessageCode;
import cn.leftshine.apkexport.utils.Settings;

/**
 * Created by Administrator on 2018/1/30.
 */

public class MultiFileHandler extends Handler {
    private final static String TAG = "FileHandler";
    private Context context;
    private static ProgressDialog progressDialog;
    private static Toast mToast;
    private FileUtils fileUtils;
    private static int  multiTotal;
    private static int multicur;
    private static int thenShare;
    private  ArrayList<Uri>  shareFiles =new ArrayList<Uri>();
    private  ArrayList<Uri>  shareFilesForO=new ArrayList<Uri>();

    public MultiFileHandler(Context context) {
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
                shareFiles.clear();
                String message = (String)msg.obj;
                multicur=0;
                multiTotal = msg.arg1;
                thenShare = msg.arg2;
                showProgressDialog(context,message,multiTotal);
                break;
            case MessageCode.MSG_COPY_COMPLETE:
                multicur++;
                int multiProgress = multicur;
                setProgressDialogProgress(multiProgress);
                String shareFilePath = msg.obj.toString();
                shareFiles.add(Uri.fromFile(new File(shareFilePath)));
                Uri contentUri = FileProvider.getUriForFile(
                        context
                        , "cn.leftshine.apkexport.fileprovider"
                        , new File(shareFilePath));
                shareFilesForO.add(contentUri);
                if(multicur==multiTotal&&multiTotal>0){
                    closeProgressDialog();
                    if(Settings.isShareWithExport()) {
                        showToast(context.getString(R.string.tip_multi_export_success) + Settings.getCustomExportPath());
                    }
                    //showToast("批量导出完成，文件保存在:"+Settings.getCustomExportPath());
                    if(thenShare == 1) {
                        fileUtils.startMultiShare(shareFiles,shareFilesForO);
                    }
                    shareFiles.clear();
                    shareFilesForO.clear();
                    if(context instanceof SystemShareActivity&&Settings.isExportDerect()){
                        //System.exit(0);
                        SystemShareActivity systemShareActivity = (SystemShareActivity)context;
                        systemShareActivity.finish();
                    }
                }


                break;
            /*case MessageCode.MSG_COPY_PROGRESS:
                Long lprogress = (Long) msg.obj;
                Log.i(TAG, "MSG_COPY_PROGRESS: "+lprogress.intValue());
                setProgressDialogProgress(lprogress.intValue());
                break;*/
            default:
                break;
        }
        super.handleMessage(msg);
    }
    public static void showProgressDialog(Context context, String msg, int multiTotal) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.title_multi_action);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        progressDialog.setMax(multiTotal);
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
