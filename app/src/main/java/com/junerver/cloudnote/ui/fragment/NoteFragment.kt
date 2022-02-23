package com.junerver.cloudnote.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.junerver.cloudnote.net.BmobMethods
import com.edusoa.ideallecturer.createJsonRequestBody
import com.edusoa.ideallecturer.fetchNetwork
import com.edusoa.ideallecturer.toBean
import com.edusoa.ideallecturer.toJson
import com.elvishew.xlog.XLog
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.R
import com.junerver.cloudnote.adapter.NoteViewBinder
import com.junerver.cloudnote.bean.*
import com.junerver.cloudnote.databinding.FragmentNoteBinding
import com.junerver.cloudnote.db.NoteUtils
import com.junerver.cloudnote.ui.activity.EditNoteActivity
import com.junerver.cloudnote.utils.NetUtils
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
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        mContext = requireContext()
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        XLog.d("自动同步一次")
        syncToDb()
    }

    private fun init() {
        val binder = NoteViewBinder()
        binder.setLongClickListener { item ->
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
                                BmobMethods.INSTANCE.delNoteById(item.objId)
                            }
                            onSuccess {
                                val delResp = it.toBean<DelResp>()
                                //本地删除
                                item.delete()
                                listAllFromDb()
                            }
                            onHttpError { errorBody, errorMsg, code ->
                                errorBody?.let {
                                    val bean = it.toBean<ErrorResp>()
                                    if (code == 101) {
                                        //云端没有该对象
                                        item.delete()
                                    } else {
                                        item.isLocalDel = true
                                        item.saveOrUpdate("objId = ?", item.objId)
                                    }
                                    listAllFromDb()
                                }
                            }
                        }
                    }
                } else {
                    item.isLocalDel = true
                    item.saveOrUpdate("objId = ?", item.objId)
                    listAllFromDb()
                }
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }
        adapter.register(binder)
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
                val resp = BmobMethods.INSTANCE.getAllNoteByUserId(map.toJson())
                emit(resp)
            }.catch { e ->
                XLog.e(e)
            }.flatMapConcat {
                val allNote = it.toBean<GetAllNoteResp>()
                allNote.results.asFlow()
            }.onEach { note ->
                //云端的每一条笔记
                val objId = note.objectId
                //根据bmob的objid查表
                val dbBean = NoteUtils.queryNoteById(objId)
                if (dbBean == null) {
                    //本地没有次数据 新建本地数据并保存数据库
                    val entity = note.toEntity()
                    entity.save()
                    XLog.d("本地没有该数据 新建\n$entity")
                }
                //存在本地对象 对比是否跟新 or 删除
                dbBean?.let {
                    if (it.isLocalDel) {
                        //本地删除
                        val resp = BmobMethods.INSTANCE.delNoteById(it.objId)
                        if (resp.contains("ok")) {
                            //远端删除成功 本地删除
                            it.delete()
                            XLog.d("远端删除成功 本地删除\n$resp")
                        }
                        return@let
                    }
                    //未本地删除对比数据相互更新
                    when {
                        note.updatedTime > it.updatedTime -> {
                            //云端内容更新更新本地数据
                            it.update(note)
                            XLog.d("使用云端数据更新本地数据库\n$it\n$note")
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
                            XLog.d("使用本地数据更新云端数据 \n$it\n$note")
                        }
                        else -> {
                            //数据相同 do nothing
                            XLog.d("本地数据与云端数据相同\n$it\n$note")
                            return@let
                        }
                    }
                    it.isSync = true
                    it.save()
                }
            }.flowOn(Dispatchers.IO).onCompletion {
                //完成时调用与末端流操作符处于同一个协程上下文范围
                XLog.d("Bmob☁️同步执行完毕，开始同步本地数据到云端")
                syncToBmob()
            }.collect {
            }
        }
    }

    //同步本地其他数据到云端
    private suspend fun syncToBmob() {
        //未同步的即本地有而云端无
        NoteUtils.listNotSync().asFlow()
            .onEach {
                XLog.d(it)
                if (it.objId.isEmpty()) {
                    //本地有云端无 本地无objId  直接上传
                    val note = it.toBmob()
                    val resp = BmobMethods.INSTANCE.postNote(
                        note.toJson(excludeFields = Constants.DEFAULT_EXCLUDE_FIELDS)
                            .createJsonRequestBody()
                    )
                    val postResp = resp.toBean<PostResp>()
                    //保存objectId
                    it.objId = postResp.objectId
                    XLog.d("本地有云端无 新建数据")
                } else {
                    //云端同步后 本地不可能出现本地有记录，且存在云端objid，但是没有和云端同步
                    // 这种情况只可能是云端手动删除了记录，但是本地没有同步，
                    // 即一个账号登录了两个客户端，但是在一个客户端中对该记录进行了删除，在另一个客户端中还存在本地记录
                    //此情况可以加入特殊标记 isCloudDel
                    it.isCloudDel = true
                    XLog.d("不太可能出现的一种情况\n$it")
                }
                it.isSync = true
                it.save()
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

    override fun onResume() {
        super.onResume()
        listAllFromDb()
    }
}