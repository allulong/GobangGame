package com.logn.gobanggame.views.utils;

import android.graphics.Point;

import com.logn.gobanggame.views.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2017/6/5.
 */

public class Caculators {
    public List<List<Point>> generator(int[][] chessPanel, int deep) {
        List<List<Point>> lists = new ArrayList<>();
        List<Point> neighbors = new ArrayList<>();
        List<Point> nextNeighbors = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (chessPanel[i][j] == GameView.CHESS_BLANK) {
                    Point point = new Point(i, j);
                    if (hsaNeighbor(chessPanel, point, 1, 1)) {
                        neighbors.add(point);
                    } else if (hsaNeighbor(chessPanel, point, 2, 2)) {
                        nextNeighbors.add(point);
                    }
                }
            }
        }
        lists.add(neighbors);
        lists.add(neighbors);
        return lists;
    }

    /**
     * 一定距离内的棋子数量不少于指定值，则返回true
     *
     * @param chessPanel 棋盘
     * @param point      将要下棋的点
     * @param distance   距离
     * @param count      范围内最少的棋子数量
     * @return
     */
    private boolean hsaNeighbor(int[][] chessPanel, Point point, int distance, int count) {
        int startX = point.x - distance;
        int startY = point.y - distance;
        int endX = point.x + distance;
        int endY = point.y + distance;
        for (int i = startX; i <= endX; i++) {
            if (i < 0 || i > 14) {
                continue;
            }
            for (int j = startY; j <= endY; j++) {
                if (j < 0 || j > 14) {
                    continue;
                }
                if (i == point.x && j == point.y) {//原来的点忽略
                    continue;
                }
                if (chessPanel[i][j] != GameView.CHESS_BLANK) {
                    count--;
                    if (count <= 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
