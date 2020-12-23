package com.example.scarx.idcardreader.utils;

import com.example.scarx.idcardreader.utils.imp.MyCallBack;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;

import java.util.concurrent.CountDownLatch;


public class IdCardRenderUtils {
    static boolean bSamStatus = false;
    static IDCardInfo idCardInfo = new IDCardInfo();
    static boolean ret = false;
    boolean bStoped;

    public boolean isbStoped() {
        return bStoped;
    }

    public void setbStoped(boolean bStoped) {
        this.bStoped = bStoped;
    }

    public void readerIdCard(final IDCardReader idCardReader, final CountDownLatch countdownLatch, final MyCallBack callBack) {
        try {
            if (null == idCardReader) {
                return;
            }
            callBack.onRequestDevicePermission();
            idCardReader.open(0);
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        callBack.onFail(e.getMessage());

                    }
                    if (!bStoped) {
                        try {
                            bSamStatus = idCardReader.getStatus(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!bSamStatus) {
                            try {
                                idCardReader.reset(0);
                            } catch (Exception e) {
                                callBack.onFail(e.getMessage());
                            }
                        }
                        try {
                            idCardReader.findCard(1);
                            idCardReader.selectCard(1);
                        } catch (Exception e) {
                            callBack.onFail(e.getMessage());

                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            callBack.onFail(e.getMessage());
                        }
                        try {
                            ret = idCardReader.readCard(0, 0, idCardInfo);
                        } catch (Exception e) {
                            callBack.onFail(e.getMessage());

                        }
                        if (ret) {
                            callBack.onSuccess(idCardInfo);
                        } else {
                            callBack.onNoCards();
                        }
                    }
                    countdownLatch.countDown();
                }

            }).start();
        } catch (Exception e) {
            callBack.onFail(e.getMessage());

        }
    }
}
