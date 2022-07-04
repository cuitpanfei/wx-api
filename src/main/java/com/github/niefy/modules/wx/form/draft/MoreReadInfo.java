
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MoreReadInfo {

    @SerializedName("article_list")
    private List<Object> articleList;

    public List<Object> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Object> articleList) {
        this.articleList = articleList;
    }

}
