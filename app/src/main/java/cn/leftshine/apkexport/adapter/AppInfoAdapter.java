package cn.leftshine.apkexport.adapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.handler.FileHandler;
import cn.leftshine.apkexport.handler.MultiFileHandler;
import cn.leftshine.apkexport.utils.AppInfo;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.GlobalData;
import cn.leftshine.apkexport.utils.MessageCode;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.utils.ToolUtils;

import static cn.leftshine.apkexport.utils.ToolUtils.DEFAULT_COPY_DATA;

public class AppInfoAdapter extends BaseAdapter implements Filterable{

	private static final String TAG = "AppInfoAdapter";
	LayoutInflater mInflater;
	//SortComparator localSortComparator;
	private List<AppInfo> mLists = new ArrayList<AppInfo>();
	private List<AppInfo> fullLists = new ArrayList<AppInfo>();
	private List<AppInfo> selectedItems = new ArrayList<AppInfo>();
	//private CharacterParser characterParser;
	MyFilter mFilter;
	Context mContext;
	Handler mHandler;
	Handler multiFileHandler;
	FileUtils fileUtils;
	int mType;
	//ActionMode actionmode;



	public AppInfoAdapter(Context context, List<AppInfo> list, int type) {
		// TODO Auto-generated constructor stub
		mContext=context;
		mInflater = LayoutInflater.from(mContext);
		//localSortComparator = new SortComparator();
		mLists = list;
		fullLists = mLists;
		//characterParser = CharacterParser.getInstance();

		mType = type;
		mHandler = new FileHandler(mContext);
		multiFileHandler =new MultiFileHandler(mContext);
		fileUtils = new FileUtils(mContext);
	}

	public int getmType() {
		return mType;
	}

