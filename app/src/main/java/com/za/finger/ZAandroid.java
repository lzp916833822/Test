package com.za.finger;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.zaz.sdk.ukey.OTG_KEY;


public class ZAandroid {
    public final static int DEVICE_SUCCESS = 0x00000000;
    public final static int DEVICE_FAILED = 0x20000001;
    public final static int DEVICE_KEY_REMOVED = 0x20000002;
    public final static int DEVICE_KEY_INVALID = 0x20000003;
    public final static int DEVICE_INVALID_PARAMETER = 0x20000004;
    public final static int DEVICE_VERIFIEDPIN_FAILED = 0x20000005;
    public final static int DEVICE_USER_NOT_LOG_IN = 0x20000006;

    public final static int DEVICE_BUFFER_TOO_SMALL = 0x20000007;
    public final static int DEVICE_CONTAINER_TOOMORE = 0x20000008;
    public final static int DEVICE_ERR_GETEKEYPARAM = 0x20000009;
    public final static int DEVICE_ERR_PINLOCKED = 0x20000010;
    public final static int DEVICE_ERR_CREATEFILE = 0x20000011;
    public final static int DEVICE_ERR_EXISTFILE = 0x20000012;
    public final static int DEVICE_ERR_OPENFILE = 0x20000013;

    public final static int DEVICE_ERR_READFILE = 0x20000014;
    public final static int DEVICE_ERR_WRITEFILE = 0x20000015;
    public final static int DEVICE_ERR_NOFILE = 0x20000016;

    public final static int DEVICE_ERR_PARAMETER_NOT_SUPPORT = 0x20000020;
    public final static int DEVICE_ERR_FUNCTION_NOT_SUPPORT = 0x20000021;

    public static OTG_KEY msyUsbKey;

    public ZAandroid(UsbManager mManager, UsbDevice mDev) {
        try {
            msyUsbKey = new OTG_KEY(mManager, mDev);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int ZAZOpenDevice(int fd, int nDeviceType, int iCom, int iBaud, int nPackageSize/*=2*/, int iDevNum/*=0*/) {
        return msyUsbKey.UsbOpen();
    }

    public int ZAZVfyPwd(int nAddr, byte[] pPassword) {
        return msyUsbKey.zazword();
    }

    public int ZAZCloseDeviceEx() {
        return msyUsbKey.CloseCard(0);
    }

    public int ZAZGetImage(int nAddr) {
        return msyUsbKey.GetImage(nAddr);
    }

    public int ZAZUpImage(int nAddr, byte[] pImageData, int[] iTempletLength) {
        return msyUsbKey.UpImage(nAddr, pImageData);
    }

    public int ZAZGenChar(int nAddr, int iBufferID) {
        return msyUsbKey.GenChar(nAddr, iBufferID);
    }

    public int ZAZSearch(int nAddr, int iBufferID, int iStartPage, int iPageNum, int[] iMbAddress) {
        return msyUsbKey.Search(nAddr, iBufferID, iStartPage, iPageNum, iMbAddress);
    }

    public int ZAZDelChar(int nAddr, int iStartPageID, int nDelPageNum) {
        return msyUsbKey.ZAZDelChar(nAddr, iStartPageID, nDelPageNum);
    }

    public int ZAZEmpty(int nAddr) {
        return msyUsbKey.Empty(nAddr);
    }

    public int ZAZRegModule(int nAddr) {
        return msyUsbKey.RegModule(nAddr);
    }

    public int ZAZStoreChar(int nAddr, int iBufferID, int iPageID) {
        return msyUsbKey.StoreChar(nAddr, iBufferID, iPageID);
    }

    public int ZAZReadIndexTable(int addr, int nPage, byte[] UserContent) {
        return msyUsbKey.ReadIndexTable(addr, nPage, UserContent);
    }

    public int ZAZUpchar(int nAddr, int iBufferID, byte[] pTemplete) {
        return msyUsbKey.Upchar(nAddr, iBufferID, pTemplete);
    }


    public int ZAZDownchar(int nAddr, int iBufferID, byte[] pTemplete,
                           int iTempletelenth) {
        return msyUsbKey.Downchar(nAddr, iBufferID, pTemplete, iTempletelenth);
    }
}
