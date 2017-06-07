package com.logn.gobanggame.views.utils;

import android.graphics.Point;
import android.util.Log;

import com.logn.gobanggame.views.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by long on 2017/6/6.
 */

public class DFS {

    private int totlePoints;
    int ABCut = 0;//AB剪枝次数

    public Point maxmin(int[][] chessPanel, int deep) {
        ABCut = 0;
        totlePoints = 0;
        int best = Scores.MIN;
        List<Point> points = generator(chessPanel, deep);
        List<Point> bestPoints = new ArrayList<>();

        if (points.size() == 0) {
            Log.e("fuck", "000000");
            return new Point(7, 7);
        }

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            chessPanel[point.x][point.y] = ChessPanel.CHESS_BLACK;  //尝试下一个黑子，默认电脑下黑子

            int v = min(chessPanel, deep - 1, Scores.MAX, best > Scores.MIN ? best : Scores.MIN);//开始找最大值
            if (v == best) {
                bestPoints.add(point);
            }

            if (v > best) {
                best = v;
                bestPoints.clear();
                bestPoints.add(point);
            }
            chessPanel[point.x][point.y] = ChessPanel.CHESS_BLANK;  //将尝试下的子移除
        }

        int len = bestPoints.size();
//        Log.e("数组长度", "长度：" + len);
        int r = new Random().nextInt(len) % len;
        Log.e("DFS", "随机选择：" + r + "   数组长度：" + len);
        Point bp = bestPoints.get(r);//实现随机选择一个位置

        Log.e("FLAG", "减掉的节点数量：" + ABCut);
        Log.e("FLAG", "当前局面分数：" + best);
        Log.e("FLAG", "随机选择的节点：(" + bp.x + "," + bp.y + ")");

