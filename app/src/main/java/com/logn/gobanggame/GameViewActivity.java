package com.logn.gobanggame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.logn.gobanggame.views.GameView;
import com.logn.gobanggame.views.interfaces.OnGameStateListener;
import com.logn.titlebar.TitleBar;

import static com.logn.gobanggame.utils.ChessType.CHESS_BLACK;

/**
 * Created by long on 2017/6/2.
 */

public class GameViewActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private GameView gameView;
    private Button btnRestart;
    private Button btnReSetOneStep;

    private boolean mModeIsP2P;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        mModeIsP2P = intent.getBooleanExtra(GameView.MODE, false);

        gameView = (GameView) findViewById(R.id.game_view);
        btnRestart = (Button) findViewById(R.id.btn_restart_game_view);
        btnReSetOneStep = (Button) findViewById(R.id.btn_reset_one_step);
        titleBar = (TitleBar) findViewById(R.id.title_bar_game_view);

        if (mModeIsP2P) {
            titleBar.setTitle(getResources().getString(R.string.person2person));
        } else {
            titleBar.setTitle(getResources().getString(R.string.person2machine));
        }
        gameView.setModeIsP2P(mModeIsP2P);

        gameView.setOnGameStateListener(gameStateListener);
        btnRestart.setOnClickListener(clickListener);
        btnReSetOneStep.setOnClickListener(clickListener);
    }

    private OnGameStateListener gameStateListener = new OnGameStateListener() {
        @Override
        public void changeState(boolean win, int chessType) {
            if (win) {
                showWinDialog(chessType);
            }
        }
    };

    private void showWinDialog(int chessType) {
        View aalayout = View.inflate(GameViewActivity.this, R.layout.item_dialog_text, null);
        TextView content = (TextView) aalayout.findViewById(R.id.tv_item_dialog);
        String data = "";
        if (mModeIsP2P) {
            if (chessType == CHESS_BLACK) {
                data = "黑棋方赢了！";
            } else {
                data = "白棋方赢了！";
            }
        } else {
            if (chessType == CHESS_BLACK) {
                data = "电脑赢了！";
            } else {
                data = "恭喜你赢了！";
            }
        }
        content.setText(data);
        AlertDialog.Builder build = new AlertDialog.Builder(GameViewActivity.this);
        build.setView(aalayout)
                .setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
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
