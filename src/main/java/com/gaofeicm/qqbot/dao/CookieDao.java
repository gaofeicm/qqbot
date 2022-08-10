package com.gaofeicm.qqbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaofeicm.qqbot.entity.Cookie;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
public interface CookieDao extends BaseMapper<Cookie> {

    /**
     * 查询激活的ck
     * @return cks
     */
    @Select("SELECT c.priority, c.qq, c.cookie, q.name, q.address, q.toke_type as tokenType, q.token, q.client_id as clientId, q.client_secret as clientSecret FROM (SELECT * FROM cookie WHERE available = '1' and expiration_time > now()) c\n" +
            "right join ql_cookie qc on (c.id = qc.cookie_id)\n" +
            "left join ql q on (q.id = qc.ql_id)\n" +
            "order by c.priority desc")
    List<Map<String, Object>> getAvailableCookie();
}
