package cn.leftshine.apkexport.utils;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.adapter.AppInfoAdapter;
import cn.leftshine.apkexport.fragment.AppFragment;

/**
 * Created by Administrator on 2018/8/14.
 */

public class ActionModeCallbackMultiple implements ActionMode.Callback{
    View actionBarView;
    TextView selectedNum;
    Context context;
    AppInfoAdapter adapter;
    ActionBar supportActionBar;
    List<AppFragment> appFragments;

    public ActionModeCallbackMultiple(Context context, ActionBar supportActionBar, List<AppFragment> tabFragments) {
        this.context = context;
        this.supportActionBar= supportActionBar;
        this.appFragments=tabFragments;
    }
    public void setAdapter(AppInfoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        if(ToolUtils.TYPE_LOCAL==adapter.getmType()){
            menu.findItem(R.id.multi_export).setVisible(false);
        }else{
            menu.findItem(R.id.multi_export).setVisible(true);
        }
        return true;
    }

    //退出多选模式时调用
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub
        //adapter.unSelectAll();
        //退出多选模式
        Toast.makeText(context,R.string.exit_multiple_mode,Toast.LENGTH_SHORT).show();

        GlobalData.setMultipleMode(false);
        for(int i=0;i<appFragments.size();i++){
            if(appFragments.get(i)!=null){
                appFragments.get(i).changeMultiSelectMode(false);
            }
        }
        supportActionBar.show();
    }

    //进入多选模式调用，初始化ActionBar的菜单和布局
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        ((Activity)context).getMenuInflater().inflate(R.menu.menu_multiple_mode, menu);
        if(actionBarView == null) {
            actionBarView = LayoutInflater.from(context).inflate(R.layout.actionbar_view_multiple, null);
            selectedNum = (TextView)actionBarView.findViewById(R.id.selected_num);
        }
        mode.setCustomView(actionBarView);
        //adapter.setActionmode(mode);
        GlobalData.setActionmode(mode);
        return true;
    }

    //ActionBar上的菜单项被点击时调用
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()) {
            case R.id.multi_export:
                adapter.multiExport(0);
                break;
            case R.id.multi_share:
                if(ToolUtils.TYPE_LOCAL==adapter.getmType()) {
                    adapter.multiShare();
                }else {
                    adapter.multiExport(1);
                }
                break;
            case R.id.select_all:
                adapter.selectAll();
                break;
            case R.id.unselect_all:
                adapter.unSelectAll();
                break;
            case R.id.exit_multi_mode:
                mode.finish();
                break;
        }
        return true;
    }


}
