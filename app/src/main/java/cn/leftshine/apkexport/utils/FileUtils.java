package cn.leftshine.apkexport.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.view.ClearEditText;

import static cn.leftshine.apkexport.utils.PermisionUtils.requestStoragePermissions;
import static cn.leftshine.apkexport.utils.PermisionUtils.verifyStoragePermissions;

/**
 * Created by Administrator on 2018/1/24.
 */

public class FileUtils {
    public static final int MODE_ONLY_EXPORT = 0;
    public static final int MODE_ONLY_EXPORT_RENAME = 5;
    public static final int MODE_EXPORT_SHARE = 1;
    public static final int MODE_EXPORT_RENAME_SHARE = 2;
    public static final int MODE_MULTI_EXPORT = 3;
    public static final int MODE_MULTI_EXPORT_SHARE = 4;

    public static final int MODE_LOCAL_SHARE = 10;
    public static final int MODE_LOCAL_INSTALL = 11;
    public static final int MODE_LOCAL_RENAME = 12;
    public static final int MODE_LOCAL_DELETE = 13;

    private final static String TAG = "FileUtils";
    private static String mCopyFileName ="Unknown.apk";
    private static Context mContext;
    public FileUtils(Context context) {
        mContext = context;
    }

    //导出操作
    public void doExport(final Handler mHandler, final int mode, final String mCurrentAppPath, String customFileName){
        mCopyFileName = customFileName;
        View dialog_edit = View.inflate(mContext,R.layout.dialog_edit,null);
        final ClearEditText txtFileName = dialog_edit.findViewById(R.id.txt_filename);
        switch (mode){
            case MODE_ONLY_EXPORT:
                //仅导出
                //mCopyFileName = customFileName + ".apk";
                doCopy(mHandler,mCurrentAppPath,mode);
                break;
            case MODE_ONLY_EXPORT_RENAME:
                //重命名导出
                //doRename();
                txtFileName.setText(mCopyFileName);
                txtFileName.setSelection(0,customFileName.length());
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.input_new_name)
                        .setView(dialog_edit)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCopyFileName = txtFileName.getText().toString();
                                doCopy(mHandler,mCurrentAppPath,mode);
                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .show();
                break;
            case MODE_EXPORT_SHARE:
                //导出后分享
                //mCopyFileName = customFileName + ".apk";
                doCopy(mHandler,mCurrentAppPath,mode);
                break;
            case MODE_EXPORT_RENAME_SHARE:
                //导出、重命名后分享
                //doRename();
                txtFileName.setText(mCopyFileName);
                txtFileName.setSelection(0,customFileName.length());
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.input_new_name)
                        .setView(dialog_edit)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCopyFileName = txtFileName.getText().toString();
                                doCopy(mHandler,mCurrentAppPath,mode);
                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .show();
                /*InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtFileName,0);*/
                break;
            case MODE_MULTI_EXPORT:
                doCopy(mHandler,mCurrentAppPath,mode);
                break;
            case MODE_MULTI_EXPORT_SHARE:
                doCopy(mHandler,mCurrentAppPath,mode);
                break;
        }
    }

    private void doCopy(final Handler mHandler, final String mCurrentAppPath, final int mode) {
        //File mFolder = new File( Environment.getExternalStorageDirectory()+File.separator+"APKExport");
        File mFolder = new File(Settings.getCustomExportPath());
        if((mode == MODE_EXPORT_SHARE||mode ==MODE_EXPORT_RENAME_SHARE||mode == MODE_MULTI_EXPORT_SHARE)&&!Settings.isShareWithExport()){
            mFolder = new File (FileUtils.getDiskCacheDir(mContext));
        }
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        final String mCopypath = new File(mFolder.getAbsolutePath(), mCopyFileName).getPath();
        Log.i(TAG, "doCopy: mCopypath="+mCopypath);
        //PermissionChecker.checkPermission(mContext,)
        if(verifyStoragePermissions((Activity) mContext)){
            //已获得权限
            //开启子线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //copyFile(mCurrentAppPath,mCopyFileName);
                    copyFile(mHandler,mCurrentAppPath,mCopypath,mode);
                }
            }).start();
        }else{
            // 未获得权限
            requestStoragePermissions((Activity) mContext);
        }

    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @param mode
     * @return boolean
     */
    public void copyFile(Handler mHandler, String oldPath, String newPath, int mode) {
        Log.i(TAG, "oldPath："+oldPath+"\nnewPath："+newPath);
        try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            Log.i(TAG, "oldFile："+oldFile+"\nnewFile："+newFile);
            if (oldFile.exists()) { //文件存在时
                long totalSize = oldFile.length();
                long copiedSize = 0;
                if (!newFile.exists())
                {
                    newFile.createNewFile();
                }
                FileInputStream fis = new FileInputStream(oldFile);
                FileOutputStream fos = new FileOutputStream(newFile);
                int count;
                if(!(MODE_MULTI_EXPORT==mode||MODE_MULTI_EXPORT_SHARE==mode)) {
                    Log.i(TAG, "copyFile start");
                    Message msgStart = Message.obtain();
                    msgStart.what = MessageCode.MSG_COPY_START;
                    msgStart.obj = newFile.getName();
                    msgStart.arg1 = mode;
                    mHandler.sendMessage(msgStart);
                }

                //文件太大的话，我觉得需要修改
                byte[] buffer = new byte[256 * 1024];
                while ((count = fis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                    copiedSize += count;
                    Message msgProgress = Message.obtain();
                    Log.i(TAG, "copiedSize:totalSize "+copiedSize+":"+totalSize);
                    msgProgress.obj = copiedSize * 100 / totalSize ;//progress的值为0到100，因此得到的百分数要乘以100;
                    msgProgress.what = MessageCode.MSG_COPY_PROGRESS;
                    mHandler.sendMessage(msgProgress);
                }
                fis.close();
                fos.flush();
                fos.close();
                Log.i(TAG, "copyFile complete");
                Message msgComplete = Message.obtain();
                msgComplete.arg1 = mode;
                msgComplete.what = MessageCode.MSG_COPY_COMPLETE;
                msgComplete.obj = newFile;
                mHandler.sendMessage(msgComplete);
            }
        }
        catch (Exception e) {
            Log.e(TAG,"复制文件出错-e:"+e);
            Toast.makeText(mContext,R.string.tip_copy_failed,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startShare(String shareFilePath) {
        Log.i(TAG, "shareFilePath:" + shareFilePath);
        try {
            File shareFile = new File(shareFilePath);
            String fileName = shareFilePath.substring(shareFilePath.lastIndexOf("/") + 1, shareFilePath.length());
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                *//*安卓N开始使用FileProvider API，但原来的file://uri仍然可用
                使用FileProvider API分享到百度云提示空文件（其实是百度云的问题），为了兼用更多设备，设置版本大于安卓O时才使用FileProvider API*//*
                Log.i(TAG, "版本大于 O ，开始使用 fileProvider 进行分享");
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(
                        mContext
                        , "cn.leftshine.apkexport.fileprovider"
                        , shareFile);
                Log.i(TAG, "contentUri："+contentUri);
                shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
            } else {
                Log.i(TAG, "普通分享");
                Uri fileUri = Uri.fromFile(shareFile);
                Log.i(TAG, "fileUri："+fileUri);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            }*/
            Log.i(TAG, "普通分享");
            Uri fileUri = Uri.fromFile(shareFile);
            Log.i(TAG, "fileUri："+fileUri);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            // 指定发送内容的类型 (MIME type)
            shareIntent.setType("application/vnd.android.package-archive");
            //shareIntent.setType("*/*");
            mContext.startActivity(Intent.createChooser(shareIntent,(mContext.getString(R.string.share_to, fileName))));
        }catch (Exception e){
            Toast.makeText(mContext,R.string.tip_share_failed,Toast.LENGTH_SHORT).show();
            Log.e(TAG, "share failed: "+e);
        }
    }
    public void startMultiShare(ArrayList<Uri> shareFiles, ArrayList<Uri> shareFilesForO) {
        Log.i(TAG, "shareFiles:");
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                *//*安卓N开始使用FileProvider API，但原来的file://uri仍然可用
                使用FileProvider API分享到百度云提示空文件（其实是百度云的问题），为了兼用更多设备，设置版本大于安卓O时才使用FileProvider API*//*
                Log.i(TAG, "版本大于 O ，开始使用 fileProvider 进行分享");
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                *//*Uri contentUri = FileProvider.getUriForFile(
                        mContext
                        , "cn.leftshine.apkexport.fileprovider"
                        , shareFile);
                Log.i(TAG, "contentUri："+contentUri);*//*
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,shareFilesForO);
            } else {
                Log.i(TAG, "普通批量分享");
                //shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,shareFiles);//Intent.EXTRA_STREAM同于传输文件流
            }*/
            Log.i(TAG, "普通批量分享");
            //shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,shareFiles);//Intent.EXTRA_STREAM同于传输文件流
            // 指定发送内容的类型 (MIME type)
            //shareIntent.setType("application/vnd.android.package-archive");
            shareIntent.setType("*/*");
            mContext.startActivity(Intent.createChooser(shareIntent,(mContext.getString(R.string.share_to, "多个文件"))));
        }catch (Exception e){
            Toast.makeText(mContext,R.string.tip_share_failed,Toast.LENGTH_SHORT).show();
            Log.e(TAG, "share failed: "+e);
        }
    }