	/*	public void setActionmode(ActionMode actionmode) {
            this.actionmode = actionmode;
            actionBarView = actionmode.getCustomView();
            selectedNum = (TextView)actionBarView.findViewById(R.id.selected_num);
        }*/
	public void setDataList(List<AppInfo> list) {
		mLists = list;
		fullLists = mLists;
		notifyDataSetChanged();
	}
	//顺便把选中的item存到selectedItems中
	public int getSelecteditemCount(){
		selectedItems.clear();
		int count=0;
		for(int i = 0; i <getCount(); i++){
			if (((AppInfo)getItem(i)).isSelect()){
				selectedItems.add(((AppInfo)getItem(i)));
				count++;
			}
		}
		return count;
	}
	public void updateSelectedCount() {
		//int selectedCount = listView.getCheckedItemCount();
		int selectedCount = getSelecteditemCount();
		View actionBarView = GlobalData.getActionmode().getCustomView();
		TextView selectedNum = (TextView)actionBarView.findViewById(R.id.selected_num);
		selectedNum.setText(selectedCount + "");
	}
	public void selectAll() {
		for(int i = 0; i < getCount(); i++) {
			((AppInfo)getItem(i)).setSelect(true);
		}
		updateSelectedCount();
		notifyDataSetChanged();
	}
	public void unSelectAll() {
		for(int i = 0; i < getCount(); i++) {
			((AppInfo)getItem(i)).setSelect(false);
		}
		updateSelectedCount();
		notifyDataSetChanged();
	}
	public void multiExport(int thenShare) {
		if(selectedItems.size()>0) {
			Log.i(TAG, "multi_copyFile start");
			Message msgStart = Message.obtain();
			msgStart.what = MessageCode.MSG_COPY_START;
			msgStart.arg1 = selectedItems.size();
			msgStart.arg2 = thenShare;
			int mode;
			if(thenShare==0) {
				mode = FileUtils.MODE_MULTI_EXPORT;
				msgStart.obj = mContext.getString(R.string.exporting);
			}
			else {
				mode = FileUtils.MODE_MULTI_EXPORT_SHARE;
				msgStart.obj = mContext.getString(R.string.prepare_for_share);
			}
			multiFileHandler.sendMessage(msgStart);
			for (int i = 0; i < selectedItems.size(); i++) {
				AppInfo info = selectedItems.get(i);

				String mCurrentPkgName = info.packageName;
				String mCurrentAppName = info.appName;
				String mCurrentAppPath = info.appSourcDir;
				String mCurrentVersionName = info.getVersionName();
				String mCurrentVersionCode = String.valueOf(info.versionCode);
				String customFileNameFormat = Settings.getCustomFileNameFormat();
				String customFileName = customFileNameFormat.replace("#N", mCurrentAppName).replace("#P", mCurrentPkgName).replace("#V", mCurrentVersionName).replace("#C", mCurrentVersionCode) + ".apk";

				fileUtils.doExport(multiFileHandler, mode, mCurrentAppPath, customFileName);

			}
		}
	}
	public void multiShare(){
		if(selectedItems.size()>0) {
			ArrayList<Uri> shareFiles = new ArrayList<Uri>();
			ArrayList<Uri> shareFilesForO = new ArrayList<Uri>();
			for (int i = 0; i < selectedItems.size(); i++) {
				AppInfo info = selectedItems.get(i);
				shareFiles.add(Uri.fromFile(new File(info.appSourcDir)));
			}
			fileUtils.startMultiShare(shareFiles, shareFilesForO);
		}
	}
/*	public void add(AppInfo info) {
		if (info != null) {
			mLists.add(info);
			Collections.sort(mLists, localSortComparator);
			notifyDataSetChanged();
		}

	}*/

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new MyFilter();
		}
		return mFilter;
	}
	 public void doSearch(String queryText){
		 if (TextUtils.isEmpty(queryText)) {
			 //mAppListView.clearTextFilter();//搜索文本为空时，清除ListView的过滤
			 Log.i(TAG, "onQueryTextChange: isEmpty");
			 getFilter().filter(null);
			 //notifyDataSetChanged();
		 } else {
			 //mAppListView.setFilterText(queryText);//设置过滤关键字
			 //通过Adapter设置可以避免弹出提示区域
			 getFilter().filter(queryText);
			 //notifyDataSetChanged();
		 }
	 }



	/*public class SortComparator implements Comparator<Object> {
		public SortComparator() {
		}

		public int compare(Object paramObject1, Object paramObject2) {
			AppInfo localAppInfo1 = (AppInfo) paramObject1;
			AppInfo localAppInfo2 = (AppInfo) paramObject2;
			//return (int) (localAppInfo1.appSize - localAppInfo2.appSize);
			String compare1 = characterParser.getSelling(localAppInfo1.appName).toUpperCase();
			String compare2 = characterParser.getSelling(localAppInfo2.appName).toUpperCase();
			Log.i(TAG, "compare: " + compare1);
			return (compare1.compareTo(compare2));
		}
	}*/

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if(ToolUtils.TYPE_USER == mType || ToolUtils.TYPE_SYSTEM == mType) {
			AppViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.app_infolist_item, null);
				holder = new AppViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (AppViewHolder) convertView.getTag();
			}
			final AppInfo info = (AppInfo) getItem(position);

			holder.mAppIcon.setImageDrawable(info.getAppIcon());
			holder.mPackageName.setText(info.getPackageName());
			holder.mAppName.setText(info.getAppName());
			holder.mAppSize.setText(ToolUtils.getDataSize(info.getAppSize()));
			holder.mAppVersionName.setText(info.getVersionName());
			if(GlobalData.isMultipleMode){
				holder.mCheckBox.setVisibility(View.VISIBLE);
			}else{
				holder.mCheckBox.setVisibility(View.GONE);
			}
			holder.mCheckBox.setChecked(info.isSelect());
			/*holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
					info.setSelect(isChecked);
					updateSelectedCount();
				}
			});*/
		/*holder.mAppData.setText(ToolUtils.getDataSize(info.getAppCache()));
		holder.mMemSize.setText(ToolUtils.getDataSize(info.getMemSize()));*/
			holder.mAppitem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.i(TAG, "mAppitem onClick: ");
					if (GlobalData.isMultipleMode) {
						info.setSelect(!info.isSelect());
						notifyDataSetChanged();
						updateSelectedCount();
					} else {
						new AlertDialog.Builder(mContext)
								.setTitle(R.string.choose_next_action)
								.setItems(R.array.copy_actions, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										/*
										*<item>导出</item>
										*<item>以自定义文件名导出</item>
										*<item>分享</item>
										*<item>以自定义文件名分享</item>
										* */
										int mode = i;
										switch (i) {
											case 0:
												mode = FileUtils.MODE_ONLY_EXPORT;
												break;
											case 1:
												mode = FileUtils.MODE_ONLY_EXPORT_RENAME;
												break;
											case 2:
												mode = FileUtils.MODE_EXPORT_SHARE;
												break;
											case 3:
												mode = FileUtils.MODE_EXPORT_RENAME_SHARE;
												break;
										}
										String mCurrentPkgName = info.packageName;
										String mCurrentAppName = info.appName;
										String mCurrentAppPath = info.appSourcDir;
										String mCurrentVersionName = info.getVersionName();
										String mCurrentVersionCode = String.valueOf(info.versionCode);
										String customFileNameFormat = Settings.getCustomFileNameFormat();
										String customFileName = customFileNameFormat.replace("#N", mCurrentAppName).replace("#P", mCurrentPkgName).replace("#V", mCurrentVersionName).replace("#C", mCurrentVersionCode) + ".apk";
										fileUtils.doExport(mHandler, mode, mCurrentAppPath, customFileName);

									}
								})
								.show();
					}
				}
			});
			holder.mAppitem.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					String long_click_action = Settings.getLongPressAction();
					Log.i(TAG, "onLongClick: long_click_action=" + long_click_action);
					if (long_click_action.equals("103")) {
						//复制应用信息
						new AlertDialog.Builder(mContext)
								.setTitle(R.string.choose_next_action)
								.setItems(R.array.copy_info_actions, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
								/*
								<item>复制应用名称</item>
        						<item>复制包名</item>
        						<item>复制版本号</item>
        						<item>复制内部版本号</item>
								 */
										//ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
										//ClipData clipData = ClipData.newPlainText(null, "");
										String copy_str = DEFAULT_COPY_DATA;
										switch (i) {
											case 0:
												copy_str = info.getAppName();
												//clipData = ClipData.newPlainText(null, info.getAppName());
												break;
											case 1:
												copy_str = info.getPackageName();
												//clipData = ClipData.newPlainText(null, info.getPackageName());
												break;
											case 2:
												copy_str = info.getVersionName();
												//clipData = ClipData.newPlainText(null, info.getVersionName());
												break;
											case 3:
												copy_str = String.valueOf(info.getVersionCode());
												break;
											case 4:
												String appNameUrl = info.getAppName();
												try {
													appNameUrl= URLEncoder.encode(appNameUrl,"utf-8");
												} catch (UnsupportedEncodingException e) {
													e.printStackTrace();
													Log.i(TAG, "URLEncode fail",e );
												}
												copy_str = "https://leftshine.gitee.io/apkexport/pages/share/market.html?appName="+appNameUrl+"&packageName="+info.getPackageName();

												break;
										}
										Log.i(TAG, "copy_str"+copy_str);
										ClipData clipData = ClipData.newPlainText(null, copy_str);
										ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
										if (cm != null) {
											cm.setPrimaryClip(clipData);

											Toast.makeText(mContext,mContext.getString(R.string.copy_success, copy_str),Toast.LENGTH_SHORT).show();
										}
									}
								})
								.show();
					} else {
						//导出操作
						int mode = FileUtils.MODE_ONLY_EXPORT;
						switch (long_click_action) {
							case "100":
								mode = FileUtils.MODE_ONLY_EXPORT;
								break;
							case "104":
								mode = FileUtils.MODE_ONLY_EXPORT_RENAME;
								break;
							case "101":
								mode = FileUtils.MODE_EXPORT_SHARE;
								break;
							case "102":
								mode = FileUtils.MODE_EXPORT_RENAME_SHARE;
								break;
						}
						String mCurrentPkgName = info.packageName;
						String mCurrentAppName = info.appName;
						String mCurrentAppPath = info.appSourcDir;
						String mCurrentVersionName = info.getVersionName();
						String mCurrentVersionCode = String.valueOf(info.versionCode);
						String customFileNameFormat  = Settings.getCustomFileNameFormat();
						String customFileName = customFileNameFormat.replace("#N",mCurrentAppName).replace("#P",mCurrentPkgName).replace("#V",mCurrentVersionName).replace("#C",mCurrentVersionCode)+".apk";
						fileUtils.doExport(mHandler, mode,mCurrentAppPath, customFileName);
					}
					//FileUtils.copyInfo(mContext,mHandler,info);
					return true;
				}
			});
		}else{
			ApkViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.local_apk_infolist_item, null);
				holder = new ApkViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ApkViewHolder) convertView.getTag();
			}
			final AppInfo info = (AppInfo) getItem(position);
			//final AppInfo info = (AppInfo) mLists.get(position);
			//Log.i(TAG, "getItem:"+info1.getAppName()+"mLists.get(position):"+info.getAppName());
			holder.LocalApkIcon.setImageDrawable(info.getAppIcon());
			holder.LocalApkName.setText(info.getAppName());
			holder.LocalApkSize.setText(ToolUtils.getDataSize(info.getAppSize()));
			String timeFormat = new SimpleDateFormat("MM-dd hh:mm").format(new Date(info.getLastUpdateTime()));
			holder.LocalApkdate.setText(timeFormat);
			if(GlobalData.isMultipleMode){
				holder.LocalApkCheckBox.setVisibility(View.VISIBLE);
			}else{
				holder.LocalApkCheckBox.setVisibility(View.GONE);
			}
			holder.LocalApkCheckBox.setChecked(info.isSelect());
			holder.LocalApkitem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					Log.i(TAG, "LocalApkitem onClick: ");
					if (GlobalData.isMultipleMode) {
						info.setSelect(!info.isSelect());
						notifyDataSetChanged();
						updateSelectedCount();
					} else {
						new AlertDialog.Builder(mContext)
								.setTitle(R.string.choose_next_action)
								.setItems(R.array.localAPK_actions, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
                                /*<item>Share</item>
                                <item>Install</item>
                                <item>Rename</item>
                                <item>Delete</item>*/
										//int mode = i + MODE_LOCAL_SHARE;
										switch (i) {
											case 0:
												//mode = MODE_LOCAL_SHARE;
												fileUtils.startShare(info.appSourcDir);
												break;
											case 1:
												//mode = MODE_LOCAL_INSTALL;
												fileUtils.install(info.appSourcDir, mContext);
												break;
											case 2:
												//mode = MODE_LOCAL_RENAME;
												final String oldName = info.appName;
												fileUtils.rename(info.appSourcDir, new FileUtils.OnListDataChangedListener() {
													@Override
													public void OnListDataChanged(String newPath) {
														File newFile = new File(newPath);
														String newName = newFile.getName();
														if (oldName != newName) {
															AppInfo newInfo = info;
															newInfo.appName = newName;
															info.setAppName(newName);
															info.setAppSourcDir(newPath);
															mLists.remove(position);
															mLists.add(position, newInfo);
															notifyDataSetChanged();

														}
													}
												});

												break;
											case 3:
												//mode = MODE_LOCAL_DELETE;
												fileUtils.delete(info.appSourcDir, new FileUtils.OnListDataChangedListener() {
													@Override
													public void OnListDataChanged(String newPath) {
														mLists.remove(position);
														notifyDataSetChanged();
													}
												});
												break;
										}
										//FileUtils.doLocalApk(context,mHandler,mode,info);
									}
								})
								.show();
					}
				}
			});
			holder.LocalApkitem.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					/*String long_click_action = Settings.getLongPressAction();
					Log.i(TAG, "onLongClick: long_click_action="+long_click_action);
					if (long_click_action.equals("103")) {*/
						//复制应用信息
						new AlertDialog.Builder(mContext)
								.setTitle(R.string.choose_next_action)
								.setItems(R.array.localAPK_copy_info_actions, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
								/*
								<item>复制文件名</item>
        						<item>复制包名</item>
        						<item>复制文件路径</item>
								 */
										//ClipData clipData = ClipData.newPlainText(null, "");
										String copy_str = DEFAULT_COPY_DATA;
										switch (i) {
											case 0:
												copy_str=info.getAppName();
												//clipData = ClipData.newPlainText(null, info.getAppName());
												break;
											case 1:
												copy_str = info.getPackageName();
												//clipData = ClipData.newPlainText(null, info.getPackageName());
												break;
											case 2:
												copy_str = info.getAppSourcDir();
												//clipData = ClipData.newPlainText(null, info.getAppSourcDir());
												break;
										}
										ClipData clipData = ClipData.newPlainText(null, copy_str);
										ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
										cm.setPrimaryClip(clipData);
										Toast.makeText(mContext,mContext.getString(R.string.copy_success, copy_str),Toast.LENGTH_LONG).show();
									}
								})
								.show();
					/*}else{
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
						fileUtils.doExport(mHandler,mode,info);
					}*/
					//FileUtils.copyInfo(context,mHandler,info);
					return true;
				}
			});
		}
		/*if(isMultipleMode){
			convertView.findViewById(R.id.appInfo_checkbox).setVisibility(View.VISIBLE);
		}else{
			convertView.findViewById(R.id.appInfo_checkbox).setVisibility(View.GONE);
		}
		//判断position位置是否被选中，改变颜色
		if(mLists.get(position).isSelect()) {
			convertView.setBackgroundColor(Color.RED);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}*/
		return convertView;
	}


	private class AppViewHolder {
		private LinearLayout mAppitem;
		private ImageView mAppIcon;
		private TextView mPackageName;
		private TextView mAppName;
		private TextView mAppSize;
		private TextView mAppVersionName;
		private CheckBox mCheckBox;
		/*private TextView mAppData;
		private TextView mMemSize;*/

		private AppViewHolder(View view) {
			mAppitem = (LinearLayout)view.findViewById(R.id.appInfo_item);
			mAppIcon = (ImageView) view.findViewById(R.id.appInfo_icon);
			mPackageName = (TextView) view.findViewById(R.id.appInfo_packageName);
			mAppName = (TextView) view.findViewById(R.id.appInfo_appName);
			mAppSize = (TextView) view.findViewById(R.id.appInfo_appSize);
			mAppVersionName = (TextView) view.findViewById(R.id.appInfo_appVersionName);
			mCheckBox=(CheckBox)view.findViewById(R.id.appInfo_checkbox);
            /*mAppData = (TextView) view.findViewById(R.id.appInfo_dataSize);
			mMemSize = (TextView) view.findViewById(R.id.appInfo_memSize);*/
		}
	}

	private class ApkViewHolder {
		private LinearLayout LocalApkitem;
		private ImageView LocalApkIcon;
		private TextView LocalApkName,LocalApkSize,LocalApkdate;
		CheckBox LocalApkCheckBox;

		public ApkViewHolder(View view) {
			LocalApkitem = (LinearLayout)view.findViewById(R.id.LocalApkInfo_item);
			LocalApkIcon = (ImageView) view.findViewById(R.id.LocalApkInfo_icon);
			LocalApkName = (TextView) view.findViewById(R.id.LocalApkInfo_appName);
			LocalApkSize = (TextView) view.findViewById(R.id.LocalApkInfo_appSize);
			LocalApkdate = (TextView)view.findViewById(R.id.LocalApkInfo_date);
			LocalApkCheckBox = (CheckBox)view.findViewById(R.id.LocalApkInfo_checkbox);
		}
	}
	
	class MyFilter extends Filter {
		//我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			FilterResults result = new FilterResults();
			List<AppInfo> list;

			if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
				list = fullLists;
			} else {//否则把符合条件的数据对象添加到集合中
				list = new ArrayList<AppInfo>();
				for (AppInfo appInfo : fullLists) {
					if (appInfo.getAppName().toUpperCase().contains(charSequence.toString().toUpperCase()) || appInfo.getPackageName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
						Log.i(TAG, "performFiltering:" + appInfo.toString());
						list.add(appInfo);
					}

				}
			}
			result.values = list; //将得到的集合保存到FilterResults的value变量中
			result.count = list.size();//将集合的大小保存到FilterResults的count变量中

			return result;
		}

		@Override
		protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
			mLists = (List<AppInfo>)filterResults.values;
			//LogUtil.d("publishResults:"+filterResults.count);
			if (filterResults.count>0){
				notifyDataSetChanged();//通知数据发生了改变
				//LogUtil.d("publishResults:notifyDataSetChanged");
			}else {
				notifyDataSetInvalidated();//通知数据失效
				//LogUtil.d("publishResults:notifyDataSetInvalidated");
			}
		}
	}


}
