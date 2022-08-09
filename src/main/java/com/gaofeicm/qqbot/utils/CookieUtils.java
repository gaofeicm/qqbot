package com.gaofeicm.qqbot.utils;

import com.gaofeicm.qqbot.entity.Cookie;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gaofeicm
 */
public class CookieUtils {

    /**
     * 获取pin
     * @param ck ck
     * @return pin
     */
    public static String getPin(String ck){
        String regexPin = "pt_pin=([^; ]+)(?=;?);";
        Matcher pinMatcher = Pattern.compile(regexPin).matcher(ck);
        if(pinMatcher.find()){
            return pinMatcher.group();
        }
        return "";
    }

    /**
     * 获取key
     * @param ck ck
     * @return key
     */
    public static String getKey(String ck){
        String regexKey = "pt_key=([^; ]+)(?=;?);";
        Matcher keyMatcher = Pattern.compile(regexKey).matcher(ck);
        if(keyMatcher.find()){
            return keyMatcher.group();
        }
        return "";
    }

    public static String getExpireDay(Cookie cookie){
        Date e = cookie.getExpirationTime();
        return getExpireDay(e) + "天";
    }

    public static int getExpireDay(Date date){
        Date now = new Date();
        String day = String.format("%.2f", (double)(date.getTime() - now.getTime()) / (1000 * 3600 * 24));
        return Integer.parseInt(day);
    }
}
