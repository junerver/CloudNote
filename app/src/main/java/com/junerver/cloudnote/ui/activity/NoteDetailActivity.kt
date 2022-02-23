package com.junerver.cloudnote.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.junerver.cloudnote.net.BmobMethods
import com.edusoa.ideallecturer.fetchNetwork
import com.edusoa.ideallecturer.toBean
import com.elvishew.xlog.XLog
import com.junerver.cloudnote.R
import com.junerver.cloudnote.bean.DelResp
import com.junerver.cloudnote.bean.ErrorResp
import com.junerver.cloudnote.databinding.ActivityNoteDetailBinding
import com.junerver.cloudnote.databinding.BackBarBinding
import com.junerver.cloudnote.db.NoteUtils
import com.junerver.cloudnote.db.entity.NoteEntity
import com.junerver.cloudnote.utils.NetUtils
import kotlinx.coroutines.launch

/**
 * 用于查看笔记的页面
 */
class NoteDetailActivity : BaseActivity<ActivityNoteDetailBinding>() {

    //看起来是个entity，其实不是
    private lateinit var mNote: NoteEntity

    private lateinit var backBarBinding: BackBarBinding
    override fun initData() {
        mNote = intent.getSerializableExtra("Note")!! as NoteEntity
        XLog.d(mNote)
    }

    override fun initView() {
        backBarBinding = BackBarBinding.bind(viewBinding.llRoot)
        backBarBinding.ivDone.visibility = View.GONE
        backBarBinding.tvBarTitle.text = "笔记详情"
        inflateView(mNote)
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
        if (image.isNullOrEmpty()) {
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
            var bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(
                video,
                MediaStore.Images.Thumbnails.MICRO_KIND
            )!!
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
            editIntent.putExtra("Note", mNote)
            startActivityForResult(editIntent, EDIT_NOTE)
        }
        viewBinding.btnDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("确认要删除这个笔记么？")
            builder.setPositiveButton(
                "确认"
            ) { _, _ ->
                if (NetUtils.isConnected(mContext)) {
                    lifecycleScope.launch {
                        //云端删除
                        fetchNetwork {
                            doNetwork {
                                BmobMethods.INSTANCE.delNoteById(mNote.objId)
                            }
                            onSuccess {
                                val delResp = it.toBean<DelResp>()
                                //本地删除
                                NoteUtils.delNoteById(mNote.objId)
                                showShortToast(getString(R.string.del_success))
                                finish()
                            }
                            onHttpError { errorBody, errorMsg, code ->
                                errorBody?.let {
                                    val bean = it.toBean<ErrorResp>()
                                    delInLocalWithoutNetWork()
                                }
                            }
                        }
                    }
                } else {
                    delInLocalWithoutNetWork()
                }
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }
        backBarBinding.ivBack.setOnClickListener { finish() }
    }

    private fun delInLocalWithoutNetWork() {
        mNote.isLocalDel = true
        mNote.saveOrUpdate("objId = ?", mNote.objId)
        showShortToast(getString(R.string.del_success))
        finish()
    }

    //编辑后返回变更
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mNote = data?.getSerializableExtra("Note")!! as NoteEntity
            inflateView(mNote)
        }
    }

    companion object {
        private const val EDIT_NOTE = 3
    }
}