package com.echooit.apkexport.activity;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.echooit.apkexport.R;
import com.echooit.apkexport.fragment.MainActivityFragment;
import com.echooit.apkexport.utils.Settings;

import java.io.File;

import static com.echooit.apkexport.utils.PermisionUtils.verifyStoragePermissions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    android.app.Fragment fragment;
    FloatingActionButton fab;
    boolean isExitSnackbarShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = new MainActivityFragment();
        transaction.replace(R.id.layout_fragment,fragment);
        //transaction.add
        transaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                anim.setDuration(500);
                anim.start();
                ((MainActivityFragment)fragmentManager.findFragmentById(R.id.layout_fragment)).refresh();
                Snackbar.make(view, R.string.Refreshing, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        //getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(Settings.isAutoClean()) {
            Snackbar.make(findViewById(R.id.layout_fragment), deleteCache() ? getString(R.string.auto_clean_success) :getString(R.string.auto_clean_fail), Snackbar.LENGTH_LONG).show();
        }

    }

    private boolean deleteCache() {
        File mFolder = new File( Environment.getExternalStorageDirectory()+File.separator+"APKExport");
        if (mFolder.exists()) {
            if (mFolder.isDirectory())
            {
                for(File file : mFolder.listFiles())
                {
                    file.delete();
                }
                return true;
            }
        }
        return false;
    }

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
       /* if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Snackbar.make(findViewById(R.id.layout_fragment), "再按一次退出", Snackbar.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }*/
        if(!isExitSnackbarShown) {
            Snackbar.make(findViewById(R.id.layout_fragment), getString(R.string.one_more_click_to_exit), Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    isExitSnackbarShown = false;
                    super.onDismissed(snackbar, event);
                }

                @Override
                public void onShown(Snackbar snackbar) {
                    isExitSnackbarShown = true;
                    super.onShown(snackbar);
                }
            }).show();
        }else{
            finish();
            System.exit(0);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
        if(id == R.id.action_about)
        {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
