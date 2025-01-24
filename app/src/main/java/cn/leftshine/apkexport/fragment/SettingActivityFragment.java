package cn.leftshine.apkexport.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.angads25.filepicker.widget.MaterialCheckbox;

import java.io.File;
import java.util.Arrays;

import cn.leftshine.apkexport.MyApplication;
import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.utils.AppUtils;
import cn.leftshine.apkexport.utils.FileUtils;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.view.ClearEditText;
import static cn.leftshine.apkexport.utils.PermisionUtils.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment {

    private static final boolean DBG = Settings.isDebug();
    private String TAG = "SettingActivityFragment";
    private static final String BUNDLE_KEY_ACTIVITY_RESULT_CODE = "ACTIVITY_RESULT_CODE";
    private SwitchPreference prefIsAutoCleanExportDir,prefIsAutoCleanCacheDir,prefIsShowLocalApk,prefIsToolbarFixed;
    private EditTextPreference prefCustomFileNameformat;
    private Preference prefRestoreDefaultSettings,prefCleanExportDir,prefGrantAllFfilesAccessPermission;
    private PreferenceCategory prefCategoryAdvance;
    private ListPreference prefLongPressAction, prefDarkMode, prefLanguage;
    private ListPreference sortType;
    private ListPreference sortOrder;
    private static Dialog dialogCustomFileNameformat,dialogCustomExportPath,dialogRestoreDefaultSettings;
    //private EditText txt_custom_filename_format;
    private ClearEditText txt_custom_filename_format,txt_custom_export_path;

    private Button btn_insert_N;
    private Button btn_insert_P;
    private Button btn_insert_V;
    private Button btn_insert_C;
    private Button btn_insert_default,btn_insert_divider;
    private Button btn_open_custom_path;
    private TextView txt_custom_filename_preview;
    private String str_custom_filename_format,str_custom_export_path;
    private Context context;
    private Fragment fragment;
    private Preference prefCustomExportPath;
    private FilePickerDialog mFilePickerDialog;
    private int mResultCode = Activity.RESULT_CANCELED;

    public SettingActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        fragment = this;
        addPreferencesFromResource(cn.leftshine.apkexport.R.xml.preferences);
        //getPreferenceManager().setSharedPreferencesName(Settings.SETTING_PREF_NAME);
        Settings.setIsNeedLoad(false);
        initViews();

        if (savedInstanceState != null) {
            setActivityResultCode(savedInstanceState.getInt(BUNDLE_KEY_ACTIVITY_RESULT_CODE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_ACTIVITY_RESULT_CODE, mResultCode);
    }

    private void setActivityResultCode(int code){
        Log.i(TAG, "setActivityResultCode: "+code);
        mResultCode = code;
        getActivity().setResult(mResultCode);
    }

    private void initViews() {
        prefIsShowLocalApk =  (SwitchPreference)findPreference(getString(R.string.key_is_show_local_apk));
        prefIsShowLocalApk.setOnPreferenceChangeListener(preferenceChangeListener);
        prefIsToolbarFixed =  (SwitchPreference)findPreference(getString(R.string.key_is_toolbar_fixed));
        prefIsToolbarFixed.setOnPreferenceChangeListener(preferenceChangeListener);
        prefIsAutoCleanExportDir = (SwitchPreference)findPreference(getString(R.string.key_is_auto_clean_exportDir));
        prefIsAutoCleanExportDir.setOnPreferenceChangeListener(preferenceChangeListener);
        prefIsAutoCleanCacheDir = (SwitchPreference)findPreference(getString(R.string.key_is_auto_clean_exportDir));
        prefIsAutoCleanCacheDir.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomFileNameformat= (EditTextPreference)findPreference(getString(R.string.key_custom_filename_format));

        //高级设置
        prefCategoryAdvance = ((PreferenceCategory)findPreference(getString(R.string.key_advance)));
        prefRestoreDefaultSettings = (Preference)findPreference(getString(R.string.key_restore_default_settings));
        prefCleanExportDir = findPreference(getString(R.string.key_clean_ExportDir));
        prefCleanExportDir.setOnPreferenceClickListener(preferenceclickListener);
        prefCleanExportDir.setSummary(getString(R.string.setting_clean_export_dir_summery)+FileUtils.getExportDirSize());
        prefGrantAllFfilesAccessPermission = findPreference(getString(R.string.key_all_files_access_permission));
        if (Build.VERSION.SDK_INT < 30 && prefCategoryAdvance != null) {
            prefCategoryAdvance.removePreference(prefGrantAllFfilesAccessPermission);
        }else{
            prefGrantAllFfilesAccessPermission.setOnPreferenceClickListener(preferenceclickListener);
        }
        //prefCustomFileNameformat.setDialogLayoutResource(R.layout.custom_file_name_dialog_layout);
        prefCustomFileNameformat.setOnPreferenceClickListener(preferenceclickListener);
        prefRestoreDefaultSettings.setOnPreferenceClickListener(preferenceclickListener);
        prefCustomFileNameformat.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());

        prefCustomExportPath = (Preference)findPreference(getString(R.string.key_custom_export_path));
        //prefCustomExportPath.setOnPreferenceClickListener(preferenceclickListener);
        prefCustomExportPath.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomExportPath.setOnPreferenceClickListener(preferenceclickListener);

        prefCustomExportPath.setSummary(Settings.getCustomExportPath());

        prefLongPressAction = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_long_press_action));
        prefLongPressAction.setOnPreferenceChangeListener(preferenceChangeListener);
        prefLongPressAction.setSummary(prefLongPressAction.getEntry());

        prefDarkMode = (ListPreference)findPreference(getString(R.string.key_dark_mode));
        prefDarkMode.setOnPreferenceChangeListener(preferenceChangeListener);
        prefDarkMode.setSummary(prefDarkMode.getEntry());

        prefLanguage = (ListPreference)findPreference(getString(R.string.key_language));
        prefLanguage.setOnPreferenceChangeListener(preferenceChangeListener);
        prefLanguage.setSummary(prefLanguage.getEntry());

        sortType = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_sort_type));
        sortType.setOnPreferenceChangeListener(preferenceChangeListener);
        sortType.setSummary(sortType.getEntry());

        sortOrder = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_sort_order));
        sortOrder.setOnPreferenceChangeListener(preferenceChangeListener);
        sortOrder.setSummary(sortOrder.getEntry());
        if (!verifyStoragePermissions(getActivity())) {
            //未获得权限
            prefIsShowLocalApk.setChecked(false);
            prefIsAutoCleanExportDir.setChecked(false);
        }
    }

    Preference.OnPreferenceClickListener preferenceclickListener=new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // TODO Auto-generated method stub
            //Log.d("config","clickListener");


            if(preference.getKey().equals(getResources().getString(R.string.key_custom_filename_format)))
            {
                dialogCustomFileNameformat = ((EditTextPreference)preference).getDialog();
                dialogCustomFileNameformat.setContentView(R.layout.custom_file_name_dialog_layout);
                /*txt_custom_filename_format = (EditText)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_format);*/
                txt_custom_filename_format = (ClearEditText) dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_format);
                btn_insert_divider =dialogCustomFileNameformat.findViewById(R.id.btn_insert_divider);
                btn_insert_N = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_N);
                btn_insert_P = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_P);
                btn_insert_V = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_V);
                btn_insert_C = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_C);
                btn_insert_default = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_default);
                txt_custom_filename_preview = (TextView)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_preview);
                btn_insert_divider.setOnClickListener(onClickListener);
                btn_insert_N.setOnClickListener(onClickListener);
                btn_insert_P.setOnClickListener(onClickListener);
                btn_insert_V.setOnClickListener(onClickListener);
                btn_insert_C.setOnClickListener(onClickListener);
                btn_insert_default.setOnClickListener(onClickListener);
                txt_custom_filename_format.addTextChangedListener(textWatcher);
                txt_custom_filename_format.setText(Settings.getCustomFileNameFormat());

                Button btn_submit = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_custom_file_name_submit);
                Button btn_cancel = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_custom_file_name_cancel);
                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_custom_filename_format = txt_custom_filename_format.getText().toString();
                        Settings.setCustomFileNameFormat(str_custom_filename_format);
                        prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());
                        dialogCustomFileNameformat.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCustomFileNameformat.dismiss();
                    }
                });
            }

            if(preference.getKey().equals(getResources().getString(R.string.key_all_files_access_permission))){
                Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 10001);
            }

            if(preference.getKey().equals(getResources().getString(R.string.key_restore_default_settings)))
            {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.setting_restore_default_settings_title)
                        .setMessage(R.string.setting_restore_default_settings_description)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("Settings", "restore_default_settings");
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                sharedPreferences.edit().clear().commit();
                                getPreferenceScreen().removeAll();
                                addPreferencesFromResource(cn.leftshine.apkexport.R.xml.preferences);
                                initViews();
                                //startActivity(new Intent(context, SettingActivity.class));
                                //getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("Settings", "cancel");
                            }
                        })
                        .show();
            }
            if(preference.getKey().equals(getResources().getString(R.string.key_clean_ExportDir))){
                if (verifyStoragePermissions(getActivity())) {
                    //已获得权限
                    //Toast.makeText(context,R.string.clean_running,Toast.LENGTH_SHORT);
                    prefCleanExportDir.setSummary(R.string.setting_clean_export_dir_summery_clean_Running);
                    Toast.makeText(context, R.string.setting_clean_export_dir_summery_clean_Running,Toast.LENGTH_SHORT).show();
                    FileUtils.cleanExportDir(context);
                    FileUtils.cleanCacheDir(context);
                    prefCleanExportDir.setSummary(R.string.setting_clean_export_dir_summery_clean_done);
                } else {
                    //Settings.setShowLocalApk(false);
                    // 未获得权限
                    requestStoragePermissions(fragment,REQUEST_EXTERNAL_STORAGE_CLEAN_EXPORT_DIR);
                    return false;
                }

            }
            /*if(preference.getKey().equals(getResources().getString(R.string.key_custom_export_path)))
            {
                dialogCustomExportPath = prefCustomExportPath.getDialog();
                txt_custom_export_path = (ClearEditText) dialogCustomExportPath.findViewById(R.id.txt_custom_export_path);
                btn_open_custom_path = (Button)dialogCustomExportPath.findViewById(R.id.btn_open_custom_path);
                btn_open_custom_path.setOnClickListener(onClickListener);
                txt_custom_export_path.setText(Settings.getCustomExportPath());
            }*/
            if(preference.getKey().equals(getResources().getString(R.string.key_custom_export_path)))
            {
                final boolean hasTouchScreen =  context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = new File(FileUtils.getFilePickerParentDir());
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                //properties.offset =  new File(FileUtils.getFilePickerRecommendedDir());
                properties.extensions = new String[]{"apk", "apk.1"};
                FilePickerDialog dialog = new FilePickerDialog(context, properties){
                    File parentFile = new File(FileUtils.getFilePickerParentDir());
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        super.onItemClick(adapterView, view, i, l);
                        if(!hasTouchScreen) {
                            MaterialCheckbox fmark = (MaterialCheckbox) view.findViewById(R.id.file_mark);
                            fmark.performClick();
                            if (DBG) Log.d(TAG, "onItemClick: "+ Arrays.toString( MarkedItemList.getSelectedPaths()));
                            parentFile = new File(MarkedItemList.getSelectedPaths()[0]).getParentFile();
                        }
                    }

                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        if (!hasTouchScreen) {
                            final Button select = (Button) findViewById(R.id.select);
                            Button cancel = (Button) findViewById(R.id.cancel);
                            select.setBackground(new Button(context).getBackground());
                            cancel.setBackground(new Button(context).getBackground());
                            markFiles(Arrays.asList(FileUtils.getFilePickerParentDir()));
                            select.setEnabled(true);
                            int color;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                color = context.getResources().getColor(R.color.colorAccent, context.getTheme());
                            }
                            else {
                                color = context.getResources().getColor(R.color.colorAccent);
                            }
                            select.setTextColor(color);
                            // do not add count on button
                            select.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String choose_button_label = context.getResources().getString(R.string.choose_button_label);
                                    if(!s.toString().equals(choose_button_label)) {
                                        select.setText(choose_button_label);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onBackPressed() {
                        super.onBackPressed();
                        if(!hasTouchScreen && parentFile != null && parentFile.exists() && parentFile.isDirectory()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(parentFile.getName());
                            item.setDirectory(parentFile.isDirectory());
                            item.setMarked(true);
                            item.setTime(parentFile.lastModified());
                            item.setLocation(parentFile.getAbsolutePath());
                            MarkedItemList.clearSelectionList();
                            MarkedItemList.addSelectedItem(item);
                        }
                    }
                };
                dialog.setTitle(R.string.dialog_title_select_dir);
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        if(files.length>=1) {
                            str_custom_export_path = files[0];
                        }
                        Settings.setCustomExportPath(str_custom_export_path);
                        prefCustomExportPath.setSummary(Settings.getCustomExportPath());
                    }
                });
                mFilePickerDialog = dialog;
                if (verifyStoragePermissions(getActivity())) {
                    //已获得权限
                    FileUtils.createIfNotExist(FileUtils.getFilePickerRecommendedDir());
                    mFilePickerDialog.show();
                } else {
                    // 未获得权限
                    requestStoragePermissions(fragment,FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT);
                }
            }
            return false;
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_insert_divider:
                    insertText(txt_custom_filename_format,"-");
                    break;
                case R.id.btn_insert_N:
                    insertText(txt_custom_filename_format,"#N");
                    //txt_custom_filename_format.append("#N");
                    break;
                case R.id.btn_insert_P:
                    insertText(txt_custom_filename_format,"#P");
                    //txt_custom_filename_format.append("#P");
                    break;
                case R.id.btn_insert_V:
                    insertText(txt_custom_filename_format,"#V");
                    //txt_custom_filename_format.append("#V");
                    break;
                case R.id.btn_insert_C:
                    insertText(txt_custom_filename_format,"#C");
                    //txt_custom_filename_format.append("#V");
                    break;
                case R.id.btn_insert_default:
                    insertText(txt_custom_filename_format,"#N-#P-#V");
                    break;
                //case R.id.btn_open_custom_path:
                    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //intent.setType(“image*//*”);//选择图片
                    //intent.setType(“audio*//*”); //选择音频
                    //intent.setType(“video*//*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                    //intent.setType(“video*//*;image*//*”);//同时选择视频和图片
                    //intent.setType("**/*//*");//无类型限制
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //startActivityForResult(intent, 10001);
                 //   break;
            }
        }
    };
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            str_custom_filename_format = txt_custom_filename_format.getText().toString();
            String thisAppName = AppUtils.getAppName(context);
            String thisPackageName = AppUtils.getPackageName(context);
            String thisVersionName = AppUtils.getVersionName(context);
            String thisVersionCode = AppUtils.getVersionCode(context);
            String preview = str_custom_filename_format.replace("#N",thisAppName).replace("#P",thisPackageName).replace("#V",thisVersionName).replace("#C",thisVersionCode);
            txt_custom_filename_preview.setText(preview);
        }
    };

    Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            Log.i(TAG, "onPreferenceChange: "+ preference.getKey() + ":" + o);
           if(getString(R.string.key_custom_filename_format).equals(preference.getKey())) {
               String otr = o.toString();
               str_custom_filename_format = txt_custom_filename_format.getText().toString();
               Settings.setCustomFileNameFormat(str_custom_filename_format);
               prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());
               return false;
           /*}else if(getString(R.string.key_restore_default_settings).equals(preference.getKey())){
               Log.i("Settings", "key_restore_default_settings");
               return false;*/
           }else if(preference.getKey().equals(getResources().getString(R.string.key_is_show_local_apk))){
               if((Boolean)o) {
                   if (verifyStoragePermissions(getActivity())) {
                       //已获得权限
                       setActivityResultCode(Activity.RESULT_OK);
                       return true;
                   } else {
                       //Settings.setShowLocalApk(false);
                       // 未获得权限
                       requestStoragePermissions(fragment,REQUEST_EXTERNAL_STORAGE_SHOWLOCALAPK);
                       return false;
                   }
               }
               setActivityResultCode(Activity.RESULT_OK);
               return true;
           }else if(preference.getKey().equals(getResources().getString(R.string.key_is_toolbar_fixed))){
               setActivityResultCode(Activity.RESULT_OK);
               return true;
           }else if(preference.getKey().equals(getResources().getString(R.string.key_is_auto_clean_exportDir))){
               if((Boolean)o) {
                   if (verifyStoragePermissions(getActivity())) {
                       //已获得权限
                       return true;
                   } else {
                       //Settings.setShowLocalApk(false);
                       // 未获得权限
                       requestStoragePermissions(fragment,REQUEST_EXTERNAL_STORAGE_AUTOCLEAN);
                       return false;
                   }
               }
               return true;
           }else if(getString(R.string.key_custom_export_path).equals(preference.getKey())){
               //str_custom_export_path = txt_custom_export_path.getText().toString();
               String value=(String)o;
               String arr[]=value.split(":");
               if(arr.length>=1) {
                   str_custom_export_path = arr[0];
               }
               Settings.setCustomExportPath(str_custom_export_path);
               prefCustomExportPath.setSummary(Settings.getCustomExportPath());
               return false;
           }else if(getString(R.string.key_long_press_action).equals(preference.getKey())){
               prefLongPressAction.setValue(o.toString());
               prefLongPressAction.setSummary(prefLongPressAction.getEntry());
               return true;
           }else if(getString(R.string.key_dark_mode).equals(preference.getKey())){
               String darkMode = o.toString();
               prefDarkMode.setValue(darkMode);
               MyApplication.setDarkMode(darkMode);
               prefDarkMode.setSummary(prefDarkMode.getEntry());
               return true;
           }else if(getString(R.string.key_language).equals(preference.getKey())){
               prefLanguage.setValue(o.toString());
               prefLanguage.setSummary(prefLanguage.getEntry());
               // todo: update Appcompat to 1.6.0  for using AppCompatDelegate.setApplicationLocales(appLocale);
               //   reference https://developer.android.com/guide/topics/resources/app-languages#use-localeconfig
               setActivityResultCode(Activity.RESULT_OK);
               ((Activity)context).recreate();
               return true;
           }else if(getString(R.string.key_sort_type).equals(preference.getKey())){
               sortType.setValue(o.toString());
               sortType.setSummary(sortType.getEntry());
               Settings.setIsNeedLoad(true);
               return true;
           }else if(getString(R.string.key_sort_order).equals(preference.getKey())){
               sortOrder.setValue(o.toString());
               sortOrder.setSummary(sortOrder.getEntry());
               Settings.setIsNeedLoad(true);
               return true;
           }else {
               return true;
           }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode="+requestCode);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_SHOWLOCALAPK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                //Settings.setShowLocalApk(true);
                prefIsShowLocalApk.setChecked(true);
            } else {
                if(isNeverAskStoragePermissions(this)){
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_go_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //isOnPermission = true;
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsShowLocalApk.setChecked(false);
                                    //finish();
                                }
                            }).show();
                }else {
                    //权限被拒绝
                    //Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestStoragePermissions(fragment,REQUEST_EXTERNAL_STORAGE_SHOWLOCALAPK);
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsShowLocalApk.setChecked(false);
                                    //finish();
                                }
                            })
                            .show();
                }
            }
        }else if(requestCode == REQUEST_EXTERNAL_STORAGE_AUTOCLEAN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                //Settings.setShowLocalApk(true);
                prefIsAutoCleanExportDir.setChecked(true);
            } else {
                if (isNeverAskStoragePermissions(this)) {
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_auto_clean_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_go_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //isOnPermission = true;
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            }).show();
                } else {
                    //权限被拒绝
                    //Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_auto_clean_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestStoragePermissions(fragment, REQUEST_EXTERNAL_STORAGE_AUTOCLEAN);
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            })
                            .show();
                }
            }
        }else if(requestCode ==REQUEST_EXTERNAL_STORAGE_CLEAN_EXPORT_DIR){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                //Toast.makeText(context,R.string.clean_running,Toast.LENGTH_SHORT);
                FileUtils.cleanExportDir(context);
                FileUtils.cleanCacheDir(context);
                prefCleanExportDir.setSummary(R.string.setting_clean_export_dir_summery_clean_Running);
            } else {
                if (isNeverAskStoragePermissions(this)) {
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_auto_clean_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_go_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //isOnPermission = true;
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            }).show();
                } else {
                    //权限被拒绝
                    //Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_auto_clean_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestStoragePermissions(fragment, REQUEST_EXTERNAL_STORAGE_AUTOCLEAN);
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            })
                            .show();
                }
            }
        }else if(requestCode == FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，继续操作
                //Toast.makeText(context,R.string.clean_running,Toast.LENGTH_SHORT);

                if(mFilePickerDialog != null)
                {   //Show dialog if the read permission has been granted.
                    FileUtils.createIfNotExist(FileUtils.getFilePickerRecommendedDir());
                    mFilePickerDialog.show();
                }
            } else {
                if (isNeverAskStoragePermissions(fragment)){
                    //权限被拒绝，并勾选不再提示
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_custom_export_path_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_go_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //isOnPermission = true;
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            }).show();
                } else {
                    //权限被拒绝
                    //Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this.getActivity())
                            .setCancelable(false)
                            .setMessage(R.string.storage_permission_custom_export_path_dialog_content)
                            .setPositiveButton(R.string.storage_permission_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestStoragePermissions(fragment, REQUEST_EXTERNAL_STORAGE_AUTOCLEAN);
                                }
                            })
                            .setNegativeButton(R.string.storage_permission_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    prefIsAutoCleanExportDir.setChecked(false);
                                    //finish();
                                }
                            })
                            .show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**获取EditText光标所在的位置*/
    private int getEditTextCursorIndex(EditText mEditText){
        return mEditText.getSelectionStart();
    }
    /**向EditText指定光标位置插入字符串*/
    private void insertText(EditText mEditText, String mText){
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
    }
/*    *//**向EditText指定光标位置删除字符串*//*
    private void deleteText(EditText mEditText){
        if(!StringUtils.isEmpty(mEditText.getText().toString())){
            mEditText.getText().delete(getEditTextCursorIndex(mEditText)-1, getEditTextCursorIndex(mEditText));
        }
    }*/

}
