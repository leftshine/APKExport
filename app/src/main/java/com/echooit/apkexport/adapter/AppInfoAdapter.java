package com.echooit.apkexport.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.echooit.apkexport.utils.AppInfo;
import com.echooit.apkexport.R;
import com.echooit.apkexport.utils.CharacterParser;
import com.echooit.apkexport.utils.ToolUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppInfoAdapter extends BaseAdapter implements Filterable {

	private static final String TAG = "AppInfoAdapter";
	private LayoutInflater mInflater;
	SortComparator localSortComparator;
	private List<AppInfo> mLists = new ArrayList<AppInfo>();
	private List<AppInfo> backLists = new ArrayList<AppInfo>();
	private CharacterParser characterParser;
	MyFilter mFilter;

	public AppInfoAdapter(Context context, List<AppInfo> list) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		localSortComparator = new SortComparator();
		mLists = list;
		backLists = mLists;
		characterParser = CharacterParser.getInstance();

	}

	public void setDataList(List<AppInfo> list) {
		mLists = list;
		notifyDataSetChanged();
	}

	public void add(AppInfo info) {
		if (info != null) {
			mLists.add(info);
			Collections.sort(mLists, localSortComparator);
			notifyDataSetChanged();
		}

	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new MyFilter();
		}
		return mFilter;
	}

	public class SortComparator implements Comparator<Object> {
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
	}

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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflater.inflate(R.layout.app_infolist_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppInfo info = (AppInfo) getItem(position);
		holder.mAppIcon.setImageDrawable(info.getAppIcon());
		holder.mPackageName.setText(info.getPackageName());
		holder.mAppName.setText(info.getAppName());
		holder.mAppSize.setText(ToolUtils.getDataSize(info.getAppSize()));
		holder.mAppVersionName.setText(info.getVersionName());
		/*holder.mAppData.setText(ToolUtils.getDataSize(info.getAppCache()));
		holder.mMemSize.setText(ToolUtils.getDataSize(info.getMemSize()));*/
		return convertView;
	}


	public class ViewHolder {
		private ImageView mAppIcon;
		private TextView mPackageName;
		private TextView mAppName;
		private TextView mAppSize;
		private TextView mAppVersionName;
		/*private TextView mAppData;
		private TextView mMemSize;*/

		public ViewHolder(View view) {
			mAppIcon = (ImageView) view.findViewById(R.id.appInfo_icon);
			mPackageName = (TextView) view.findViewById(R.id.appInfo_packageName);
			mAppName = (TextView) view.findViewById(R.id.appInfo_appName);
			mAppSize = (TextView) view.findViewById(R.id.appInfo_appSize);
			mAppVersionName = (TextView) view.findViewById(R.id.appInfo_appVersionName);
            /*mAppData = (TextView) view.findViewById(R.id.appInfo_dataSize);
			mMemSize = (TextView) view.findViewById(R.id.appInfo_memSize);*/
		}
	}

	class MyFilter extends Filter {
		//我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			FilterResults result = new FilterResults();
			List<AppInfo> list;

			if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
				list = backLists;
			} else {//否则把符合条件的数据对象添加到集合中
				list = new ArrayList<AppInfo>();
				for (AppInfo appInfo : backLists) {
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
