package com.logn.gobanggame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.logn.titlebar.TitleBar;

public class HomeViewActivity extends AppCompatActivity {

    private TitleBar titlebar;
    private Button person2Person;
    private Button person2Machine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        initView();
        initValue();
    }

    private void initView() {
        titlebar = (TitleBar) findViewById(R.id.title_bar_home);
        person2Person = (Button) findViewById(R.id.btn_home_p2p);
        person2Machine = (Button) findViewById(R.id.btn_home_p2m);
    }

    private void initValue() {
        person2Person.setOnClickListener(btnListener);
        person2Machine.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_home_p2p:
                    Toast.makeText(HomeViewActivity.this, "对弈模式", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(HomeViewActivity.this, GameViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_home_p2m:
                    Toast.makeText(HomeViewActivity.this, "人机对战", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(HomeViewActivity.this, "...", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
