package cn.leftshine.apkexport.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import cn.leftshine.apkexport.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String description = getString(R.string.about_page_description);
        if(!TextUtils.isEmpty(description) && description.indexOf("\\n") >= 0)
            description = description.replace("\\n", "\n");
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(description)
                .setImage(R.mipmap.ic_launcher)
                .addItem(getVersionElement())
                .addItem(getFeedbackElement())
                .addEmail("leftshine@139.com")
                .addWebsite("https://www.cnblogs.com/leftshine/")
                //.addGitHub("tiann")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
        //setContentView(R.layout.activity_about);
    }
    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    Element getVersionElement() {
        Element version = new Element();
        String versionName = "unknown";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        version.setTitle(getResources().getString(R.string.about_version_title, versionName));
        return version;
    }

    Element getFeedbackElement() {

        Element feedback = new Element();
        final String qqGroup = "610047919";
        feedback.setTitle(getResources().getString(R.string.about_feedback_title, qqGroup));

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 /*ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, qqGroup));
            }
            Toast.makeText(v.getContext(), getResources().getString(R.string.about_feedback_tips), Toast.LENGTH_SHORT).show();*/
                joinQQGroup("kxVc87XQ3TzFECy3YhETolYKBS11-iE6");

            }
        });
        return feedback;
    }
    /****************
     *
     * 发起添加群流程。群号：Android黑科技交流群(610047919) 的 key 为： kxVc87XQ3TzFECy3YhETolYKBS11-iE6
     * 调用 joinQQGroup(kxVc87XQ3TzFECy3YhETolYKBS11-iE6) 即可发起手Q客户端申请加群 Android黑科技交流群(610047919)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
