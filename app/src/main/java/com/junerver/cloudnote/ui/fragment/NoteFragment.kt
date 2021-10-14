package com.junerver.cloudnote.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.junerver.cloudnote.R
import com.junerver.cloudnote.adapter.NoteRecyclerAdapter
import com.junerver.cloudnote.databinding.FragmentNoteBinding
import com.junerver.cloudnote.db.entity.Note
import com.junerver.cloudnote.db.entity.NoteEntity
import com.junerver.cloudnote.ui.activity.EditNoteActivity
import com.junerver.cloudnote.ui.activity.NoteDetailActivity
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : BaseFragment() {

    private var _binding: FragmentNoteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private lateinit var mDataAdapter: NoteRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mNoteEntities: MutableList<NoteEntity> = ArrayList<NoteEntity>()

    fun init() {
        mDataAdapter = NoteRecyclerAdapter(activity)
        mDataAdapter!!.setDataList(mNoteEntities)
        binding.rvList.adapter = mDataAdapter
        //设置固定大小
        binding.rvList.setHasFixedSize(true)
        //创建线性布局
        mLayoutManager = LinearLayoutManager(activity)
        //垂直方向
        mLayoutManager.orientation = RecyclerView.VERTICAL
        //给RecyclerView设置布局管理器
        binding.rvList.layoutManager = mLayoutManager

        //设置点击事件
//        mLRecyclerViewAdapter.setOnItemClickListener(object : AdapterView.OnItemClickListener() {
//            fun onItemClick(view: View?, i: Int) {
//                val noteEntity: NoteEntity = mDataAdapter.getDataList().get(i)
//                val showIntent = Intent(activity, NoteDetailActivity::class.java)
//                showIntent.putExtra("Note", noteEntity)
//                startActivity(showIntent)
//            }
//
//            fun onItemLongClick(view: View?, i: Int) {
//                // TODO: 2016/9/20 可以设置长按删除
//            }
//        })

        //同步按钮点击事件
        binding.ivSync.setOnClickListener {
            binding.ivSync.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.anim_sync
                )
            ) //动画效果
//            synvToDb()
//            NotesSyncToBmobObservable.syncToBmob()
//                .doOnCompleted(object : Action0() {
//                    fun call() {
//                        if (NetUtils.isConnected(mContext)) {
//                            showShortToast(mContext.getString(R.string.sync_success))
//                        } else {
//                            showShortToast(mContext.getString(R.string.check_connect))
//                        }
//                    }
//                })
//                .subscribe()
            //用于同步本地数据到bmob云
            //！本地有云端无 本地无objId  直接上传
            //本地有云端有 本地有 objId 更新云端
            //本地无 云端有 从云端下载 （存不存在本地删除但是没有同步动作到云？ 本地删除时应该增加一个删除字段）
            //数据全部合并处理完成后显示出来
        }
        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(activity, EditNoteActivity::class.java))
        }
    }


    // 从本地数据库去除数据填充到rv
//    override fun onResume() {
//        super.onResume()
//        NotesFromDatabaseObservable.ofDate()
//            .subscribe(this)
//    }
//
//    fun onCompleted() {
//        //更新配适器数据
//        mDataAdapter.setDataList(mNoteEntities)
//    }
//
//    fun onError(throwable: Throwable?) {}
//    fun onNext(noteEntities: List<NoteEntity>) {
//        //从数据库获取本地数据
//        mNoteEntities = noteEntities
//    }

//    fun synvToDb() {
//        val query: BmobQuery<Note> = BmobQuery<Note>()
//        query.addWhereEqualTo("userObjId", BmobUser.getCurrentUser().getUsername())
//        query.setLimit(50) //查询本用户的50条笔记
//        query.findObjects(object : FindListener<Note?>() {
//            fun done(list: List<Note>, e: BmobException?) {
//                if (e == null) {
//                    Logger.d("共查询到：" + list.size)
//                    for (note in list) {
//                        val entity: NoteEntity = note.toEntity()
//                        entity.objId = note.objectId
//                        CloudNoteApp.getNoteEntityDao().insertOrReplace(entity)
//                    }
//                    NotesFromDatabaseObservable.ofDate()
//                        .subscribe(this@NoteFragment)
//                } else {
//                    Logger.d("bmob查询失败：" + e.getMessage().toString() + "," + e.getErrorCode())
//                }
//            }
//        })
//    }
}