package com.junerver.cloudnote.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.NoteEntity;

/**
 * 用于编辑笔记的页面
 * 更新数据操作设计bmob的数据更新操作只要获取到objectId就可以更新数据了
 */

public class EditNoteActivity extends BaseActivity {


    private NoteEntity mNoteEntity;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mNoteEntity= getIntent().getParcelableExtra("Note");
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_note;
    }
}
