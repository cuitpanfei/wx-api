package com.github.niefy.modules.sys.controller;


import com.github.niefy.common.annotation.SysLog;
import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;
import com.github.niefy.common.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/config")
@Api(tags = {"系统配置信息"})
public class SysConfigController extends AbstractController {
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:config:list")
    @ApiOperation(value = "配置项列表",notes = "配置项需专业人员修改")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysConfigService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:config:info")
    @ApiOperation(value = "配置详情",notes = "")
    public Result info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.getById(id);

        return Result.ok().put("config", config);
    }

    /**
     * 保存配置
     */
    @SysLog("保存配置")
    @PostMapping("/save")
    @RequiresPermissions("sys:config:save")
    @ApiOperation(value = "保存配置",notes = "")
    public Result save(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.saveConfig(config);

        return Result.ok();
    }

    /**
     * 修改配置
     */
    @SysLog("修改配置")
    @PostMapping("/update")
    @RequiresPermissions("sys:config:update")
    @ApiOperation(value = "修改配置",notes = "")
    public Result update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.update(config);

        return Result.ok();
    }

    /**
     * 删除配置
     */
    @SysLog("删除配置")
    @PostMapping("/delete")
    @RequiresPermissions("sys:config:delete")
    @ApiOperation(value = "删除配置",notes = "")
    public Result delete(@RequestBody Long[] ids) {
        sysConfigService.deleteBatch(ids);

        return Result.ok();
    }

}
