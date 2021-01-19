package com.eloam.process.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.eloam.process.R
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.ui.ViewsLogWebViewActivity
import com.eloam.process.utils.MyLocalLog
import com.eloam.process.utils.MyLocalLog.myLogSdf
import kotlinx.android.synthetic.main.upload_file_item.view.*

class ViewWorkLogAdapter(private val onVisibility: () -> Unit) :
    PagingDataAdapter<DataInfo, RecyclerView.ViewHolder>(object : ItemCallback<DataInfo>() {

        override fun areItemsTheSame(oldItem: DataInfo, newItem: DataInfo): Boolean {

            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataInfo, newItem: DataInfo): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holders = holder as WorkLogViewHolder
        getItem(position)?.let { holders.onShowView(it, holders.itemView, onVisibility) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return WorkLogViewHolder.getView(parent)
    }


    class WorkLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {

            fun getView(parent: ViewGroup): WorkLogViewHolder {
                return WorkLogViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.upload_file_item, parent, false)
                )

            }

        }


        fun onShowView(item: DataInfo, holder: View, onVisibility: () -> Unit) {
            val logName = myLogSdf.format(item.reportTime) + MyLocalLog.MYLOGFILEName
            holder.checkBox.visibility = View.GONE
            holder.testWorkTv.text = item.employeeNo
            holder.logNameTv.text = logName
            holder.operateTv.setOnClickListener {
                ViewsLogWebViewActivity.onStartActivity(holder.context, logName, item.baseUrl)
            }

        }

    }
}