package com.gaofeicm.qqbot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.qqbot.dao.QlCookieDao;
import com.gaofeicm.qqbot.dao.QlDao;
import com.gaofeicm.qqbot.entity.Ql;
import com.gaofeicm.qqbot.entity.QlCookie;
import com.gaofeicm.qqbot.service.QlCookieService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class QlCookieServiceImpl extends ServiceImpl<QlDao, Ql> implements QlCookieService {

    @Resource
    private QlCookieDao dao;

    /**
     * 保存青龙 cookie
     * @param entity 实体
     * @return 执行成功数
     */
    @Override
    public int saveQlCookie(QlCookie entity) {
        if(entity.getId() == null){
            return dao.insert(entity);
        }else{
            return dao.updateById(entity);
        }
    }

    /**
     * 获取ql
     * param 参数集合
     * @return ql集合
     */
    @Override
    public List<QlCookie> getQlCookie(Map<String, Object> param) {
        QueryWrapper<QlCookie> wrapper = new QueryWrapper<>();
        param.forEach(wrapper::eq);
        return dao.selectList(wrapper);
    }
}
