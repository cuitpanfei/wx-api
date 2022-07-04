package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.wx.entity.Article;
import com.github.niefy.modules.wx.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;


/**
 * 文章
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:16
 */
@RestController
@RequestMapping("/manage/article")
@Api(tags = {"文章管理-管理后台"})
public class ArticleManageController {
    @Autowired
    private ArticleService articleService;


    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:article:list")
    @ApiOperation(value = "列表")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = articleService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("wx:article:info")
    @ApiOperation(value = "详情")
    public Result info(@PathVariable("id") Integer id) {
        Article article = articleService.getById(id);

        return Result.ok().put("article", article);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:article:save")
    @ApiOperation(value = "保存")
    public Result save(@RequestBody Article article) {
        articleService.saveArticle(article);

        return Result.ok();
    }

    /**
     * 发布到公众号草稿
     */
    @PostMapping("/wxAssets/draftPublish")
    @RequiresPermissions("wx:article:draft-publish")
    @ApiOperation(value = "发布到公众号草稿")
    public Result draftPublish(@RequestBody Article article) {
        if (article.getId() == null) {
            return Result.error("未知的的非法文章，不能发布！");
        } else {
            articleService.draftPublish(article);
        }
        return Result.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:article:delete")
    @ApiOperation(value = "删除")
    public Result delete(@RequestBody Integer[] ids) {
        articleService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
