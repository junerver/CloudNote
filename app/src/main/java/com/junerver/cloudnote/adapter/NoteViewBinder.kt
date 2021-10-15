package com.junerver.cloudnote.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.edusoa.ideallecturer.utils.TimeUtils.formatToTimeString
import com.junerver.cloudnote.R
import com.junerver.cloudnote.db.entity.NoteEntity
import com.junerver.cloudnote.ui.activity.NoteDetailActivity

/**
 * @Author Junerver
 * @Date 2021/10/14-16:46
 * @Email junerver@gmail.com
 * @Version v1.0
 * @Description
 */
class NoteViewBinder : ItemViewBinder<NoteEntity,NoteViewBinder.ViewHolder>() {


    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val cardview :CardView = itemView.findViewById(R.id.root)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvSummary: TextView = itemView.findViewById(R.id.tvSummary)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NoteEntity) {
        holder.cardview.setOnClickListener {
            val showIntent = Intent(it.context, NoteDetailActivity::class.java)
            showIntent.putExtra("Note", item)
            it.context.startActivity(showIntent)
        }
        holder.tvTitle.text = item.title
        holder.tvDate.text = (item.updatedTime*1000).formatToTimeString()
        holder.tvSummary.text = item.summary
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_note, parent, false)
        return ViewHolder(root)
    }


}