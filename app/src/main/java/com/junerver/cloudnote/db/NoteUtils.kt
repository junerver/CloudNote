package com.junerver.cloudnote.db

import com.junerver.cloudnote.db.entity.Note
import com.junerver.cloudnote.db.entity.NoteEntity
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll
import org.litepal.extension.findFirst

/**
 * Created by junerver on 2016/9/10.
 * update : 2021年10月14日14:23:52
 */
object NoteUtils {
    /**
     * 列出全部数据 排除本地删除
     * @return
     */
    fun listAll(): List<NoteEntity> {
        return LitePal.where("isLocalDel = false").order("updatedTime desc").find()
    }

    /**
     * 列出未同步的所有数据
     *
     * @return
     */
    fun listNotSync(): List<NoteEntity> {
        return LitePal.where("isSync = false")
            .find()

    }

    //根据id从数据库查找
    fun queryNoteById(id: String): NoteEntity? {
        return LitePal.where("objId = '$id'").findFirst()
    }
}