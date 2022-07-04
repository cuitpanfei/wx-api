package com.github.niefy.modules.oss.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.github.niefy.common.exception.RRException;
import com.github.niefy.common.utils.ConfigConstant;
import com.github.niefy.common.utils.Constant;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;
import com.github.niefy.common.validator.ValidatorUtils;
import com.github.niefy.common.validator.group.AliyunGroup;
import com.github.niefy.common.validator.group.QcloudGroup;
import com.github.niefy.common.validator.group.QiniuGroup;
import com.github.niefy.modules.oss.cloud.CloudStorageConfig;
import com.github.niefy.modules.oss.cloud.OSSFactory;
import com.github.niefy.modules.oss.entity.SysOssEntity;
import com.github.niefy.modules.oss.service.SysOssService;
import com.github.niefy.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sys/oss")
@Api(tags = {"对象存储/文件上传"})
public class SysOssController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private WxMpService wxMpService;

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     * 列表
     */
    @ApiOperation(value = "文件列表", notes = "对象存储管理的文件")
    @GetMapping("/list")
    @RequiresPermissions("sys:oss:all")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysOssService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "云存储配置信息", notes = "首次使用前先管理后台新增配置")
    public Result config() {
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

        return Result.ok().put("config", config);
    }


    /**
     * 保存云存储配置信息
     */
    @PostMapping("/saveConfig")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "保存云存储配置信息")
    public Result saveConfig(@RequestBody CloudStorageConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getType() == Constant.CloudService.QINIU.getValue()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(config, QiniuGroup.class);
        } else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        } else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(config, QcloudGroup.class);
        }

        sysConfigService.updateValueByKey(KEY, JSON.toJSONString(config));

        return Result.ok();
    }


    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "上传文件到OSS")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }

        //上传文件
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        File tmpFile = FileUtils.createTmpFile(file.getInputStream(), System.currentTimeMillis() + "", suffix);
        byte[] fileBytes = FileUtil.readBytes(tmpFile);
        String url = Objects.requireNonNull(OSSFactory.build()).uploadSuffix(fileBytes, suffix);

        //保存文件信息
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.save(ossEntity);
        url = wxMpService.getMaterialService().mediaImgUpload(tmpFile).getUrl();
        ossEntity.setMediaUrl(url);
        FileUtil.del(tmpFile);
        return Result.ok().put("url", url);
    }


    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "删除文件", notes = "只删除记录，云端文件不会删除")
    public Result delete(@RequestBody Long[] ids) {
        sysOssService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
