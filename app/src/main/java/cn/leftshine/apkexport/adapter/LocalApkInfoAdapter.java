package cn.leftshine.apkexport.adapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.utils.ToolUtils;

import static cn.leftshine.apkexport.utils.FileUtils.MODE_EXPORT_RENAME_SHARE;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_EXPORT_SHARE;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_LOCAL_DELETE;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_LOCAL_INSTALL;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_LOCAL_RENAME;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_LOCAL_SHARE;
import static cn.leftshine.apkexport.utils.FileUtils.MODE_ONLY_EXPORT;

/**
 * Created by Administrator on 2018/2/7.
 */

public class LocalApkInfoAdapter extends AppInfoAdapter {
    private static final String TAG = "LocalApkInfoAdapter";

    public LocalApkInfoAdapter(Context context, List<AppInfo> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.local_apk_infolist_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo info = (AppInfo) getItem(position);
        holder.LocalApkIcon.setImageDrawable(info.getAppIcon());
        holder.LocalApkName.setText(info.getAppName());
        holder.LocalApkSize.setText(ToolUtils.getDataSize(info.getAppSize()));
        //holder.LocalApkdate.setText();
        holder.LocalApkitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "LocalApkitem onClick: ");
                new AlertDialog.Builder(context)
                        .setTitle(R.string.choose_next_action)
                        .setItems(R.array.localAPK_actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /*<item>Share</item>
                                <item>Install</item>
                                <item>Rename</item>
                                <item>Delete</item>*/
                                int mode = i + MODE_LOCAL_SHARE;
                                switch (i){
                                    case 0:
                                        mode = MODE_LOCAL_SHARE;
                                        break;
                                    case 1:
                                        mode = MODE_LOCAL_INSTALL;
                                        break;
                                    case 2:
                                        mode = MODE_LOCAL_RENAME;
                                        break;
                                    case 3:
                                        mode = MODE_LOCAL_DELETE;
                                        break;
                                }
                                FileUtils.doLocalApk(context,mHandler,mode,info);
                            }
                        })
                        .show();
            }
        });
        holder.LocalApkitem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String long_click_action = Settings.getLongPressAction();
                Log.i(TAG, "onLongClick: long_click_action="+long_click_action);
                if (long_click_action.equals("103")) {
                    //复制应用信息
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.choose_next_action)
                            .setItems(R.array.copy_info_actions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
								/*
								<item>复制应用名称</item>
        						<item>复制包名</item>
        						<item>复制版本号</item>
								 */
                                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText(null, "");
                                    switch (i) {
                                        case 0:
                                            clipData = ClipData.newPlainText(null, info.getAppName());
                                            break;
                                        case 1:
                                            clipData = ClipData.newPlainText(null, info.getPackageName());
                                            break;
                                        case 2:
                                            clipData = ClipData.newPlainText(null, info.getVersionName());
                                            break;
                                    }
                                    cm.setPrimaryClip(clipData);
                                }
                            })
                            .show();
                }else{
                    //导出操作
                    int mode = MODE_ONLY_EXPORT;
                    switch (long_click_action){
                        case "100":
                            mode = MODE_ONLY_EXPORT;
                            break;
                        case "101":
                            mode = MODE_EXPORT_SHARE;
                            break;
                        case "102":
                            mode = MODE_EXPORT_RENAME_SHARE;
                            break;
                    }
                    FileUtils.doExport(context,mHandler,mode,info);
                }
                //FileUtils.copyInfo(context,mHandler,info);
                return true;
            }
        });
        return convertView;
    }
    public class ViewHolder {
        private LinearLayout LocalApkitem;
        private ImageView LocalApkIcon;
        private TextView LocalApkName,LocalApkSize,LocalApkdate;

        public ViewHolder(View view) {
            LocalApkitem = (LinearLayout)view.findViewById(R.id.LocalApkInfo_item);
            LocalApkIcon = (ImageView) view.findViewById(R.id.LocalApkInfo_icon);
            LocalApkName = (TextView) view.findViewById(R.id.LocalApkInfo_appName);
            LocalApkSize = (TextView) view.findViewById(R.id.LocalApkInfo_appSize);
            LocalApkdate = (TextView)view.findViewById(R.id.LocalApkInfo_date);
        }
    }
}