//region
   /* public static void doLocalApk(Context context, Handler mHandler, int mode, AppInfo appInfo) {
        mContext=context;
        //String mCurrentPkgName = appInfo.packageName;
        String mCurrentAppName = appInfo.appName;
        final String mCurrentAppPath = appInfo.appSourcDir;
        //String mCurrentVersionName = appInfo.getVersionName();
        *//*String customFileNameFormat  = Settings.getCustomFileNameFormat();
        String customFileName = customFileNameFormat.replace("#N",mCurrentAppName).replace("#P",mCurrentPkgName).replace("#V",mCurrentVersionName);*//*
        switch (mode){
            case MODE_LOCAL_SHARE:
                //分享
                startShare(mCurrentAppPath);
                break;
            case MODE_LOCAL_INSTALL:
                //安装
                install(mCurrentAppPath,context);
                break;
            case MODE_LOCAL_RENAME:
                //重命名
                rename(mCurrentAppPath);
                break;
            case MODE_LOCAL_DELETE:
                //删除

                break;
        }

    }*/
//endregion
    //region action of local apk
    public  void rename(final String mCurrentAppPath, final OnListDataChangedListener onListDataChangedListener) {
        View view2 = View.inflate(mContext,R.layout.dialog_edit,null);
        final ClearEditText txtFileName = view2.findViewById(R.id.txt_filename);
        final File file = new File(mCurrentAppPath);
        String customFileName = file.getName();
        txtFileName.setText(customFileName);
        txtFileName.setSelection(0,customFileName.lastIndexOf("."));
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.input_new_name)
                .setView(view2)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = txtFileName.getText().toString();
                        File newFile = new File(file.getParent(),newName);
                        file.renameTo(newFile);
                        onListDataChangedListener.OnListDataChanged(newFile.getPath());
                        notifyMediaScan();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }

    public void install(String filePath, Context context) {
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.i(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    mContext
                    , "cn.leftshine.apkexport.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.i(TAG, "普通安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
    public void delete(final String mCurrentAppPath, final OnListDataChangedListener onListDataChangedListener) {

        new AlertDialog.Builder(mContext)
                .setTitle(R.string.confirm_delete)
                //.setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(mCurrentAppPath);
                        file.delete();
                        onListDataChangedListener.OnListDataChanged(null);
                        notifyMediaScan();

                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }
    //endregion action of local apk

    public interface OnListDataChangedListener{
        public void OnListDataChanged(String newPath);
    }

    public static void  notifyMediaScan(){
        Log.i(TAG, "notifyMediaScan");
        String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
        String[] mimeTypes = new String[]{"application/vnd.android.package-archive"};
        MediaScannerConnection.scanFile(mContext,paths, mimeTypes, null);
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}
