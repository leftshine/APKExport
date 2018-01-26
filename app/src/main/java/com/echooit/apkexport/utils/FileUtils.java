package com.echooit.apkexport.utils;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.echooit.apkexport.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018/1/24.
 */

public class FileUtils {
    private final static String TAG = "FileUtils";
    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(Handler mHandler, String oldPath, String newPath) {
        Log.i(TAG, "oldPath："+oldPath+"\nnewPath："+newPath);
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File mFolder = new File( Environment.getExternalStorageDirectory()+File.separator+"APKExport");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            File newFile = new File(mFolder.getAbsolutePath(), newPath);
            Log.i(TAG, "oldfile："+oldfile+"\nnewFile："+newFile);
            if (oldfile.exists()) { //文件存在时
                long totalSize = oldfile.length();
                long copiedSize = 0;
                if (!newFile.exists())
                {
                    newFile.createNewFile();
                }
                FileInputStream fis = new FileInputStream(oldfile);
                FileOutputStream fos = new FileOutputStream(newFile);
                int count;
                Log.i(TAG, "copyFile start");

                //文件太大的话，我觉得需要修改
                byte[] buffer = new byte[256 * 1024];
                while ((count = fis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                    copiedSize += count;
                    Message msg = Message.obtain();
                    Log.i(TAG, "copiedSize:totalSize "+copiedSize+":"+totalSize);
                    msg.obj = copiedSize * 100 / totalSize ;//progress的值为0到100，因此得到的百分数要乘以100;
                    msg.what = MessageCode.MSG_COPY_PROGRESS;
                    mHandler.sendMessage(msg);
                }
                fis.close();
                fos.flush();
                fos.close();
                Log.i(TAG, "copyFile complete");
                Message msg = Message.obtain();
                msg.what = MessageCode.MSG_COPY_COMPLETE;
                msg.obj = newFile;
                mHandler.sendMessage(msg);
            }
        }
        catch (Exception e) {
            Log.e(TAG,"复制文件出错-e:"+e);
            e.printStackTrace();
        }
    }

    public static void startShare(Fragment fragment, String shareFilePath) {
        Log.i(TAG, "shareFilePath:" + shareFilePath);
        try {
            File shareFile = new File(shareFilePath);
            String fileName = shareFilePath.substring(shareFilePath.lastIndexOf("/") + 1, shareFilePath.length());
            Uri fileUri = Uri.fromFile(shareFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,fileUri);
            // 指定发送内容的类型 (MIME type)
            shareIntent.setType("application/vnd.android.package-archive");
            fragment.startActivity(Intent.createChooser(shareIntent,(fragment.getString(R.string.share_to, fileName))));
        }catch (Exception e){
            Log.e(TAG, "share failed: "+e);
        }
    }
}
