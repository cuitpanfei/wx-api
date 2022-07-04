package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.wx.config.WxMpHtmlParamsHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/article")
@Api(tags = {"文章管理-管理后台"})
public class CategoryController {

    @GetMapping("/category/list")
    @RequiresPermissions("wx:article:category:list")
    @ApiOperation(value = "分类列表")
    public Result list() {
        return Result.ok().put("category", WxMpHtmlParamsHolder.loadArticleCategory());
    }
}
