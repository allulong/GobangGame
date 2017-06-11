package com.logn.gobanggame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.logn.gobanggame.utils.DefaultValue;
import com.logn.gobanggame.utils.SpUtils;

/**
 * Created by long on 2017/6/11.
 */

public class SettingActivity extends FragmentActivity {

    private CheckBox mCheckBoxBGM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initValue();
    }

    private void initValue() {
        boolean canPlay = (boolean) SpUtils.get(this, DefaultValue.KEY_SP_BGM_PERMISSION, false);
        mCheckBoxBGM.setChecked(canPlay);
    }

    private void initView() {
        mCheckBoxBGM = (CheckBox) findViewById(R.id.setting_checkbox_bgm);
        mCheckBoxBGM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.put(SettingActivity.this, DefaultValue.KEY_SP_BGM_PERMISSION, isChecked);
            }
        });
    }

}
