package com.logn.gobanggame.utils;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2017/6/19.
 */

public class Array2StringUtils {
    public static String getStringFromArray(List<Point> list) {
        StringBuffer r = new StringBuffer();
        if (list == null)
            return "";
        r.append("[");
        for (Point p : list) {
            r.append("(" + p.x + "," + p.y + ")");
        }
        r.append("]");
        return r.toString();
    }

    public static List<Point> getPosFromString(String s) {
        List<Point> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer(s);
        int i = -1;
        int j = 0;
        int x = 0, y = 0;
        while ((j = sb.indexOf(",", j + 1)) != -1) {
            i = sb.indexOf("(", i);
            String flag = sb.substring(i + 1, j);
            x = Integer.parseInt(flag);

            i = sb.indexOf(")", i);
            flag = sb.substring(j + 1, i);
            y = Integer.parseInt(flag);

            list.add(new Point(x, y));
        }
        return list;
    }
}
