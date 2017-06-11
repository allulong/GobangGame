package com.logn.gobanggame.factory.base;

import com.logn.gobanggame.utils.PlayStrategy;

/**
 * Created by long on 2017/6/11.
 */

public class StrategyFactory {

    public static IStrategy create(Class<? extends IStrategy> clazz) {
        String clazzName = clazz.getName();
        if (clazz.equals(PlayStrategy.class.getName())) {
            return new PlayStrategy();
        }
        return null;
    }
}
