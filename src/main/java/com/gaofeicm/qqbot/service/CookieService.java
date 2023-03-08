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

    /**
     * 查询过期已启用的ck
     * @return ck集合
     */
    List<Map<String, Object>> getExpCookie();

    /**
     * 按id更新oid
     * @param id id
     * @param oid oid
     * @return 操作结果
     */
    int updateCookieOidById(String id, String oid);

    /**
     * 查询cookie及面板信息
     * @param id id
     * @return cks cks
     */
    Map<String, Object> getCookieById(String id);

    /**
     * 按qq更新wxid
     * @param qq qq
     * @param wxid wxid
     * @return 操作结果
     */
    int updateCookieWxidByQq(String qq, String wxid);

    /**
     * 按wxid获取qq
     * @param wxid wxid
     * @return
     */
    String getQqByWxid(String wxid);

    /**
     * 获取所有ck按qq排序
     * param 参数集合
     * @return ck集合
     */
    public List<Cookie> getCookieOrderByQq(Map<String, Object> param);
}
