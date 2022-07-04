package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.wx.form.MaterialFileDeleteForm;
import com.github.niefy.modules.wx.service.WxAssetsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftArticles;
import me.chanjar.weixin.mp.bean.draft.WxMpUpdateDraft;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialArticleUpdate;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialCountResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNewsBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.material.WxMpNewsArticle;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信公众号素材管理
 * 参考官方文档：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html
 * 参考WxJava开发文档：https://github.com/Wechat-Group/WxJava/wiki/MP_永久素材管理
 */
@RestController
@RequestMapping("/manage/wxAssets")
@Api(tags = {"公众号素材-管理后台"})
public class WxAssetsManageController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WxAssetsService wxAssetsService;

    /**
     * 获取素材总数
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialCount")
    @ApiOperation(value = "文件素材总数")
    public Result materialCount(@CookieValue String appid) throws WxErrorException {
        WxMpMaterialCountResult res = wxAssetsService.materialCount(appid);
        return Result.ok().put(res);
    }

    /**
     * 获取素材总数
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialNewsInfo")
    @ApiOperation(value = "图文素材")
    public Result materialNewsInfo(@CookieValue String appid, String mediaId) throws WxErrorException {
        WxMpMaterialNews res = wxAssetsService.materialNewsInfo(appid, mediaId);
        return Result.ok().put(res);
    }


    /**
     * 根据类别分页获取非图文素材列表
     *
     * @param type
     * @param page
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialFileBatchGet")
    @RequiresPermissions("wx:wxassets:list")
    @ApiOperation(value = "根据类别分页获取非图文素材列表")
    public Result materialFileBatchGet(@CookieValue String appid, @RequestParam(defaultValue = "image") String type,
                                       @RequestParam(defaultValue = "1") int page) throws WxErrorException {
        WxMpMaterialFileBatchGetResult res = wxAssetsService.materialFileBatchGet(appid, type, page);
        return Result.ok().put(res);
    }

    /**
     * 分页获取图文素材列表
     *
     * @param page
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialNewsBatchGet")
    @RequiresPermissions("wx:wxassets:list")
    @ApiOperation(value = "分页获取图文素材列表")
    public Result materialNewsBatchGet(@CookieValue String appid, @RequestParam(defaultValue = "1") int page) throws WxErrorException {
        WxMpMaterialNewsBatchGetResult res = wxAssetsService.materialNewsBatchGet(appid, page);
        return Result.ok().put(res);
    }

    /**
     * 添加图文永久素材
     *
     * @param articles
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialNewsUpload")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "添加图文永久素材")
    public Result materialNewsUpload(@CookieValue String appid, @RequestBody List<WxMpNewsArticle> articles) throws WxErrorException {
        if (articles.isEmpty()) {
            return Result.error("图文列表不得为空");
        }
        List<WxMpDraftArticles> list = articles.stream()
                .map(WxAssetsService::transform)
                .collect(Collectors.toList());
        WxMpMaterialUploadResult res = wxAssetsService.materialNewsUpload(appid, list);
        return Result.ok().put(res);
    }

    /**
     * 修改图文素材文章
     *
     * @param form
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialArticleUpdate")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "修改图文永久素材文章")
    public Result materialArticleUpdate(@CookieValue String appid, @RequestBody WxMpMaterialArticleUpdate form) throws WxErrorException {
        WxMpNewsArticle article = form.getArticles();
        if (article == null) {
            return Result.error("文章不得为空");
        }
        WxMpDraftArticles draftArticles = WxAssetsService.transform(article);
        WxMpUpdateDraft draft = WxMpUpdateDraft.builder().articles(draftArticles)
                .mediaId(form.getMediaId())
                .index(form.getIndex()).build();
        wxAssetsService.materialArticleUpdate(appid, draft);
        return Result.ok();
    }

    /**
     * 添加多媒体永久素材
     *
     * @param file
     * @param fileName
     * @param mediaType
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    @PostMapping("/materialFileUpload")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "添加多媒体永久素材")
    public Result materialFileUpload(@CookieValue String appid, MultipartFile file, String fileName, String mediaType) throws WxErrorException, IOException {
        if (file == null) {
            return Result.error("文件不得为空");
        }

        WxMpMaterialUploadResult res = wxAssetsService.materialFileUpload(appid, mediaType, fileName, file);
        return Result.ok().put(res);
    }

    /**
     * 删除素材
     *
     * @param form
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialDelete")
    @RequiresPermissions("wx:wxassets:delete")
    @ApiOperation(value = "删除素材")
    public Result materialDelete(@CookieValue String appid, @RequestBody MaterialFileDeleteForm form) throws WxErrorException {
        boolean res = wxAssetsService.materialDelete(appid, form.getMediaId());
        return Result.ok().put(res);
    }



}
