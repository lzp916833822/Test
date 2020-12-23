package com.example.scarx.idcardreader.utils.imp;

import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;

public interface MyCallBack {
    void onSuccess(IDCardInfo idCardInfo);
    void onRequestDevicePermission();
    void onFail(String error);
    void onNoCards();
}
