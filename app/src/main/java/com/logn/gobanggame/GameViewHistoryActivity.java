package com.logn.gobanggame;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.logn.gobanggame.utils.ChessArrayUtils;
import com.logn.gobanggame.views.GameView;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

/**
 * Created by long on 2017/6/19.
 */

public class GameViewHistoryActivity extends FragmentActivity {

    private List<Point> chessBlack;
    private List<Point> chessWhite;

    private FButton btnNext;
    private FButton btnFormer;

    private int chessCount;
    private GameView gameView;

    private int maxChessCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameview_history);

        initView();
        initData();
    }

    private void initView() {
        btnNext = (FButton) findViewById(R.id.btn_next_step);
        btnFormer = (FButton) findViewById(R.id.btn_former_step);
        gameView = (GameView) findViewById(R.id.game_view_history);

        btnFormer.setOnClickListener(listener);
        btnNext.setOnClickListener(listener);
    }

    private void initData() {
        chessBlack = new ArrayList<>(ChessArrayUtils.chessBlack);
        chessWhite = new ArrayList<>(ChessArrayUtils.chessWhite);

        chessCount = 0;
        maxChessCount = chessBlack.size() + chessWhite.size();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_former_step:
                    if (chessCount > 0) {
                        chessCount--;
                        updateChessPanel(chessCount, false);
                    } else {
//                        Toast.makeText(GameViewHistoryActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_next_step:
                    if (chessCount < maxChessCount) {
                        chessCount++;
                        updateChessPanel(chessCount, true);
                    } else {
                        Toast.makeText(GameViewHistoryActivity.this, "棋子已经下完", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(GameViewHistoryActivity.this, "...", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updateChessPanel(int chessCount, boolean add) {
        if (add) {
            if (chessCount % 2 == 1) {
                gameView.addBlackChess(chessBlack.get(gameView.getmChessBlackArray().size()));
            } else {
                gameView.addWhiteChess(chessWhite.get(gameView.getmChessWhiteArray().size()));
            }
        } else {
            if (chessCount % 2 != 1) {
                gameView.removeBlackChess(chessBlack.get(gameView.getmChessBlackArray().size() - 1));
            } else {
                gameView.removeWhiteChess(chessWhite.get(gameView.getmChessWhiteArray().size() - 1));
            }
        }
        gameView.updateChessPanel();
    }
}
