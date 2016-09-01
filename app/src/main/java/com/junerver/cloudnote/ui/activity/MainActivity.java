package com.junerver.cloudnote.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;

import java.security.acl.NotOwnerException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tvUsername)
    TextView mTvUsername;
    @BindView(R.id.etTitle)
    EditText mEtTitle;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.btnSubmit)
    Button mBtnSubmit;

    @Override
    protected void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            mTvUsername.setText(bmobUser.getUsername());
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @OnClick(R.id.btnSubmit)
    public void onClick() {
        final String title = mEtTitle.getText().toString().trim();
        final String content = mEtContent.getText().toString().trim();
        final String summary;
        if (content.length() >= 10) {
            summary = content.substring(0, 9);
        } else {
            summary = content;
        }

        final Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setSummary(summary);
        note.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Log.d("tag","创建数据成功：" + objectId+"   data:"+note.getUpdatedAt()+"    "+note.getCreatedAt());
                    NoteEntity noteEntity = new NoteEntity(title, summary, content, note.getCreatedAt(), note.getObjectId());
                    CloudNoteApp.getNoteEntityDao().insert(noteEntity);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
