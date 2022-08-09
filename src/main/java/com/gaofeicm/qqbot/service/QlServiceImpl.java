package com.gaofeicm.qqbot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.qqbot.dao.QlDao;
import com.gaofeicm.qqbot.entity.Ql;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class QlServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Resource
    private QlDao dao;

    public int saveQl(Ql entity) {
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
    public List<Ql> getQl(Map<String, Object> param) {
        QueryWrapper<Ql> wrapper = new QueryWrapper<>();
        param.forEach(wrapper::eq);
        return dao.selectList(wrapper);
    }
}
