package com.junerver.cloudnote.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.observable.NotesSaveToDbAndBmobObservable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.functions.Action1;

/**
 * 用于编辑笔记的页面
 * 更新数据操作设计bmob的数据更新操作只要获取到objectId就可以更新数据了
 */

public class EditNoteActivity extends BaseActivity implements Observer<NoteEntity> {


    @BindView(R.id.ivBack)
    ImageView mIvBack;
    @BindView(R.id.ivDone)
    ImageView mIvDone;
    @BindView(R.id.etNoteTitle)
    EditText mEtNoteTitle;
    @BindView(R.id.etNoteContent)
    EditText mEtNoteContent;
    @BindView(R.id.btnImage)
    LinearLayout mBtnImage;
    @BindView(R.id.btnVideo)
    LinearLayout mBtnVideo;
    @BindView(R.id.ibImage)
    ImageButton mIbImage;
    @BindView(R.id.ibVideo)
    ImageButton mIbVideo;

    private NoteEntity mNoteEntity;
    private boolean isNew;  //是否是新笔记
    private File mImageFile, mVideoFile;
    private Uri mImageUri, mVideoUri;
    private static final int TAKE_IMAGE = 1;
    private static final int TAKE_VIDEO = 2;
    private long id = 0L; //id

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        mNoteEntity = getIntent().getParcelableExtra("Note");
        if (mNoteEntity != null) {
            //不为空说明是编辑
            isNew = false;
            String title = mNoteEntity.getTitle();
            String content = mNoteEntity.getContent();
            String image = mNoteEntity.getImage();
            String video = mNoteEntity.getVideo();

            if (title != null) {
                mEtNoteTitle.setText(title);
            }
            if (content != null) {
                mEtNoteContent.setText(content);
            }
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(image);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbImage.getWidth()), Math.max(500, mIbImage.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                mIbImage.setImageBitmap(bitmap);
            }
            if (video != null) {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbVideo.getWidth()), Math.max(500, mIbVideo.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                mIbVideo.setImageBitmap(bitmap);
            }
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


    @OnClick({R.id.ivBack, R.id.ivDone, R.id.btnImage, R.id.btnVideo, R.id.ibImage, R.id.ibVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                //后退图标
                finish();
                break;
            case R.id.ivDone:
                //如果是新建笔记就新建实例，否则使用原来的实例
                NoteEntity noteEntity = isNew ? new NoteEntity() : mNoteEntity;
                String title = mEtNoteTitle.getText().toString().trim();
                String content = mEtNoteContent.getText().toString().trim();
                String summary = getSummary(content);
                if (mImageUri != null) {
                    String image = mImageUri.getPath();
                    noteEntity.setImage(image);
                }
                if (mVideoUri != null) {
                    String video = mVideoUri.getPath();
                    noteEntity.setVideo(video);
                }

                noteEntity.setTitle(title);
                noteEntity.setContent(content);
                noteEntity.setSummary(summary);
                noteEntity.setDate(getStringDate());
                NotesSaveToDbAndBmobObservable.save(noteEntity, isNew)
                        .subscribe(this);
                showProgress();
                break;
            case R.id.btnImage:
                //添加照片
                insertImage();
                break;
            case R.id.btnVideo:
                //添加视频
                insertVideo();
                break;
            case R.id.ibImage:
                if (mImageUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mImageUri, "image/*");
                    startActivity(intent);
                }
                break;
            case R.id.ibVideo:
                if (mVideoUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mVideoUri, "video/mpeg4");
                    startActivity(intent);
                }
                break;
        }
    }

    private String getSummary(String content) {
        String summary;
        if (content.length() >= 10) {
            summary = content.substring(0, 9);
        } else {
            summary = content;
        }
        return summary;
    }

    //获取字符串格式的时间
    private String getTime() {
        Calendar c = Calendar.getInstance();
        long time = c.getTime().getTime();
        return String.valueOf(time);
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    //插入图片
    private void insertImage() {
        mImageFile = new File(Environment.getExternalStorageDirectory(), getTime() + ".png");
        mImageUri = Uri.fromFile(mImageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, TAKE_IMAGE);
    }

    //插入视频
    private void insertVideo() {
        mVideoFile = new File(Environment.getExternalStorageDirectory(), getTime() + ".mp4");
        mVideoUri = Uri.fromFile(mVideoFile);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
        startActivityForResult(intent, TAKE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mImageUri.getPath(), options);
            int scale = Math.min(options.outWidth / mIbImage.getWidth(), options.outHeight / mIbImage.getHeight());
            scale = scale == 0 ? 1 : scale;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getPath(), options);
            mIbImage.setImageBitmap(bitmap);
        } else if (requestCode == TAKE_VIDEO) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mVideoUri.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, mIbVideo.getWidth(), mIbVideo.getHeight(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIbVideo.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onCompleted() {
        closeProgress();
        showShortToast(getString(R.string.save_success));
        finish();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onNext(NoteEntity noteEntity) {
        Intent intent = new Intent();
        intent.putExtra("Note", noteEntity);
        setResult(RESULT_OK, intent);
    }
}
