package com.payby.pos.ecr.utils;

public class Utils {

    private static long lastClickTime = 0;
    private static long MIN_CLICK_DELAY_TIME = 1000;

    public static boolean isDoubleClick() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - lastClickTime < MIN_CLICK_DELAY_TIME) {
            return true;
        }
        lastClickTime = curClickTime;
        return false;
    }

}
