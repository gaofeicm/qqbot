package com.gaofeicm.qqbot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaofeicm.qqbot.dao.QlDao;
import com.gaofeicm.qqbot.entity.Ql;
import com.gaofeicm.qqbot.service.QlService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class QlServiceImpl extends ServiceImpl<QlDao, Ql> implements QlService {

    @Resource
    private QlDao dao;

    @Override
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
    @Override
    public List<Ql> getQl(Map<String, Object> param) {
        QueryWrapper<Ql> wrapper = new QueryWrapper<>();
        param.forEach(wrapper::eq);
        return dao.selectList(wrapper);
    }
}
