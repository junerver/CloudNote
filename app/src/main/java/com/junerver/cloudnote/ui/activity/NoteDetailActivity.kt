package com.junerver.cloudnote.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.elvishew.xlog.XLog
import com.junerver.cloudnote.R
import com.junerver.cloudnote.databinding.ActivityNoteDetailBinding
import com.junerver.cloudnote.databinding.BackBarBinding
import com.junerver.cloudnote.db.entity.Note
import com.junerver.cloudnote.db.entity.NoteEntity

/**
 * 用于查看笔记的页面
 */
class NoteDetailActivity : BaseActivity<ActivityNoteDetailBinding>() {

    private lateinit var mNoteEntity: NoteEntity

    private lateinit var backBarBinding: BackBarBinding
    override fun initData() {
        mNoteEntity = intent.getSerializableExtra("Note")!! as NoteEntity
        XLog.d(mNoteEntity)
    }

    override fun initView() {
        backBarBinding = BackBarBinding.bind(viewBinding.llRoot)
        backBarBinding.ivDone.visibility = View.GONE
        backBarBinding.tvBarTitle.text = "笔记详情"
        inflateView(mNoteEntity)
    }

    private fun inflateView(noteEntity: NoteEntity) {
        val title: String = noteEntity.title
        val content: String = noteEntity.content
        val image: String = noteEntity.image
        val video: String = noteEntity.video
        if (title.isNotEmpty()) {
            viewBinding.tvNoteTitle.text = title
        }
        if (content.isNotEmpty()) {
            viewBinding.tvNoteContent.text = content
        }
        if (image.isNotEmpty()) {
            var bitmap: Bitmap = BitmapFactory.decodeFile(image)
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                Math.max(500, viewBinding.ivImage.width),
                Math.max(500, viewBinding.ivImage.height),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
            viewBinding.ivImage.setImageBitmap(bitmap)
        }
        if (video.isNotEmpty()) {
            var bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND)!!
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                Math.max(500, viewBinding.ibVideo.width),
                Math.max(500, viewBinding.ibVideo.height),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
            viewBinding.ibVideo.setImageBitmap(bitmap)
        }
    }

    override fun setListeners() {
        viewBinding.btnEdit.setOnClickListener {
            val editIntent = Intent(mContext, EditNoteActivity::class.java)
            editIntent.putExtra("Note", mNoteEntity)
            startActivityForResult(editIntent, EDIT_NOTE)
        }
        viewBinding.btnDelete.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("确认要删除这个笔记么？")
            builder.setPositiveButton("确认"
            ) { _, _ ->
                val note = Note()
                note.objectId = mNoteEntity.objId
                //网络请求删除

                //本地数据库删除

                showShortToast(getString(R.string.del_success))
                finish()
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }
        backBarBinding.ivBack.setOnClickListener { finish() }
    }


    //编辑后返回变更
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mNoteEntity = data?.getParcelableExtra("Note")!!
            inflateView(mNoteEntity)
        }
    }

    companion object {
        private const val EDIT_NOTE = 3
    }
}