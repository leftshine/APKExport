package cn.leftshine.apkexport.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.angads25.filepicker.view.FilePickerPreference;

import cn.leftshine.apkexport.R;
import cn.leftshine.apkexport.utils.AppUtils;
import cn.leftshine.apkexport.utils.Settings;
import cn.leftshine.apkexport.view.ClearEditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment {

    private SwitchPreference prefIsAutoClean;
    private EditTextPreference prefCustomFileNameformat;
    private ListPreference prefLongPressAction;
    private ListPreference sortType;
    private ListPreference sortOrder;
    private static Dialog dialogCustomFileNameformat,dialogCustomExportPath;
    //private EditText txt_custom_filename_format;
    private ClearEditText txt_custom_filename_format,txt_custom_export_path;
    private Button btn_insert_N;
    private Button btn_insert_P;
    private Button btn_insert_V;
    private Button btn_insert_C;
    private Button btn_insert_default;
    private Button btn_open_custom_path;
    private TextView txt_custom_filename_preview;
    private String str_custom_filename_format,str_custom_export_path;
    private Context context;
    private FilePickerPreference prefCustomExportPath;


    public SettingActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        addPreferencesFromResource(cn.leftshine.apkexport.R.xml.preferences);
        //getPreferenceManager().setSharedPreferencesName(Settings.SETTING_PREF_NAME);
        Settings.setIsNeedLoad(false);
        initViews();
    }

    private void initViews() {
        prefIsAutoClean = (SwitchPreference)findPreference(getString(R.string.key_is_auto_clean));
        prefCustomFileNameformat= (EditTextPreference)findPreference(getString(R.string.key_custom_filename_format));
        prefCustomFileNameformat.setOnPreferenceClickListener(preferenceclickListener);
        prefCustomFileNameformat.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());

        prefCustomExportPath = (FilePickerPreference)findPreference(getString(R.string.key_custom_export_path));
        //prefCustomExportPath.setOnPreferenceClickListener(preferenceclickListener);
        prefCustomExportPath.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomExportPath.setSummary(Settings.getCustomExportPath());

        prefLongPressAction = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_long_press_action));
        prefLongPressAction.setOnPreferenceChangeListener(preferenceChangeListener);
        prefLongPressAction.setSummary(prefLongPressAction.getEntry());

        sortType = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_sort_type));
        sortType.setOnPreferenceChangeListener(preferenceChangeListener);
        sortType.setSummary(sortType.getEntry());

        sortOrder = (ListPreference)findPreference(getString(cn.leftshine.apkexport.R.string.key_sort_order));
        sortOrder.setOnPreferenceChangeListener(preferenceChangeListener);
        sortOrder.setSummary(sortOrder.getEntry());

    }
    Preference.OnPreferenceClickListener preferenceclickListener=new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // TODO Auto-generated method stub
            //Log.d("config","clickListener");

            if(preference.getKey().equals(getResources().getString(R.string.key_custom_filename_format)))
            {
                dialogCustomFileNameformat = prefCustomFileNameformat.getDialog();
                /*txt_custom_filename_format = (EditText)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_format);*/
                txt_custom_filename_format = (ClearEditText) dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_format);
                btn_insert_N = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_N);
                btn_insert_P = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_P);
                btn_insert_V = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_V);
                btn_insert_C = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_C);
                btn_insert_default = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_default);
                txt_custom_filename_preview = (TextView)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_preview);
                btn_insert_N.setOnClickListener(onClickListener);
                btn_insert_P.setOnClickListener(onClickListener);
                btn_insert_V.setOnClickListener(onClickListener);
                btn_insert_C.setOnClickListener(onClickListener);
                btn_insert_default.setOnClickListener(onClickListener);
                txt_custom_filename_format.addTextChangedListener(textWatcher);
                txt_custom_filename_format.setText(Settings.getCustomFileNameFormat());
            }

            /*if(preference.getKey().equals(getResources().getString(R.string.key_custom_export_path)))
            {
                dialogCustomExportPath = prefCustomExportPath.getDialog();
                txt_custom_export_path = (ClearEditText) dialogCustomExportPath.findViewById(R.id.txt_custom_export_path);
                btn_open_custom_path = (Button)dialogCustomExportPath.findViewById(R.id.btn_open_custom_path);
                btn_open_custom_path.setOnClickListener(onClickListener);
                txt_custom_export_path.setText(Settings.getCustomExportPath());
            }*/
            return false;
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case cn.leftshine.apkexport.R.id.btn_insert_N:
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
           if(getString(R.string.key_custom_filename_format).equals(preference.getKey())) {
               String otr = o.toString();
               str_custom_filename_format = txt_custom_filename_format.getText().toString();
               Settings.setCustomFileNameFormat(str_custom_filename_format);
               prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());
               return false;
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
