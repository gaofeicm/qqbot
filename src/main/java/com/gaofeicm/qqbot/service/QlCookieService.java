package com.gaofeicm.qqbot.service;

import com.gaofeicm.qqbot.entity.QlCookie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public interface QlCookieService{

    /**
     * 保存青龙 cookie
     * @param entity 实体
     * @return 执行成功数
     */
    int saveQlCookie(QlCookie entity);

    /**
     * 获取ql cookie
     * param 参数集合
     * @return ql集合
     */
    List<QlCookie> getQlCookie(Map<String, Object> param);
}
