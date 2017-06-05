package com.logn.gobanggame.views.utils;

import android.graphics.Point;

import com.logn.gobanggame.views.GameView;
import com.logn.gobanggame.views.interfaces.ChessBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by long on 2017/6/5.
 */

public class PlayStrategy {
    private int[] tupleScoreTable;
    private int[][] scoreTable;

    public PlayStrategy() {
        tupleScoreTable = new int[]{
                7, 35, 800, 15000, 800000,
                15, 400, 1800, 100000, 0, 0
        };
        scoreTable = new int[15][15];
        //初始化评分表
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                scoreTable[row][col] = tupleNumContainsPos(row, col) * tupleScoreTable[0];
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
    private int tupleNumContainsPos(int row, int col) {
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

    public Point getNextStep(GameView gameView, int chessType, int stepIndex, Point prevStep) {
        if (stepIndex == 0 && chessType == GameView.CHESS_BLACK) {
            //更新评分表
            return new Point(7, 7);
        } else if (stepIndex == 1 && chessType == GameView.CHESS_WHITE) {
            //将评分表改为防守模式
        }
        //更新评分表
        updateScoreTable(gameView, prevStep, chessType == GameView.CHESS_BLACK ? GameView.CHESS_BLACK : GameView.CHESS_WHITE);
        //寻找最优位置
        Point bestPos = findBestPos(gameView, chessType);
        //更新评分表
        updateScoreTable(gameView, prevStep, chessType);
        return bestPos;
    }

    private void updateScoreTable(ChessBoard chessBoard, Point point, int chessType) {
        int[][] pointState = new int[4][9];
        int x, y;
        //水平方向
        for (x = point.x - 4; x < point.x + 5; x++) {
            if (x < 0 || x > 14) {
                pointState[0][x - point.x + 4] = GameView.CHESS_VIRTUAL;
            } else {
                pointState[0][x - point.x + 4] = chessBoard.getPointState(x, point.y);
            }
        }
        //竖直方向
        for (y = point.y - 4; y < point.y + 5; y++) {
            if (y < 0 || y > 14) {
                pointState[1][y - point.y + 4] = GameView.CHESS_VIRTUAL;
            } else {
                pointState[1][y - point.y + 4] = chessBoard.getPointState(point.x, y);
            }
        }
        //反斜线方向
        for (x = point.x - 4, y = point.y - 4; x < point.x + 5; x++, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[2][y - point.y + 4] = GameView.CHESS_VIRTUAL;
            } else {
                pointState[2][y - point.y + 4] = chessBoard.getPointState(x, y);
            }
        }
        //斜线方向
        for (x = point.x + 4, y = point.y - 4; x > point.x - 5; x--, y++) {
            if (x < 0 || y < 0 || x > 14 || y > 14) {
                pointState[3][y - point.y + 4] = GameView.CHESS_VIRTUAL;
            } else {
                pointState[3][y - point.y + 4] = chessBoard.getPointState(x, y);
            }
        }
        for (int i = 0; i < 4; i++) {
            pointState[i][4] = GameView.CHESS_BLANK;
        }

        int[][] formerTuples = new int[4][5];
        int[][] changedTuples = new int[4][5];

        int white, black, blank;
        //处理四条线上的元组
        for (int i = 0; i < 4; i++) {
            int start = 0;
            int finish = 0;
            //处理前面四个子，找到开始的子（棋盘内）
            for (int j = 0; j < 4; j++) {
                if (pointState[i][j] == GameView.CHESS_VIRTUAL) {
                    changedTuples[i][j] = formerTuples[i][j] = GameView.CHESS_VIRTUAL;
                } else {
                    start = j;
                    break;
                }
            }
            //处理后面的四个子，找到结束的子（棋盘内）
            for (int j = 8; j > 4; j--) {
                if (pointState[i][j] == GameView.CHESS_VIRTUAL) {
                    changedTuples[i][j] = formerTuples[i][j] = GameView.CHESS_VIRTUAL;
                } else {
                    finish = j;
                    break;
                }
            }
            white = 0;
            black = 0;
            blank = 0;
            //存储每条线上的第一个五元组
            for (int j = start; j < start + 4; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
            }
            //
            //tuples recognition sliding
            for (int j = start + 4; j <= finish; j++) {
                if (pointState[i][j] == GameView.CHESS_BLANK) {
                    blank++;
                } else if (pointState[i][j] == GameView.CHESS_BLACK) {
                    black++;
                } else if (pointState[i][j] == GameView.CHESS_WHITE) {
                    white++;
                }
                //deal with formerTuples
                if (black > 0 && white > 0) {
                    formerTuples[i][j - 4] = GameView.POLLUTED;
                } else if (black == 0 && white == 0) {
                    formerTuples[i][j - 4] = GameView.BLANK;
                } else if (black == 0) {
                    formerTuples[i][j - 4] = (white + 4) + GameView.BLANK;
                } else {
                    formerTuples[i][j - 4] = black + GameView.BLANK;
                }
                //deal with changedTuples,increase for change
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

        int[][] changedScore = new int[4][5];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int score = tupleScoreTable[changedTuples[i][j] - GameView.BLANK] - tupleScoreTable[formerTuples[i][j] - GameView.BLANK];
                changedScore[i][j] = score;
            }
        }
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

    private Point findBestPos(ChessBoard chessBoard, int chessType) {
        //generally con not happen,all the position is not Okay
        if (invalidBestPos.size() + 1 >= 15 * 15) {
            return invalidBestPos.get((new Random()).nextInt(invalidBestPos.size()));
        }

        List<Point> bestPosList = new ArrayList<>();
        bestPosList.add(new Point(0, 0));
        //find the point whose score is highest
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if (!invalidBestPos.contains(new Point(row, col))) {
                    if (scoreTable[col][row] > scoreTable[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        bestPosList.clear();
                        bestPosList.add(new Point(row, col));
                    } else if (scoreTable[col][row] ==
                            scoreTable[bestPosList.get(0).x][bestPosList.get(0).y]) {
                        bestPosList.add(new Point(row, col));
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
        while (isFobidden(chessBoard, bestPosList.get(index), chessType)) {
            invalidBestPos.add(bestPosList.get(index));
            bestPosList.remove(index);
            index = rnd.nextInt(bestPosList.size());
            if (index == 0) {
                findBestPos(chessBoard, chessType);
            }
        }
        invalidBestPos.clear();
        return bestPosList.get(index);
    }

    private boolean isFobidden(ChessBoard chessBoard, Point point, int chessType) {
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
