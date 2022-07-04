package com.github.niefy.modules.wx.handler;

import java.util.Map;
import java.util.Objects;

import com.github.niefy.common.utils.WxReplayStorageHolder;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.MsgReplyService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.util.StringUtils;

/**
 * @author Binary Wang
 */
@Component
public class SubscribeHandler extends AbstractHandler {
    @Autowired
    MsgReplyService msgReplyService;
    @Autowired
    WxUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        logger.info("新关注用户 OPENID: {}，事件：{}", wxMessage.getFromUser(), wxMessage.getEventKey());
        // 处理特殊事件，如用户扫描带参二维码关注
        if (StringUtils.hasText(wxMessage.getEventKey())) {
            return handleSpecial(wxMessage);
        }
        String appid = WxMpConfigStorageHolder.get();
        userService.refreshUserInfo(wxMessage.getFromUser(), appid);
        boolean autoReplyed = msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());
        if (!autoReplyed) {
            WxMpXmlOutMessage message = WxReplayStorageHolder.pop();
            if (Objects.nonNull(message)) {
                return message;
            }
        }
        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    protected WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) {
        this.logger.info("特殊请求-新关注用户 OPENID: {}", wxMessage.getFromUser());
        //对关注事件和扫码事件分别处理
        String appid = WxMpConfigStorageHolder.get();
        userService.refreshUserInfo(wxMessage.getFromUser(), appid);
        boolean autoReplyed = msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());
        if (!autoReplyed) {
            WxMpXmlOutMessage message = WxReplayStorageHolder.pop();
            if (Objects.nonNull(message)) {
                return message;
            }
        }
        return null;
    }

}
