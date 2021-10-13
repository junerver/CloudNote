package com.junerver.cloudnote.db.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
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

    //更新日期
    var date: String = ""

    //保存bmob云实体id（用于查询bmob对象）
    var objId: String = ""

    //是否已经同步
    var isSync = false
}