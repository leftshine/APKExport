package cn.leftshine.apkexport.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.utils.AppUtils;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import static cn.leftshine.apkexport.utils.PermisionUtils.requestStoragePermissions;

public class AboutActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        String description = getString(R.string.about_page_description);
        if(!TextUtils.isEmpty(description) && description.indexOf("\\n") >= 0)
            description = description.replace("\\n", "\n");
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(description)
                .setImage(R.mipmap.ic_launcher)
                .addItem(getVersionElement())
                //.addItem(getQQGroupElement(getResources().getString(R.string.about_feedback_title, "610047919"),"kxVc87XQ3TzFECy3YhETolYKBS11-iE6"))
                .addItem(getQQGroupElement(getResources().getString(R.string.about_feedback_title2, "427159859"),"MLFoNWi_oAOF5A77cv2ek8PnGTMKLIzv"))
                //.addEmail("leftshine@139.com")
                .addItem(getFeedBackElement())
                //.addWebsite("https://www.cnblogs.com/leftshine/")
                .addItem(getWebsiteElement(R.drawable.ic_home_black_24dp,getResources().getString(R.string.about_website_title),"https://leftshine.gitlab.io/apkexport/"))
                //.addItem(getWebsiteElement(R.drawable.ic_loyalty_black_24dp,getResources().getString(R.string.about_donate_title),"https://leftshine.gitlab.io/apkexport/donate/index.html"))
                //.addGitHub("tiann")
                .addItem(getOpenSourceLicensesElement())
                .addItem(getCopyRightsElement())

                .create();

        setContentView(aboutPage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setContentView(R.layout.activity_about);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.coolapk.com/apk/176960");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        return version;
    }

    Element getFeedBackElement() {
        Element feedback = new Element();
        feedback.setTitle(getResources().getString(R.string.about_email_contact));
        feedback.setIconDrawable(R.drawable.ic_email_black_24dp);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:leftshine@foxmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.about_email_contact_subject, AppUtils.getAppName(AboutActivity.this)));
                    intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.about_email_contact_text));
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(mContext,R.string.tip_eamil_ActivityNotFoundException,Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(mContext,R.string.tip_eamil_failed,Toast.LENGTH_SHORT).show();
                }
            }
        });
        return feedback;
    }

    Element getWebsiteElement(int icon, String title, final String url) {

        Element websiteElement = new Element();
        websiteElement.setTitle(title);
        websiteElement.setIconDrawable(icon);
        websiteElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        return websiteElement;
    }

    Element getOpenSourceLicensesElement() {
        Element websiteElement = new Element();
        websiteElement.setTitle(getResources().getString(R.string.about_open_source_licenses_title));
        websiteElement.setIconDrawable(R.drawable.ic_description_black_24dp);
        websiteElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = new TextView(mContext);
                tv.setText(Html.fromHtml(" " +
                        "android-about-page: <a href=\"https://github.com/medyo/android-about-page\">MIT</a>"+"<br>"+
                        "android-filepicker: <a href=\"https://github.com/Angads25/android-filepicker\">Apache</a>"));
                tv.setPadding(50,50,50,50);

                tv.setMovementMethod(LinkMovementMethod.getInstance());
                new AlertDialog.Builder(mContext)
                        .setCancelable(true)
                        .setTitle(R.string.about_open_source_licenses_title)
                        .setView(tv)
                        //.setMessage()
                        //.setView(R.layout.custom_open_source_licenses_dialog)

                        .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        return websiteElement;
    }

    Element getQQGroupElement(String title, final String key) {

        Element qgroup = new Element();
        qgroup.setTitle(title);
        qgroup.setIconDrawable(R.drawable.ic_chat_black_24dp);
        qgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 /*ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, qqGroup));
            }
            Toast.makeText(v.getContext(), getResources().getString(R.string.about_feedback_tips), Toast.LENGTH_SHORT).show();*/
                joinQQGroup(key);

            }
        });
        return qgroup;
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
            Toast.makeText(this,R.string.about_no_QQ_app,Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
