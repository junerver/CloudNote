package com.junerver.cloudnote.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;

import java.lang.annotation.ElementType;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 用于编辑笔记的页面
 * 更新数据操作设计bmob的数据更新操作只要获取到objectId就可以更新数据了
 */

public class EditNoteActivity extends BaseActivity {


    @BindView(R.id.ivBack)
    ImageView mIvBack;
    @BindView(R.id.ivDone)
    ImageView mIvDone;
    @BindView(R.id.etNoteTitle)
    EditText mEtNoteTitle;
    @BindView(R.id.etNoteContent)
    EditText mEtNoteContent;
    @BindView(R.id.btnPicture)
    LinearLayout mBtnPicture;
    @BindView(R.id.btnVideo)
    LinearLayout mBtnVideo;

    private NoteEntity mNoteEntity;
    private boolean isNew;  //是否是新笔记
    private long id=0L; //id

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            //不为空说明是编辑
            isNew = false;
            mNoteEntity = getIntent().getParcelableExtra("Note");
            //获取id
            id = mNoteEntity.getId();
            mEtNoteTitle.setText(mNoteEntity.getTitle());
            mEtNoteContent.setText(mNoteEntity.getContent());
        } else {
            //为新笔记
            isNew = true;
        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ivBack, R.id.ivDone, R.id.btnPicture, R.id.btnVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivDone:
                if (isNew) {
                    String title = mEtNoteTitle.getText().toString().trim();
                    String content = mEtNoteContent.getText().toString().trim();
                    String summary;
                    if (content.length() >= 10) {
                        summary = content.substring(0, 9);
                    } else {
                        summary = content;
                    }
                    //创建bmob对象
                    // TODO: 2016/10/11
                    // 下面这部分逻辑应该整合成Observable 大致逻辑应该是判断是否有网络，
                    // 有网络时保存bmob对象，然后保存数据库，没有网络时只保存本地数据库
                    Note note = new Note();
                    note.setTitle(title);
                    note.setContent(content);
                    note.setSummary(summary);
                    note.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                BmobQuery<Note> query = new BmobQuery<Note>();
                                query.getObject(objectId, new QueryListener<Note>() {
                                    @Override
                                    public void done(Note object, BmobException e) {
                                        if (e == null) {
                                            NoteEntity noteEntity = new NoteEntity();
                                            noteEntity.setId(new Date().getTime());
                                            noteEntity.setTitle(object.getTitle());
                                            noteEntity.setContent(object.getContent());
                                            noteEntity.setSummary(object.getSummary());
                                            noteEntity.setObjId(object.getObjectId());
                                            noteEntity.setDate(object.getUpdatedAt());
                                            CloudNoteApp.getNoteEntityDao().insert(noteEntity);
                                        } else {
                                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    //todo:本地数据库数据更新与Bmob数据更新
                }
                break;
            case R.id.btnPicture:
                break;
            case R.id.btnVideo:
                break;
        }
    }
}
