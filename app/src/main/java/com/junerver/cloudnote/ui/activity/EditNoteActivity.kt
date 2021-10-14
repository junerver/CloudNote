package com.junerver.cloudnote.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import com.junerver.cloudnote.R
import com.junerver.cloudnote.databinding.ActivityEditNoteBinding
import com.junerver.cloudnote.db.entity.NoteEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * 用于编辑笔记的页面
 * 更新数据操作设计bmob的数据更新操作只要获取到objectId就可以更新数据了
 */
class EditNoteActivity : BaseActivity<ActivityEditNoteBinding>() {


    private var mNoteEntity: NoteEntity? = null
    //是否是新笔记
    private var isNew = false
    private var mImageFile: File? = null
    private var mVideoFile: File? = null
    private var mImageUri: Uri? = null
    private var mVideoUri: Uri? = null
    private val id = 0L //id
    override fun initView() {}
    override fun initData() {
        mNoteEntity = intent.getParcelableExtra("Note")!!
        if (mNoteEntity != null) {
            //不为空说明是编辑
            isNew = false
            val title: String = mNoteEntity!!.title
            val content: String = mNoteEntity!!.content
            val image: String = mNoteEntity!!.image
            val video: String = mNoteEntity!!.video
            if (title.isNotEmpty()) {
                viewBinding.etNoteTitle.setText(title)
            }
            if (content.isNotEmpty()) {
                viewBinding.etNoteContent.setText(content)
            }
            if (image.isNotEmpty()) {
                var bitmap: Bitmap = BitmapFactory.decodeFile(image)
                bitmap = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    Math.max(500, viewBinding.ibImage.width),
                    Math.max(500, viewBinding.ibImage.height),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT
                )
                viewBinding.ibImage.setImageBitmap(bitmap)
            }
            if (video.isNotEmpty()) {
                var bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND)!!
                bitmap = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    max(500, viewBinding.ibVideo.width),
                    max(500, viewBinding.ibVideo.height),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT
                )
                viewBinding.ibVideo.setImageBitmap(bitmap)
            }
        } else {
            //为新笔记
            isNew = true
        }
    }

    override fun setListeners() {
        //添加照片
        viewBinding.btnImage.setOnClickListener { insertImage() }
        //添加视频
        viewBinding.btnVideo.setOnClickListener { insertVideo() }

        viewBinding.ibImage.setOnClickListener {
            if (mImageUri != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(mImageUri, "image/*")
                startActivity(intent)
            }
        }
        viewBinding.ibVideo.setOnClickListener {
            if (mVideoUri != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(mVideoUri, "video/mpeg4")
                startActivity(intent)
            }
        }
        viewBinding.backBar.ivBack.setOnClickListener { finish() }
        viewBinding.backBar.ivDone.setOnClickListener {
            //如果是新建笔记就新建实例，否则使用原来的实例
            val noteEntity: NoteEntity = if (isNew) NoteEntity() else mNoteEntity!!
            val title: String = viewBinding.etNoteTitle.text.toString().trim()
            val content: String = viewBinding.etNoteContent.getText().toString().trim()
            val summary = getSummary(content)
            if (mImageUri != null) {
                val image = mImageUri!!.path
                noteEntity.image = image!!
            }
            if (mVideoUri != null) {
                val video = mVideoUri!!.path
                noteEntity.video = video!!
            }
            noteEntity.title = title
            noteEntity.content = content
            noteEntity.summary = summary
            noteEntity.date = stringDate
//            NotesSaveToDbAndBmobObservable.save(noteEntity, isNew)
//                .subscribe(this)
            //数据同时上传云端与数据库
            showProgress()
        }

    }

    private fun getSummary(content: String): String {
        val summary: String = if (content.length >= 10) {
            content.substring(0, 9)
        } else {
            content
        }
        return summary
    }

    //获取字符串格式的时间
    private val time: String
        private get() {
            val c = Calendar.getInstance()
            val time = c.time.time
            return time.toString()
        }

    //插入图片
    private fun insertImage() {
        mImageFile = File(Environment.getExternalStorageDirectory(), "$time.png")
        mImageUri = Uri.fromFile(mImageFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(intent, TAKE_IMAGE)
    }

    //插入视频
    private fun insertVideo() {
        mVideoFile = File(Environment.getExternalStorageDirectory(), "$time.mp4")
        mVideoUri = Uri.fromFile(mVideoFile)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri)
        startActivityForResult(intent, TAKE_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE) {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mImageUri!!.path, options)
            var scale: Int = Math.min(
                options.outWidth / viewBinding.ibImage.getWidth(),
                options.outHeight / viewBinding.ibImage.getHeight()
            )
            scale = if (scale == 0) 1 else scale
            options.inJustDecodeBounds = false
            options.inSampleSize = scale
            val bitmap: Bitmap = BitmapFactory.decodeFile(mImageUri!!.path, options)
            viewBinding.ibImage.setImageBitmap(bitmap)
        } else if (requestCode == TAKE_VIDEO) {
            var bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(
                mVideoUri!!.path!!,
                MediaStore.Images.Thumbnails.MICRO_KIND
            )!!
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                viewBinding.ibVideo.getWidth(),
                viewBinding.ibVideo.getHeight(),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
            viewBinding.ibVideo.setImageBitmap(bitmap)
        }
    }

//    fun onCompleted() {
//        closeProgress()
//        showShortToast(getString(R.string.save_success))
//        finish()
//    }
//
//    fun onError(throwable: Throwable?) {}
//    fun onNext(noteEntity: NoteEntity?) {
//        val intent = Intent()
//        intent.putExtra("Note", noteEntity)
//        setResult(Activity.RESULT_OK, intent)
//    }

    companion object {
        private const val TAKE_IMAGE = 1
        private const val TAKE_VIDEO = 2

        /**
         * 获取现在时间
         *
         * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
         */
        val stringDate: String
            get() {
                val currentTime = Date()
                val formatter =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                return formatter.format(currentTime)
            }
    }
}