package com.junerver.cloudnote.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
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
    @BindView(R.id.ibImage)
    ImageButton mIbImage;
    @BindView(R.id.ibVideo)
    ImageButton mIbVideo;

    private NoteEntity mNoteEntity;

    @Override
    protected void initView() {

        mIvDone.setVisibility(View.GONE);

        String title = mNoteEntity.getTitle();
        String content = mNoteEntity.getContent();
        String image = mNoteEntity.getImage();
        String video = mNoteEntity.getVideo();

        if (title != null) {
            mTvNoteTitle.setText(title);
        }
        if (content != null) {
            mTvNoteContent.setText(content);
        }
        if (image != null) {
            mIbImage.setImageBitmap(BitmapFactory.decodeFile(image));
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
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_detail;
    }


    @OnClick({R.id.btnEdit, R.id.btnDelete, R.id.ivBack, R.id.ibImage, R.id.ibVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                Intent editIntent = new Intent(mContext, EditNoteActivity.class);
                editIntent.putExtra("Note", mNoteEntity);
                startActivity(editIntent);
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
            case R.id.ibImage:
                //查看图片
                break;
            case R.id.ibVideo:
                //查看视频
                break;
        }
    }


}
