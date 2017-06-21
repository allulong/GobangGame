package com.logn.gobanggame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.logn.gobanggame.views.GameView;

import info.hoang8f.widget.FButton;

public class HomeViewActivity extends AppCompatActivity {

    private FButton btnPerson2Person;
    private FButton btnPerson2Machine;
    private FButton btnSetting;
    private FButton btnHistory;

    private long lastPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        initView();
        initValue();
    }

    private void initView() {
        btnPerson2Person = (FButton) findViewById(R.id.btn_home_p2p);
        btnPerson2Machine = (FButton) findViewById(R.id.btn_home_p2m);
        btnSetting = (FButton) findViewById(R.id.btn_home_setting);
        btnHistory = (FButton) findViewById(R.id.btn_home_history);
    }

    private void initValue() {
        btnPerson2Person.setOnClickListener(btnListener);
        btnPerson2Machine.setOnClickListener(btnListener);
        btnSetting.setOnClickListener(btnListener);
        btnHistory.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_home_p2p:
                    Intent intent1 = new Intent();
                    intent1.setClass(HomeViewActivity.this, GameViewActivity.class);
                    intent1.putExtra(GameView.MODE, true);
                    startActivity(intent1);
                    break;
                case R.id.btn_home_p2m:
                    Intent intent2 = new Intent();
                    intent2.setClass(HomeViewActivity.this, GameViewActivity.class);
                    intent2.putExtra(GameView.MODE, false);
                    startActivity(intent2);
                    break;
                case R.id.btn_home_history:
                    //跳转到历史列表界面
                    Intent intent3 = new Intent();
                    intent3.setClass(HomeViewActivity.this, HistoryActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.btn_home_setting:
                    Intent setting = new Intent();
                    setting.setClass(HomeViewActivity.this, SettingActivity.class);
                    startActivity(setting);
                    break;
                default:
                    Toast.makeText(HomeViewActivity.this, "...", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        long newPressTime = System.currentTimeMillis();
        if ((newPressTime - lastPressTime) > 2000) {
            Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT).show();
            lastPressTime = newPressTime;
        } else {
            finish();
        }

    }
}
