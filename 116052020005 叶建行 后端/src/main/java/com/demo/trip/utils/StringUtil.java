package com.demo.trip.utils;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    public static Integer changeString(String string){
        if (string != null && !StringUtils.isBlank(string)) {
            return Integer.valueOf(string);
        }else {
            return null;
        }
    }
}