        String lists = "";
        for (Point p : bestPoints) {
            lists += " (" + p.x + "," + p.y + ") ";
        }
        Log.e("FLAG", "best----> " + lists);
        return bp;
    }

    private int min(int[][] chessPanel, int deep, int alpha, int beta) {
        Log.e("FLAG_MIN", "深度：" + deep + "");
        int v = evaluate(chessPanel);
        totlePoints++;
        if (deep <= 0 || win(chessPanel) != GameView.CHESS_BLANK) {
            return v;
        }

        int best = Scores.MAX;
        List<Point> points = generator(chessPanel, deep);

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            chessPanel[p.x][p.y] = GameView.CHESS_WHITE;
            int vv = max(chessPanel, deep - 1, best < alpha ? best : alpha, beta);
            chessPanel[p.x][p.y] = GameView.CHESS_BLANK;

            if (vv < best) {
                best = vv;
            }
            if (vv < beta) {
                ABCut++;
                break;
            }
        }
        return best;
    }

    private int max(int[][] chessPanel, int deep, int alpha, int beta) {
//        Log.e("FLAG_MAX", "深度：" + deep + "");
        int v = evaluate(chessPanel);
        totlePoints++;

        if (deep <= 0 || win(chessPanel) != GameView.BLANK) {//到达深度或者此位置不为空，则跳出
            return v;
        }

        int best = Scores.MIN;
        List<Point> points = generator(chessPanel, deep);

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            chessPanel[p.x][p.y] = GameView.CHESS_BLACK;
            int vv = min(chessPanel, deep - 1, alpha, best > beta ? best : beta);
            chessPanel[p.x][p.y] = GameView.CHESS_BLANK;

            if (vv > best) {
                best = vv;
            }
            if (vv > alpha) {//AB剪枝
                ABCut++;
                break;
            }
        }
        return best;
    }

    private int win(int[][] chessPanel) {
        List<List<Integer>> rows = flat(chessPanel);

        for (int i = 0; i < rows.size(); i++) {
            int value = eRow(rows.get(i), GameView.CHESS_BLACK);
            if (value >= Scores.FIVE) {
                return GameView.CHESS_BLACK;
            }
            value = eRow(rows.get(i), GameView.CHESS_WHITE);
            if (value >= Scores.FIVE) {
                return GameView.CHESS_WHITE;
            }
        }
        return GameView.CHESS_BLANK;
    }

    private int evaluate(int[][] chessPanel) {
        List<List<Integer>> rows = flat(chessPanel);
        int humScore = eRows(rows, GameView.CHESS_WHITE);
        int comScore = eRows(rows, GameView.CHESS_BLACK);
        return comScore - humScore;
    }

    private int eRows(List<List<Integer>> rows, int chessType) {
        int r = 0;
        for (int i = 0; i < rows.size(); i++) {
            r += eRow(rows.get(i), chessType);
        }
        return r;
    }

    private int eRow(List<Integer> lines, int chessType) {
        int count = 0;
        int block = 0;
        int value = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i) == chessType) {
                count = 1;
                block = 0;
                if (i == 0) {
                    block = 1;
                } else if (lines.get(i) == chessType) {
                    block = 1;
                }

                for (i = i + 1; i < lines.size(); i++) {
                    if (lines.get(i) == chessType) {
                        count++;
                    } else {
                        break;
                    }
                }
                if (i == lines.size() || lines.get(i) != GameView.CHESS_BLANK) {
                    block++;
                }
                value += score(count, block);
            }
        }
        return value;
    }

    private int score(int count, int block) {
        if (count >= 5) {
            return Scores.FIVE;
        }
        if (block == 0) {
            switch (count) {
                case 1:
                    return Scores.ONE;
                case 2:
                    return Scores.TWO;
                case 3:
                    return Scores.THREE;
                case 4:
                    return Scores.FOUR;
            }
        } else {
            switch (count) {

                case 1:
                    return Scores.BLOCKED_ONE;
                case 2:
                    return Scores.BLOCKED_TWO;
                case 3:
                    return Scores.BLOCKED_THREE;
                case 4:
                    return Scores.BLOCKED_FOUR;
            }
        }
        return 0;
    }


    /**
     * 将棋盘上的信息转化为几个一维数组
     *
     * @param chessPanel
     * @return
     */
    private List<List<Integer>> flat(int[][] chessPanel) {
        List<List<Integer>> rows = new ArrayList<>();
        List<Integer> row;
        //横向
        for (int i = 0; i < 15; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                row.add(chessPanel[j][i]);
            }
            rows.add(row);
        }
        //纵向
        for (int i = 0; i < 15; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                row.add(chessPanel[i][j]);
            }
            rows.add(row);
        }

        // \/方向
        for (int i = 0; i < 2 * 15; i++) {
            row = new ArrayList<>();
            for (int j = 0; j <= i && j < 15; j++) {
                if (i - j < 15) {
                    row.add(chessPanel[j][i - j]);
                }
            }
            if (!row.isEmpty()) {
                rows.add(row);
            }
        }

        //  \\ 方向
        for (int i = -1 * 15 + 1; i < 15; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                if (j + i >= 0 && j + i < 15) {
                    row.add(chessPanel[j][j + i]);
                }
            }
            if (!row.isEmpty()) {
                rows.add(row);
            }
        }
        return rows;
    }


    /**
     * 在当前位置下一个棋子的分数
     *
     * @param chessPanel
     * @param point
     * @param chessType
     * @return
     */
    private int scorePoint(int[][] chessPanel, Point point, int chessType) {
        int result = 0;
        int count = 0, block = 0;

        int len = chessPanel.length;

        //横向
        count = 1;  //默认把当前位置当做己方棋子。因为算的是当前下了一个己方棋子后的分数
        block = 0;

        for (int i = point.x + 1; true; i++) {
            if (i >= len) {
                block++;
                break;
            }
            int t = chessPanel[i][point.y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i = point.x - 1; true; i--) {
            if (i < 0) {
                block++;
                break;
            }
            int t = chessPanel[i][point.y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        result += score(count, block);

        //纵向
        count = 1;
        block = 0;

        for (int i = point.y + 1; true; i++) {
            if (i >= len) {
                block++;
                break;
            }
            int t = chessPanel[point.x][i];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i = point.y - 1; true; i--) {
            if (i < 0) {
                block++;
                break;
            }
            int t = chessPanel[point.x][i];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        result += score(count, block);


        // \\
        count = 1;
        block = 0;

        for (int i = 1; true; i++) {
            int x = point.y + i, y = point.x + i;
            if (x >= len || y >= len) {
                block++;
                break;
            }
            int t = chessPanel[x][y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i = 1; true; i++) {
            int x = point.y - i, y = point.x - i;
            if (x < 0 || y < 0 || x >= 15 || y >= 15) {
                block++;
                break;
            }
            int t = chessPanel[x][y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        result += score(count, block);


        // \/
        count = 1;
        block = 0;

        for (int i = 1; true; i++) {
            int x = point.y + i, y = point.x - i;
            if (x >= len || y >= len) {
                block++;
                break;
            }
            int t = chessPanel[x][y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        for (int i = 1; true; i++) {
            int x = point.y - i, y = point.x + i;
            if (x < 0 || y < 0 || x >= 15 || y >= 15) {
                block++;
                break;
            }
            int t = chessPanel[x][y];
            if (t == GameView.CHESS_BLANK) {
                break;
            }
            if (t == chessType) {
                count++;
                continue;
            } else {
                block++;
                break;
            }
        }

        result += score(count, block);

        return result;
    }


    public List<Point> generator(int[][] chessPanel, int deep) {
        List<Point> neighbors = new ArrayList<>();
        List<Point> nextNeighbors = new ArrayList<>();
        List<Point> fives = null;
        List<Point> fours = null;
        List<Point> twoThrees = null;
        List<Point> threes = null;
        List<Point> twos = null;
        Log.e("遍历邻居", "深度：" + deep);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (chessPanel[i][j] == GameView.CHESS_BLANK) { //此位置必须为空
                    Point point = new Point(i, j);
                    if (hasNeighbor(chessPanel, point, 1, 1)) { //必须有邻居
                        int scoreHum = scorePoint(chessPanel, point, GameView.CHESS_WHITE);
                        int scoreCom = scorePoint(chessPanel, point, GameView.CHESS_BLACK);

                        if (scoreCom >= Scores.FIVE) {  //电脑能成五元组，将其他的删掉并且只返回这个点
                            Log.e("FLAG_TUPLES", "电脑5-" + point.toString() + "   ::" + scoreCom);
                            neighbors.clear();
                            nextNeighbors.clear();
                            neighbors.add(point);
                            return neighbors;
                        } else if (scoreHum >= Scores.FIVE) {//要是玩家能成五元组，加入到五元组数列中，但是不要现在返回，电脑还可能直接成五元组

                            Log.e("FLAG_TUPLES", "玩家5-" + point.toString() + "   ::" + scoreHum);
                            if (fives == null) {
                                fives = new ArrayList<>();
                            }
                            fives.add(point);
                        } else if (scoreCom >= Scores.FOUR) {
                            Log.e("FLAG_TUPLES", "电脑4-" + point.toString() + "   ::" + scoreCom);
                            if (fours == null) {
                                fours = new ArrayList<>();
                            }
                            fours.add(0, point);
                        } else if (scoreHum >= Scores.FOUR) {
                            Log.e("FLAG_TUPLES", "玩家4-" + point.toString() + "   ::" + scoreHum);
                            if (fours == null) {
                                fours = new ArrayList<>();
                            }
                            fours.add(point);
                        } else if (scoreCom >= 2 * Scores.THREE) {//双三
                            Log.e("FLAG_TUPLES", "电脑23-" + point.toString() + "   ::" + scoreCom);
                            if (twoThrees == null) {
                                twoThrees = new ArrayList<>();
                            }
                            twoThrees.add(0, point);
                        } else if (scoreHum >= 2 * Scores.THREE) {//双三
                            Log.e("FLAG_TUPLES", "玩家23-" + point.toString() + "   ::" + scoreHum);
                            if (twoThrees == null) {
                                twoThrees = new ArrayList<>();
                            }
                            twoThrees.add(point);
                        } else if (scoreCom >= Scores.THREE) {
                            Log.e("FLAG_TUPLES", "电脑3-" + point.toString() + "   ::" + scoreCom);
                            if (threes == null) {
                                threes = new ArrayList<>();
                            }
                            threes.add(0, point);
                        } else if (scoreHum >= Scores.THREE) {
                            Log.e("FLAG_TUPLES", "玩家3-" + point.toString() + "   ::" + scoreHum);
                            if (threes == null) {
                                threes = new ArrayList<>();
                            }
                            threes.add(point);
                        } else if (scoreCom >= Scores.TWO) {
                            Log.e("FLAG_TUPLES", "电脑2-" + point.toString() + "   ::" + scoreCom);
                            if (twos == null) {
                                twos = new ArrayList<>();
                            }
                            twos.add(0, point);
                        } else if (scoreHum >= Scores.TWO) {
                            Log.e("FLAG_TUPLES", "玩家2-" + point.toString() + "   ::" + scoreHum);
                            if (twos == null) {
                                twos = new ArrayList<>();
                            }
                            twos.add(point);
                        } else {
                            Log.e("FLAG_TUPLES", "其他0-" + point.toString() + "   ::" + scoreHum + "   ::" + scoreCom);
                            neighbors.add(point);
                        }
                    } else if (deep >= 2 && hasNeighbor(chessPanel, point, 2, 2)) {
                        nextNeighbors.add(point);
                    }
                }
            }
        }
        Log.e("FLAG", "邻居数组：" + neighbors.size() + "," + nextNeighbors.size());

        String r = "";
        for (Point p : neighbors) {
            r += " (" + p.x + "," + p.y + ") ";
        }
        Log.e("FLAG", "----> " + r);
        if (fives != null) {
            return fives;
        }
        if (fours != null) {
            return fours;
        }
        if (twoThrees != null) {
            return twoThrees;
        }
        //将剩下的点全部返回
        if (threes == null) {
            threes = new ArrayList<>();
        }
        if (twos != null) {
            threes.addAll(twos);
        }
        threes.addAll(neighbors);
        threes.addAll(nextNeighbors);
        return threes;
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
    private boolean hasNeighbor(int[][] chessPanel, Point point, int distance, int count) {
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
