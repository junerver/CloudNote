package com.junerver.cloudnote;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.junerver.cloudnote.db.dao.DaoMaster;
import com.junerver.cloudnote.db.dao.DaoSession;
import com.junerver.cloudnote.db.dao.NoteEntityDao;

import cn.bmob.v3.Bmob;

/**
 * Created by Junerver on 2016/8/31.
 */
public class CloudNoteApp extends Application {

    public static DaoSession daoSession;
    public static SQLiteDatabase db;
    public DaoMaster.DevOpenHelper helper;
    public DaoMaster daoMaster;
    public static NoteEntityDao noteEntityDao;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Bmob SDK
        Bmob.initialize(this, Constants.APPID);
        setupDatabase();
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        helper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME, null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        noteEntityDao = daoSession.getNoteEntityDao();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static NoteEntityDao getNoteEntityDao() {
        return noteEntityDao;
    }
}
