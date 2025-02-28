package cn.leftshine.apkexport.utils;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private final int type;
	public long appCache = 0;
	public Drawable appIcon = null;
	public String appName = "";
	public String fileName = "";
	public String appSourcDir = "";
	public long appSize = 0;
	public Boolean isSelected = Boolean.valueOf(false);
	public String packageName = "";
	public int versionCode = 0;
	public String versionName = "";
	private int pid; // android.system.uid=1000
	private int uid; // 
	private int memSize;
	private String processName;
	public long firstInstallTime;
	public long lastUpdateTime;
	private int installed =1;
	private boolean isSelect;
	private boolean isShowCheckBox;

	public AppInfo(int type) {
		this.type = type;
	}
	//private String fileLastModifyTime;
/*

	public String getFileLastModifyTime() {
		return fileLastModifyTime;
	}

	public void setFileLastModifyTime(String fileLastModifyTime) {
		this.fileLastModifyTime = fileLastModifyTime;
	}
*/

	public int getType() {
		return type;
	}

	public long getFirstInstallTime() {
		return firstInstallTime;
	}

	public void setFirstInstallTime(long firstInstallTime) {
		this.firstInstallTime = firstInstallTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public long getAppCache() {
		return appCache;
	}
	public void setAppCache(long appCache) {
		this.appCache = appCache;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public String getAppName() { return appName;	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getFileName() { return fileName;	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAppSourcDir() { return appSourcDir;}
	public void setAppSourcDir(String appSourcDir) { this.appSourcDir = appSourcDir;}
	public long getAppSize() {
		return appSize;
	}
	public void setAppSize(long appSize) {
		this.appSize = appSize;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getMemSize() {
		return memSize;
	}
	public void setMemSize(int memSize) {
		this.memSize = memSize;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setInstalled(int installed) {
		this.installed = installed;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public boolean isShowCheckBox() {
		return isShowCheckBox;
	}

	public void setShowCheckBox(boolean showCheckBox) {
		isShowCheckBox = showCheckBox;
	}
}