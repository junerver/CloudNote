package com.junerver.cloudnote.db.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import com.edusoa.ideallecturer.utils.TimeUtils.convertToTimestamp
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import kotlinx.parcelize.Parcelize
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * Created by junerver on 2016/9/1.
 * 用于保存在本地的实体类
 * update 16-10-12:增加字段 image video
 */
// 用于处理 Lint 的错误提示
@SuppressLint("ParcelCreator")
@Parcelize
class NoteEntity : LitePalSupport(), Parcelable {
    //本地数据库id 计划采用unix时间戳代替
    var id: Long = 0

    //标题
    var title: String = ""

    //文章摘要
    var summary: String = ""

    //文章正文
    var content: String = ""

    //图片
    var image: String = ""

    //视频
    var video: String = ""

    //保存bmob云实体id（用于查询bmob对象）
    var objId: String = ""

    //是否已经同步
    var isSync = false

    //是否本地删除，当删除时本地云端需要一同删除，如果云端没有删除成功，本地修改字段
    var isLocalDel = false

    //幕后字段用于对比更新
    var updatedTime: Long = -1L


    var createdTime: Long = -1L

    fun update(note: Note): NoteEntity {
        this.title = note.title
        this.content = note.content
        this.summary = note.summary
        this.id = note.dbId
        this.updatedTime = note.updatedTime
        this.createdTime = note.createdTime
        this.image = note.image
        this.video = note.video
        this.objId = note.objectId
        return this
    }

    fun toBmob(): Note {
        val note = Note()
        note.content = this.content
        note.dbId = this.id
        note.title = this.title
        note.summary = this.summary
        note.image = this.image
        note.video = this.video
        note.updatedTime = this.updatedTime
        note.createdTime = this.createdTime
        note.userObjId = SpUtils.decodeString(Constants.SP_USER_ID)
        return note
    }

    override fun toString(): String {
        return "NoteEntity(id=$id, title='$title', summary='$summary', content='$content', image='$image', video='$video', objId='$objId', isSync=$isSync, isLocalDel=$isLocalDel, updatedTime=$updatedTime, createdTime=$createdTime)"
    }


}