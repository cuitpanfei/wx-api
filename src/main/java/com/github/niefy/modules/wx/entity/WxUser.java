package com.github.niefy.modules.wx.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信粉丝
 * @author Nifury
 * @date 2017-9-27
 */
@Data
@TableName("wx_user")
public class WxUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private String openid;
    private String appid;
    private String phone;
    private String nickname;
    @Deprecated
    private int sex=0;
    @Deprecated
    private String city;
    @Deprecated
    private String province;
    private String headimgurl;
    @JSONField(name = "subscribe_time")
    private Date subscribeTime;
    private boolean subscribe;
    private String unionid;
    private String remark;
    private JSONArray tagidList;
    private String subscribeScene;
    private String qrSceneStr;

    public WxUser() {
    }

    public WxUser(String openid) {
        this.openid = openid;
    }

    public WxUser(String openid,String appid) {
        this.openid = openid;
        this.appid = appid;
    }

    public WxUser(WxMpUser wxMpUser,String appid) {
        this.openid = wxMpUser.getOpenId();
        this.appid = appid;
        this.subscribe=wxMpUser.getSubscribe();
        if(this.subscribe){
            this.subscribeTime = new Date(wxMpUser.getSubscribeTime()*1000);
            this.unionid=wxMpUser.getUnionId();
            this.remark=wxMpUser.getRemark();
            this.tagidList=JSON.parseArray(JSON.toJSONString(wxMpUser.getTagIds()));
            this.subscribeScene=wxMpUser.getSubscribeScene();
            String qrScene =  wxMpUser.getQrScene();
            this.qrSceneStr= !StringUtils.hasText(qrScene) ? wxMpUser.getQrSceneStr() : qrScene;
        }
    }

    public WxUser(WxOAuth2UserInfo wxMpUser, String appid) {
        this.openid = wxMpUser.getOpenid();
        this.appid = appid;
		this.subscribe=wxMpUser.getNickname()!=null;
		if(this.subscribe){
            this.nickname = wxMpUser.getNickname();
            this.headimgurl = wxMpUser.getHeadImgUrl();
            this.unionid=wxMpUser.getUnionId();
		}
    }

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}
