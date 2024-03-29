package com.gaofeicm.qqbot.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.command.entity.Command;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.utils.*;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gaofeicm
 */
@Component
@EnableScheduling
public class JdServiceImpl {

    @Resource
    private CookieService cookieService;

    @Resource
    private RemoteQlServiceImpl remoteQlService;

    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取用户京东农场
     * @param ck ck
     * @return 用户信息
     */
    @SneakyThrows
    public JSONObject getJdnc(String ck){
        JSONObject map = new JSONObject();
        JSONObject jdncData1 = this.getJdncData1(ck);
        JSONObject jdncData2 = this.getJdncData2(ck);
        if(jdncData1 == null || jdncData2 == null){
            return null;
        }
        JSONObject farmUserPro = jdncData2.getJSONObject("farmUserPro");
        if(farmUserPro != null){
            double waterEveryDayT = jdncData1.getJSONObject("totalWaterTaskInit").getDoubleValue("totalWaterTaskTimes");
            double waterTotalT = (farmUserPro.getIntValue("treeTotalEnergy") - farmUserPro.getIntValue("treeEnergy") - farmUserPro.getIntValue("totalEnergy ")) / 10d;
            double waterD = 0D;
            if(waterEveryDayT != 0){
                waterD = Double.parseDouble(String.format("%.2f", (waterTotalT / waterEveryDayT)));
            }
            map.put("JdFarmProdName", farmUserPro.getString("name"));
            map.put("JdtreeEnergy", farmUserPro.getString("treeEnergy"));
            map.put("JdtreeTotalEnergy", farmUserPro.getString("treeTotalEnergy"));
            map.put("treeState", farmUserPro.getString("treeState"));
            map.put("JdwaterTotalT", waterTotalT);
            map.put("JdwaterD", waterD);
        }
        return map;
    }

