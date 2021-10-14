package com.junerver.cloudnote.db.entity

import com.edusoa.ideallecturer.utils.TimeUtils.convertToTimestamp

/**
 * Created by junerver on 2016/9/1.
 * 用于bmob云后端使用的后端实体
 *
 * update 16-10-12:增加字段 image video
 */
class Note {
    //标题
    var title: String = ""

    //简介
    var summary: String = ""

    //正文
    var content: String = ""

    //图片路径
    var image: String = ""

    //视频路径
    var video: String = ""

    //上传用户的objid
    var userObjId: String = ""

    //数据库id
    var dbId: Long = 0

    //bmob内部字段 id
    var objectId: String = ""

    //bmob内部字段 更新时间
    var updatedAt = ""
        set(value) {
            field = value
            updatedTime = value.convertToTimestamp()
        }

    //bmob内部字段 创建时间
    var createdAt = ""
        set(value) {
            field = value
            createdTime = value.convertToTimestamp()
        }

    //幕后字段用于对比更新
    var updatedTime: Long = -1L
        get() {
            //本字段未赋值时被调用
            return if (updatedAt.isNotEmpty() and (field == -1L)) {
                updatedAt.convertToTimestamp()
            } else {
                field
            }
        }

    var createdTime: Long = -1L


    override fun toString(): String {
        return "Note(title='$title', summary='$summary', content='$content', image='$image', video='$video', userObjId='$userObjId', dbId=$dbId, objectId='$objectId', updatedAt='$updatedAt', createdAt='$createdAt', _updatedTime=$updatedTime, _createdTime=$createdTime)"
    }

    fun toEntity(): NoteEntity {
        val entity = NoteEntity()
        entity.content = this.content
        entity.id = this.dbId
        entity.title = this.title
        entity.summary = this.summary
        entity.image = this.image
        entity.video = this.video
        entity.createdTime = this.createdTime
        entity.updatedTime = this.updatedTime
        return entity
    }

    fun update(entity: NoteEntity) {
        this.title = entity.title
        this.content = entity.content
        this.summary = entity.summary
        this.dbId = entity.id
        this.image = entity.image
        this.video = entity.video
        this.createdTime = entity.createdTime
        this.updatedTime =entity.updatedTime
    }
}