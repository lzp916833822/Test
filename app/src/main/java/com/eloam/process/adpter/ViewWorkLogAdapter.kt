package com.eloam.process.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.eloam.process.R
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.databinding.UploadFileItemBinding
import com.eloam.process.ui.ViewsLogWebViewActivity
import com.eloam.process.utils.MyLocalLog
import com.eloam.process.utils.MyLocalLog.myLogSdf

class ViewWorkLogAdapter :
    PagingDataAdapter<DataInfo, RecyclerView.ViewHolder>(object : ItemCallback<DataInfo>() {
        override fun areItemsTheSame(oldItem: DataInfo, newItem: DataInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataInfo, newItem: DataInfo): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding: UploadFileItemBinding? = DataBindingUtil.getBinding(holder.itemView)
        getItem(position)?.let { (holder as WorkLogViewHolder).onShowView(it, binding) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WorkLogViewHolder.getView(parent)
    }


    class WorkLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun getView(parent: ViewGroup): WorkLogViewHolder {
                val binding: UploadFileItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.upload_file_item,
                    parent,
                    false
                )
                return WorkLogViewHolder(binding.root)

            }

        }


        fun onShowView(item: DataInfo, binding: UploadFileItemBinding?) {
            val logName = myLogSdf.format(item.reportTime) + MyLocalLog.MYLOGFILEName
            binding?.checkBox?.visibility = View.GONE
            binding?.testWorkTv?.text = item.employeeNo
            binding?.logNameTv?.text = logName
            binding?.operateTv?.setOnClickListener {
                ViewsLogWebViewActivity.onStartActivity(binding.root.context,logName,item.baseUrl)
            }


        }

    }
}