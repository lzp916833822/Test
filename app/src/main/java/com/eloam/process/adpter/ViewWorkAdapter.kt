package com.eloam.process.adpter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.eloam.process.R
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.databinding.UploadFileItemBinding


abstract class ViewWorkAdapter(context: Context, diffCallback: DiffUtil.ItemCallback<DataInfo>) :
    BaseBindingPagingAdapter<Any, UploadFileItemBinding>(context, diffCallback) {


    override fun getLayoutResId(viewType: Int): Int {
        return R.layout.upload_file_item
    }

    override fun onBindItem(binding: UploadFileItemBinding?, item: Any?, position: Int) {

    }


}