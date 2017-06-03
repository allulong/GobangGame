package com.logn.gobanggame.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import static android.R.attr.y;

/**
 * Created by long on 2017/6/2.
 */

public class GameView extends ViewGroup {

    private int count = 0;

    //线条数量
    private static final int MAX_LINE = 15;

    //线条的宽度
    private int mPanelWidth;

    //线条的高度
    private float mLineHeight;

    private Paint mPaint;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initPaint();
    }

    private void initView() {
        mPaint = new Paint();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("流程", "onLayout" + count++);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("流程", "onMeasure" + count++);
        //获取高宽值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int hightSize = MeasureSpec.getSize(heightMeasureSpec);
        int hightMode = MeasureSpec.getMode(heightMeasureSpec);

        //拿到宽和高的最小值，也就是宽
        int width = Math.min(widthSize, heightMeasureSpec);

        //根据测量模式细节处理
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = hightSize;
        } else if (hightMode == MeasureSpec.UNSPECIFIED) {
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
        Log.e("流程", "onSizeChanged" + count++);

        //拿到宽
        mPanelWidth = w;
        //分割
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("流程", "onDraw" + count++);
        super.onDraw(canvas);
        drawLine(canvas);
        drawDots(canvas);
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
        float lineHeight = mLineHeight;

        //遍历，绘制线条
        for (int i = 0; i < MAX_LINE; i++) {
            //横坐标
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            //纵坐标
            int y = (int) ((0.5 + i) * lineHeight);

            //绘制横
            canvas.drawLine(startX, y, endX, y, mPaint);
            //绘制纵
            canvas.drawLine(y, startX, y, endX, mPaint);

            Log.e("坐标", "" + startX + "::" + endX + "::" + y);
        }

    }

    private void drawDots(Canvas canvas) {
        mPaint.setStrokeWidth(6);

        float lineHeight = mLineHeight;

        float certenDot = 0;
        float dot1 = 0;
        float dot2 = 0;

        //遍历，绘制线条
        for (int i = 0; i < MAX_LINE; i++) {
            if (i == 3) {
                dot1 = (int) ((0.5 + i) * lineHeight);
            }
            if (i == 7) {
                certenDot = (int) ((0.5 + i) * lineHeight);
            }
            if (i == 11) {
                dot2 = (int) ((0.5 + i) * lineHeight);
            }
        }

        //绘画天元点
        canvas.drawCircle(certenDot, certenDot, 3, mPaint);

        canvas.drawCircle(dot1, dot1, 3, mPaint);
        canvas.drawCircle(dot2, dot2, 3, mPaint);
        canvas.drawCircle(dot1, dot2, 3, mPaint);
        canvas.drawCircle(dot2, dot1, 3, mPaint);

    }
}
