package com.junerver.cloudnote;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.junerver.cloudnote.db.dao.DaoMaster;
import com.junerver.cloudnote.db.dao.DaoSession;
import com.junerver.cloudnote.db.dao.NoteEntityDao;

import cn.bmob.v3.Bmob;

/**
 * Created by Junerver on 2016/8/31.
 */
public class CloudNoteApp extends Application {

    public static DaoSession mDaoSession;
    public static SQLiteDatabase mDb;
    public DaoMaster.DevOpenHelper mHelper;
    public DaoMaster mDaoMaster;
    public static NoteEntityDao mNoteEntityDao;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Bmob SDK
        Bmob.initialize(this, Constants.APPID);
        setupDatabase();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    private void setupDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME, null);
        mDb = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
        mNoteEntityDao = mDaoSession.getNoteEntityDao();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static SQLiteDatabase getDb() {
        return mDb;
    }

    public static NoteEntityDao getNoteEntityDao() {
        return mNoteEntityDao;
    }
}
