package com.gaofeicm.qqbot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.qqbot.dao.CookieDao;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.utils.CookieUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class CookieServiceImpl extends ServiceImpl<CookieDao, Cookie> implements CookieService {

    @Resource
    private CookieDao dao;

    /**
     * 保存ck
     * @param cookie ck
     * @return
     */
    public int saveCookie(Cookie cookie) {
        if(cookie.getId() == null){
            cookie.setPriority(0);
            cookie.setCreateTime(new Date());
            cookie.setUpdateTime(new Date());
            cookie.setExpirationTime(new Date());
            return dao.insert(cookie);
        }else{
            return dao.updateById(cookie);
        }
    }

    /**
     * 按QQ获取ck
     * @param qq
     * @return
     */
    public List<Cookie> getCookieByQq(String qq) {
        return dao.selectList(new QueryWrapper<Cookie>().eq("qq", qq));
    }

    /***
     * 按QQ和ck获取ck
     * @param qq qq
     * @param ck pin
     * @return ck
     */
    public Cookie getCookieByQqAndCk(String qq, String ck) {
        return dao.selectOne(new QueryWrapper<Cookie>().eq("qq", qq).eq("pt_pin", CookieUtils.getPin(ck)));
    }

    /***
     * 按pin获取ck
     * @param ck qq
     * @return ck
     */
    @Override
    public Cookie getCookieByPin(String ck) {
        return dao.selectOne(new QueryWrapper<Cookie>().eq("pt_pin", CookieUtils.getPin(ck)));
    }

    /**
     * 获取所有ck
     * param 参数集合
     * @return ck集合
     */
    public List<Cookie> getCookie(Map<String, Object> param) {
        QueryWrapper<Cookie> wrapper = new QueryWrapper<>();
        param.forEach(wrapper::eq);
        return dao.selectList(wrapper);
    }

    /**
     * 获取可用的ck
     * param 参数集合
     * @return ck集合
     */
    @Override
    public List<Map<String, Object>> getAvailableCookie() {
        return dao.getAvailableCookie();
    }

    /**
     * 查询过期已启用的ck
     * @return ck集合
     */
    @Override
    public List<Map<String, Object>> getExpCookie() {
        return dao.getExpCookie();
    }

}
