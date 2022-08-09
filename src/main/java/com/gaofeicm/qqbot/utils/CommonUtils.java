package com.gaofeicm.qqbot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

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

    public static String getAdminQq(){
        return adminQq;
    }

    /**
     * 查找对象在集合的位置
     * @param object 对象
     * @param list 集合
     * @return 位置
     */
    public static int indexOf(Object object, List<Object> list){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if(object == list.get(i) || object.equals(list.get(i))){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 生成随机位数的字符
     * @param random 位数
     * @return 随机字符
     */
    public static String randomString(int random){
        StringBuffer sb = new StringBuffer();
        String t = "0123456789abcdef";
        for(int i = 0;i < random; i ++){
            sb.append(t.charAt((int) Math.floor(Math.random() * t.length())));
        }
        return sb.toString();
    }

    /**
     * 获取字符串的Unicode编码
     * @param str 字符串
     * @return 编码
     */
    public static String getUnicode(String str) {
        String strTemp = "";
        if (str != null) {
            for (char c : str.toCharArray()) {
                if (c > 255) {
                    strTemp += "\\u" + Integer.toHexString((int)c);
                } else {
                    strTemp += "\\u00" + Integer.toHexString((int)c);
                }
            }
        }
        return strTemp;
    }

    /**
     * HMACSHA加密
     * @param data 加密字符
     * @param key 密钥
     * @return 加密文本
     */
    public static String HMACSHA256(String data, String key) {
        StringBuilder sb = new StringBuilder();
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
