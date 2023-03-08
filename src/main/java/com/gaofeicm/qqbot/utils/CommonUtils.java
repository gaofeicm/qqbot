package com.gaofeicm.qqbot.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gaofeicm
 */
@Component
public class CommonUtils {

    private static String adminQq;

    @Value("${admin.qq}")
    public void setAdminQq(String adminQq) {
        CommonUtils.adminQq = adminQq;
    }

    public static String getAdminQq() {
        return adminQq;
    }

    /**
     * 查找对象在集合的位置
     *
     * @param object 对象
     * @param list   集合
     * @return 位置
     */
    public static int indexOf(Object object, List<Object> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (object == list.get(i) || object.equals(list.get(i)) || object.toString().startsWith((String) list.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 生成随机位数的字符
     *
     * @param random 位数
     * @return 随机字符
     */
    public static String randomString(int random) {
        StringBuffer sb = new StringBuffer();
        String t = "0123456789abcdef";
        for (int i = 0; i < random; i++) {
            sb.append(t.charAt((int) Math.floor(Math.random() * t.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机位数的数字
     *
     * @param random 位数
     * @return 随机数字
     */
    public static String randomNumber(int random) {
        StringBuffer sb = new StringBuffer();
        String t = "0123456789";
        for (int i = 0; i < random; i++) {
            sb.append(t.charAt((int) Math.floor(Math.random() * t.length())));
        }
        return sb.toString();
    }

    /**
     * 获取字符串的Unicode编码
     *
     * @param str 字符串
     * @return 编码
     */
    public static String getUnicode(String str) {
        String strTemp = "";
        if (str != null) {
            for (char c : str.toCharArray()) {
                if (c > 255) {
                    strTemp += "\\u" + Integer.toHexString((int) c);
                } else {
                    strTemp += "\\u00" + Integer.toHexString((int) c);
                }
            }
        }
        return strTemp;
    }

    /**
     * HMACSHA加密
     *
     * @param data 加密字符
     * @param key  密钥
     * @return 加密文本
     */
    public static String HmacSHA256(String data, String key) {
        StringBuilder sb = new StringBuilder();
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] array = sha256HMAC.doFinal(data.getBytes("UTF-8"));
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /****
     * 传入具体日期 ，返回具体日期增加n个月。
     * @param date 时间
     * @param month 差值
     * @return date
     */
    public static Date subMonth(Date date, int month) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.MONTH, month);
        return rightNow.getTime();
    }

    /**
     * 获取所有请求参数
     *
     * @param request HttpServletRequest请求
     * @return 请求参数
     */
    public static JSONObject getAllRequestParam(HttpServletRequest request) {
        JSONObject obj = new JSONObject();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                //如果字段的值为空，判断若值为空，则删除这个字段>
                if (null != value && !"".equals(value)) {
                    obj.put(en, value);
                }
            }
        }
        return obj;
    }

    /**
     * 正则表达式
     */
    private static Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    /**
     * 将字符串中的中文转码
     * @param str 字符串
     * @return 转码后的字符串
     * @throws UnsupportedEncodingException 不支持的字符编码
     */
    public static String encodeChineseStr(String str) throws UnsupportedEncodingException {
        if(str == null){
            return "";
        }
        int index = 0;
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find()) {
            sb.append(str, index, m.start());
            sb.append(URLEncoder.encode(m.group(), "UTF-8"));
            index = m.end();
        }
        sb.append(str, index, str.length());
        return sb.toString();
    }

    /**
     * 创建范围内的随机数（包括）
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int generateRangeRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }
}