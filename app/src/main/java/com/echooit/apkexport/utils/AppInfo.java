package com.echooit.apkexport.utils;

import android.graphics.drawable.Drawable;

public class AppInfo {
	
	public long appCache = 0;
	public Drawable appIcon = null;
	public String appName = "";
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
}