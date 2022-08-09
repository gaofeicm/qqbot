package com.gaofeicm.qqbot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.qqbot.dao.CookieDao;
import com.gaofeicm.qqbot.entity.Cookie;
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
public class CookieServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Resource
    private CookieDao dao;

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

    public List<Cookie> getCookieByQq(String qq) {
        return dao.selectList(new QueryWrapper<Cookie>().eq("qq", qq));
    }

    public Cookie getCookieByQqAndCk(String qq, String ck) {
        return dao.selectOne(new QueryWrapper<Cookie>().eq("qq", qq).eq("pt_pin", CookieUtils.getPin(ck)));
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
}
