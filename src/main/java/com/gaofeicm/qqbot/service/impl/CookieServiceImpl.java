package com.gaofeicm.qqbot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.common.utils.RedisUtil;
import com.gaofeicm.qqbot.dao.CookieDao;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.CookieUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class CookieServiceImpl extends ServiceImpl<CookieDao, Cookie> implements CookieService {

    @Resource
    private CookieDao dao;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 保存ck
     * @param cookie ck
     * @return
     */
    public int saveCookie(Cookie cookie) {
        int count = 0;
        if(cookie.getId() == null){
            cookie.setPriority(0);
            cookie.setCreateTime(new Date());
            cookie.setUpdateTime(new Date());
            cookie.setExpirationTime(new Date());
            count = dao.insert(cookie);
            updateRedis(cookie, 0);
        }else{
            count = dao.updateById(cookie);
            updateRedis(cookie, 1);
        }
        return count;
    }

    private void updateRedis(Cookie cookie, int type){
        JSONArray array = JSONArray.parseArray(redisUtil.get(cookie.getQq()));
        if(type == 0){
            array.add(cookie);
            redisUtil.set(cookie.getQq(), JSON.toJSONString(array));
        }else{
            int index = -1;
            for (int i = 0; i < array.size(); i++) {
                Object o = array.get(i);
                Cookie ck = JSONObject.parseObject(JSON.toJSONString(o), Cookie.class);
                if(ck.getPtPin().equals(cookie.getPtPin())){
                    array.set(i, dao.selectById(cookie.getId()));
                    index = i;
                    break;
                }
            }
            if(index > -1){
                redisUtil.set(cookie.getQq(), JSON.toJSONString(array));
            }else{
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新ck时出现了异常！本地redis未匹配到当前对应值，已重新加载对应qq的redis数据！");
                redisUtil.set(cookie.getQq(), JSON.toJSONString(this.getCookie(new HashMap<>(0))));
            }
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

    /**
     * 按QQ更新oid
     * @param id id
     * @param oid oid
     * @return 操作结果
     */
    public int updateCookieOidById(String id, String oid) {
        Cookie cookie = new Cookie();
        cookie.setOid(oid);
        return dao.update(cookie, new UpdateWrapper<Cookie>().eq("id", id));
    }

    /**
     * 查询cookie及面板信息
     *
     * @param id id
     * @return cks cks
     */
    @Override
    public Map<String, Object> getCookieById(String id) {
        return dao.getCookieById(id);
    }

    /**
     * 按QQ更新wxid
     * @param qq qq
     * @param wxid wxid
     * @return 操作结果
     */
    public int updateCookieWxidByQq(String qq, String wxid) {
        Cookie cookie = new Cookie();
        cookie.setWxid(wxid);
        return dao.update(cookie, new UpdateWrapper<Cookie>().eq("qq", qq));
    }

    /**
     * 按wxid获取qq
     *
     * @param wxid wxid
     * @return
     */
    @Override
    public String getQqByWxid(String wxid) {
        String qq = null;
        List<Cookie> list = dao.selectList(new QueryWrapper<Cookie>().eq("wxid", wxid));
        if(list != null && list.size() > 0){
            qq = list.get(0).getQq();
        }
        return qq;
    }

    /**
     * 获取所有ck按qq排序
     * param 参数集合
     * @return ck集合
     */
    public List<Cookie> getCookieOrderByQq(Map<String, Object> param) {
        QueryWrapper<Cookie> wrapper = new QueryWrapper<>();
        param.forEach(wrapper::eq);
        wrapper.orderBy(true, true, "qq");
        return dao.selectList(wrapper);
    }

}
