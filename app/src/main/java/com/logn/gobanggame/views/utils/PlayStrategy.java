package com.logn.gobanggame.views.utils;

import android.graphics.Point;
import android.util.Log;

import com.logn.gobanggame.views.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by long on 2017/6/5.
 */

public class PlayStrategy {

    private int[] tupleScoreTable;
    private int[][] scoreTable; //每一个点的分数
    private int[][] scoreTableHum;
    private int[][] scoreTableCom;

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
        scoreTableHum = new int[15][15];
        //初始化评分表
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                //根据 五元组的数量*空元组的分数 初始化评分表
                scoreTableHum[row][col] = tuplesContainsPos(row, col) * tupleScoreTable[0];
            }
        }
        scoreTableCom = new int[15][15];
        //初始化评分表
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                //根据 五元组的数量*空元组的分数 初始化评分表
                scoreTableCom[row][col] = tuplesContainsPos(row, col) * tupleScoreTable[0];
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
     * @param chessType
     * @param stepIndex
     * @param prevStep
     * @return
     */
    public Point getNextStep(int[][] chessPanel, int chessType, int stepIndex, Point prevStep) {
        if (stepIndex == 0 && chessType == GameView.CHESS_BLACK) {
            //更新评分表
            Point p = new Point(7, 7);
            updateScoreTable(chessPanel, p, chessType);
            return p;
        } else if (stepIndex == 1 && chessType == GameView.CHESS_WHITE) {
            //将评分表改为防守模式
        }
        //更新评分表
//        updateScoreTable(chessPanel, prevStep, chessType == GameView.CHESS_BLACK ? GameView.CHESS_BLACK : GameView.CHESS_WHITE);
        updateScoreHum(chessPanel);
        updateScoreCom(chessPanel);
        //寻找最优位置
        showScoreTable();
        Point bestPos = findBestPos(chessPanel, chessType);
        //更新评分表
//        updateScoreTable(chessPanel, bestPos, chessType);
        updateScoreHum(chessPanel);
        updateScoreCom(chessPanel);
        return bestPos;
    }

    private void showScoreTable() {
        String score = ".\n";
        for (int i = 0; i < scoreTableCom.length; i++) {
            for (int j = 0; j < scoreTableCom[0].length; j++) {
                score += "\t\t" + scoreTableCom[i][j];
            }
            score += "\n";
        }
        Log.e("显示分数表", "" + score);
    }


    private void updateScoreCom(int[][] chessPanel) {
        updateScore(chessPanel, GameView.CHESS_BLACK);
    }

    private void updateScoreHum(int[][] chessPanel) {
        updateScore(chessPanel, GameView.CHESS_WHITE);
    }


    private void updateScore(int[][] chessPanel, int chessType) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (chessPanel[i][j] == GameView.CHESS_BLANK) {
                    if (chessType == GameView.CHESS_BLACK) {
                        scoreTableCom[i][j] = getScore(chessPanel, chessType, new Point(i, j));
                    } else {
                        scoreTableHum[i][j] = getScore(chessPanel, chessType, new Point(i, j));
                    }
                }
            }
        }
    }

    /**
     * 计算在位置point下chessType类型的棋子的分数
     *
     * @param chessPanel
     * @param point
     * @param chessType
     */
    private int getScore(int[][] chessPanel, int chessType, Point point) {
        //存储四条线的上的棋子的类型
        int[][] pointState = new int[4][9];
        int x, y;
        //水平方向
        for (x = point.x - 4; x < point.x + 5; x++) {
            if (x < 0 || x > 14) {
                pointState[0][x - point.x + 4] = GameView.VIRTUAL;//超出棋盘的位置
            } else {
                pointState[0][x - point.x + 4] = chessPanel[x][point.y];
            }
        }
        //竖直方向
        for (y = point.y - 4; y < point.y + 5; y++) {
            if (y < 0 || y > 14) {
                pointState[1][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[1][y - point.y + 4] = chessPanel[point.x][y];
            }
        }
        //   \反斜线方向
        for (x = point.x - 4, y = point.y - 4; x < point.x + 5; x++, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[2][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[2][y - point.y + 4] = chessPanel[x][y];
            }
        }
        //   /斜线方向
        for (x = point.x + 4, y = point.y - 4; x > point.x - 5; x--, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[3][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[3][y - point.y + 4] = chessPanel[x][y];
            }
        }

        for (int i = 0; i < 4; i++) {
            pointState[i][4] = GameView.CHESS_BLANK;
            //           pointState[i][4] = chessType;
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
                if (pointState[i][j] == GameView.VIRTUAL) {
                    changedTuples[i][j] = formerTuples[i][j] = GameView.VIRTUAL;
                } else {
                    start = j;
                    break;
                }
            }
            //处理后面的五个子，找到结束的子（棋盘内）
            for (int j = 8; j >= 4; j--) {
                if (pointState[i][j] == GameView.VIRTUAL) {
                    changedTuples[i][j - 4] = formerTuples[i][j - 4] = GameView.VIRTUAL;
                } else {
                    finish = j;
                    break;
                }
            }
            white = 0;
            black = 0;
            blank = 0;
            //记录前四个点的棋子类型的数量，没有棋子的位置也归为一种类型
            for (int j = start; j < start + 4; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
            }
            //开始检查这条线上的五元组
            for (int j = start + 4; j <= finish; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
                //deal with formerTuples
                if (black > 0 && white > 0) {   //元组中有黑棋和白棋
                    formerTuples[i][j - 4] = GameView.POLLUTED;
                } else if (black == 0 && white == 0) {  //元组中没有棋子
                    formerTuples[i][j - 4] = GameView.BLANK;
                } else if (black == 0) {    //元组中只有白棋
                    formerTuples[i][j - 4] = (white + 4) + GameView.BLANK;//对应到5-8
                } else {    //元组中只有黑棋
                    formerTuples[i][j - 4] = black + GameView.BLANK;    //对应到1-4
                }
                //deal with changedTuples,increase for change
                //加上在此位置下一个棋子后的情况
                if (chessType == GameView.CHESS_BLACK) {
                    black++;
                } else {
                    white++;
                }
                //recognize
                if (black > 0 && white > 0) {
                    changedTuples[i][j - 4] = GameView.POLLUTED;
                } else if (black == 0 && white == 0) {
                    changedTuples[i][j - 4] = GameView.BLANK;
                } else if (black == 0) {
                    changedTuples[i][j - 4] = (white + 4) + GameView.BLANK;
                } else {
                    changedTuples[i][j - 4] = black + GameView.BLANK;
                }
                //deal with changedTuples,decrease
                if (chessType == GameView.CHESS_BLACK) {
                    black--;
                } else {
                    white--;
                }

                //slide to next tuple ,so the first of current tuple should be dropped
                if (pointState[i][j - 4] == GameView.CHESS_BLANK) {
                    blank--;
                } else if (pointState[i][j - 4] == GameView.CHESS_BLACK) {
                    black--;
                } else if (pointState[i][j - 4] == GameView.CHESS_WHITE) {
                    white--;
                }
            }
        }

        //到这里已经将四条线上的元组情况记录下来了

        //存储下棋子前后的分数变化值
        int score = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                score += tupleScoreTable[changedTuples[i][j] - GameView.BLANK];//根据元组情况叠加分数
            }
        }
        return score;
    }

    /**
     * 更新评分表
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
                pointState[0][x - point.x + 4] = GameView.VIRTUAL;//超出棋盘的位置
            } else {
                pointState[0][x - point.x + 4] = chessPanel[x][point.y];
            }
        }
        //竖直方向
        for (y = point.y - 4; y < point.y + 5; y++) {
            if (y < 0 || y > 14) {
                pointState[1][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[1][y - point.y + 4] = chessPanel[point.x][y];
            }
        }
        //   \反斜线方向
        for (x = point.x - 4, y = point.y - 4; x < point.x + 5; x++, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[2][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[2][y - point.y + 4] = chessPanel[x][y];
            }
        }
        //   /斜线方向
        for (x = point.x + 4, y = point.y - 4; x > point.x - 5; x--, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[3][y - point.y + 4] = GameView.VIRTUAL;
            } else {
                pointState[3][y - point.y + 4] = chessPanel[x][y];
            }
        }

        for (int i = 0; i < 4; i++) {
            pointState[i][4] = GameView.CHESS_BLANK;
            //           pointState[i][4] = chessType;
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
                if (pointState[i][j] == GameView.VIRTUAL) {
                    changedTuples[i][j] = formerTuples[i][j] = GameView.VIRTUAL;
                } else {
                    start = j;
                    break;
                }
            }
            //处理后面的五个子，找到结束的子（棋盘内）
            for (int j = 8; j >= 4; j--) {
                if (pointState[i][j] == GameView.VIRTUAL) {
                    changedTuples[i][j - 4] = formerTuples[i][j - 4] = GameView.VIRTUAL;
                } else {
                    finish = j;
                    break;
                }
            }
            white = 0;
            black = 0;
            blank = 0;
            //记录前四个点的棋子类型的数量，没有棋子的位置也归为一种类型
            for (int j = start; j < start + 4; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
            }
            //开始检查这条线上的五元组
            for (int j = start + 4; j <= finish; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
                //deal with formerTuples
                if (black > 0 && white > 0) {   //元组中有黑棋和白棋
                    formerTuples[i][j - 4] = GameView.POLLUTED;
                } else if (black == 0 && white == 0) {  //元组中没有棋子
                    formerTuples[i][j - 4] = GameView.BLANK;
                } else if (black == 0) {    //元组中只有白棋
                    formerTuples[i][j - 4] = (white + 4) + GameView.BLANK;//对应到5-8
                } else {    //元组中只有黑棋
                    formerTuples[i][j - 4] = black + GameView.BLANK;    //对应到1-4
                }
                //deal with changedTuples,increase for change
                //加上在此位置下一个棋子后的情况
                if (chessType == GameView.CHESS_BLACK) {
                    black++;
                } else {
                    white++;
                }
                //recognize
                if (black > 0 && white > 0) {
                    changedTuples[i][j - 4] = GameView.POLLUTED;
                } else if (black == 0 && white == 0) {
                    changedTuples[i][j - 4] = GameView.BLANK;
                } else if (black == 0) {
                    changedTuples[i][j - 4] = (white + 4) + GameView.BLANK;
                } else {
                    changedTuples[i][j - 4] = black + GameView.BLANK;
                }
                //deal with changedTuples,decrease
                if (chessType == GameView.CHESS_BLACK) {
                    black--;
                } else {
                    white--;
                }

                //slide to next tuple ,so the first of current tuple should be dropped
                if (pointState[i][j - 4] == GameView.CHESS_BLANK) {
                    blank--;
                } else if (pointState[i][j - 4] == GameView.CHESS_BLACK) {
                    black--;
                } else if (pointState[i][j - 4] == GameView.CHESS_WHITE) {
                    white--;
                }
            }
        }

        //到这里已经将四条线上的元组情况记录下来了

        //存储下棋子前后的分数变化值
        int[][] changedScore = new int[4][5];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int score = tupleScoreTable[changedTuples[i][j] - GameView.BLANK] - tupleScoreTable[formerTuples[i][j] - GameView.BLANK];
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
            for (x = (point.x >= 4 ? point.x - 4 : 0),
                         y = (point.y <= 10 ? point.y + 4 : 14);
                 x <= (point.x <= 10 ? point.x + 4 : 14) &&
                         y >= (point.y >= 4 ? point.y - 4 : 0);
                 x++, y--) {
                if (scoreTable[x][y] != 0)//not occupied
                {
                    scoreTable[x][y] += changedScoreSum[3][x - point.x + 4];
                    if (scoreTable[x][y] < 0) {
                        scoreTable[x][y] = 0;
                    }
                }
            }
        }
    }

    List<Point> invalidBestPos = new ArrayList<>();

    private Point findBestPos(int[][] chessPanel, int chessType) {
        //一般都不会发生，所有的位置都不够好
        if (invalidBestPos.size() >= 15 * 15) {
            return invalidBestPos.get((new Random()).nextInt(invalidBestPos.size()));
        }

        List<Point> bestPosList = new ArrayList<>();
        bestPosList.add(new Point(0, 0));   //添加第一个点
        //寻找分数最高的点
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if (!invalidBestPos.contains(new Point(x, y))) {//确保不会添加无效的点，即有禁手的点
                    if (scoreTableCom[y][x] > scoreTableCom[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        //找到一个分数更大的点，清除之前的点，添加现在的点
                        Point p = new Point(x, y);
                        bestPosList.clear();
                        bestPosList.add(p);
                        Log.e("更好的分数", p.toString() + ":" + scoreTableCom[x][y] + "\t->" + bestPosList.toString());
                    } else if (scoreTableCom[y][x] ==
                            scoreTableCom[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        //找到另一个分数与之前最大分数的点相同的点，加入数组
                        Point p = new Point(x, y);
                        bestPosList.add(p);
                        Log.e("同样好的分数", p.toString() + ":" + scoreTableCom[x][y] + "\t->" + bestPosList.toString());
                    }
                }
            }
        }

        Log.e("最好的数组", "" + bestPosList.toString());
        //通常不会发生，没有最好的点，即已经没有可以下棋子的位置了
        if (bestPosList.size() == 0) {
            return new Point(-1, -1);
        }

        //找出分数最好的一组位置，在其中随机选择一个位置作为电脑的下一步
        Random rnd = new Random();
        int index = rnd.nextInt(bestPosList.size());
        //随机选择一个点，在有禁手的情况下，如果这个点是禁手位置，则移除
        while (isFobidden(chessPanel, bestPosList.get(index), chessType)) {
            invalidBestPos.add(bestPosList.get(index));
            bestPosList.remove(index);
            if (bestPosList.isEmpty()) {   //如为全部都有禁手，则继续寻找分数低一点的点
                return findBestPos(chessPanel, chessType);
            }
            index = rnd.nextInt(bestPosList.size());
        }
        invalidBestPos.clear();
        return bestPosList.get(index);
    }

    /**
     * 用于有禁手
     *
     * @param chessPanel
     * @param point
     * @param chessType
     * @return
     */
    private boolean isFobidden(int[][] chessPanel, Point point, int chessType) {
//        Referee platformCheck = new Referee();
//        PlayStepResultEnum stepResult=platformCheck.Check(chessBoard, new ChessBoardPoint(point.Row, point.Col), PieceTypeEnum.Black);
//        //it seems the platform does not check four-three-three
//
//        if (stepResult == PlayStepResultEnum.Four_Four ||
//                stepResult == PlayStepResultEnum.Three_Three ||
//                stepResult == PlayStepResultEnum.Overline)
//        {
//            if (pieceType == PieceTypeEnum.Black)
//            {
//                return true;
//            }
//            else
//            {
//                stepResult = platformCheck.Check(chessBoard, new ChessBoardPoint(point.Row, point.Col), PieceTypeEnum.White);
//                if (stepResult == PlayStepResultEnum.Four_Four ||
//                        stepResult == PlayStepResultEnum.Three_Three||
//                        stepResult==PlayStepResultEnum.Win)
//                {
//                    return false;
//                }
//            }
//        }


        return false;
    }

}
