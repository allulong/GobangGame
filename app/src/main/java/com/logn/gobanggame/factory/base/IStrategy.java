package com.logn.gobanggame.factory.base;

import android.graphics.Point;

/**
 * Created by long on 2017/6/11.
 */

public interface IStrategy {
    public Point getNextStep(int[][] chessPanel, int chessType, int stepIndex, Point prevStep);
    public void resetOnStep();
}
