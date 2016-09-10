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
    public static List<NoteEntity> list(){
        List<NoteEntity> noteEntities;
        noteEntities= CloudNoteApp.getNoteEntityDao()
                .queryBuilder()
                .orderDesc(NoteEntityDao.Properties.Id)
                .list();
        return noteEntities;
    }
}
