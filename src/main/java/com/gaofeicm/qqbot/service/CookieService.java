package com.gaofeicm.qqbot.service;

import com.gaofeicm.qqbot.entity.Cookie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public interface CookieService {

    /**
     * 保存ck
     * @param cookie ck
     * @return
     */
    int saveCookie(Cookie cookie);

    /**
     * 按QQ获取ck
     * @param qq
     * @return
     */
    List<Cookie> getCookieByQq(String qq);

    /***
     * 按QQ和ck获取ck
     * @param qq qq
     * @param ck pin
     * @return ck
     */
    Cookie getCookieByQqAndCk(String qq, String ck);

    /***
     * 按pin获取ck
     * @param ck qq
     * @return ck
     */
    Cookie getCookieByPin(String ck);

    /**
     * 获取所有ck
     * param 参数集合
     * @return ck集合
     */
    List<Cookie> getCookie(Map<String, Object> param);

    /**
     * 获取可用的ck
     * param 参数集合
     * @return ck集合
     */
    List<Map<String, Object>> getAvailableCookie();
}
