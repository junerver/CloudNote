package com.junerver.cloudnote.db.entity

import com.edusoa.ideallecturer.utils.TimeUtils.convertToTimestamp
import com.google.gson.annotations.Expose
import com.junerver.cloudnote.db.entity.NoteEntity

/**
 * Created by junerver on 2016/9/1.
 * 用于bmob云后端使用的后端实体
 *
 * update 16-10-12:增加字段 image video
 */
class Note {
    //标题
    var title: String=""

    //简介
    var summary: String=""

    //正文
    var content: String=""

    //图片路径
    var image: String=""

    //视频路径
    var video: String=""

    //上传用户的objid
    var userObjId: String=""

    //数据库id
    var dbId: Long = 0

    //bmob内部字段 id
    @Expose(serialize = false, deserialize = true)
    var objectId:String =""

    //bmob内部字段 更新时间
    @Expose(serialize = false, deserialize = true)
    var updatedAt=""
        set(value) {
            field = value
            _updatedTime = value.convertToTimestamp()
        }

    //bmob内部字段 创建时间
    @Expose(serialize = false, deserialize = true)
    var createdAt=""
        set(value) {
            field = value
            _createdTime = value.convertToTimestamp()
        }

    //幕后字段用于对比更新
    @Expose(serialize = false, deserialize = false)
    var _updatedTime:Long =-1L

    @Expose(serialize = false, deserialize = false)
    var _createdTime:Long =-1L
}