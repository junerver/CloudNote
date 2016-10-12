package com.junerver.cloudnote.db;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.db.dao.NoteEntityDao;
import com.junerver.cloudnote.db.entity.NoteEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junerver on 2016/9/10.
 */
public class NoteUtils {

    /**
     * 列出全部数据
     * @return
     */
    public static List<NoteEntity> list(){
        List<NoteEntity> noteEntities;
        noteEntities= CloudNoteApp.getNoteEntityDao()
                .queryBuilder()
                .orderDesc(NoteEntityDao.Properties.Id)
                .list();
        return noteEntities;
    }

    /**
     * 列出未同步的所有数据
     * @return
     */
    public static List<NoteEntity> listNotSync(){
        //找出数据库中所有同步标记为否的
        List<NoteEntity> noteEntities;
        noteEntities=CloudNoteApp.getNoteEntityDao()
                .queryBuilder()
                .where(NoteEntityDao.Properties.IsSync.eq(false))
                .build()
                .list();
        return noteEntities;
    }
}
