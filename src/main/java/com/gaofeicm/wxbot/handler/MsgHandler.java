package com.gaofeicm.wxbot.handler;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.common.CommonVariable;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.event.message.FriendMessageEvent;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import com.gaofeicm.wxbot.builder.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@Component
public class MsgHandler extends AbstractHandler {

    @Resource
    private CookieService cookieService;

    @Resource
    private FriendMessageEvent friendMessageEvent;

    private static final ConcurrentHashMap<String, HashMap<String, Object>> TASK_QUEUE = new ConcurrentHashMap<>();

    private Pattern pattern1 = Pattern.compile("绑定QQ[0-9]*");

    private Pattern pattern2 = Pattern.compile("CODE[0-9]*");

    private Pattern pattern3 = Pattern.compile("[0-9]*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService weixinService, WxSessionManager sessionManager) {
        long begin = System.currentTimeMillis();
        String message = "";
        //判断消息类型
        Long msgId = wxMessage.getMsgId();
        System.out.println("收到消息的id为：" + msgId);
        if(wxMessage.getMsgType() != null && "text".equals(wxMessage.getMsgType())){
            String msg = wxMessage.getContent();
            String wx = wxMessage.getFromUser();
            log.info("收到WX：" + wx + "的消息，内容为:" + msg);
            //message = "已经收到您的消息，内容为：" + msg;
            message = execMsg(msg, wx);
        }else{
            message = "当前只支持处理文字消息！";
        }
        long end = System.currentTimeMillis();
        log.info("---------------------------本次命令消耗时间：" + (end - begin) / 1000 + "秒");
        return new TextBuilder().build(message, wxMessage, weixinService);
    }

    private String execMsg(String msg, String wx){
        String message = "";
        switch (msg){
            case "绑定":
                message = "请回复以下内容进行绑定（不要输入其他内容，输入取消退出绑定过程）：\r\n例如：绑定QQ12345678";
                TASK_QUEUE.put(wx, new HashMap<>());
                break;
            case "取消":
                TASK_QUEUE.remove(wx);
                message = "已退出当前会话。";
                break;
            default:
                message = beforeGo(msg, wx);
        }
        return message;
    }

    private String beforeGo(String msg, String wx){
        String message = "";
        //绑定QQ
        if(pattern1.matcher(msg).matches() && TASK_QUEUE.containsKey(wx)){
            String qq = msg.substring(4);
            if(pattern3.matcher(qq).matches()) {
                List<Cookie> cookieList = cookieService.getCookieByQq(qq);
                if(cookieList != null && cookieList.size() > 0){
                    String code = "CODE" + CommonUtils.randomNumber(6);
                    HashMap<String, Object> map = TASK_QUEUE.get(wx);
                    map = map == null ? new HashMap<>(2) : map;
                    map.put("code", code);
                    map.put("qq", qq);
                    TASK_QUEUE.put(wx, map);
                    MessageUtils.sendPrivateMsg(qq, "微信绑定提醒：\r\n微信号：" + wx + "正在绑定当前QQ号，若非本人操作，请忽略，若非本人操作且多次收到确认信息，请向管理员举报！\r\n本次绑定验证码：");
                    MessageUtils.sendPrivateMsg(qq, code);
                    message = "已将验证码发送至对应QQ号，请输入验证码完成绑定。";
                }else{
                    message = "该QQ未找到符合的记录！";
                }
            }else {
                message = "输入内容格式错误，请参照示例内容重新输入!";
            }
        }else if(pattern2.matcher(msg).matches() && TASK_QUEUE.containsKey(wx)){
            HashMap<String, Object> map = TASK_QUEUE.get(wx);
            if(map != null && msg.equals(map.get("code"))){
                String qq = (String) map.get("qq");
                cookieService.updateCookieWxidByQq(qq, wx);
                message = "已完成绑定，目前仅支持部分命令！\r\n因微信限制只能在5秒内回复信息，如果你有多个ck，请分别发送：“查询1”或者“查询2”，不要发送其他内容！如果没有回复，请重新发送！";
            }else{
                message = "验证码不正确！";
            }
        }else{
            String qq = cookieService.getQqByWxid(wx);
            if(qq == null){
                message = "请先绑定qq号！回复“绑定”命令按提示操作！";
            }else{
                CommonVariable.DOING_LIST.add(qq);
                try {
                    message = friendMessageEvent.handleStr(new JSONObject(2) {{
                        put("message", msg);
                        put("user_id", qq);
                    }});
                    CommonVariable.DOING_LIST.remove(qq);
                }catch (Exception e){
                    CommonVariable.DOING_LIST.remove(qq);
                }
            }
        }
        return message;
    }
}
