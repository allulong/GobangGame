package com.logn.gobanggame.views.interfaces;

/**
 * Created by long on 2017/6/3.
 */

public interface IGames {
    /**
     * 开始游戏
     */
    void start();

    /**
     * 恢复游戏
     */
    void reStart();

    /**
     * 悔一步棋
     */
    void resetOneStep();

    /**
     * 暂停游戏
     */
    void pause();

    /**
     * 停止游戏
     */
    void stop();
//    int getScore();
}
