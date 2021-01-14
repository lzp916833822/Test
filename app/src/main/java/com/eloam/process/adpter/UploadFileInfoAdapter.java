package com.eloam.process.adpter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import androidx.databinding.ViewDataBinding;

import com.eloam.process.R;
import com.eloam.process.data.entity.MyLogInfo;
import com.eloam.process.databinding.UploadFileItemBinding;
import com.eloam.process.utils.JUtils;
import com.eloam.process.utils.MyLocalLog;

import java.io.File;


public class UploadFileInfoAdapter extends BaseBindingAdapter {

    public UploadFileInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.upload_file_item;
    }

    @Override
    protected void onBindItem(ViewDataBinding binding, Object item, int position) {
        MyLogInfo myLogInfo = (MyLogInfo) item;
        UploadFileItemBinding itemBinding = (UploadFileItemBinding) binding;
        itemBinding.checkBox.setChecked(myLogInfo.isCheck());
        itemBinding.checkBox.setText("");
        itemBinding.testWorkTv.setText(myLogInfo.getEmployeeNo());
        itemBinding.logNameTv.setText(MyLocalLog.INSTANCE.getMyLogSdf().format(myLogInfo.getReportTime()) + MyLocalLog.MYLOGFILEName);
        itemBinding.operateTv.setOnClickListener(v -> {
            onOpenFile(myLogInfo);
        });
        onCheckView(myLogInfo, itemBinding);

    }

    private void onOpenFile(MyLogInfo myLogInfo) {
        File file = new File(myLogInfo.getFilePath());
        if (file.exists()) {
            try {
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                }
                uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, "text/plain");
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JUtils.INSTANCE.onToastLong(R.string.file_no_exists);
        }
    }

    private void onCheckView(MyLogInfo myLogInfo, com.eloam.process.databinding.UploadFileItemBinding itemBinding) {
        itemBinding.checkBox.setOnClickListener(v -> {
            myLogInfo.setCheck(itemBinding.checkBox.isChecked());
        });
    }

}
