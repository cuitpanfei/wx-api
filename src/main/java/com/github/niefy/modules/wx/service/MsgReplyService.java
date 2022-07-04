package com.github.niefy.modules.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.builder.outxml.BaseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * 公众号消息处理
 * 官方文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#7
 * WxJava客服消息文档：https://github.com/Wechat-Group/WxJava/wiki/MP_主动发送消息（客服消息）
 */
public interface MsgReplyService {
    Logger logger = LoggerFactory.getLogger(MsgReplyService.class);

    /**
     * 根据规则配置通过微信客服消息接口自动回复消息
     *
     * @param appid
     * @param exactMatch 是否精确匹配
     * @param toUser     用户openid
     * @param keywords   匹配关键词
     * @return 是否已自动回复，无匹配规则则不自动回复
     */
    boolean tryAutoReply(String appid, boolean exactMatch, String toUser, String keywords);

    default void reply(String toUser, String replyType, String replyContent) {
        try {
            if (WxConsts.KefuMsgType.TEXT.equals(replyType)) {
                this.replyText(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.IMAGE.equals(replyType)) {
                this.replyImage(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.VOICE.equals(replyType)) {
                this.replyVoice(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.VIDEO.equals(replyType)) {
                this.replyVideo(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MUSIC.equals(replyType)) {
                this.replyMusic(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.NEWS.equals(replyType)) {
                this.replyNews(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MPNEWS.equals(replyType)) {
                this.replyMpNews(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.WXCARD.equals(replyType)) {
                this.replyWxCard(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MINIPROGRAMPAGE.equals(replyType)) {
                this.replyMiniProgram(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MSGMENU.equals(replyType)) {
                this.replyMsgMenu(toUser, replyContent);
            }
        } catch (Exception e) {
            logger.error("自动回复出错：", e);
        }
    }

    default WxMpXmlOutMessage replayBySelfHandler(String toUser, String replyType, String replyContent) {
        return replayBySelfHandler(null, toUser, replyType, replyContent);
    }

    default WxMpXmlOutMessage replayBySelfHandler(String fromUser, String toUser, String replyType, String replyContent) {
        BaseBuilder baseBuilder = null;
        if (WxConsts.XmlMsgType.TEXT.equals(replyType)) {
            baseBuilder = WxMpXmlOutMessage.TEXT().content(replyContent);
        } else if (WxConsts.XmlMsgType.IMAGE.equals(replyType)) {
            baseBuilder = WxMpXmlOutMessage.IMAGE().mediaId(replyContent);
        } else if (WxConsts.XmlMsgType.VOICE.equals(replyType)) {
            baseBuilder = WxMpXmlOutMessage.VOICE().mediaId(replyContent);
        } else if (WxConsts.XmlMsgType.VIDEO.equals(replyType)) {
            baseBuilder = WxMpXmlOutMessage.VIDEO().mediaId(replyContent);
        } else if (WxConsts.XmlMsgType.MUSIC.equals(replyType)) {
            JSONObject json = JSON.parseObject(replyContent);
            baseBuilder = WxMpXmlOutMessage.MUSIC().musicUrl(json.getString("musicurl"))
                    .hqMusicUrl(json.getString("hqmusicurl"))
                    .title(json.getString("title"))
                    .description(json.getString("description"))
                    .thumbMediaId(json.getString("thumb_media_id"));
        } else if (WxConsts.XmlMsgType.NEWS.equals(replyType)) {
            WxMpXmlOutNewsMessage.Item article = JSON.parseObject(replyContent, WxMpXmlOutNewsMessage.Item.class);
            baseBuilder = WxMpXmlOutMessage.NEWS().addArticle(article);
        }
        if (Objects.nonNull(baseBuilder)) {
            baseBuilder.fromUser(Optional.ofNullable(fromUser).orElse("gh_2b50a79cdb27"));
            baseBuilder.toUser(toUser);
            WxMpXmlOutMessage outMessage = (WxMpXmlOutMessage) baseBuilder.build();
            return outMessage;
        }
        return null;
    }

    /**
     * 回复文字消息
     */
    void replyText(String toUser, String replyContent) throws WxErrorException;

    /**
     * 回复图片消息
     */
    void replyImage(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复录音消息
     */
    void replyVoice(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复视频消息
     */
    void replyVideo(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复音乐消息
     */
    void replyMusic(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复图文消息（点击跳转到外链）
     * 图文消息条数限制在1条以内
     */
    void replyNews(String toUser, String newsInfoJson) throws WxErrorException;

    /**
     * 回复公众号文章消息（点击跳转到图文消息页面）
     * 图文消息条数限制在1条以内
     */
    void replyMpNews(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复卡券消息
     */
    void replyWxCard(String toUser, String cardId) throws WxErrorException;

    /**
     * 回复小程序消息
     */
    void replyMiniProgram(String toUser, String miniProgramInfoJson) throws WxErrorException;

    /**
     * 回复菜单消息
     */
    void replyMsgMenu(String toUser, String msgMenusJson) throws WxErrorException;
}
