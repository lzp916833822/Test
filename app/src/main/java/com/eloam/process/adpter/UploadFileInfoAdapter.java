package com.eloam.process.adpter;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import com.eloam.process.R;


public class UploadFileInfoAdapter extends BaseBindingAdapter {

    public UploadFileInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.upload_file_item;
    }

    @Override
    protected void onBindItem(ViewDataBinding binding, Object item) {


    }

}