    /**
     * 获取用户京东农场数据1
     * @param ck ck
     * @return 用户信息
     */
    @SneakyThrows
    private JSONObject getJdncData1(String ck){
        Map<String, String> header = new HashMap<>(2);
        header.put("Cookie", ck);
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=taskInitForFarm&appid=wh5&body=" + URLEncoder.encode("{\"version\":14,\"channel\":1,\"babelChannel\":\"120\"}", "UTF-8");
        String s = HttpRequestUtils.doGet(url, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户京东农场数据2
     * @param ck ck
     * @return 用户信息
     */
    @SneakyThrows
    private JSONObject getJdncData2(String ck){
        Map<String, String> header = new HashMap<>(13);
        header.put("Cookie", ck);
        header.put("accept", "*/*");
        header.put("accept-encoding", "gzip, deflate, br");
        header.put("accept-language", "zh-CN,zh;q=0.9");
        header.put("cache-control", "no-cache");
        header.put("origin", "https://home.m.jd.com");
        header.put("pragma", "no-cache");
        header.put("referer", "https://home.m.jd.com/myJd/newhome.action");
        header.put("sec-fetch-dest", "empty");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-site");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=initForFarm";
        String body = "body=" + URLEncoder.encode("{ \"version\": 4 }", "UTF-8") + "&appid=wh5&clientVersion=9.1.0";
        String s = HttpRequestUtils.doPost(url, body, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户赚赚金币
     * @param ck ck
     * @return 用户信息
     */
    @SneakyThrows
    public JSONObject getJdww(String ck){
        Map<String, String> header = new HashMap<>(6);
        header.put("Cookie", ck);
        header.put("Host", "api.m.jd.com");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Origin", "https://joypark.jd.com");
        header.put("Referer", "https://joypark.jd.com/?activityId=LsQNxL7iWDlXUs6cFl-AAg&lng=113.387899&lat=22.512678&sid=4d76080a9da10fbb31f5cd43396ed6cw&un_area=19_1657_52093_0");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=joyBaseInfo";
        String body = "body=" + URLEncoder.encode("{\"taskId\":\"\",\"inviteType\":\"\",\"inviterPin\":\"\",\"linkId\":\"LsQNxL7iWDlXUs6cFl-AAg\"}", "UTF-8") + "&appid=activities_platform";
        String s = HttpRequestUtils.doPost(url, body, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户京东秒杀
     * @param ck ck
     * @return 结果
     */
    public JSONObject getJdms(String ck){
        Map<String, String> header = new HashMap<>(5);
        header.put("Cookie", ck);
        header.put("origin", "https://h5.m.jd.com");
        header.put("referer", "https://h5.m.jd.com/babelDiy/Zeus/2NUvze9e1uWf4amBhe1AV6ynmSuH/index.html");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?appid=SecKill2020";
        String body = "functionId=homePageV2&body=&client=wh5&clientVersion=1.0.0";
        String s = HttpRequestUtils.doPost(url, body, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户赚赚金币
     * @param ck ck
     * @return 用户信息
     */
    public JSONObject getJdzz(String ck){
        Map<String, String> header = new HashMap<>(8);
        header.put("Cookie", ck);
        header.put("Host", "api.m.jd.com");
        header.put("Connection", "keep-alive");
        header.put("Content-Type", "application/json");
        header.put("Accept-Language", "zh-cn");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Referer", "http://wq.jd.com/wxapp/pages/hd-interaction/index/index");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=interactTaskIndex&body=&client=wh5&clientVersion=9.1.0";
        String s = HttpRequestUtils.doGet(url, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户极速版金币
     * @param ck ck
     * @return 用户信息
     */
    @SneakyThrows
    public JSONObject getJdjs(String ck){
        Map<String, String> header = new HashMap<>(6);
        header.put("Cookie", ck);
        header.put("Host", "api.m.jd.com");
        header.put("Accept-Language", "zh-Hans-CN;q=1, ja-CN;q=0.9");
        header.put("accept", "*/*");
        header.put("kernelplatform", "RN");
        header.put("User-Agent", "JDMobileLite/3.1.0 (iPad; iOS 14.4; Scale/2.00)");
        long time = System.currentTimeMillis();
        String body = "{\"method\":\"userCashRecord\",\"data\":{\"channel\":1,\"pageNum\":1,\"pageSize\":20}}";
        String str = "lite-android&" + body + "&android&3.1.0&MyAssetsService.execute&" + time + "&846c4c32dae910ef";
        String sign = CommonUtils.HmacSHA256(str, "12aea658f76e453faf803d15c40a72e0");
        String url = "https://api.m.jd.com/client.actionapi?functionId=MyAssetsService.execute&body=" + URLEncoder.encode(body, "UTF-8") + "&appid=lite-android&client=android&uuid=846c4c32dae910ef&clientVersion=3.1.0&t=" + time + "&sign=" + sign;
        String s = HttpRequestUtils.doGet(url, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户京喜牧场鸡蛋
     * @param ck ck
     * @return 用户信息
     */
    public JSONObject getJxmc(String ck){
        Map<String, String> header = new HashMap<>(9);
        header.put("Cookie", ck);
        header.put("Host", "m.jingxi.com");
        header.put("Connection", "keep-alive");
        header.put("Accept", "application/json");
        header.put("Accept-Language", "zh-cn");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Referer", "https://st.jingxi.com/pingou/jxmc/index.html");
        header.put("Origin", "https://st.jingxi.com");
        header.put("User-Agent", this.getAgent());
        String url = "https://m.jingxi.com/jxmc/queryservice/GetHomePageInfo?channel=7&sceneid=1001&activeid=null&activekey=null&isgift=1&isquerypicksite=1&_stk=channel%2Csceneid&_ste=1";
        url += "&h5st="+ CommonUtils.randomString(32) + "&_=" + System.currentTimeMillis() + 2 + "&sceneval=2&g_login_type=1&callback=jsonpCBK" + CommonUtils.randomString(1) + "&g_ty=ls";
        String s = HttpRequestUtils.doGet(url, header);
        if(s != null){
            s = s.substring(10, s.length() - 1);
            return JSONObject.parseObject(s);
        }else{
            return new JSONObject();
        }
    }

    /**
     * 获取用户红包数据
     * @param ck ck
     * @return 用户信息
     */
    public JSONObject getRed(String ck){
        Map<String, String> header = new HashMap<>(8);
        header.put("Cookie", ck);
        header.put("Host", "m.jingxi.com");
        header.put("Connection", "keep-alive");
        header.put("Accept", "*/*");
        header.put("Accept-Language", "zh-cn");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Referer", "https://st.jingxi.com/my/redpacket.shtml?newPg=App&jxsid=16156262265849285961");
        header.put("User-Agent", this.getAgent());
        String url = "https://m.jingxi.com/user/info/QueryUserRedEnvelopesV2?type=1&orgFlag=JD_PinGou_New&page=1&cashRedType=1&redBalanceFlag=1&channel=1&_=";
        url += System.currentTimeMillis() + "&sceneval=2&g_login_type=1&g_ty=ls";
        String s = HttpRequestUtils.doGet(url, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户红京东萌宠
     * @param ck ck
     * @return 京东萌宠信息
     */
    @SneakyThrows
    public JSONObject getJdmc(String ck, String functionId){
        Map<String, String> header = new HashMap<>(4);
        header.put("Cookie", ck);
        header.put("Host", "api.m.jd.com");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=" + functionId;
        String body = "body=" + URLEncoder.encode("{\"version\":\"2\",\"channel\":\"app\"", "UTF-8") + "&appid=wh5&loginWQBiz=pet-town&clientVersion=9.0.4";
        String s = HttpRequestUtils.doPost(url, body, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 组织京东数据
     * @param ck ck
     * @return 结果
     */
    @SneakyThrows
    public Map<String, Object> getBean(String ck){
        Map<String, Object> beans = new HashMap<>(2);
        long todayIn = 0L;
        long todayOut = 0L;
        long yesterdayIn = 0L;
        long yesterdayOut = 0L;
        List<Long> todayBeans = new ArrayList<>();
        List<Long> yesterdayBeans = new ArrayList<>();
        long time1 = ((System.currentTimeMillis() + 28800000) / 86400000) * 86400000 - 28800000 - (24 * 60 * 60 * 1000);
        long time2 = ((System.currentTimeMillis() + 28800000) / 86400000) * 86400000 - 28800000;
        int page = 1;
        boolean flag = true;
        while (flag) {
            JSONObject bean = this.getBeanData(ck, page);
            if (bean != null && "0".equals(bean.getString("code"))) {
                page ++;
                JSONArray detailList = bean.getJSONArray("detailList");
                for (Object o : detailList) {
                    JSONObject obj = JSON.parseObject(o.toString());
                    String date = obj.getString("date");
                    long time = SDF.parse(date).getTime();
                    String eventMassage = obj.getString("eventMassage");
                    long amount = obj.getLongValue("amount");
                    if(time >= time2 && (!eventMassage.contains("退还") && !eventMassage.contains("扣赠"))){
                        todayBeans.add(amount);
                    } else if(time1 <= time && time < time2 && (!eventMassage.contains("退还") && !eventMassage.contains("扣赠"))){
                        yesterdayBeans.add(amount);
                    } else if (time1 > time) {
                        flag = false;
                        break;
                    }
                }
            }else{
                flag = false;
                break;
            }
        }
        for (Long b : todayBeans) {
            if(b > 0){
                todayIn += b;
            }else {
                todayOut += (b * -1);
            }
        }
        for (Long b : yesterdayBeans) {
            if(b > 0){
                yesterdayIn += b;
            }else {
                yesterdayOut += (b * -1);
            }
        }
        beans.put("today", "收" + todayIn + "豆,支" + todayOut + "豆");
        beans.put("yesterday", "收" + yesterdayIn + "豆,支" + yesterdayOut + "豆");
        return beans;
    }

    /**
     * 获取用户京豆列表
     * @param ck ck
     * @return 结果
     */
    @SneakyThrows
    private JSONObject getBeanData(String ck, int page){
        Map<String, String> header = new HashMap<>(4);
        header.put("Cookie", ck);
        header.put("Host", "api.m.jd.com");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("User-Agent", this.getAgent());
        String url = "https://api.m.jd.com/client.action?functionId=getJingBeanBalanceDetail";
        String body = "body=" + URLEncoder.encode("{ \"pageSize\": \"20\", \"page\":\"" + page + "\" }", "UTF-8") + "&appid=ld";
        String s = HttpRequestUtils.doPost(url, body, header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户信息1
     * @param ck ck
     * @return 用户信息
     */
    public JSONObject getUserInfo1(String ck){
        Map<String, String> header = new HashMap<>(8);
        header.put("Cookie", ck);
        header.put("Host", "me-api.jd.com");
        header.put("Accept", "*/*");
        header.put("Connection", "keep-alive");
        header.put("Accept-Language", "zh-cn");
        header.put("Referer", "https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("User-Agent", this.getAgent());
        String s = HttpRequestUtils.doGet("https://me-api.jd.com/user_new/info/GetJDUserInfoUnion", header);
        return JSONObject.parseObject(s);
    }

    /**
     * 获取用户信息2
     * @param ck ck
     * @return 用户信息
     */
    public JSONObject getUserInfo2(String ck){
        Map<String, String> header = new HashMap<>(7);
        header.put("Cookie", ck);
        header.put("Host", "wxapp.m.jd.com");
        header.put("content-type", "application/x-www-form-urlencoded");
        header.put("Connection", "keep-alive");
        header.put("Referer", "https://servicewechat.com/wxa5bf5ee667d91626/161/page-frame.html");
        header.put("Accept-Encoding", "gzip,compress,br,deflate");
        header.put("User-Agent", this.getAgent());
        String s = HttpRequestUtils.doPost("https://wxapp.m.jd.com/kwxhome/myJd/home.json?&useGuideModule=0&bizId=&brandId=&fromType=wxapp&timestamp=" + System.currentTimeMillis(),  header);
        return JSONObject.parseObject(s);
    }

    /**
     * 加载ck信息，判断是否登陆
     * @param  ck ck
     * @param  qq qq
     * @return 检查结果
     */
    @SneakyThrows
    public String loadCk(String ck, String qq) {
        String msg = "";
        String pin = CookieUtils.getPin(ck);
        //对pin转码
        pin = CommonUtils.encodeChineseStr(pin);
        String key = CookieUtils.getKey(ck);
        ck = pin + key;
        JSONObject o = this.checkLogin(ck);
        if (o.getIntValue("islogin") == 1) {
            msg = "提交成功，账号有效！";
        } else if (o.getIntValue("islogin") == 0) {
            msg = "账号无效，请重新获取！";
            return msg;
        } else {
            msg = "账号状态未知，请联系管理员！";
            return msg;
        }
        //谁提交ck，QQ号就改为谁的，如果有的话
        Cookie cookieByPin = cookieService.getCookieByPin(ck);
        if(cookieByPin != null){
            Cookie cookie = new Cookie();
            cookie.setId(cookieByPin.getId());
            cookie.setQq(qq);
            cookie.setCookie(ck);
            cookie.setPtPin(pin);
            cookie.setPtKey(key);
            cookieService.saveCookie(cookie);
        }
        //判断是否添加
        Cookie cookie = cookieService.getCookieByQqAndCk(qq, ck);
        if(cookie == null){
            //添加
            cookie = new Cookie();
            cookie.setQq(qq);
            cookie.setCookie(ck);
            cookie.setAvailable(0);
            cookie.setPtPin(CookieUtils.getPin(ck));
            cookie.setPtKey(CookieUtils.getKey(ck));
            cookieService.saveCookie(cookie);
            msg += "您是首次添加此账号，提交请求已发送给管理员，等待审核！";
            String message = "QQ:" + qq + ",pin:" + pin + "，id:" + cookie.getId() + "，正在请求添加账号，是否同意？\r\n1.同意(一个月)\r\n2.同意(一年)r\n3.同意(3个月)r\n4.同意(半年)\r\n5.拒绝";
            Command command = CommandUtils.getDefaultCommand(message);
            command.setOption("1", "2", "3");
            command.setAction("setCookieExpForMonth", "setCookieExpForYear", "setCookieExpForQuarter", "setCookieExpForHalfYear", "refuseCookie");
            command.putParam("from", qq).putParam("to", CommonUtils.getAdminQq()).putParam("id", cookie.getId()).putParam("ck", ck);
            CommandUtils.addCommand(CommonUtils.getAdminQq(), command);
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), message);
        }else{
            if(CookieUtils.getExpireDay(cookie.getExpirationTime()) > 0){
                cookie.setCookie(ck);
                cookie.setPtPin(CookieUtils.getPin(ck));
                cookie.setPtKey(CookieUtils.getKey(ck));
                cookie.setAvailable(1);
                cookie.setUpdateTime(new Date());
                cookieService.saveCookie(cookie);
                msg += "ck有效期约为30天,代挂服务时间约剩" + CookieUtils.getExpireDay(cookie);
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "QQ：" + cookie.getQq() + "，pin：" + pin + "，更新ck成功！开始同步至对应面板！");
                //同步至面板
                remoteQlService.updateQlOriginalData(cookie.getId());
            }else{
                msg += "您的账号已过期，请续费后重新提交!";
            }
        }
        return msg;
    }

    /**
     * 检查ck是否是登陆状态
     * @param ck ck
     * @return
     */
    public JSONObject checkLogin(String ck){
        Map<String, String> header = new HashMap<>(3);
        header.put("Cookie", ck);
        header.put("referer", "https://h5.m.jd.com/");
        header.put("User-Agent", this.getAgent());
        String s = HttpRequestUtils.doGet("https://plogin.m.jd.com/cgi-bin/ml/islogin", header);
        JSONObject o = JSONObject.parseObject(s);
        return o;
    }


    /**
     * 检查ck合法性
     * @param msg ck
     * @return 结果
     */
    public String checkCk(String msg){
        msg = msg.replace(" ","");
        String regexPin = "^pt_pin=([^; ]+)(?=;?);pt_key=([^; ]+)(?=;?);$";
        String regexKey = "^pt_key=([^; ]+)(?=;?);pt_pin=([^; ]+)(?=;?);$";
        Matcher pinMatcher = Pattern.compile(regexPin).matcher(msg);
        if(pinMatcher.find()){
            return pinMatcher.group();
        }
        Matcher keyMatcher = Pattern.compile(regexKey).matcher(msg);
        if(keyMatcher.find()){
            return keyMatcher.group();
        }
        int i1 = msg.indexOf("pt_key=");
        int i2 = msg.indexOf("pt_pin=");
        if(i1 > -1 && i2 > -1){
            if(i2 > i1){
                msg.substring(i1);
                String a = msg.substring(i1);
                int i = a.indexOf(";", a.indexOf("pt_pin=")) + 1;
                return a.substring(0, i).replaceAll("&amp;#160;", "");
            }else{
                msg.substring(i2);
                String a = msg.substring(i2) + 1;
                int i = a.indexOf(";", a.indexOf("pt_key=")) + 1;
                return a.substring(0, i).replaceAll("&amp;#160;", "");
            }
        }
        return null;
    }

    /**
     * 获取用户信息字符串
     * @param levelName 等级名称
     * @param isPlusVip 是否是vip
     * @param userLevel 用户等级
     * @param jingXiang 京享值
     * @return
     */
    public String getAccountInfoStr(String levelName, String isPlusVip, String userLevel, String jingXiang) {
        if (levelName.length() > 2) {
            levelName = levelName.substring(0, 2);
        }
        switch (levelName) {
            case "注册":
                levelName = "\uD83D\uDE0A普通";
                break;
            case "钻石":
                levelName = "\uD83D\uDC8E钻石";
                break;
            case "金牌":
                levelName = "\uD83E\uDD47金牌";
                break;
            case "银牌":
                levelName = "\uD83E\uDD48银牌";
                break;
            case "铜牌":
                levelName = "\uD83E\uDD49铜牌";
                break;
            default:
        }
        if ("1".equals(isPlusVip)) {
            return levelName + "Plus, 等级:" + userLevel + ", " + jingXiang;
        } else {
            return levelName + "会员, 等级:" + userLevel + ", " + jingXiang;
        }
    }

    /**
     * 取得随机Agent
     * @return agent
     */
    private String getAgent(){
        return USER_AGENTS[CommonUtils.generateRangeRandomNumber(0, USER_AGENTS.length)];
    }

    private static final String[] USER_AGENTS = new String[]{
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; ONEPLUS A5010 Build/QKQ1.191014.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;14.3;network/4g;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;android;10.1.6;9;network/4g;Mozilla/5.0 (Linux; Android 9; Mi Note 3 Build/PKQ1.181007.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; GM1910 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
        "jdapp;android;10.1.6;9;network/wifi;Mozilla/5.0 (Linux; Android 9; 16T Build/PKQ1.190616.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;13.6;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.6;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.5;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.1;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.3;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.7;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.1;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.3;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;13.4;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 13_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.3;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;android;10.1.6;9;network/wifi;Mozilla/5.0 (Linux; Android 9; MI 6 Build/PKQ1.190118.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
        "jdapp;android;10.1.6;11;network/wifi;Mozilla/5.0 (Linux; Android 11; Redmi K30 5G Build/RKQ1.200826.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045511 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;11.4;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79",
        "jdapp;android;10.1.6;10;;network/wifi;Mozilla/5.0 (Linux; Android 10; M2006J10C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; M2006J10C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; ONEPLUS A6000 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045224 Mobile Safari/537.36",
        "jdapp;android;10.1.6;9;network/wifi;Mozilla/5.0 (Linux; Android 9; MHA-AL00 Build/HUAWEIMHA-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
        "jdapp;android;10.1.6;8.1.0;network/wifi;Mozilla/5.0 (Linux; Android 8.1.0; 16 X Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
        "jdapp;android;10.1.6;8.0.0;network/wifi;Mozilla/5.0 (Linux; Android 8.0.0; HTC U-3w Build/OPR6.170623.013; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;14.0.1;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; LYA-AL00 Build/HUAWEILYA-AL00L; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;14.2;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.3;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.2;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;android;10.1.6;8.1.0;network/wifi;Mozilla/5.0 (Linux; Android 8.1.0; MI 8 Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; Redmi K20 Pro Premium Edition Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;14.3;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;iPhone;10.1.6;14.3;network/4g;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
        "jdapp;android;10.1.6;11;network/wifi;Mozilla/5.0 (Linux; Android 11; Redmi K20 Pro Premium Edition Build/RKQ1.200826.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045513 Mobile Safari/537.36",
        "jdapp;android;10.1.6;10;network/wifi;Mozilla/5.0 (Linux; Android 10; MI 8 Build/QKQ1.190828.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
        "jdapp;iPhone;10.1.6;14.1;network/wifi;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
    };
}
