package com.junerver.cloudnote.db.entity

import com.junerver.cloudnote.db.entity.NoteEntity

/**
 * Created by junerver on 2016/9/1.
 * 用于bmob云后端使用的后端实体
 *
 * update 16-10-12:增加字段 image video
 */
class Note {
    var title: String? = null
    var summary: String? = null
    var content: String? = null
    var image: String? = null
    var video: String? = null
    var userObjId: String? = null
    var dbId: Long = 0
    var objectId:String =""
}