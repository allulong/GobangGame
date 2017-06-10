package com.logn.gobanggame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.logn.gobanggame.views.GameView;

public class HomeViewActivity extends AppCompatActivity {

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
                default:
                    Toast.makeText(HomeViewActivity.this, "...", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
