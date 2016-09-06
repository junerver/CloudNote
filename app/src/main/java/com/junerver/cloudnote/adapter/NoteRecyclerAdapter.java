package com.junerver.cloudnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.NoteEntity;

/**
 * Created by junerver on 2016/9/6.
 */
public class NoteRecyclerAdapter extends ListBaseAdapter {
    private LayoutInflater mInflater;

    public NoteRecyclerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(mInflater.inflate(R.layout.item_note,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NoteEntity noteEntity = mDataList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvTitle.setText(noteEntity.getTitle());
        viewHolder.tvDate.setText(noteEntity.getDate());
        viewHolder.tvSummary.setText(noteEntity.getSummary());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvSummary;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvSummary = (TextView) itemView.findViewById(R.id.tvSummary);
        }
    }
}
