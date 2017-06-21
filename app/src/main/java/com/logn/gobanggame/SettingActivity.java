package com.logn.gobanggame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.logn.gobanggame.utils.DefaultValue;
import com.logn.gobanggame.utils.SpUtils;
import com.logn.titlebar.TitleBar;

/**
 * Created by long on 2017/6/11.
 */

public class SettingActivity extends FragmentActivity {

    private CheckBox mCheckBoxBGM;
    private TitleBar titleBar;

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

        titleBar = (TitleBar) findViewById(R.id.title_bar_setting);
        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

}
