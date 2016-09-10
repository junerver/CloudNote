package com.junerver.cloudnote.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by junerver on 2016/9/1.
 * 用于保存在本地的实体类
 */
@Entity
public class NoteEntity{

    /**
     * objectId : abd876be69
     * updatedAt : 2016-09-01 17:19:55
     *
     */

    @Id(autoincrement = true)
    private long id;        //本地数据库id 自增长

    @Property(nameInDb = "TITLE")
    private String title;       //标题

    @Property(nameInDb = "SUMMARY")
    private String summary;     //文章摘要

    @Property(nameInDb = "CONTENT")
    private String content;     //文章正文

    @Property(nameInDb = "DATE")
    private String date;   //更新日期

    @Property(nameInDb = "OBJ_ID")
    private String objId;   //保存bmob云实体id（用于查询bmob对象）

    @Transient
    private long unixTime =0L;

    public String getObjId() {
        return this.objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Generated(hash = 546637644)
    public NoteEntity(long id, String title, String summary, String content,
            String date, String objId) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.date = date;
        this.objId = objId;
    }

    public NoteEntity(String title, String summary, String content, String date, String objId) {
        this.id = new Date().getTime();
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.date = date;
        this.objId = objId;
    }

    @Generated(hash = 734234824)
    public NoteEntity() {
    }

    public long getUnixTime() {
        if (this.unixTime == 0L) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = simpleDateFormat.parse(getDate());
                long time = date.getTime();
                this.unixTime = time/1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unixTime;
    }

}


