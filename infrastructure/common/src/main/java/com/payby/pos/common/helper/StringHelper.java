package com.payby.pos.common.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    public static boolean isContainChinese(final CharSequence input) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static String replaceChineseString(String string, String replacement) {
        String regex = "[\u4e00-\u9fa5]"; // 中文正则
        return string.replaceAll(regex, replacement);
    }

}
