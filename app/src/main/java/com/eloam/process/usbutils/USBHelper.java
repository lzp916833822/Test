package com.eloam.process.usbutils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.TextView;

import com.eloam.process.data.entity.StatueOpen;
import com.eloam.process.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by sage on 18/6/21.
 * Description: 封装Usb接口通信的工具类
 * <p>
 * 使用USB设备：
 * 1.添加权限：
 * <uses-feature  android:name="android.hardware.usb.host" android:required="true">
 * </uses-feature>
 * 2.Manifest中添加以下<intent-filter>，获取USB操作的通知：
 * <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
 * 3.添加设备过滤信息，气筒usb_xml可以自由修改：
 * <meta-data  android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
 * android:resource="@xml/usb_xml"></meta-data>
 * 4.根据目标设备的vendorId和productId过滤USB设备,拿到UsbDevice操作对象
 * 5.获取设备通讯通道
 * 6.连接
 */
//public class USBHelper extends AppCompatActivity{
public class USBHelper extends Activity {
    private final static int USB_OPEN_OK = 0;//usb正常打开
    private final static int USB_PERMISSION_OK = 10001; //USB授权成功
    private final static int USB_PERMISSION_FAIL = 10002;//USB授权失败
    private final static int USB_FIND_THIS_FAIL = 10003;//没有找到指定设备
    private final static int USB_FIND_ALL_FAIL = 10004;//没有找到任何设备
    private final static int USB_OPEN_FAIL = 10005;//USB设备打开失败
    private final static int USB_PASSWAY_FAIL = 10006;//USB通道打开失败
    private final static int USB_SEND_DATA_OK = 10007;//USB发送数据成功
    private final static int USB_SEND_DATA_FAIL = 10008;//USB发送数据失败

    private static final int LEN_MAX = 256;
    private static final int TEXT_MAX = 1600;
    private static final int MAX_HID_LENGTH = TEXT_MAX;

    private  static final  int VendorID = 4070;//10205;//0x0525;//0x0c40;      // vendorID--10205   ProductId--259 vendorID--43777   ProductId--61186
    private  static final  int ProductID = 33054;//259;//0x7a42;//a4a8;//0x7a18;

    private static final String TAG = "USBDeviceUtil";
    public static final String ACTION_USB_PERMISSION = "com.eloam.process.USB_PERMISSION";
    private static USBHelper util;
    private UsbDevice usbDevice; //目标USB设备
    private UsbManager usbManager;
    /**
     * 块输出端点
     */
    private UsbEndpoint epBulkOut;
    private UsbEndpoint epBulkIn;
    /**
     * 控制端点
     */
    private UsbEndpoint epControl;
    /**
     * 中断端点
     */
    private UsbEndpoint epIntEndpointOut;
    private UsbEndpoint epIntEndpointIn;

    private PendingIntent intent; //意图
    private UsbDeviceConnection conn = null;

    private OnFindListener listener;

    private int statue = USB_OPEN_OK;

    // nico:
    private boolean scannerAuth;
    /**
     * 调式打印
     */
    private String str;

    public void DebugInitial(TextView tv) {
        //setContentView(R.layout.activity_main); //  disable to use
        //tvPrintf = findViewById(R.id.tv_printf);
        String str = "nico debug";
    }

    public static int getVendorID() {
        return VendorID;
    }

    public static int getProductID() {
        return ProductID;
    }


    public static USBHelper getInstance(Context _context, BroadcastReceiver broadcastReceiver) {
        if (util == null) util = new USBHelper(_context, broadcastReceiver);

        return util;
    }


    private USBHelper(Context _context, BroadcastReceiver broadcastReceiver) {
        intent = PendingIntent.getBroadcast(_context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        _context.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public UsbManager getUsbManager() {
        return usbManager;
    }

    /**
     * 找到自定设备
     */
    public UsbDevice getUsbDevice(int vendorId, int productId,Context context) {
        //1)创建usbManager
        if (usbManager == null)
            usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //2)获取到所有设备 选择出满足的设备
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.i(TAG, "vendorID--" + device.getVendorId() + "ProductId--" + device.getProductId());
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                return device; // 获取USBDevice
            }
        }
        statue = USB_FIND_THIS_FAIL;
        return null;
    }

