package com.junerver.cloudnote.adapter;
import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.edusoa.ideallecturer.ExtensionsKt;
import com.junerver.cloudnote.db.entity.NoteEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Junerver on 2016/8/10.
 */
public class ListBaseAdapter extends RecyclerView.Adapter {

    protected Context mContext;
    protected int mScreenWidth;

    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }

    protected ArrayList<NoteEntity> mDataList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<NoteEntity> getDataList() {
        return mDataList;
    }

    public void setDataList(Collection<NoteEntity> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(Collection<NoteEntity> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }
}

