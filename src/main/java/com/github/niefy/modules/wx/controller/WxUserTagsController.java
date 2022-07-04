package com.github.niefy.modules.wx.controller;

import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.form.WxUserTaggingForm;
import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 粉丝标签
 */
@RestController
@RequestMapping("/wxUserTags")
@RequiredArgsConstructor
@Api(tags = {"粉丝标签"})
public class WxUserTagsController {
    @Autowired
    WxUserTagsService wxUserTagsService;
    @Autowired
    WxUserService wxUserService;
    private final WxMpService wxMpService;

    @GetMapping("/userTags")
    @ApiOperation(value = "当前用户的标签")
    public Result userTags(@CookieValue String appid, @CookieValue String openid){
        if(openid==null){
            return Result.error("none_openid");
        }
        this.wxMpService.switchoverTo(appid);
        WxUser wxUser = wxUserService.getById(openid);
        if(wxUser==null){
            wxUser=wxUserService.refreshUserInfo(openid,appid);
            if(wxUser==null) {
                return Result.error("not_subscribed");
            }
        }
        return Result.ok().put(wxUser.getTagidList());
    }

    @PostMapping("/tagging")
    @ApiOperation(value = "给用户绑定标签")
    public Result tagging(@CookieValue String appid, @CookieValue String openid , @RequestBody WxUserTaggingForm form) {
        this.wxMpService.switchoverTo(appid);
        try {
            wxUserTagsService.tagging(form.getTagid(),openid);
        }catch (WxErrorException e){
            WxError error = e.getError();
            if(50005==error.getErrorCode()){//未关注公众号
                return Result.error("not_subscribed");
            }else {
                return Result.error(error.getErrorMsg());
            }
        }
        return Result.ok();
    }

    @PostMapping("/untagging")
    @ApiOperation(value = "解绑标签")
    public Result untagging(@CookieValue String appid, @CookieValue String openid , @RequestBody WxUserTaggingForm form) throws WxErrorException {
        this.wxMpService.switchoverTo(appid);
        wxUserTagsService.untagging(form.getTagid(),openid);
        return Result.ok();
    }
}