    /**
     * 查找本机所有的USB设备
     */
    public List<UsbDevice> getUsbDevices(Context context) {
        str = "nico getUsbDevices";

        //1)创建usbManager
        if (usbManager == null)
            usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //2)获取到所有设备 选择出满足的设备
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        //创建返回数据
        List<UsbDevice> lists = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.i(TAG, "vendorID--" + device.getVendorId() + "ProductId--" + device.getProductId());
            str += "find SAGE QRscanner,vendorID--" + device.getVendorId() + " ProductId--" + device.getProductId();
            lists.add(device);
        }
        return lists;
    }


    /**
     * 根据指定的vendorId和productId连接USB设备
     *
     * @param vendorId      产商id
     * @param productId     产品id
     * @param mainViewModel
     */
    public int connection(int vendorId, int productId, MainViewModel mainViewModel,Context context) {
        usbDevice = getUsbDevice(vendorId, productId,context);
        //3)查找设备接口
        if (usbDevice == null) {
            Log.e(TAG, "未找到目标设备，请确保供应商ID" + vendorId + "和产品ID" + productId + "是否配置正确");
            str += "未找到目标设备，请确保供应商ID" + vendorId + "和产品ID" + productId + "是否配置正确\n";
            return statue;
        }
        UsbInterface usbInterface = null;
        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            //一个设备上面一般只有一个接口，有两个端点，分别接受和发送数据
            usbInterface = usbDevice.getInterface(i);
            break;
        }
        //4)获取usb设备的通信通道endpoint
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            switch (ep.getType()) {
                case UsbConstants.USB_ENDPOINT_XFER_BULK://USB端口传输
                    if (UsbConstants.USB_DIR_OUT == ep.getDirection()) {//输出
                        epBulkOut = ep;
                        Log.e(TAG, "获取发送数据的端点");
                        str += "BULK 获取发送数据的端点\n";
                    } else {
                        epBulkIn = ep;
                        Log.e(TAG, "获取接受数据的端点");
                        str += "BULK 获取接受数据的端点\n";
                    }
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_CONTROL://控制
                    epControl = ep;
                    Log.e(TAG, "find the ControlEndPoint:" + "index:" + i + "," + epControl.getEndpointNumber());
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_INT://中断
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {//输出
                        epIntEndpointOut = ep;
                        Log.e(TAG, "find the InterruptEndpointOut:" + "index:" + i + "," + epIntEndpointOut.getEndpointNumber());
                        str += "find the InterruptEndpointOut:" + "index:" + i + ",ep" + epIntEndpointOut.getEndpointNumber();

                    }
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        epIntEndpointIn = ep;
                        Log.e(TAG, "find the InterruptEndpointIn:" + "index:" + i + "," + epIntEndpointIn.getEndpointNumber());
                        str += "\nfind the InterruptEndpointIn:" + "index:" + i + ",ep" + epIntEndpointIn.getEndpointNumber();

                    }
                    break;
                default:
                    break;
            }
        }
        //5)打开conn连接通道
        if (usbManager.hasPermission(usbDevice)) {
            //有权限，那么打开
            conn = usbManager.openDevice(usbDevice);
        } else {
            usbManager.requestPermission(usbDevice, intent);
            if (usbManager.hasPermission(usbDevice)) { //权限获取成功
                conn = usbManager.openDevice(usbDevice);
            } else {
                Log.e(TAG, "没有权限");
                str += "没有权限\n";

                statue = USB_PERMISSION_FAIL;
            }
        }
        if (null == conn) {
            Log.e(TAG, "不能连接到设备");
            str += "不能连接到设备\n";

            statue = USB_OPEN_FAIL;
            return statue;
        }
        //打开设备
        if (conn.claimInterface(usbInterface, true)) {
            if (conn != null && mainViewModel != null) {// 到此你的android设备已经连上zigbee设备
                mainViewModel.getStatueOpen().postValue(new StatueOpen(3, 0));
                Log.d(TAG, "open设备成功！");
                str += "open设备成功\n";

            }
            final String mySerial = conn.getSerial();
            Log.i(TAG, "设备serial number：" + mySerial);
            str += "设备serial number：" + mySerial;

            statue = USB_OPEN_OK;
        } else {
            Log.i(TAG, "无法打开连接通道。");
            str += " -无法打开连接通道\n";

            statue = USB_PASSWAY_FAIL;
            conn.close();
        }
        return statue;
    }

    private void memset(byte[] buf, byte val, int len) {
        for (int i = 0; i < len; i++) {
            buf[i] = val;
        }
    }

    private boolean checksum(byte[] str, int len) {
        int i;
        int sum = 0, sum_tmp = 0;
        int str_len = len - 4; // sizeof(long) = 4
        if (str == null) return false;
        if (len > TEXT_MAX) return false;
        //TRACE("\n");
        for (i = 0; i < str_len; i++) {
            sum += str[i];
            //TRACE("i:%d,%c \n",i,str[i]);
        }
        //TRACE("\n");

        //memcpy((unsigned char *)&sum_tmp, (unsigned char *)&str[i], sizeof(uint32_t));
        for (int k = 0; k < 4; k++) {
            int shift = k * 8;
            sum_tmp += (str[i + k] & 0xFF) << shift;
        }

        //TRACE("line:%d,i:%d,str_len:%d,check sum:%ld, sum_tmp:%ld\n",__LINE__, i,str_len,sum,sum_tmp);
        if (sum_tmp == sum) {
            Log.i(TAG, "usb check_sum OK\n");
            //memset((char *)&str[i], 0, sizeof(uint32_t));
            for (int k = 0; k < 4; k++) {
                str[i + k] = 0;
            }
            return true;
        }
        return false;
    }

    /*
读写用户自定义数据最大支持2K，覆盖式写入，每次只能存一条数据。

pBuf:写入的数据,
uiBufLen:写入数据的长度
return：>0成功 ，0: timer out, -1失败
*/
    private int Scan_data_write(byte[] pBuf, int uiBufLen) {
        return sendData(pBuf);
    }
    /*
pBuf  : 读出的数据,
uiBufLen  : 期望读出数据的长度,
return :  实际读出数据的长度, （读取失败：返回-1）
timerout: 读超时，默认6000 = 6s
*/
//    private int Scan_data_read(byte[] pBuf, int uiBufLen, int timeout) {
//        int ret = -1;
//        byte[] hid_buf=new byte[128];
//        int usb_recv_index = 0;
//        boolean head_flag = false;
//        int len = 0;
//
//        while(true) {
//            //memset(hid_buf, 0, sizeof(hid_buf));
//            for(int i=0; i<128;i++){
//                hid_buf[i] = 0;
//            }
//            ret = readData(hid_buf, 32, timeout);//ReadInputReport(hid_buf, sizeof(hid_buf),timeout);
//            if(ret == 10002 || ret==-1){ // timer out
//                Log.i(TAG,"ReadInputReport timer out\n");
//                try{
//                    Thread.sleep(20);
//                }catch(Exception e){
//                }
//                continue;
//            }else{
//                int i;
//                if((usb_recv_index==0) && !head_flag){ // data len
//                    head_flag = true;
//                    //memcpy(&len, hid_buf, sizeof(uint32_t));
//                    for(i = 0; i < 4; i++){
//                        int shift= i * 8;
//                        len +=(hid_buf[i] & 0xFF) << shift;
//                    }
//
//                    if(len>MAX_HID_LENGTH){ // no data len
//                        return -1;
//                    }
//                    Log.i(TAG, "len:"+len);
//                    continue;
//                }
//                Log.i(TAG, "read:-> ");
//                for(i=0; i<ret; i++){
//                    Log.i(TAG, "read:-> " +hid_buf[i]);
//                }
//                Log.i(TAG, "\n");
//                Log.i(TAG, "\n");
//                //memcpy(&pBuf[usb_recv_index], hid_buf, ret);
//                for(i=0; i<ret;i++){
//                    pBuf[usb_recv_index+i] = hid_buf[i];
//                }
//                usb_recv_index += ret;
//                //TRACE("len:%d,index :%d, ret:%d\n",len,usb_recv_index,ret);
//                if(usb_recv_index >= len){
//                    if(!checksum(pBuf,len)){// 校验数据完整性
//                        Log.i(TAG,"host check_sum OK\n");
//                        len -=4; // 4byte check sum
//                        ret = len;
//                        break;
//                    }else{
//                        Log.i(TAG,"host check_sum ERROR\n");
//                        ret= -1;
//                        break;
//                    }
//                }
//            }
//        }
//        //读结束符
//        if(ret>0){ // read enter(0x0d, 0x0a)
//            //memset(hid_buf, 0, sizeof(hid_buf));
//            for(int i=0; i<128;i++){
//                hid_buf[i] = 0;
//            }
//            ret = readData(hid_buf, 2, 20);//ReadInputReport(hid_buf, 2,20); // 10ms
//            if(ret<=0){
//                Log.i(TAG,"read enter ==>read ERROR!data le\n"+ret);
//            }else{
//                if(hid_buf[0]!=0){
//                    pBuf[len] = hid_buf[0];
//                    len +=1;
//                }
//                if(hid_buf[1]!=0){
//                    pBuf[len] = hid_buf[1];
//                    len +=1;
//                }
//            }
//            ret = len;
//        }
//        return ret;
//    }

    /**
     * 根据指定的vendorId和productId连接USB设备
     *
     * @param mainViewModel
     */
    private boolean findMyHID(MainViewModel mainViewModel,Context context) {
        List<UsbDevice> list2 = getUsbDevices(context);
        for (UsbDevice device : list2) {
            int statue = connection(VendorID, ProductID, mainViewModel,context);
            Log.i(TAG, "连接状态：" + statue);
            if (statue == 0) return true;
        }
        return false;
    }
    /**
     Function:	auth_function
     Description:
     Calls:
     Called By:
     parameter:
     Return: FLASE:ERROR
     author:
     罗伟彪
     */
    /**
     * 根据指定的vendorId和productId连接USB设备
     *
     * @param mainViewModel
     */
    public int scanInit(MainViewModel mainViewModel,Context context) {
        scannerAuth = false;
        //1: find SAGE hid
        if (!findMyHID(mainViewModel,context)) return -1;
        Log.i(TAG, "find my hid!");
        scannerAuth = true;
        return 0;
    }

    /**
     * 通过USB发送数据
     */
    public int sendData(byte[] buffer) {
        str += "sendData 1.\n";


        if (conn == null || epIntEndpointOut == null) return -1;
        str += "\nsendData 2.\n";


        if (conn.bulkTransfer(epIntEndpointOut, buffer, buffer.length, 0) >= 0) {
            //0 或者正数表示成功
            Log.i(TAG, "发送成功");
            str += "发送成功\n";

            statue = USB_PERMISSION_OK;
        } else {
            Log.i(TAG, "发送失败的");
            str += "发送失败的\n";

            statue = USB_PERMISSION_FAIL;
        }
        return statue;
    }

    /**
     * 从USB读数据
     */
    public int readData(byte[] buffer, int length, int timeout) {
        //str = "\nreadData 1.\n";
        // 

        if (conn == null || epIntEndpointIn == null) return -1;
        //str = "\nreadData 2.\n";
        //

        if (conn.bulkTransfer(epIntEndpointIn, buffer, length, timeout) >= 0) {
            //0 或者正数表示成功
            Log.i(TAG, "接收成功");
            //str = "接收成功\n" + buffer.toString();
            // 
            statue = USB_PERMISSION_OK;
        } else {
            Log.i(TAG, "接收失败");
            //str += "发送失败的\n";
            //
            statue = USB_PERMISSION_FAIL;
        }
        return statue;
    }

    /**
     * 关闭USB连接
     */
    public void close() {
        if (conn != null) { //关闭USB设备
            conn.close();
            conn = null;
        }
        util = null;
    }

    /**
     * 是否找到设备回调
     */
    public interface OnFindListener {
        void onFind(UsbDevice usbDevice, UsbManager usbManager);

        void onFail(String error);
    }

}

