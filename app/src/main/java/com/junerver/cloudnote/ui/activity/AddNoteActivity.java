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

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class AddNoteActivity extends BaseActivity {


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
        return R.layout.activity_add_note;
    }



    @OnClick(R.id.btnSubmit)
    public void onClick() {
        String title = mEtTitle.getText().toString().trim();
        String content = mEtContent.getText().toString().trim();
        String summary;
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
                    BmobQuery<Note> query = new BmobQuery<Note>();
                    query.getObject(objectId, new QueryListener<Note>() {
                        @Override
                        public void done(Note object, BmobException e) {
                            if(e==null){
                                NoteEntity noteEntity = new NoteEntity();
                                noteEntity.setId(new Date().getTime());
                                noteEntity.setTitle(object.getTitle());
                                noteEntity.setContent(object.getContent());
                                noteEntity.setSummary(object.getSummary());
                                noteEntity.setObjId(object.getObjectId());
                                noteEntity.setDate(object.getUpdatedAt());
                                CloudNoteApp.getNoteEntityDao().insert(noteEntity);
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }

                    });
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
