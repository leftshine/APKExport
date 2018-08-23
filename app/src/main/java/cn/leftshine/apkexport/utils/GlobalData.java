package cn.leftshine.apkexport.utils;

import android.support.v7.view.ActionMode;

/**
 * Created by Administrator on 2018/8/13.
 */

public class GlobalData {
    public static boolean isMultipleMode;
    public static ActionMode actionmode;
    public static boolean isMultipleMode() {
        return isMultipleMode;
    }

    public static void setMultipleMode(boolean multipleMode) {
        isMultipleMode = multipleMode;
    }

    public static ActionMode getActionmode() {
        return actionmode;
    }

    public static void setActionmode(ActionMode actionmode) {
        GlobalData.actionmode = actionmode;
    }
}
