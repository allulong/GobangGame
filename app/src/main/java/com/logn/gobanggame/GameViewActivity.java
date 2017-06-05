package com.logn.gobanggame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.logn.gobanggame.views.GameView;
import com.logn.gobanggame.views.interfaces.OnGameStateListener;
import com.logn.titlebar.TitleBar;

/**
 * Created by long on 2017/6/2.
 */

public class GameViewActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private GameView gameView;
    private Button btnRestart;
    private Button btnReSetOneStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        boolean modeP2P = intent.getBooleanExtra(GameView.MODE, false);

        gameView = (GameView) findViewById(R.id.game_view);
        btnRestart = (Button) findViewById(R.id.btn_restart_game_view);
        btnReSetOneStep = (Button) findViewById(R.id.btn_reset_one_step);
        titleBar = (TitleBar) findViewById(R.id.title_bar_game_view);

        if (modeP2P) {
            titleBar.setTitle(getResources().getString(R.string.person2person));
        } else {
            titleBar.setTitle(getResources().getString(R.string.person2machine));
        }
        gameView.setmModeIsP2P(modeP2P);

        gameView.setOnGameStateListener(gameStateListener);
        btnRestart.setOnClickListener(clickListener);
        btnReSetOneStep.setOnClickListener(clickListener);
    }

    private OnGameStateListener gameStateListener = new OnGameStateListener() {
        @Override
        public void changeState(boolean win, int chessType) {
            if (win) {
                Toast.makeText(GameViewActivity.this, "恭喜玩家" + chessType + "赢了", Toast.LENGTH_SHORT).show();
                showWinDialog(chessType);
            }
        }
    };

    private void showWinDialog(int chessType) {
        AlertDialog.Builder build = new AlertDialog.Builder(GameViewActivity.this);
        build.setTitle("游戏结束").setMessage("赢家：" + chessType).setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameView.reStart();
            }
        });
        build.create().show();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_restart_game_view:
                    gameView.reStart();
                    break;
                case R.id.btn_reset_one_step:
                    gameView.resetOneStep();
                    break;
                default:
            }
        }
    };
}
