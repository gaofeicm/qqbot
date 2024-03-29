package com.gaofeicm.qqbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaofeicm.qqbot.entity.Cookie;
import org.apache.ibatis.annotations.Param;
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
    @Select("SELECT c.id, c.priority, c.qq, c.cookie, q.name, q.address, q.toke_type as tokenType, q.token, q.client_id as clientId, q.client_secret as clientSecret FROM (SELECT * FROM cookie WHERE available = '1' and expiration_time > now()) c\n" +
            "join ql_cookie qc on (c.id = qc.cookie_id)\n" +
            "join ql q on (q.id = qc.ql_id)\n" +
            "order by c.priority desc,c.create_time")
    List<Map<String, Object>> getAvailableCookie();

    /**
     * 查询过期已启用的ck
     * @return
     */
    @Select("SELECT * FROM cookie c\n" +
            "where c.expiration_time <= NOW()\n" +
            "and c.available = '1'")
    List<Map<String, Object>> getExpCookie();

    /**
     * 查询cookie及面板信息
     * @param id id
     * @return cks cks
     */
    @Select("SELECT qc.id qcid, c.id cid, q.id qid, c.oid, q.name, c.qq, c.cookie, q.address, q.toke_type as tokenType, q.token, q.client_id as clientId, q.client_secret as clientSecret" +
            " FROM (SELECT * FROM cookie WHERE id=#{id}) c" +
            " join ql_cookie qc on (c.id = qc.cookie_id)" +
            " join ql q on (q.id = qc.ql_id)")
    Map<String, Object> getCookieById(@Param("id") String id);

    /**
     * 按wxid获取qq
     * @param wxid wxid
     * @return
     */
    @Select("SELECT DISTINCT c.qq FROM COOKIE c WHERE c.wxid = #{wxid}")
    List<String> getQqByWxid(@Param("wxid") String wxid);
}
