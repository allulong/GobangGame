package com.logn.gobanggame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.logn.gobanggame.R;
import com.logn.gobanggame.utils.ChessJudger;
import com.logn.gobanggame.utils.PlayStrategy;
import com.logn.gobanggame.views.interfaces.IGames;
import com.logn.gobanggame.views.interfaces.OnGameStateListener;

import java.util.ArrayList;
import java.util.List;

import static com.logn.gobanggame.utils.ChessType.CHESS_BLACK;
import static com.logn.gobanggame.utils.ChessType.CHESS_BLANK;
import static com.logn.gobanggame.utils.ChessType.CHESS_WHITE;

/**
 * Created by long on 2017/6/2.
 * <p>
 * 棋盘坐标x、y方向和手机x、y坐标一致
 */

public class GameView extends ViewGroup implements IGames {

    public static final String MODE = "mode";

    /**
     * 棋子的大小占棋盘格子宽度的 四分之三
     */
    private static final float CHESS_SCALE_WIDTH = 3.0f / 4;

    /**
     * 整个棋盘的线条数量
     */
    private static final int MAX_LINE = 15;


    private boolean curColorIsWithe = false;//正准备下的棋子是否是白色

    private boolean gameOver;
    /**
     * 判断是否是对弈模式
     */
    private boolean mModeIsP2P;

    private int resetCount = 1;

    public void setModeIsP2P(boolean mModeIsP2P) {
        this.mModeIsP2P = mModeIsP2P;
        start();
    }

    public void setOnGameStateListener(OnGameStateListener onGameStateListener) {
        onGameStateListeners.add(onGameStateListener);
    }

    private List<OnGameStateListener> onGameStateListeners;

    private int mCountChess = 0;
    private int mPanelWidth;//棋盘的总宽度
    private float mGridWidth;//格子的宽度
    /**
     * 画棋盘用的画笔
     */
    private Paint mPaint;

    //记录棋子的位置信息
    private List<Point> mChessWhiteArray;
    private List<Point> mChessBlackArray;
    //记录整个棋盘的信息
    private int[][] chessPanel;

    /**
     * 分数表
     */
    private int[][] scoreTable;

    /**
     * 拿到图片
     */
    private Bitmap mChessWhiteBm;
    private Bitmap mChessBlackBm;
    private Bitmap mChessFlagBm;

    private PlayStrategy playStrategy;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initPaint();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取高宽值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //拿到宽和高的最小值，也就是宽
        int width = Math.min(widthSize, heightMeasureSpec);

