
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ShortContent {

    @Expose
    private List<Object> list;

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

}
