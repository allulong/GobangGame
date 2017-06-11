package com.logn.gobanggame.utils;

import android.graphics.Point;
import android.util.Log;

import com.logn.gobanggame.factory.base.IStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.logn.gobanggame.utils.ChessType.CHESS_BLACK;
import static com.logn.gobanggame.utils.ChessType.CHESS_BLANK;
import static com.logn.gobanggame.utils.ChessType.CHESS_WHITE;


/**
 * Created by long on 2017/6/5.
 */

public class PlayStrategy implements IStrategy{

    private int[] tupleScoreTable;
    private int[][] scoreTable; //每一个点的分数
    private int[][] scoreTableFormer;//记录前一步的评分表

    private int resetCount = 0; //悔棋步数

    public PlayStrategy() {
        tupleScoreTable = new int[11];
        tupleScoreTable[0] = 7;     //没有棋子的五元组的得分
        tupleScoreTable[1] = 35;    //1-4 指五元组存在1到四个黑棋的得分
        tupleScoreTable[2] = 800;
        tupleScoreTable[3] = 15000;
        tupleScoreTable[4] = 800000;
        tupleScoreTable[5] = 15;
        tupleScoreTable[1] = 35;    //5-8 指五元组存在1到四个白棋的得分
        tupleScoreTable[6] = 400;
        tupleScoreTable[7] = 1800;
        tupleScoreTable[8] = 100000;
        tupleScoreTable[9] = 0; //元组不存在
        tupleScoreTable[10] = 0;    //指五元组中既有黑棋又有白棋
        scoreTable = new int[15][15];
        //初始化评分表
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                //根据 五元组的数量*空元组的分数 初始化评分表
                scoreTable[row][col] = tuplesContainsPos(row, col) * tupleScoreTable[0];
            }
        }
        scoreTableFormer = new int[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                //根据 五元组的数量*空元组的分数 初始化评分表
                scoreTableFormer[row][col] = tuplesContainsPos(row, col) * tupleScoreTable[0];
            }
        }
    }


    public void resetOnStep() {
        if (resetCount <= 0) {
            return;
        }
        copyScoreTable(scoreTableFormer, scoreTable);
        resetCount = 0;
    }

    /**
     * 将评分表 scoreTable 复制到评分表 scoreTableFormer
     *
     * @param scoreTableFormer
     * @param scoreTable
     */
    private void copyScoreTable(int[][] scoreTable, int[][] scoreTableFormer) {
        if (scoreTableFormer.length != scoreTable.length || scoreTableFormer[0].length != scoreTable[0].length) {
            Log.e("没有复制啊", "123");
            return;
        }
        for (int i = 0; i < scoreTable.length; i++) {
            for (int j = 0; j < scoreTable[0].length; j++) {
                scoreTableFormer[i][j] = scoreTable[i][j];
            }
        }
    }

    /**
     * 获得包含位置(row,col)在内的五元组的数量
     *
     * @param row
     * @param col
     * @return
     */
    private int tuplesContainsPos(int row, int col) {
        if (row > 7) {
            row = 14 - row;
        }
        if (col > 7) {
            col = 14 - col;
        }
        if (row < col) {
            row += col;
            col = row - col;
            row = row - col;
        }
        if (col >= 4) {
            return 20;
        } else if (row >= 4) {
            return 3 * (col + 1) + 5;
        } else if ((col + row) == 4) {
            return 2 * (col + 1) + row + 1 + 1;
        } else if ((col + row) == 5) {
            return 2 * (col + 1) + row + 1 + 2;
        } else if ((col + row) == 6) {
            return 2 * (col + 1) + row + 1 + 3;
        } else {
            return 2 * (col + 1) + row + 1;
        }
    }

    /**
     * 获得下一步棋子的最佳位置
     *
     * @param chessPanel
     * @param chessType  接下来要下的棋子的颜色
     * @param stepIndex
     * @param prevStep
     * @return
     */
    public Point getNextStep(int[][] chessPanel, int chessType, int stepIndex, Point prevStep) {
        if (stepIndex == 0 && chessType == CHESS_BLACK) {
            //更新评分表
            Point p = new Point(7, 7);
            updateScoreTable(chessPanel, p, chessType);
            return p;
        } else if (stepIndex == 1 && chessType == CHESS_WHITE) {
            //将评分表改为防守模式
        }
        //更新评分表
        //玩家下子后，记录更新评分表前的值，并重置可悔棋步数为1
        copyScoreTable(scoreTable, scoreTableFormer);
        resetCount = 1;

        updateScoreTable(chessPanel, prevStep, chessType != CHESS_BLACK ? CHESS_BLACK : CHESS_WHITE);
        //寻找最优位置
        Point bestPos = findBestPos(chessPanel, chessType);
        //更新评分表
        updateScoreTable(chessPanel, bestPos, chessType);
        return bestPos;
    }

    private void showScoreTable(String data, int[][] scoreTable) {
        String score = data + ".\n";
        for (int i = 0; i < scoreTable.length; i++) {
            for (int j = 0; j < scoreTable[0].length; j++) {
                String s = "" + scoreTable[j][i] + "          ";
                s = s.substring(0, 10);
                score += "" + s;
            }
            score += "\n";
        }
        Log.e("显示分数表", "" + score);
    }

    /**
     * 根据刚下的棋子，更新评分表
     *
     * @param chessPanel 最新的棋子分布
     * @param point      刚下的棋子的位置
     * @param chessType  刚下的棋子的类型
     */
    private void updateScoreTable(int[][] chessPanel, Point point, int chessType) {
        //存储四条线的上的棋子的类型
        int[][] pointState = new int[4][9];
        int x, y;
        //水平方向
        for (x = point.x - 4; x < point.x + 5; x++) {
            if (x < 0 || x > 14) {
                pointState[0][x - point.x + 4] = ScoreType.VIRTUAL;//超出棋盘的位置
            } else {
                pointState[0][x - point.x + 4] = chessPanel[x][point.y];
            }
        }
        //竖直方向
        for (y = point.y - 4; y < point.y + 5; y++) {
            if (y < 0 || y > 14) {
                pointState[1][y - point.y + 4] = ScoreType.VIRTUAL;
            } else {
                pointState[1][y - point.y + 4] = chessPanel[point.x][y];
            }
        }
        //   \反斜线方向
        for (x = point.x - 4, y = point.y - 4; x < point.x + 5; x++, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[2][y - point.y + 4] = ScoreType.VIRTUAL;
            } else {
                pointState[2][y - point.y + 4] = chessPanel[x][y];
            }
        }
        //   /斜线方向
        for (x = point.x + 4, y = point.y - 4; x > point.x - 5; x--, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[3][y - point.y + 4] = ScoreType.VIRTUAL;
            } else {
                pointState[3][y - point.y + 4] = chessPanel[x][y];
            }
        }

        for (int i = 0; i < 4; i++) {
            pointState[i][4] = CHESS_BLANK;
        }

        int[][] formerTuples = new int[4][5];   //前一个元组
        int[][] changedTuples = new int[4][5];  //后一个元组

        int white, black, blank;
        //处理四条线上的元组
        for (int i = 0; i < 4; i++) {
            int start = 0;
            int finish = 0;
            //处理前面五个子，找到开始的子（棋盘内）
            for (int j = 0; j <= 4; j++) {
                if (pointState[i][j] == ScoreType.VIRTUAL) {
                    //第j(0-4)个位置中有一个是超出棋盘范围的则包含其的元组也赋值为   VIRTUAL
                    changedTuples[i][j] = formerTuples[i][j] = ScoreType.VIRTUAL;
                } else {
                    start = j;
                    break;
                }
            }
            //处理后面的五个子，找到结束的子（棋盘内）
            for (int j = 8; j >= 4; j--) {
                if (pointState[i][j] == ScoreType.VIRTUAL) {
                    //第j(8-4)个位置中有一个是超出棋盘范围的则包含其的元组也赋值为   VIRTUAL
                    changedTuples[i][j - 4] = formerTuples[i][j - 4] = ScoreType.VIRTUAL;
                } else {
                    finish = j;
                    break;
                }
            }
            white = 0;
            black = 0;
            blank = 0;
            //记录第i条线上前四个点的棋子类型的数量，没有棋子的位置也归为一种类型
            for (int j = start; j < start + 4; j++) {
                if (pointState[i][j] == CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == CHESS_WHITE) {
                    white++;
                }
            }
            //开始检查这条线上的五元组
            for (int j = start + 4; j <= finish; j++) {
                if (pointState[i][j] == CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == CHESS_WHITE) {
                    white++;
                }
                //处理下子前的元组
                if (black > 0 && white > 0) {   //元组中有黑棋和白棋
                    formerTuples[i][j - 4] = ScoreType.POLLUTED;
                } else if (black == 0 && white == 0) {  //元组中没有棋子
                    formerTuples[i][j - 4] = ScoreType.BLANK;
                } else if (black == 0) {    //元组中只有白棋
                    formerTuples[i][j - 4] = (white + 4) + ScoreType.BLANK;//对应到5-8
                } else {    //元组中只有黑棋
                    formerTuples[i][j - 4] = black + ScoreType.BLANK;    //对应到1-4
                }
                //deal with changedTuples,increase for change
                //处理下子后的元组
                if (chessType == CHESS_BLACK) {
                    black++;
                    blank--;
                } else {
                    white++;
                    blank--;
                }
                //recognize
                if (black > 0 && white > 0) {
                    changedTuples[i][j - 4] = ScoreType.POLLUTED;
                } else if (black == 0 && white == 0) {
                    changedTuples[i][j - 4] = ScoreType.BLANK;
                } else if (black == 0) {
                    changedTuples[i][j - 4] = (white + 4) + ScoreType.BLANK;
                } else {
                    changedTuples[i][j - 4] = black + ScoreType.BLANK;
                }
                //deal with changedTuples,decrease
                if (chessType == CHESS_BLACK) {
                    black--;
                    blank++;
                } else {
                    white--;
                    blank++;
                }

                //slide to next tuple ,so the first of current tuple should be dropped
                if (pointState[i][j - 4] == CHESS_BLANK) {
                    blank--;
                } else if (pointState[i][j - 4] == CHESS_BLACK) {
                    black--;
                } else if (pointState[i][j - 4] == CHESS_WHITE) {
                    white--;
                }
            }
        }

        //到这里已经将四条线上的元组情况记录下来了

        //存储下棋子前后的分数变化值
        int[][] changedScore = new int[4][5];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int score = tupleScoreTable[changedTuples[i][j] - ScoreType.BLANK] - tupleScoreTable[formerTuples[i][j] - ScoreType.BLANK];
                changedScore[i][j] = score;
            }
        }

        //存储下棋子前后的分数变化总值
        int[][] changedScoreSum = new int[4][9];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int sum = 0;
                for (int m = 0; m <= j; m++) {
                    sum += changedScore[i][m];
                }
                changedScoreSum[i][j] = sum;
            }
            for (int j = 8; j > 4; j--) {
                int sum = 0;
                for (int m = 8; m >= j; m--) {
                    sum += changedScore[i][m - 4];
                }
                changedScoreSum[i][j] = sum;
            }

        }


        //update center point,occupied point's score is 0
        scoreTable[point.x][point.y] = 0;
        //update general points
        //horizontal
        for (x = (point.x >= 4 ? point.x - 4 : 0);
             x <= (point.x <= 10 ? point.x + 4 : 14);
             x++) {
            if (scoreTable[x][point.y] != 0)//not occupied
            {
                scoreTable[x][point.y] += changedScoreSum[0][x - point.x + 4];
                if (scoreTable[x][point.y] < 0) {
                    scoreTable[x][point.y] = 0;
                }
            }
        }
        //vertical
        for (y = (point.y >= 4 ? point.y - 4 : 0);
             y <= (point.y <= 10 ? point.y + 4 : 14);
             y++) {
            if (scoreTable[point.x][y] != 0)//not occupied
            {
                scoreTable[point.x][y] += changedScoreSum[1][y - point.y + 4];
                if (scoreTable[point.x][y] < 0) {
                    scoreTable[point.x][y] = 0;
                }
            }
        }
        //backslash
        for (x = (point.x >= 4 ? point.x - 4 : 0),
                     y = (point.y >= 4 ? point.y - 4 : 0);
             x <= (point.x <= 10 ? point.x + 4 : 14) &&
                     y <= (point.y <= 10 ? point.y + 4 : 14);
             x++, y++) {
            if (scoreTable[x][y] != 0)//not occupied
            {
                scoreTable[x][y] += changedScoreSum[2][y - point.y + 4];
                if (scoreTable[x][y] < 0) {
                    scoreTable[x][y] = 0;
                }
            }
        }
        //slash
        for (x = (point.x <= 10 ? point.x + 4 : 14),
                     y = (point.y >= 4 ? point.y - 4 : 0);
             x >= (point.x >= 4 ? point.x - 4 : 0) &&
                     y <= (point.y <= 10 ? point.y + 4 : 14);
             x--, y++) {
            if (scoreTable[x][y] != 0)//not occupied
            {
                scoreTable[x][y] += changedScoreSum[3][x - point.x + 4];
                if (scoreTable[x][y] < 0) {
                    scoreTable[x][y] = 0;
                }
            }
        }
    }

    /**
     * 无效的位置
     * 当要实现有禁手的时候，用于存储黑棋不能下的位置的集合
     */
    List<Point> invalidBestPos = new ArrayList<>();

    /**
     * 根据棋盘评分表，寻找分数最大的位置的集合，并且随机返回其中一个位置
     *
     * @param chessPanel
     * @param chessType
     * @return
     */
    private Point findBestPos(int[][] chessPanel, int chessType) {
        //generally con not happen,all the position is not Okay
        if (invalidBestPos.size() + 1 >= 15 * 15) {
            return invalidBestPos.get((new Random()).nextInt(invalidBestPos.size()));
        }

        List<Point> bestPosList = new ArrayList<>();
        bestPosList.add(new Point(0, 0));
        //find the point whose score is highest
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Point p = new Point(col, row);
                if (!invalidBestPos.contains(p)) {
                    if (scoreTable[col][row] > scoreTable[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        bestPosList.clear();
                        bestPosList.add(p);
                    } else if (scoreTable[col][row] ==
                            scoreTable[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        bestPosList.add(p);
                    }
                }
            }
        }

        //generally connot happen,there is no bestPos
        if (bestPosList.size() == 0) {
            return new Point(-1, -1);
        }

        Random rnd = new Random();
        int index = rnd.nextInt(bestPosList.size());
        while (isFobidden(chessPanel, bestPosList.get(index), chessType)) {
            invalidBestPos.add(bestPosList.get(index));
            bestPosList.remove(index);
            index = rnd.nextInt(bestPosList.size());
            if (index == 0) {
                findBestPos(chessPanel, chessType);
            }
        }
        invalidBestPos.clear();
        Log.e("找出的最好的点的集合", "" + bestPosList.toString());
        return bestPosList.get(index);
    }

    /**
     * 用于有禁手，暂时不考虑有禁手的情况
     *
     * @param chessPanel
     * @param point
     * @param chessType
     * @return 有禁手的位置返回true，否则返回false
     */
    private boolean isFobidden(int[][] chessPanel, Point point, int chessType) {
        return false;
    }

}
