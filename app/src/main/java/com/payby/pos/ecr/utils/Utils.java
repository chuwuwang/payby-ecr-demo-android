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

    /**
     * 字节数组转换为16进制字符串
     */
    public static String bytes2HexString(final byte[] bytes) {
        byte[] buffer = new byte[bytes.length * 2];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            byte value = bytes[i];
            buffer[j++] = (byte) int2HexChar(value >> 4 & 0X0F);
            buffer[j++] = (byte) int2HexChar(value & 0X0F);
        }
        return new String(buffer);
    }

    /**
     * Int值转换为16进制字符
     */
    public static char int2HexChar(int value) {
        if (value > 9) {
            return (char) (value - 10 + 'A');
        }
        return (char) (value + '0');
    }

}
