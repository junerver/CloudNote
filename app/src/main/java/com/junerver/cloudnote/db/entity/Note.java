package com.junerver.cloudnote.db.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by junerver on 2016/9/1.
 * 用于bmob云后端使用的后端实体
 */
public class Note extends BmobObject {

    private String title;
    private String summary;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
