package com.logn.gobanggame.views.utils;

/**
 * Created by long on 2017/6/6.
 * <p>
 * 棋型表示
 * 用一个6位数表示棋型，从高位到低位分别表示
 * 连五，活四，眠四，活三，活二/眠三，活一/眠二, 眠一
 */

public class Scores {
    private static final int ONE = 10;
    private static final int TWO = 100;
    private static final int THREE = 1000;
    private static final int FOUR = 100000;
    private static final int FIVE = 1000000;

    private static final int BLOCKED_ONE = 1;
    private static final int BLOCKED_TWO = 10;
    private static final int BLOCKED_THREE = 100;
    private static final int BLOCKED_FOUR = 10000;

    private static final int MAX = FIVE * 10;
    private static final int MIN = MAX * -1;
}
