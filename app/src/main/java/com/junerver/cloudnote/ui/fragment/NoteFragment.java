package com.junerver.cloudnote.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.adapter.NoteRecyclerAdapter;
import com.junerver.cloudnote.db.entity.NoteEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends BaseFragment {

    @BindView(R.id.ivMine)
    ImageView mIvMine;
    @BindView(R.id.ivSync)
    ImageView mIvSync;
    @BindView(R.id.rvList)
    LRecyclerView mRvList;

    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private NoteRecyclerAdapter mDataAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<NoteEntity> mNoteEntities = new ArrayList<>();
    private Context mContext;

    @Override
    protected void init() {
        mContext = getActivity().getApplicationContext();
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
                String msg="同步中！";

                showShortToast(msg);
                break;
        }
    }

    private void showShortToast(String msg) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
