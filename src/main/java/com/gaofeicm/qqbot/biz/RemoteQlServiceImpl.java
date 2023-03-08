package com.gaofeicm.qqbot.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.entity.Ql;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.service.QlService;
import com.gaofeicm.qqbot.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Service
public class RemoteQlServiceImpl {

    @Resource
    private CookieService cookieService;

    @Resource
    private QlService qlService;

    /**
     * 同步cookie到面板
     * @param qq qq
     * @param msg 命令
     */
    public void syncQlCookie(String qq, String msg) {
        if (!qq.equals(CommonUtils.getAdminQq())) {
            return;
        }
        List<Map<String, Object>> cks = cookieService.getAvailableCookie();
        Map<String, Map<String, Object>> qls = new HashMap<>();
        Map<String, JSONArray> tck = new HashMap<>();
        for (Map<String, Object> ck : cks) {
            String name = ck.get("name").toString();
            if (tck.get(name) == null) {
                tck.put(name, new JSONArray());
            }
            if (qls.get(name) == null) {
                qls.put(name, new HashMap<String, Object>() {{
                    put("name", ck.get("name"));
                    put("address", ck.get("address"));
                    put("token", ck.get("token"));
                    put("tokenType", ck.get("tokenType"));
                }});
            }
            tck.get(name).add(new HashMap<String, Object>(3) {{
                put("value", ck.get("cookie"));
                put("name", "JD_COOKIE");
                put("remarks", ck.get("qq") + "-" + ck.get("id"));
            }});
        }
        StringBuilder sb = new StringBuilder();
        if(msg.length() > 2){
            String n = msg.substring(2);
            if(tck.containsKey(n)){
                JSONArray array = tck.get(n);
                this.deleteQlOriginalData(qls.get(n));
                this.addQlOriginalData(qls.get(n), array);
                sb.append("面板名称：").append(qls.get(n).get("name")).append("，拥有").append(array.size()).append("条ck\r\n");
                MessageUtils.sendPrivateMsg(qq, "同步完成\r\n" + sb.toString());
            }else{
                MessageUtils.sendPrivateMsg(qq, "无法找到名称为：" + n + "的面板面板");
            }
        }else{
            tck.forEach((s, list) -> {
                this.deleteQlOriginalData(qls.get(s));
                this.addQlOriginalData(qls.get(s), list);
                sb.append("面板名称：").append(qls.get(s).get("name")).append("，拥有").append(list.size()).append("条ck\r\n");
            });
            MessageUtils.sendPrivateMsg(qq, "同步完成\r\n" + sb.toString());
        }
    }

