package com.junerver.cloudnote.ui.fragment

import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.junerver.cloudnote.adapter.NoteRecyclerAdapter
import com.junerver.cloudnote.db.entity.Note
import com.junerver.cloudnote.db.entity.NoteEntity
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : BaseFragment() {
    @BindView(R.id.ivMine)
    var mIvMine: ImageView? = null

    @BindView(R.id.ivSync)
    var mIvSync: ImageView? = null

    @BindView(R.id.rvList)
    var mRvList: LRecyclerView? = null

    @BindView(R.id.fabAddNote)
    var mFabAddNote: FloatingActionButton? = null
    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var mDataAdapter: NoteRecyclerAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mNoteEntities: List<NoteEntity> = ArrayList<NoteEntity>()

    protected fun init() {
        mDataAdapter = NoteRecyclerAdapter(mContext)
        mDataAdapter.setDataList(mNoteEntities)
        mLRecyclerViewAdapter = LRecyclerViewAdapter(mContext, mDataAdapter)
        mRvList.setAdapter(mLRecyclerViewAdapter)
        //设置固定大小
        mRvList.setHasFixedSize(true)
        //创建线性布局
        mLayoutManager = LinearLayoutManager(mContext)
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL)
        //给RecyclerView设置布局管理器
        mRvList.setLayoutManager(mLayoutManager)
        //禁用下拉刷新
        mRvList.setPullRefreshEnabled(false)
        //设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener() {
            fun onItemClick(view: View?, i: Int) {
                val noteEntity: NoteEntity = mDataAdapter.getDataList().get(i)
                val showIntent = Intent(mContext, NoteDetailActivity::class.java)
                showIntent.putExtra("Note", noteEntity)
                startActivity(showIntent)
            }

            fun onItemLongClick(view: View?, i: Int) {
                // TODO: 2016/9/20 可以设置长按删除
            }
        })
    }

    protected val layoutId: Int
        protected get() = R.layout.fragment_note

    @OnClick([R.id.ivMine, R.id.ivSync])
    fun onClick(view: View) {
        when (view.id) {
            R.id.ivMine -> {
            }
            R.id.ivSync -> {
                mIvSync!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        mContext,
                        R.anim.anim_sync
                    )
                ) //动画效果
                synvToDb()
                NotesSyncToBmobObservable.syncToBmob()
                    .doOnCompleted(object : Action0() {
                        fun call() {
                            if (NetUtils.isConnected(mContext)) {
                                showShortToast(mContext.getString(R.string.sync_success))
                            } else {
                                showShortToast(mContext.getString(R.string.check_connect))
                            }
                        }
                    })
                    .subscribe()
            }
        }
    }

    @OnClick(R.id.fabAddNote)
    fun onClick() {
        //进入添加页面
        startActivity(Intent(mContext, EditNoteActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        NotesFromDatabaseObservable.ofDate()
            .subscribe(this)
    }

    fun onCompleted() {
        //更新配适器数据
        mDataAdapter.setDataList(mNoteEntities)
    }

    fun onError(throwable: Throwable?) {}
    fun onNext(noteEntities: List<NoteEntity>) {
        //从数据库获取本地数据
        mNoteEntities = noteEntities
    }

    fun synvToDb() {
        val query: BmobQuery<Note> = BmobQuery<Note>()
        query.addWhereEqualTo("userObjId", BmobUser.getCurrentUser().getUsername())
        query.setLimit(50) //查询本用户的50条笔记
        query.findObjects(object : FindListener<Note?>() {
            fun done(list: List<Note>, e: BmobException?) {
                if (e == null) {
                    Logger.d("共查询到：" + list.size)
                    for (note in list) {
                        val entity: NoteEntity = note.toEntity()
                        entity.objId = note.objectId
                        CloudNoteApp.getNoteEntityDao().insertOrReplace(entity)
                    }
                    NotesFromDatabaseObservable.ofDate()
                        .subscribe(this@NoteFragment)
                } else {
                    Logger.d("bmob查询失败：" + e.getMessage().toString() + "," + e.getErrorCode())
                }
            }
        })
    }
}