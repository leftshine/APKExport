package com.echooit.apkexport.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
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

import com.echooit.apkexport.R;
import com.echooit.apkexport.utils.AppUtils;
import com.echooit.apkexport.utils.Settings;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment {

    private SwitchPreference prefIsAutoClean;
    private EditTextPreference prefCustomFileNameformat ;
    private static Dialog dialogCustomFileNameformat;
    private EditText txt_custom_filename_format;
    private Button btn_insert_N;
    private Button btn_insert_P;
    private Button btn_insert_V;
    private Button btn_insert_default;
    private TextView txt_custom_filename_preview;
    private String str_custom_filename_format;
    private Context context;
    public SettingActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().setSharedPreferencesName(Settings.SETTING_PREF_NAME);
        initViews();
    }

    private void initViews() {
        prefIsAutoClean = (SwitchPreference)findPreference(getString(R.string.key_is_auto_clean));
        prefCustomFileNameformat= (EditTextPreference)findPreference(getString(R.string.key_custom_filename_format));
        prefCustomFileNameformat.setOnPreferenceClickListener(preferenceclickListener);
        prefCustomFileNameformat.setOnPreferenceChangeListener(preferenceChangeListener);
        prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());
    }
    Preference.OnPreferenceClickListener preferenceclickListener=new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // TODO Auto-generated method stub
            //Log.d("config","clickListener");

            //点击Manually_Server
            if(preference.getKey().equals(getResources().getString(R.string.key_custom_filename_format)))
            {
                dialogCustomFileNameformat = prefCustomFileNameformat.getDialog();
                txt_custom_filename_format = (EditText)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_format);
                btn_insert_N = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_N);
                btn_insert_P = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_P);
                btn_insert_V = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_V);
                btn_insert_default = (Button)dialogCustomFileNameformat.findViewById(R.id.btn_insert_default);
                txt_custom_filename_preview = (TextView)dialogCustomFileNameformat.findViewById(R.id.txt_custom_filename_preview);
                btn_insert_N.setOnClickListener(onClickListener);
                btn_insert_P.setOnClickListener(onClickListener);
                btn_insert_V.setOnClickListener(onClickListener);
                btn_insert_default.setOnClickListener(onClickListener);
                txt_custom_filename_format.addTextChangedListener(textWatcher);
                txt_custom_filename_format.setText(Settings.getCustomFileNameFormat());
            }
            return false;
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
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
                case R.id.btn_insert_default:
                    insertText(txt_custom_filename_format,"#N-#P-#V");
                    break;
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
            String preview = str_custom_filename_format.replace("#N",thisAppName).replace("#P",thisPackageName).replace("#V",thisVersionName);
            txt_custom_filename_preview.setText(preview);
        }
    };

    Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
           String otr=  o.toString();
           str_custom_filename_format = txt_custom_filename_format.getText().toString();
           Settings.setCustomFileNameFormat(str_custom_filename_format);
           prefCustomFileNameformat.setSummary(Settings.getCustomFileNameFormat());
           return false;
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
