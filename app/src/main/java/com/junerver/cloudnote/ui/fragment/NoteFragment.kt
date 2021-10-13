package com.junerver.cloudnote.ui.fragment;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.adapter.NoteRecyclerAdapter;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.observable.NotesFromDatabaseObservable;
import com.junerver.cloudnote.observable.NotesSyncToBmobObservable;
import com.junerver.cloudnote.ui.activity.EditNoteActivity;
import com.junerver.cloudnote.ui.activity.MainActivity;
import com.junerver.cloudnote.ui.activity.NoteDetailActivity;
import com.junerver.cloudnote.utils.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends BaseFragment implements Observer<List<NoteEntity>> {

    @BindView(R.id.ivMine)
    ImageView mIvMine;
    @BindView(R.id.ivSync)
    ImageView mIvSync;
    @BindView(R.id.rvList)
    LRecyclerView mRvList;
    @BindView(R.id.fabAddNote)
    FloatingActionButton mFabAddNote;

    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private NoteRecyclerAdapter mDataAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<NoteEntity> mNoteEntities = new ArrayList<>();

    @Override
    protected void init() {

        mDataAdapter = new NoteRecyclerAdapter(mContext);
        mDataAdapter.setDataList(mNoteEntities);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mContext, mDataAdapter);

        mRvList.setAdapter(mLRecyclerViewAdapter);
        //设置固定大小
        mRvList.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(mContext);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        mRvList.setLayoutManager(mLayoutManager);
        //禁用下拉刷新
        mRvList.setPullRefreshEnabled(false);
        //设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                NoteEntity noteEntity = mDataAdapter.getDataList().get(i);
                Intent showIntent = new Intent(mContext, NoteDetailActivity.class);
                showIntent.putExtra("Note", noteEntity);
                startActivity(showIntent);
            }

            @Override
            public void onItemLongClick(View view, int i) {
                // TODO: 2016/9/20 可以设置长按删除
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_note;
    }


    @OnClick({R.id.ivMine, R.id.ivSync})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMine:
                break;
            case R.id.ivSync:
                mIvSync.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_sync));   //动画效果
                synvToDb();
                NotesSyncToBmobObservable.syncToBmob()
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                if (NetUtils.isConnected(mContext)) {
                                    showShortToast(mContext.getString(R.string.sync_success));
                                } else {
                                    showShortToast(mContext.getString(R.string.check_connect));
                                }
                            }
                        })
                        .subscribe();
                break;
        }
    }

    @OnClick(R.id.fabAddNote)
    public void onClick() {
        //进入添加页面
        startActivity(new Intent(mContext, EditNoteActivity.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        NotesFromDatabaseObservable.ofDate()
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        //更新配适器数据
        mDataAdapter.setDataList(mNoteEntities);
    }

    @Override
    public void onError(Throwable throwable) {}

    @Override
    public void onNext(List<NoteEntity> noteEntities) {
        //从数据库获取本地数据
        this.mNoteEntities = noteEntities;
    }

    public void synvToDb() {
        BmobQuery<Note> query = new BmobQuery<Note>();
        query.addWhereEqualTo("userObjId", BmobUser.getCurrentUser().getUsername());
        query.setLimit(50); //查询本用户的50条笔记
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if (e == null) {
                    Logger.d("共查询到：" + list.size());
                    for (Note note : list) {
                        NoteEntity entity = note.toEntity();
                        entity.setObjId(note.getObjectId());
                        CloudNoteApp.getNoteEntityDao().insertOrReplace(entity);
                    }
                    NotesFromDatabaseObservable.ofDate()
                            .subscribe(NoteFragment.this);
                } else {
                    Logger.d("bmob查询失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
}