    /**
     * 更新远程ck
     * @param cid cookieId
     */
    public void updateQlOriginalData(String cid){
        Map<String, Object> map = cookieService.getCookieById(cid);
        if(map.get("oid") == null){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "数据库中无此账号对应青龙面板的记录！请先手动添加！");
            return;
        }
        Map<String, String> header = this.getQlHeader(map);
        JSONObject value = new JSONObject(4) {{
            put("id", map.get("oid"));
            put("value", map.get("cookie"));
            put("name", "JD_COOKIE");
            put("remarks", map.get("qq") + "-" + map.get("cid"));
        }};
        String t = HttpRequestUtils.doGet(map.get("address") + "/open/envs??searchValue=" + CookieUtils.getPin(map.get("cookie").toString()) + "&t=" + System.currentTimeMillis(), header);
        if (t != null) {
            JSONObject jsonObject = JSONObject.parseObject(t);
            if ("200".equals(jsonObject.getString("code"))) {
                if(jsonObject.get("data") != null) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data != null && data.size() == 1){
                        JSONObject o = (JSONObject)data.get(0);
                        if(!map.get("oid").equals(o.get("id"))){
                            value.remove("id");
                            addQlOriginalData(map, new com.alibaba.fastjson2.JSONArray(){{add(value);}});
                            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新cookie成功，面板" + map.get("name") + "cookie与当前账号不匹配，已转为添加ck方法！");
                            return;
                        }
                    }
                }else{
                    value.remove("id");
                    addQlOriginalData(map, new com.alibaba.fastjson2.JSONArray(){{add(value);}});
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新cookie成功，原面板" + map.get("name") + "无法找到当前ck，已转为添加ck方法！");
                    return;
                }
            }
        }
        String s = HttpRequestUtils.doPut(map.get("address") + "/open/envs?t=" + System.currentTimeMillis(), value.toJSONString(), header);
        if (s != null) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if ("200".equals(jsonObject.getString("code"))) {
                if(jsonObject.get("data") != null) {
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + map.get("qq") + "，cookie：" + map.get("cookie") + "，已更新至面板：" + map.get("name"));
                    //启用cookie
                    this.enableQlOriginalData(map);
                }else {
                    value.remove("id");
                    addQlOriginalData(map, new com.alibaba.fastjson2.JSONArray(){{add(value);}});
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新cookie成功，原面板" + map.get("name") + "更新方法无返回记录，已转为添加ck方法！");
                }
            }else if("401".equals(jsonObject.getString("code"))){
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "token错误，开始重试获取新的token！" + map.toString());
                JSONObject qlToken = CommandUtils.getQlToken(map.get("address") + "/open/auth/token?client_id=" + map.get("clientId") + "&client_secret=" + map.get("clientSecret"));
                if(qlToken == null){
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "无法获取token！请检查配置及服务状态！面板：" + map.get("name"));
                }else{
                    Ql ql = new Ql();
                    ql.setId(map.get("qid").toString());
                    ql.setTokeType(qlToken.getString("token_type"));
                    ql.setToken(qlToken.getString("token"));
                    int i = qlService.saveQl(ql);
                    if(i > 0){
                        MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "成功更新面板：" + map.get("name") + "的token！");
                        map.put("token", ql.getToken());
                        map.put("tokenType", ql.getTokeType());
                        header = this.getQlHeader(map);
                        s = HttpRequestUtils.doPut(map.get("address") + "/open/envs?t=" + System.currentTimeMillis(), value.toJSONString(), header);
                        jsonObject = JSONObject.parseObject(s);
                        if ("200".equals(jsonObject.getString("code"))) {
                            if(jsonObject.get("data") != null) {
                                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + map.get("qq") + "，cookie：" + map.get("cookie") + "，已使用新的token更新至面板：" + map.get("name"));
                                //启用cookie
                                this.enableQlOriginalData(map);
                            }else {
                                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新cookie失败，可能是面板" + map.get("name") + "里面已不存在此记录，请手动操作！");
                            }
                        }else{
                            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "使用新的token更新ck依旧错误，请检查后台日志！面板：" + map.get("name"));
                        }
                    }else{
                        MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "面板：" + map.get("name") + "保存新的token失败！");
                    }
                }
            }
        } else {
            value.remove("id");
            addQlOriginalData(map, new com.alibaba.fastjson2.JSONArray(){{add(value);}});
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "更新cookie成功，原面板" + map.get("name") + "更新数据失败，已转为添加ck方法！");
        }
    }

    /**
     * 启用账号
     * @param map map
     */
    private void enableQlOriginalData(Map<String, Object> map){
        JSONArray array = new JSONArray(1);
        array.add(map.get("oid"));
        Map<String, String> header = this.getQlHeader(map);
        String s = HttpRequestUtils.doPut(map.get("address") + "/open/envs/enable?t=" + System.currentTimeMillis(), array.toJSONString(), header);
        if (s != null) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if ("200".equals(jsonObject.getString("code"))) {
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "已成功启用账号！");
            } else {
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "启用账号出错了！请查看后台日志");
            }
        }
    }

    /**
     * 添加面板原始数据
     *
     * @param map  map
     * @param list list
     */
    public void addQlOriginalData(Map<String, Object> map, JSONArray list) {
        try {
            Map<String, String> header = this.getQlHeader(map);
            String body = list.toJSONString();
            String s = HttpRequestUtils.doPost(map.get("address") + "/open/envs?t=" + System.currentTimeMillis(), body, header);
            if (s != null) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (!"200".equals(jsonObject.getString("code"))) {
                    throw new RuntimeException("code为" + jsonObject.getString("code"));
                } else {
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (array != null && array.size() > 0) {
                        for (Object data : array) {
                            JSONObject o = JSONObject.parseObject(JSON.toJSONString(data));
                            String remarks = o.getString("remarks");
                            if(remarks != null){
                                String[] split = remarks.split("-");
                                if(split.length > 1){
                                    String id = split[1];
                                    String oid = o.getString("id");
                                    int count = cookieService.updateCookieOidById(id, oid);
                                    if (count < 1) {
                                        throw new RuntimeException("更新cookie对应的id出现了异常！");
                                    }
                                }
                            }
                        }
                    }else{
                        MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "添加ck到远程时返回的状态码为200，但是未返回值，请查看日志排查！");
                    }
                }
            } else {
                throw new RuntimeException("添加面板原始数据返回值为null");
            }
        } catch (Exception e) {
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "添加原始数据出现了异常！" + map.toString() + e.getMessage());
            throw e;
        }
    }

    /**
     * 删除面板原始数据
     *
     * @param map map
     */
    private void deleteQlOriginalData(Map<String, Object> map) {
        try {
            JSONArray qlData = this.getQlData(map);
            JSONArray ids = new JSONArray();
            for (Object data : qlData) {
                JSONObject jsonObject = JSONObject.parseObject(data.toString());
                ids.add(jsonObject.getString("id"));
            }
            if (!ids.isEmpty()) {
                Map<String, String> header = this.getQlHeader(map);
                String s = HttpRequestUtils.doDelete(map.get("address") + "/open/envs?&t=" + System.currentTimeMillis(), ids.toJSONString(), header);
                if (s != null) {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    if (!"200".equals(jsonObject.getString("code"))) {
                        throw new RuntimeException();
                    }
                } else {
                    throw new RuntimeException("删除面板原始数据返回值为null");
                }
            }
        } catch (Exception e) {
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "删除原始数据出现了异常！" + map.toString() + e.getMessage());
            throw e;
        }
    }

    /**
     * 获取青龙数据
     *
     * @param map map
     * @return array
     */
    private JSONArray getQlData(Map<String, Object> map) {
        Map<String, String> header = this.getQlHeader(map);
        String s = HttpRequestUtils.doGet(map.get("address") + "/open/envs?searchValue=JD_COOKIE&t=" + System.currentTimeMillis(), header);
        if (s != null) {
            return JSONObject.parseObject(s).getJSONArray("data");
        } else {
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "获取青龙面板数据出现了异常！" + map.get("address"));
            throw new RuntimeException();
        }
    }

    private Map<String, String> getQlHeader(Map<String, Object> map){
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", map.get("tokenType") + " " + map.get("token"));
        header.put("User-Agent", "qqbot client");
        header.put("content-type", "application/json");
        return header;
    }
}