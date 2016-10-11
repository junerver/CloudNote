package com.junerver.cloudnote.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.adapter.NoteRecyclerAdapter;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.observable.NotesListFromDatabaseObservable;
import com.junerver.cloudnote.ui.activity.AddNoteActivity;
import com.junerver.cloudnote.ui.activity.EditNoteActivity;
import com.junerver.cloudnote.ui.activity.ShowNoteActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

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
                Intent showIntent = new Intent(mContext, ShowNoteActivity.class);
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
                showShortToast("我的！");
                break;
            case R.id.ivSync:
                // TODO: 2016/9/6  同步，启用observable将数据库的数据与后端数据对比并同步
                String msg = "同步中！";

                showShortToast(msg);
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
        NotesListFromDatabaseObservable.ofDate()
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        //更新配适器数据
        Logger.d("配适器更新数据！");
        mDataAdapter.setDataList(mNoteEntities);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onNext(List<NoteEntity> noteEntities) {
        //从数据库获取本地数据
        Logger.d("fragment页面接收到数据");
        this.mNoteEntities=noteEntities;
    }
}
