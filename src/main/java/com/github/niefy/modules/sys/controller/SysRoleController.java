package com.github.niefy.modules.sys.controller;

import com.github.niefy.common.annotation.SysLog;
import com.github.niefy.common.utils.Constant;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;
import com.github.niefy.common.validator.ValidatorUtils;
import com.github.niefy.modules.sys.entity.SysRoleEntity;
import com.github.niefy.modules.sys.service.SysRoleMenuService;
import com.github.niefy.modules.sys.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/role")
@Api(tags = {"管理后台角色"})
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:role:list")
    @ApiOperation(value = "角色列表",notes = "")
    public Result list(@RequestParam Map<String, Object> params) {
        //如果不是超级管理员，则只查询自己创建的角色列表
        if (getUserId() != Constant.SUPER_ADMIN) {
            params.put("createUserId", getUserId());
        }

        PageUtils page = sysRoleService.queryPage(params);

        return Result.ok().put("page", page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    @RequiresPermissions("sys:role:select")
    @ApiOperation(value = "拥有的角色列表",notes = "")
    public Result select() {
        Map<String, Object> map = new HashMap<>(4);

        //如果不是超级管理员，则只查询自己所拥有的角色列表
        if (getUserId() != Constant.SUPER_ADMIN) {
            map.put("create_user_id", getUserId());
        }
        List<SysRoleEntity> list = sysRoleService.listByMap(map);

        return Result.ok().put("list", list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    @RequiresPermissions("sys:role:info")
    @ApiOperation(value = "角色详情",notes = "")
    public Result info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return Result.ok().put("role", role);
    }

    /**
     * 保存角色
     */
    @SysLog("保存角色")
    @PostMapping("/save")
    @RequiresPermissions("sys:role:save")
    @ApiOperation(value = "保存角色",notes = "")
    public Result save(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        role.setCreateUserId(getUserId());
        sysRoleService.saveRole(role);

        return Result.ok();
    }

    /**
     * 修改角色
     */
    @SysLog("修改角色")
    @PostMapping("/update")
    @RequiresPermissions("sys:role:update")
    @ApiOperation(value = "修改角色",notes = "")
    public Result update(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        role.setCreateUserId(getUserId());
        sysRoleService.update(role);

        return Result.ok();
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @PostMapping("/delete")
    @RequiresPermissions("sys:role:delete")
    @ApiOperation(value = "删除角色",notes = "")
    public Result delete(@RequestBody Long[] roleIds) {
        sysRoleService.deleteBatch(roleIds);

        return Result.ok();
    }
}
