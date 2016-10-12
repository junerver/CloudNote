package com.junerver.cloudnote.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by junerver on 2016/9/1.
 * 用于保存在本地的实体类
 * update 16-10-12:增加字段 image video
 */
@Entity
public class NoteEntity implements Parcelable {

    /**
     * objectId : abd876be69
     * updatedAt : 2016-09-01 17:19:55
     *
     */

    @Id(autoincrement = true)
    private long id;        //本地数据库id 计划采用unix时间戳代替

    @Property(nameInDb = "TITLE")
    private String title;       //标题

    @Property(nameInDb = "SUMMARY")
    private String summary;     //文章摘要

    @Property(nameInDb = "CONTENT")
    private String content;     //文章正文

    @Property(nameInDb = "IMAGE")
    private String image;   //图片



    @Property(nameInDb = "VIDEO")
    private String video;   //视频

    @Property(nameInDb = "DATE")
    private String date;   //更新日期

    @Property(nameInDb = "OBJ_ID")
    private String objId;   //保存bmob云实体id（用于查询bmob对象）

    @Property(nameInDb = "IS_SYNC")
    private boolean isSync;     //是否已经同步


    @Generated(hash = 290996040)
    public NoteEntity(long id, String title, String summary, String content,
            String image, String video, String date, String objId, boolean isSync) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.image = image;
        this.video = video;
        this.date = date;
        this.objId = objId;
        this.isSync = isSync;
    }

    @Generated(hash = 734234824)
    public NoteEntity() {
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

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

    public boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeString(this.image);
        dest.writeString(this.video);
        dest.writeString(this.date);
        dest.writeString(this.objId);
        dest.writeByte(this.isSync ? (byte) 1 : (byte) 0);
    }

    protected NoteEntity(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.image = in.readString();
        this.video = in.readString();
        this.date = in.readString();
        this.objId = in.readString();
        this.isSync = in.readByte() != 0;
    }

    @Transient
    public static final Parcelable.Creator<NoteEntity> CREATOR = new Parcelable.Creator<NoteEntity>() {
        @Override
        public NoteEntity createFromParcel(Parcel source) {
            return new NoteEntity(source);
        }

        @Override
        public NoteEntity[] newArray(int size) {
            return new NoteEntity[size];
        }
    };
}


