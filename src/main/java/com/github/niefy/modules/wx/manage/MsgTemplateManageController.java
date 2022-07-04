package com.github.niefy.modules.wx.manage;

import java.util.Arrays;
import java.util.Map;

import com.github.niefy.modules.wx.entity.MsgTemplate;
import com.github.niefy.modules.wx.form.TemplateMsgBatchForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import me.chanjar.weixin.common.error.WxErrorException;

import com.github.niefy.modules.wx.service.MsgTemplateService;
import com.github.niefy.modules.wx.service.TemplateMsgService;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;


/**
 * 消息模板
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgTemplate")
@Api(tags = {"消息模板-管理后台","模板消息的模板"})
public class MsgTemplateManageController {
    @Autowired
    private MsgTemplateService msgTemplateService;
    @Autowired
    private TemplateMsgService templateMsgService;
    @Autowired
    private WxMpService wxMpService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:msgtemplate:list")
    @ApiOperation(value = "列表")
    public Result list(@CookieValue String appid, @RequestParam Map<String, Object> params) {
        params.put("appid",appid);
        PageUtils page = msgTemplateService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("wx:msgtemplate:info")
    @ApiOperation(value = "详情-通过ID")
    public Result info(@PathVariable("id") Long id) {
        MsgTemplate msgTemplate = msgTemplateService.getById(id);

        return Result.ok().put("msgTemplate", msgTemplate);
    }
    /**
     * 信息
     */
    @GetMapping("/getByName")
    @RequiresPermissions("wx:msgtemplate:info")
    @ApiOperation(value = "详情-通过名称")
    public Result getByName(String name){
        MsgTemplate msgTemplate = msgTemplateService.selectByName(name);

        return Result.ok().put("msgTemplate", msgTemplate);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "保存")
    public Result save(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.save(msgTemplate);

        return Result.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("wx:msgtemplate:update")
    @ApiOperation(value = "修改")
    public Result update(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.updateById(msgTemplate);

        return Result.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:msgtemplate:delete")
    @ApiOperation(value = "删除")
    public Result delete(@RequestBody String[] ids) {
        msgTemplateService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

    /**
     * 同步公众号模板
     */
    @PostMapping("/syncWxTemplate")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "同步公众号模板")
    public Result syncWxTemplate(@CookieValue String appid) throws WxErrorException {
        this.wxMpService.switchoverTo(appid);
        msgTemplateService.syncWxTemplate(appid);
        return Result.ok();
    }

    /**
     * 批量向用户发送模板消息
     * 通过用户筛选条件（一般使用标签筛选），将消息发送给数据库中所有符合筛选条件的用户
     */
    @PostMapping("/sendMsgBatch")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "批量向用户发送模板消息",notes = "将消息发送给数据库中所有符合筛选条件的用户")
    public Result sendMsgBatch(@CookieValue String appid, @RequestBody TemplateMsgBatchForm form) {
        this.wxMpService.switchoverTo(appid);
        templateMsgService.sendMsgBatch(form, appid);
        return Result.ok();
    }


}
