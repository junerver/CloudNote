package com.junerver.cloudnote.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.dao.NoteEntityDao;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用于查看笔记的页面
 */

public class NoteDetailActivity extends BaseActivity {


    @BindView(R.id.tvNoteTitle)
    TextView mTvNoteTitle;
    @BindView(R.id.tvNoteContent)
    TextView mTvNoteContent;
    @BindView(R.id.btnEdit)
    LinearLayout mBtnEdit;
    @BindView(R.id.btnDelete)
    LinearLayout mBtnDelete;
    @BindView(R.id.ivBack)
    ImageView mIvBack;
    @BindView(R.id.ivDone)
    ImageView mIvDone;
    @BindView(R.id.ivImage)
    ImageView mIvImage;
    @BindView(R.id.ibVideo)
    ImageButton mIbVideo;

    private NoteEntity mNoteEntity;
    private long id = 0L;
    private static final int EDIT_NOTE = 3;

    @Override
    protected void initView() {
        mIvDone.setVisibility(View.GONE);
        inflateView(mNoteEntity);
    }

    private void inflateView(NoteEntity noteEntity) {
        String title = noteEntity.getTitle();
        String content = noteEntity.getContent();
        String image = noteEntity.getImage();
        String video = noteEntity.getVideo();

        if (title != null) {
            mTvNoteTitle.setText(title);
        }
        if (content != null) {
            mTvNoteContent.setText(content);
        }
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(image);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIvImage.getWidth()), Math.max(500, mIvImage.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIvImage.setImageBitmap(bitmap);
        }
        if (video != null) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbVideo.getWidth()), Math.max(500, mIbVideo.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIbVideo.setImageBitmap(bitmap);
        }

    }

    @Override
    protected void initData() {
        mNoteEntity = getIntent().getParcelableExtra("Note");
        id = mNoteEntity.getId();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_detail;
    }


    @OnClick({R.id.btnEdit, R.id.btnDelete, R.id.ivBack, R.id.ivImage, R.id.ibVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                Intent editIntent = new Intent(mContext, EditNoteActivity.class);
                editIntent.putExtra("Note", mNoteEntity);
                startActivityForResult(editIntent, EDIT_NOTE);
                break;
            case R.id.btnDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认要删除这个笔记么？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note note = new Note();
                        note.setObjectId(mNoteEntity.getObjId());
                        note.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Logger.i("bmob数据删除成功");
                                    //清除本地数据
                                    CloudNoteApp.getNoteEntityDao().delete(mNoteEntity);
                                    showShortToast("笔记已删除！");
                                    finish();
                                } else {
                                    Logger.i("bmob数据删除失败：" + e.getMessage() + "," + e.getErrorCode());
                                    showShortToast("错误！请稍后再试！");
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivImage:
                //查看图片
                break;
            case R.id.ibVideo:
                //查看视频
                break;
        }
    }

    //编辑后返回变更
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mNoteEntity=data.getParcelableExtra("Note");
            inflateView(mNoteEntity);
        }
    }
}
