package com.logn.gobanggame.utils;

import android.graphics.Point;

/**
 * Created by long on 2017/6/10.
 */

public class ChessJudger {

    /**
     * @param point
     * @return
     */
    public static boolean checkWin(int[][] chessPanel, Point point) {
        //从两条阴线和两条阳线中，距离为4的棋子内判断是否有五元组，如果有就返回true
        //水平方向
        int count = 0;
        int MAX_LINE = chessPanel.length;
        int curColor = chessPanel[point.x][point.y];
        for (int i = point.x - 4; i <= point.x + 4; i++) {
            if (i >= 0 && i < MAX_LINE) {
                if (chessPanel[i][point.y] == curColor) {
                    count++;
                } else {
                    count = 0;
                }
            }
            if (count == 5) {
                return true;
            }
        }
        //竖直方向
        count = 0;
        for (int i = point.y - 4; i <= point.y + 4; i++) {
            if (i >= 0 && i < MAX_LINE) {
                if (chessPanel[point.x][i] == curColor) {
                    count++;
                } else {
                    count = 0;
                }
            }
            if (count == 5) {
                return true;
            }
        }
        //反斜线方向
        count = 0;
        for (int i = point.x - 4, j = point.y - 4; i <= point.x + 4 && j <= point.y + 4; i++, j++) {
            if (i >= 0 && i < MAX_LINE && j >= 0 && j < MAX_LINE) {
                if (chessPanel[i][j] == curColor) {
                    count++;
                } else {
                    count = 0;
                }
            }
            if (count == 5) {
                return true;
            }
        }
        //斜线方向
        count = 0;
        for (int i = point.x + 4, j = point.y - 4; i >= point.x - 4 && j <= point.y + 4; i--, j++) {
            if (i >= 0 && i < MAX_LINE && j >= 0 && j < MAX_LINE) {
                if (chessPanel[i][j] == curColor) {
                    count++;
                } else {
                    count = 0;
                }
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }
}