        //根据测量模式细节处理
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        //设置这样就是一个正方形了
        setMeasuredDimension(width, width);
    }

    /**
     * 测量大小
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //拿到棋盘宽度
        mPanelWidth = w;
        //分割棋盘，拿到棋盘中格子的宽度
        mGridWidth = mPanelWidth * 1.0f / MAX_LINE;

        //根据固定比例，获得棋子的宽度
        int chessWidth = (int) (mGridWidth * CHESS_SCALE_WIDTH);

        //设置棋子的大小
        mChessBlackBm = Bitmap.createScaledBitmap(mChessBlackBm, chessWidth, chessWidth, false);
        mChessWhiteBm = Bitmap.createScaledBitmap(mChessWhiteBm, chessWidth, chessWidth, false);
        mChessFlagBm = Bitmap.createScaledBitmap(mChessFlagBm, chessWidth, chessWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawDots(canvas);
        drawChesses(canvas);
        drawDotForNewChess(canvas);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        //设置颜色
        mPaint.setColor(0x88000000);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //设置防抖动
        mPaint.setDither(true);
        //设置Style
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制棋盘的方法
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {

        mPaint.setStrokeWidth(1);
        //获取高宽
        int w = mPanelWidth;
        float gridWidth = mGridWidth;

        //遍历，绘制线条
        for (int i = 0; i < MAX_LINE; i++) {
            //横坐标
            int startX = (int) (gridWidth / 2);
            int endX = (int) (w - gridWidth / 2);

            //纵坐标
            int y = (int) ((0.5 + i) * gridWidth);

            //绘制横
            canvas.drawLine(startX, y, endX, y, mPaint);
            //绘制纵
            canvas.drawLine(y, startX, y, endX, mPaint);
        }

    }

    /**
     * 绘画棋盘上的五个点
     *
     * @param canvas
     */
    private void drawDots(Canvas canvas) {
        mPaint.setStrokeWidth(6);

        float gridWidth = mGridWidth;

        float centerDot = 0;
        float dot1 = 0;
        float dot2 = 0;

        //遍历，绘制线条
        for (int i = 0; i < MAX_LINE; i++) {
            if (i == 3) {
                dot1 = (int) ((0.5 + i) * gridWidth);
            }
            if (i == 7) {
                centerDot = (int) ((0.5 + i) * gridWidth);
            }
            if (i == 11) {
                dot2 = (int) ((0.5 + i) * gridWidth);
            }
        }

        //绘画天元点
        canvas.drawCircle(centerDot, centerDot, 3, mPaint);

        //绘画其他四个点
        canvas.drawCircle(dot1, dot1, 3, mPaint);
        canvas.drawCircle(dot2, dot2, 3, mPaint);
        canvas.drawCircle(dot1, dot2, 3, mPaint);
        canvas.drawCircle(dot2, dot1, 3, mPaint);

    }

    private void drawChesses(Canvas canvas) {
        //绘画白棋
        Point point = null;
        for (int i = 0; i < mChessWhiteArray.size(); i++) {
            point = mChessWhiteArray.get(i);
            canvas.drawBitmap(mChessWhiteBm, mGridWidth * (1.0f / 4 * 0.5f + point.x), mGridWidth * (1.0f / 4 * 0.5f + point.y), null);
        }
        //绘画黑棋
        for (int i = 0; i < mChessBlackArray.size(); i++) {
            point = mChessBlackArray.get(i);
            canvas.drawBitmap(mChessBlackBm, mGridWidth * (1.0f / 4 * 0.5f + point.x), mGridWidth * (1.0f / 4 * 0.5f + point.y), null);
        }
    }


    /**
     * 绘画红点
     *
     * @param canvas
     */
    private void drawDotForNewChess(Canvas canvas) {
        if (mChessBlackArray.size() > 0 || mChessWhiteArray.size() > 0) {
            Point point = null;
            if (!curColorIsWithe) {  //即将下的棋子为黑色，则最新下的一个棋子一定是白色
                //取白色最后一个下的棋子的坐标
                point = mChessWhiteArray.get(mChessWhiteArray.size() - 1);
            } else {  //否则为黑色
                point = mChessBlackArray.get(mChessBlackArray.size() - 1);
            }
            if (point != null) {
                //计算实际坐标
                float x = mGridWidth * (1.0f / 4 * 0.5f + point.x);
                float y = mGridWidth * (1.0f / 4 * 0.5f + point.y);
                canvas.drawBitmap(mChessFlagBm, x, y, null);
            }
        }
    }

    /**
     * 根据触摸位置判断棋子的位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver) {
            for (OnGameStateListener listener : onGameStateListeners) {
                if (listener != null) {
                    listener.changeState(gameOver, curColorIsWithe ? CHESS_BLACK : CHESS_WHITE);
                }
            }
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //获取触坐标
                int x = (int) event.getX();
                int y = (int) event.getY();

                //将像素坐标转换为棋盘坐标，方向与手机像素坐标一直，并封装成坐标点
                x = (int) (x / mGridWidth);
                y = (int) (y / mGridWidth);

                Point point = new Point(x, y);

                boolean setSucceed = setChessForPerson(point, curColorIsWithe ? CHESS_WHITE : CHESS_BLACK);

                if (setSucceed && !mModeIsP2P && !gameOver) {
                    Point mPoint = playStrategy.getNextStep(chessPanel, curColorIsWithe ? CHESS_WHITE : CHESS_BLACK, mCountChess, point);
                    setChess(mPoint, CHESS_BLACK);

                    resetCount = 1;
                }

                break;
            default:
        }
        return true;
    }

    private void clearChessPanel() {
        if (chessPanel == null) {
            chessPanel = new int[MAX_LINE][MAX_LINE];
        }
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                chessPanel[i][j] = 0;
            }
        }
    }


    /**
     * 初始化各项数据
     */
    private void init() {
        mChessBlackArray = new ArrayList<>();
        mChessWhiteArray = new ArrayList<>();

        chessPanel = new int[MAX_LINE][MAX_LINE];
        scoreTable = new int[MAX_LINE][MAX_LINE];

        onGameStateListeners = new ArrayList<>();

        mChessBlackBm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_chess_black);
        mChessWhiteBm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_chess_white);
        mChessFlagBm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_chess_red_dot);

        curColorIsWithe = false;
        mCountChess = 0;
        gameOver = false;

        mModeIsP2P = true;

        playStrategy = new PlayStrategy();

        mPaint = new Paint();
    }

    private boolean setChessForPerson(Point point, int chessType) {
        int x = point.x;
        int y = point.y;
        if (chessPanel[x][y] != CHESS_BLANK) {
//            Log.e("错误-重复下子", "位置 (" + x + "," + y + ") 已经有棋子了");
//            showPanel();
            return false;
        } else {
            setChess(point, chessType);
            return true;
        }
    }

    /**
     * 在固定位置下棋子，并检查游戏是否结束
     *
     * @param point     下棋子的位置，必定不为空
     * @param chessType 棋子类型，只能为黑白两种类型
     */
    private void setChess(Point point, int chessType) {
        if (chessType == CHESS_BLACK) {
            mChessBlackArray.add(point);
        } else if (chessType == CHESS_WHITE) {
            mChessWhiteArray.add(point);
        } else {
//            Log.e("棋子类型错误", "企图下的棋子类型为：" + chessType);
            return;
        }
        chessPanel[point.x][point.y] = chessType;
        mCountChess++;
        curColorIsWithe = !curColorIsWithe;

        invalidate();   //更新棋盘界面

        if (gameOver = ChessJudger.checkWin(chessPanel, point)) {//游戏结束，调用监听器

            for (OnGameStateListener listener : onGameStateListeners) {
                if (listener != null) {
                    listener.changeState(gameOver, curColorIsWithe ? CHESS_BLACK : CHESS_WHITE);
                }
            }

        }

    }

    /**
     * 模式确定后，如果为人机模式则需要初始化第一棋子
     */
    @Override
    public void start() {
        if (!mModeIsP2P) {//获得最合适的位置后下子
            Point point = playStrategy.getNextStep(chessPanel, curColorIsWithe ? CHESS_WHITE : CHESS_BLACK, mCountChess, null);
            setChess(point, curColorIsWithe ? CHESS_WHITE : CHESS_BLACK);
        }
    }

    @Override
    public void reStart() {
        //重置数据
        mChessWhiteArray.clear();
        mChessBlackArray.clear();
        clearChessPanel();

        curColorIsWithe = false;

        mCountChess = 0;
        gameOver = false;

        playStrategy = new PlayStrategy();

        start();

        //更新棋盘
        invalidate();
    }

    @Override
    public void resetOneStep() {
        if (gameOver || mChessBlackArray.size() == 0) { //游戏结束后不可悔棋||棋盘上还没有棋子
            return;
        }
        if (mModeIsP2P) {
            if (curColorIsWithe) {//撤销黑棋
                int index = mChessBlackArray.size() - 1;
                Point point = mChessBlackArray.get(index);
                mChessBlackArray.remove(index);
                chessPanel[point.x][point.y] = 0;
            } else {//撤销白棋
                int index = mChessWhiteArray.size() - 1;
                Point point = mChessWhiteArray.get(index);
                mChessWhiteArray.remove(index);
                chessPanel[point.x][point.y] = 0;
            }
            curColorIsWithe = !curColorIsWithe;
        } else {
            if (resetCount == 1 && mChessWhiteArray.size() > 0) {//有白色棋子且仅能撤销最新一步白色棋子，黑棋同时撤销
                resetCount = 0;

                playStrategy.resetOnStep();//后退评分表

                int index;
                Point point;
                //撤销黑棋和白棋
                index = mChessBlackArray.size() - 1;
                point = mChessBlackArray.get(index);
                mChessBlackArray.remove(index);
                chessPanel[point.x][point.y] = 0;

                index = mChessWhiteArray.size() - 1;
                point = mChessWhiteArray.get(index);
                mChessWhiteArray.remove(index);
                chessPanel[point.x][point.y] = 0;
            }

        }
        invalidate();
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }
}
