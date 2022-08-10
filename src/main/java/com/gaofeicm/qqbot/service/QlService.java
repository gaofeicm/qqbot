package com.gaofeicm.qqbot.service;

import com.gaofeicm.qqbot.entity.Ql;

import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
public interface QlService {

    /**
     * 保存青龙面板
     * @param entity 实体
     * @return 执行成功数
     */
    int saveQl(Ql entity);

    /**
     * 获取ql
     * param 参数集合
     * @return ql集合
     */
    List<Ql> getQl(Map<String, Object> param);
}
