package com.junerver.cloudnote.bean

import com.junerver.cloudnote.db.entity.Note

/**
 * @Description 对应全部日志数据的模型
 * @Author Junerver
 * Created at 2021/10/14 10:10
 */
data class GetAllNoteResp(var results: List<Note>)