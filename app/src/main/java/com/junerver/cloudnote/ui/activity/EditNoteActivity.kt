package com.junerver.cloudnote.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import com.junerver.cloudnote.net.BmobMethods
import com.edusoa.ideallecturer.*
import com.edusoa.ideallecturer.utils.TimeUtils
import com.elvishew.xlog.XLog
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.bean.ErrorResp
import com.junerver.cloudnote.bean.PostResp
import com.junerver.cloudnote.bean.PutResp
import com.junerver.cloudnote.databinding.ActivityEditNoteBinding
import com.junerver.cloudnote.db.entity.NoteEntity
import com.junerver.cloudnote.utils.NetUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.asRequestBody
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

    //是否是新笔记 是新建entity 还是使用旧的entity
    private var isNew = true
    private var mImageFile: File? = null
    private var mVideoFile: File? = null
    private var mImageUri: Uri? = null
    private var mVideoUri: Uri? = null

    override fun initView() {
        viewBinding.backBar.tvBarTitle.text = "新建笔记"
    }

    override fun initData() {
        mNoteEntity = intent.getSerializableExtra("Note") as NoteEntity?
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
                var bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(
                    video,
                    MediaStore.Images.Thumbnails.MICRO_KIND
                )!!
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
            val content: String = viewBinding.etNoteContent.text.toString().trim()
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
            //初次创建时赋值id与创建时间
            if (isNew) {
                noteEntity.createdTime = TimeUtils.currentTimeSecond()
            }
            noteEntity.updatedTime = TimeUtils.currentTimeSecond()
            //只要点击了提交按钮都视为变更了内容
            noteEntity.isSync = false
            //序列化传递过来的实例会丢失isSave等litepal保留字段，从而导致使用save会直接再创建一个新的对象
            noteEntity.saveOrUpdate("objId = ?", noteEntity.objId)
            //此时实例已经持久化
            mNoteEntity = noteEntity
            done()
            if (NetUtils.isConnected(mContext)) {
                showProgress()
                launch {
                    if (isNew) {
                        //新建笔记 post 提交
                        fetchNetwork {
                            doNetwork {
                                BmobMethods.INSTANCE.postNote(
                                    noteEntity.toBmob()
                                        .toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS)
                                        .createJsonRequestBody()
                                )
                            }
                            onSuccess {
                                closeProgress()
                                val postResp = it.toBean<PostResp>()
                                isNew = false
                                noteEntity.isSync = true
                                noteEntity.objId = postResp.objectId
                                noteEntity.save()
                                showShortToast("保存成功！")
                            }
                            onHttpError { errorBody, errorMsg, code ->
                                closeProgress()
                                errorBody?.let {
                                    val bean = it.toBean<ErrorResp>()
                                    XLog.d(errorMsg + bean.error)
                                    showLongToast(errorMsg + bean.error)
                                }
                            }
                        }
                    } else {
                        //更新
                        fetchNetwork {
                            doNetwork {
                                BmobMethods.INSTANCE.putNoteById(
                                    noteEntity.objId,
                                    noteEntity.toBmob()
                                        .toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS)
                                        .createJsonRequestBody()
                                )
                            }
                            onSuccess {
                                closeProgress()
                                val putResp = it.toBean<PutResp>()
                                noteEntity.isSync = true
                                noteEntity.save()
                                showShortToast("保存成功！")
                            }
                            onHttpError { errorBody, errorMsg, code ->
                                closeProgress()
                                errorBody?.let {
                                    val bean = it.toBean<ErrorResp>()
                                    showLongToast(errorMsg + bean.error)
                                }
                            }
                        }
                    }
                }
            } else {
                //此时保存完成 不是新的实例
                isNew = false
                showShortToast("保存成功！")
            }
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
        mImageFile = File(getExternalFilesDir(null), "$time.png")
        mImageUri = getUriForFile(mImageFile!!)
        XLog.d(mImageFile)
        XLog.d(mImageUri)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(intent, TAKE_IMAGE)
    }

    //插入视频
    private fun insertVideo() {
        mVideoFile = File(getExternalFilesDir(null), "$time.mp4")
        mVideoUri = getUriForFile(mVideoFile!!)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri)
        startActivityForResult(intent, TAKE_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_IMAGE) {
                XLog.d(data)
//            val options: BitmapFactory.Options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            BitmapFactory.decodeFile(mImageUri!!.path, options)
//            var scale: Int = Math.min(
//                options.outWidth / viewBinding.ibImage.getWidth(),
//                options.outHeight / viewBinding.ibImage.getHeight()
//            )
//            scale = if (scale == 0) 1 else scale
//            options.inJustDecodeBounds = false
//            options.inSampleSize = scale
//            val bitmap: Bitmap = BitmapFactory.decodeFile(mImageUri!!.path, options)
//            viewBinding.ibImage.setImageBitmap(bitmap)

                mImageFile?.let { viewBinding.ibImage.load(it) }
                XLog.d(mImageFile?.absolutePath)
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

    }

    fun done() {
        val intent = Intent()
        intent.putExtra("Note", mNoteEntity)
        setResult(RESULT_OK, intent)
    }

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

    suspend fun file() {
        val file = File("")
        val resp = BmobMethods.INSTANCE.postFile("test.jpg", file.asRequestBody())
    }
}