package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.service.WxAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;



/**
 * 公众号账号
 *
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
@RestController
@RequestMapping("/manage/wxAccount")
@Api(tags = {"公众号账号-管理后台"})
public class WxAccountManageController {
    @Autowired
    private WxAccountService wxAccountService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:wxaccount:list")
    @ApiOperation(value = "列表")
    public Result list(){
        List<WxAccount> list = wxAccountService.list();
        wxAccountService.checkAndUpdate(list);
        return Result.ok().put("list", list);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{appid}")
    @RequiresPermissions("wx:wxaccount:info")
    @ApiOperation(value = "详情")
    public Result info(@PathVariable("appid") String appid) {
        WxAccount wxAccount = wxAccountService.getById(appid);

        return Result.ok().put("wxAccount", wxAccount);
    }


    /**
     * 全量同步
     */
    @PostMapping("/sync/{appid}")
    @RequiresPermissions("wx:wxaccount:save")
    @ApiOperation(value = "全量同步")
    public Result sync(@PathVariable("appid") String appid) {
        boolean status = wxAccountService.sync(appid);
        return Result.ok().put("sync", status);
    }

    /**
     * 全量同步-前置处理
     */
    @PostMapping("/sync/pre/{appid}")
    @RequiresPermissions("wx:wxaccount:save")
    @ApiOperation(value = "全量同步前置处理")
    public Result syncPre(@PathVariable("appid") String appid) {
        Object qrcode = wxAccountService.syncPre(appid);
        return Result.ok().put("qrcode", qrcode).put("expire", "590");
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:wxaccount:save")
    @ApiOperation(value = "保存")
    public Result save(@RequestBody WxAccount wxAccount) {
        wxAccountService.save(wxAccount);

        return Result.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:wxaccount:delete")
    @ApiOperation(value = "删除")
    public Result delete(@RequestBody String[] appids){
		wxAccountService.removeByIds(Arrays.asList(appids));

        return Result.ok();
    }

}
