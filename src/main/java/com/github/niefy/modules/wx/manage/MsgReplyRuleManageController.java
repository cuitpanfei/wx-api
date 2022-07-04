package com.github.niefy.modules.wx.manage;

import java.util.Arrays;
import java.util.Map;

import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;


/**
 * 自动回复规则
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgReplyRule")
@Api(tags = {"自动回复规则-管理后台"})
public class MsgReplyRuleManageController {
    @Autowired
    private MsgReplyRuleService msgReplyRuleService;
    @Autowired
    private WxMpService wxMpService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:msgreplyrule:list")
    @ApiOperation(value = "列表")
    public Result list(@CookieValue String appid, @RequestParam Map<String, Object> params) {
        params.put("appid",appid);
        PageUtils page = msgReplyRuleService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{ruleId}")
    @RequiresPermissions("wx:msgreplyrule:info")
    @ApiOperation(value = "详情")
    public Result info(@PathVariable("ruleId") Integer ruleId) {
        MsgReplyRule msgReplyRule = msgReplyRuleService.getById(ruleId);

        return Result.ok().put("msgReplyRule", msgReplyRule);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:msgreplyrule:save")
    @ApiOperation(value = "保存")
    public Result save(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.save(msgReplyRule);

        return Result.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("wx:msgreplyrule:update")
    @ApiOperation(value = "修改")
    public Result update(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.updateById(msgReplyRule);

        return Result.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:msgreplyrule:delete")
    @ApiOperation(value = "删除")
    public Result delete(@RequestBody Integer[] ruleIds) {
        msgReplyRuleService.removeByIds(Arrays.asList(ruleIds));

        return Result.ok();
    }

}
