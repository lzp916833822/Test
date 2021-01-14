package com.eloam.process.adpter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.DiffUtil;

import com.eloam.process.R;
import com.eloam.process.data.entity.DataInfo;
import com.eloam.process.databinding.UploadFileItemBinding;
import com.eloam.process.utils.MyLocalLog;

import org.jetbrains.annotations.NotNull;

public class ViewAdapter extends BaseBindingPagingAdapter<DataInfo,UploadFileItemBinding> {
    public ViewAdapter(Context context, @NotNull DiffUtil.ItemCallback diffCallback) {
        super(context, diffCallback);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.upload_file_item;
    }

    @Override
    protected void onBindItem(UploadFileItemBinding binding, DataInfo item, int position) {
        UploadFileItemBinding itemBinding =  binding;
        itemBinding.checkBox.setVisibility(View.GONE);
        itemBinding.testWorkTv.setText(item.getEmployeeNo());
        itemBinding.logNameTv.setText(MyLocalLog.INSTANCE.getMyLogSdf().format(item.getReportTime()) + MyLocalLog.MYLOGFILEName);

    }
}
