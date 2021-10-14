package com.junerver.cloudnote.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.dslx.digtalclassboard.net.BmobMethods
import com.edusoa.ideallecturer.createJsonRequestBody
import com.edusoa.ideallecturer.toBean
import com.edusoa.ideallecturer.toJson
import com.elvishew.xlog.XLog
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.R
import com.junerver.cloudnote.adapter.NoteViewBinder
import com.junerver.cloudnote.bean.GetAllNoteResp
import com.junerver.cloudnote.bean.PostResp
import com.junerver.cloudnote.bean.PutResp
import com.junerver.cloudnote.databinding.FragmentNoteBinding
import com.junerver.cloudnote.db.NoteUtils
import com.junerver.cloudnote.ui.activity.EditNoteActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : BaseFragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var mLayoutManager: LinearLayoutManager
    private val adapter = MultiTypeAdapter()
    private val items = ArrayList<Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        adapter.register(NoteViewBinder())
        binding.rvList.adapter = adapter
        //设置固定大小
        binding.rvList.setHasFixedSize(true)
        //创建线性布局
        mLayoutManager = LinearLayoutManager(activity)
        //垂直方向
        mLayoutManager.orientation = RecyclerView.VERTICAL
        //给RecyclerView设置布局管理器
        binding.rvList.layoutManager = mLayoutManager
        adapter.items = items


        //同步按钮点击事件
        binding.ivSync.setOnClickListener {
            binding.ivSync.startAnimation(
                //动画效果
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.anim_sync
                )
            )
            syncToDb()

        }
        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(activity, EditNoteActivity::class.java))
        }
        listAllFromDb()
    }


    private fun syncToDb() {
        lifecycleScope.launch {
            //sync to db
            flow {
                val map = mapOf("userObjId" to SpUtils.decodeString(Constants.SP_USER_ID))
                println(map)
                val resp = BmobMethods.INSTANCE.getAllNoteByUserId(map.toJson())
                emit(resp)
            }.map {
                println()
                println("${Thread.currentThread().name}: $it")
                val allNote = it.toBean<GetAllNoteResp>()
                allNote
            }.flatMapConcat {
                it.results.asFlow()
            }.onEach { note ->
                //云端的每一条笔记
                val objId = note.objectId
                val dbId = note.dbId
                val dbBean = NoteUtils.queryNoteById(dbId)
                if (dbBean == null) {
                    //本地没有次数据 新建本地数据并保存数据库
                    val entity = note.toEntity()
                    entity.save()
                }
                //存在本地对象 对比是否跟新 or 删除
                dbBean?.let {
                    if (it.isLocalDel) {
                        //本地删除
                        val resp = BmobMethods.INSTANCE.delNoteById(it.objId)
                        if (resp.contains("ok")) {
                            //远端删除成功 本地删除
                            it.delete()
                        }
                        return@let
                    }
                    //未本地删除对比数据相互更新
                    when {
                        note.updatedTime > it.updatedTime -> {
                            //云端内容更新更新本地数据
                            it.update(note)
                        }
                        note.updatedTime < it.updatedTime -> {
                            //云端数据小于本地 更新云端
                            note.update(it)
                            val resp = BmobMethods.INSTANCE.putNoteById(
                                objId,
                                note.toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS)
                                    .createJsonRequestBody()
                            )
                            val putResp = resp.toBean<PutResp>()
                        }
                        else -> {
                            //数据相同
                            //do nothing
                        }
                    }
                    it.isSync = true
                    it.saveOrUpdate()
                }
            }.flowOn(Dispatchers.IO).onCompletion {
                //完成时调用与末端流操作符处于同一个协程上下文范围
                syncToBmob()
            }.collect {
                println()
                println("${Thread.currentThread().name}: $it")
            }
        }
    }

    private suspend fun syncToBmob() {
        //未同步的即本地有而云端无
        NoteUtils.listNotSync().asFlow()
            .onEach {
                //本地有云端无 本地无objId  直接上传
                if (it.objId.isEmpty()) {
                    val note = it.toBmob()
                    val resp = BmobMethods.INSTANCE.postNote(
                        note.toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS)
                            .createJsonRequestBody()
                    )
                    val postResp = resp.toBean<PostResp>()
                    //保存objectId
                    it.objId = postResp.objectId
                } else {
                    //云端同步后 本地不可能出现本地有记录，且存在云端objid，但是没有和云端同步
                    XLog.d("不太可能出现的一种情况\n$it")
                }
                it.isSync = true
                it.saveOrUpdate()
            }.flowOn(Dispatchers.IO).onCompletion {
                //完成时调用与末端流操作符处于同一个协程上下文范围
                listAllFromDb()
            }.collect {
                XLog.d(it)
            }
    }

    //列出本地数据
    private fun listAllFromDb() {
        lifecycleScope.launch {
            val dbwork = async(Dispatchers.IO) {
                NoteUtils.listAll()
            }
            val list = dbwork.await()
            items.clear()
            items.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }
}