package com.centent.core.util;

public class StringUtil {

    /**
     * 首字母大写
     *
     * @param str String
     * @return String
     * @since 0.0.1
     */
    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
