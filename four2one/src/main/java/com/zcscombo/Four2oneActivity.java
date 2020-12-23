package com.zcscombo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.citic.lib.utils.ISOUtil;
import com.android.citic.lib.utils.StringUtil;
import com.imagpay.Apdu_Send;
import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.bean.SBCard;
import com.imagpay.emv.EMVApp;
import com.imagpay.emv.EMVCapk;
import com.imagpay.emv.EMVConstants;
import com.imagpay.emv.EMVListener;
import com.imagpay.emv.EMVParam;
import com.imagpay.emv.EMVResponse;
import com.imagpay.emv.EMVRevoc;
import com.imagpay.emvl2test.EmvCoreJNI;
import com.imagpay.emvl2test.EmvTermParam;
import com.imagpay.emvl2test.Param;
import com.imagpay.emvl2test.TransAPDU;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.PrintStatus;
import com.imagpay.usb.USBConstants;
import com.imagpay.usb.UsbHandler;
import com.imagpay.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Four2oneActivity extends Activity {
    static String TAG = "ZCSCombo";
    UsbHandler _handler;
    Settings _setting;
    MessageHandler _mHandler;
    TextView tv_message;
    boolean magFlag = false;
    boolean m1Flag = false;
    Apdu_Send send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_421);
        init();
        send = new Apdu_Send();
        send.setCommand(StringUtils.convertHexToBytes("00840000"));
        send.setLE((short) 0x08);
    }

    private void init() {
        _handler = new UsbHandler(this);
        _setting = new Settings(_handler);
        tv_message = (TextView) findViewById(R.id.tv_status);
        _mHandler = new MessageHandler(tv_message);
        // for emv chip card test
        _handler.setShowAPDU(true);
        // for some phones that can detect usb insert
        // _handler.setAutoConn(true);
        _handler.addSwipeListener(new SwipeListener() {
            @Override
            public void onStopped(SwipeEvent event) {
                sendMessage("onStopped:" + event.getValue());
            }

            @Override
            public void onStarted(SwipeEvent event) {
                sendMessage("onStarted:" + event.getValue());
            }

            @Override
            public void onReadData(SwipeEvent event) {
                sendMessage("onReadData:" + event.getValue());
            }

            @Override
            public void onParseData(SwipeEvent event) {
                if (magFlag || m1Flag) {
                    sendMessage("Final(16)=> " + event.getValue());
                    String[] tmps = event.getValue().trim().replaceAll("..", "$0 ").split(" ");
                    StringBuffer sbf = new StringBuffer();
                    for (String str : tmps) {
                        sbf.append((char) Integer.parseInt(str, 16));
                    }
                    sendMessage("Final(10)=> " + sbf.toString());
                }
            }

            @Override
            public void onDisconnected(SwipeEvent event) {
                sendMessage("onDisconnected:" + event.getValue());
            }

            @Override
            public void onConnected(SwipeEvent event) {
                sendMessage("onConnected:" + event.getValue());
            }

            @Override
            public void onPermission(SwipeEvent event) {
                if (event.getType() == SwipeEvent.TYPE_PERMISSION_GRANTED) {
                    _handler.connect();
                    readVersion();
                }
            }

            @Override
            public void onCardDetect(CardDetected arg0) {
            }

            @Override
            public void onPrintStatus(PrintStatus arg0) {
            }
        });
        _handler.addEMVListener(new EMVListener() {
            @Override
            public EMVResponse onSubmitData() {
                Log.e(TAG, "123333");
                return null;
            }

            @Override
            public int onSelectApp(List arg0) {
                return 0;
            }

            @Override
            public void onReversalData() {
            }

            @Override
            public String onReadPin(int arg0, int arg1) {
                return "4315";
            }

            @Override
            public boolean onReadData() {
                return true;
            }

            @Override
            public void onConfirmData() {
            }
        });
    }

    public void btnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnConn) {
            int nRet = _handler.connect();
            Log.d(TAG, "Conn res:" + nRet);
            sendMessage("Conn res:" + nRet);
            if (nRet == USBConstants.USB_NO_PERMISSION) {
                // If usb device does not get permission,manual to
                // request usb permission
                _handler.checkPermission();
                return;
            }
            readVersion();
        } else if (id == R.id.btnMAG) {//磁条卡
            new Thread(new Runnable() {
                @Override
                public void run() {
                    magTest();
                }
            }).start();
        } else if (id == R.id.btnIC) {//ic卡
            new Thread(new Runnable() {
                @Override
                public void run() {
                    icTest();
                }
            }).start();
        }
//        else if (id == R.id.btnM1) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    m1Test();
//                    // encryption();
//                }
//            }).start();
//        }
//        else if (id == R.id.btn4442) {
//            sle4442Test();
//        } else if (id == R.id.btn4428) {
//            sle4428Test();
//        } else if (id == R.id.btnat24) {
//            String des = "11111111222222223333333344444444";
//            // sendMessage("key:"+_setting.set3DESKey(des));
//            String bdk = "aaaaaaaabbbbbbbbccccccccdddddddd";
//            String ksn = "11223344556677000000";
//            // sendMessage("dukpt:"+_setting.setDukptKey(bdk, ksn));
//            sendMessage("mode:" + _setting.setWorkMode(Work_Type.DUKPT));
//            sendMessage(_setting.setWriteSN("20150729000000000000000000000000") + "");
//            sendMessage(_setting.setReadSN());
//            // at24Test();
//        }
//        else if (id == R.id.btnID) {
//            Intent intent = new Intent();
//            intent.setClass(Four2oneActivity.this, IDCardTestAcitivty.class);
//            startActivity(intent);
//            finish();
//        }
        else if (id == R.id.btnSB) {//社保卡
            readSBCard();
        }
//        else if (id == R.id.btnGJ) {
//            sendMessage("ATR:" + _setting.reset(5));
//            sendMessage("APDU:" + _setting.getDataWithAPDU(5, send));
//        }
//        else if (id == R.id.btnSFZ) {
//            sendMessage("typeB request:" + _setting.typeBRequst());
//        }
//        else if (id == R.id.btnNFC) {
//            startNFC();
//        }
    }

    /**
     * 社保卡读卡
     */
    private void readSBCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String reset = _setting.icReset();
                if (reset == null) {
                    sendMessage("请先插入社保卡...");
                    return;
                }
                sendMessage("正在读卡, 请勿拔出...");
                SBCard sbCard = _setting.readSBCard();
                if (sbCard != null) {
                    sendMessage(sbCard.toString());
                }
            }
        }).start();

    }

    private int dialogChoiceIndex = 0;

    /**
     * nfc读卡选择
     */
    private void startNFC() {
        CharSequence[] nfcItems = {"QuickPass", "PayWare", "PayPass"};
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Choose card mode").setSingleChoiceItems(nfcItems, dialogChoiceIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        dialogChoiceIndex = 0;
                        nfcThread(EmvCoreJNI.EMV_TEST_CONFIG_QPBOC);
                        break;
                    case 1:
                        dialogChoiceIndex = 1;
                        nfcThread(EmvCoreJNI.EMV_TEST_CONFIG_PAYWARE);
                        break;
                    case 2:
                        dialogChoiceIndex = 2;
                        nfcThread(EmvCoreJNI.EMV_TEST_CONFIG_PAYPASS);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void nfcThread(final int kernelConfig) {
        new Thread(new Runnable() {
            public void run() {
                nfc(kernelConfig);
            }
        }).start();
    }

    /**
     * nfc读卡
     */
    private void nfc(int kernelConfig) {
        String off = _setting.off(Settings.SLOT_NFC);
        sendMessage("off  " + off);
        EmvCoreJNI dds = new EmvCoreJNI();
        final String path = dds.exists();
        dds.addTransMethod(new TransAPDU() {
            @Override
            public byte[] onTransmitApdu(byte[] arg0) {
                String input = StringUtils.convertBytesToHex(arg0);
                Log.e(TAG, "Send==> " + input);
                sendMessage("Send ==> " + input);
                int len = input.length();
                String command = input.substring(0, 4 * 2);
                int lc = Integer.parseInt(input.substring(8, 10), 16);
                String dataIn = input.substring(10, len - 2);
                int le = Integer.parseInt(input.substring(len - 2), 16);
                Apdu_Send apdu_Send = new Apdu_Send();
                apdu_Send.setCommand(StringUtils.convertHexToBytes(command));
                apdu_Send.setLC((short) lc);
                apdu_Send.setDataIn(StringUtils.convertHexToBytes(dataIn));
                apdu_Send.setLE((short) le);
                String dataWithAPDU = _setting.getDataWithAPDU(Settings.SLOT_NFC, apdu_Send);
                Log.e(TAG, "Resposne <== " + dataWithAPDU);
                sendMessage("Resposne <== " + dataWithAPDU);
                if (dataWithAPDU == null) {
                    return null;
                }
                String res = dataWithAPDU.replaceAll(" ", "");
                res = res.substring(4);
                return StringUtils.convertHexToBytes(res);
            }

            @Override
            public String onGetDataPath() {
                return path;
            }

            @Override
            public int onMenuAppSel(String[] arg0) {
                return 0;
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String reset = _setting.reset(Settings.SLOT_NFC);
        Log.e(TAG, "reset success" + reset);
        if (reset != null) {
            sendMessage("card near field");
        } else {
            sendMessage("no card near field");
            return;
        }
        Param param = new Param(this);
        param.setHostType(0);
        param.setKernelConfig(kernelConfig);
        dds.EmvTermParamAndKernelInit(param);
        dds.EmvTransParamInit(ISOUtil.zeropad("1", 12), param);
        sendMessage("TranCurrCode:" + EmvTermParam.TranCurrCode);
        byte[] bTransResult = new byte[1];
        byte[] bCVMType = new byte[1];
        bCVMType[0] = (byte) 0x00;
        byte[] bBalance = new byte[6];
        int resp = -1;
        if (kernelConfig == EmvCoreJNI.EMV_TEST_CONFIG_QPBOC || kernelConfig == EmvCoreJNI.EMV_TEST_CONFIG_PAYWARE) {
            resp = dds.EmvQTrans(bBalance, bTransResult, bCVMType);
        } else if (kernelConfig == EmvCoreJNI.EMV_TEST_CONFIG_PAYPASS) {
            resp = dds.EmvPayPassTrans(bTransResult); // PayPass
        }
        if (resp == 0) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Four2oneActivity.this, "quick pass read successful...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final int respF = resp;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Four2oneActivity.this, "quick pass read failed...<" + respF + ">", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (bTransResult[0] == (byte) EmvCoreJNI.ONLINE_M) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Four2oneActivity.this, "Please take the card, online consumption.", Toast.LENGTH_SHORT).show();
                }
            });
            int tagListt[] = {0x57};
            String tag57 = dds.getIcField(tagListt);
            if (tag57 != null && tag57.length() > 4) {
                String track2 = tag57.substring(4);
                if (track2.indexOf("D") > 0) {
                    int index = track2.indexOf("D");
                    final String _pan = track2.substring(0, index);
                    int index2 = track2.indexOf("D") + 1;
                    final String _exp = track2.substring(index2, index2 + 4);
                    sendMessage("track2:" + track2);
                    sendMessage("cardmun:" + _pan);
                    sendMessage("_exp:" + _exp);
                }
            }
        } else if (bTransResult[0] == (byte) EmvCoreJNI.APPROVE_M) {
            Toast.makeText(Four2oneActivity.this, "Transaction approval\n" + "available balance:" + StringUtil.TwoWei(ISOUtil.hexString(bBalance)), Toast.LENGTH_SHORT).show();
            int tagListt[] = {0x57};
            String tag57 = dds.getIcField(tagListt);
            if (tag57 != null && tag57.length() > 4) {
                String track2 = tag57.substring(4);
                if (track2.indexOf("D") > 0) {
                    int index = track2.indexOf("D");
                    final String _pan = track2.substring(0, index);
                    int index2 = track2.indexOf("D") + 1;
                    final String _exp = track2.substring(index2, index2 + 4);
                    sendMessage("track2:" + track2);
                    sendMessage("cardmun:" + _pan);
                    sendMessage("_exp:" + _exp);
                }
            }
        } else if (bTransResult[0] == (byte) EmvCoreJNI.DECLINE_M) {
            Toast.makeText(Four2oneActivity.this, "The transaction is rejected", Toast.LENGTH_SHORT).show();
        }
        // EmvTermParam.ucTerminalCountry = ISOUtil.hex2byte("0156");
        // EmvTermParam.ucTranCurrCode = ISOUtil.hex2byte("0156");
        // EmvTermParam.ucTermCapa = ISOUtil.hex2byte("E0E1C8");
        // dds.EmvCoreInit(new EmvTermParam());
        // byte[] bPAN = new byte[10];
        // int[] PANLen = new int[1];
        // int
        // resp=dds.EmvReadPANProc(EmvCoreJNI.KERNAL_CONTACTLESS_ENTRY_POINT,ISOUtil.hex2byte(DateUtil.DateToStr(new
        // Date(), "yyMMdd")),bPAN,PANLen);
        // if(resp==0){
        // sendMessage("cardnum:"+ISOUtil.hexString(bPAN,0,PANLen[0]));
        // }
    }

    private void readVersion() {
        try {
            String res = _setting.readVersion();
            if (res == null) {
                sendMessage(res + " is null");
                return;
            }
            String[] ss = res.replaceAll("..", "$0 ").split(" ");
            StringBuffer sbf = new StringBuffer();
            for (String d : ss) {
                sbf.append((char) Integer.parseInt(d, 16));
            }
            sendMessage("Version:" + sbf.toString());
        } catch (Exception e) {
            // TODO: handle exception
            sendMessage(e.getMessage());
        }
    }

    private void magTest() {
        sendMessage("Start to read magnetic stripe card......");
        String tmp = _setting.magOpen();
        if (tmp == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {// only status code is 00,means ok
                    sendMessage("Pls swipe your magnetic stripe card......");
                    String res = _setting.magSwipe();
                    if (res != null && res.equals("00")) {
                        magFlag = true;
                        // data format:1byte track statu code+track data(1byte
                        // len+data)
                        sendMessage(_setting.magRead());
                        magFlag = false;
                        _setting.magReset();
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void icTest() {
        // ic
        sendMessage("Start to read emv ic card......");
        EMVParam param = new EMVParam();
        param.setSlot((byte) 0x00);
        // param.setReadOnly(true);
        // if call setReadOnly(true), SDK will only read card data
        // if call setReadOnly(false), SDK will read card data and verify pin
        // and submit data
        // 商户名
        param.setMerchName("4368696E61");// hex string of china
        // 商户类别码
        param.setMerchCateCode("0001");
        // 商户标识(商户号)
        param.setMerchId("313233343536373839303132333435");
        // 终端标识(POS号)
        param.setTermId("3132333435363738");
        // 终端类型
        param.setTerminalType((byte) 0x22);
        // 终端性能
        param.setCapability("E0F8C8");
        // param.setCapability("E028C8");//do not support pin
        // 终端扩展性能
        param.setExCapability("F00000A001");
        // 交易货币代码指数
        param.setTransCurrExp((byte) 0x02);
        // 终端国家代码
        param.setCountryCode("0840");
        // 交易货币代码
        param.setTransCurrCode("0840");
        // 当前交易类型
        param.setTransType((byte) 0x00);// EMVConstants.TRANS_TYPE_GOODS/EMVConstants.TRANS_TYPE_CASH/EMVConstants.TRANS_TYPE_CASHBACK
        // IFD序列号
        param.setTermIFDSn("3838383838383838");// SN is 88888888
        // 授权金额
        param.setAuthAmnt(8000000);// transaction amount
        // 其他金额
        param.setOtherAmnt(0);
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyMMdd");
        // 交易日期指针(3 BYTE)
        param.setTransDate(sdf.format(date));
        sdf = new SimpleDateFormat("HHmmss");
        // 交易时间指针(3 BYTE)
        param.setTransTime(sdf.format(date));
        // FIME parameters(MasterCard Test Card), if other card type, need to
        // change.
        loadMasterCardCapks(param);
        loadMasterCardRevocs(param);
        // Visa
        loadVisaRevocs(param);
        loadChinaAIDs(param);
        _setting.icReset();
        _handler.kernelInit(param);
        // if (_handler.icReset() != null) {
        _handler.process();
        // }
        _handler.icOff();
        String data = _handler.getTLVDataByTag(0x5a);
        if (data != null)
            sendMessage("CarNo:" + data);
        else
            sendMessage("CarNo:");
        data = _handler.getTLVDataByTag(0x5F20);
        if (data != null) {
            StringBuffer sb = new StringBuffer();
            String[] holder_data = data.replaceAll("..", "$0 ").trim().split(" ");
            for (String s : holder_data) {
                sb.append((char) Integer.parseInt(s, 16));
            }
            sendMessage("CarHolder:" + sb.toString());
        } else
            sendMessage("CarHolder:");
        data = _handler.getTLVDataByTag(0x5F24);
        if (data != null)
            sendMessage("Exp Date:" + data);
        else
            sendMessage("Exp Date:");
    }

    private void encryption() {
        // sendMessage(_setting.write3DesKey("00000000000000000000000000000000","aaaaaaaabbbbbbbbccccccccdddddddd")+"");
        sendMessage(_setting.writeWorkMode(Settings.TYPE_PLAINTEXT) + "");
    }

    private void m1Test() {
        sendMessage("Start to read M1 card......");
        // sendMessage(_setting.m1ReadSec("FFFFFFFFFFFF", "0E"));
        // // 分步读
        // m1Flag = true;
        String str;
        // while (true) {
        str = _setting.m1Request();
        // if (str != null)
        // break;
        // try {
        // Thread.sleep(500);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        sendMessage("M1 request:" + str);
        if (str == null)
            return;
        sendMessage(_setting.m1Select(str) + "");
        sendMessage(_setting.m1Auth("00", "FFFFFFFFFFFF") + "");
        sendMessage("Block0:" + _setting.m1ReadBlock("00") + "");
        sendMessage("Block1:" + _setting.m1ReadBlock("01") + "");
        sendMessage("Block2:" + _setting.m1ReadBlock("02") + "");
        sendMessage("Block3:" + _setting.m1ReadBlock("03") + "");
        // m1Flag = false;
    }

    private void loadChinaAIDs(EMVParam ep) {
        // PBOC_TEST_APP
        EMVApp ea = new EMVApp();
        ea.setAppName("");
        ea.setAID("A0000003330101");
        ea.setSelFlag(EMVConstants.PART_MATCH);
        ea.setPriority((byte) 0x00);
        ea.setTargetPer((byte) 0x00);
        ea.setMaxTargetPer((byte) 0x00);
        ea.setFloorLimitCheck((byte) 0x01);
        ea.setFloorLimit(2000);
        ea.setThreshold((byte) 0x00);
        ea.setTACDenial("0000000000");
        ea.setTACOnline("0000001000");
        ea.setTACDefault("0000000000");
        ea.setAcquierId("000000123456");
        ea.setDDOL("039F3704");
        ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
        ea.setVersion("0096");
        ep.addApp(ea);
    }

    private void loadMasterCardCapks(EMVParam ep) {
        // FE
        EMVCapk ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xFE);
        ec.setModul("A653EAC1C0F786C8724F737F172997D63D1C3251C4" + "4402049B865BAE877D0F398CBFBE8A6035E24AFA08" + "6BEFDE9351E54B95708EE672F0968BCD50DCE40F78" + "3322B2ABA04EF137EF18ABF03C7DBC5813AEAEF3"
                + "AA7797BA15DF7D5BA1CBAF7FD520B5A482D8D3FE" + "E105077871113E23A49AF3926554A70FE10ED728CF793B62A1");
        ec.setExponent("03");
        ec.setExpDate("491231");// YYMMDD
        ec.setCheckSum("9A295B05FB390EF7923F57618A9FDA2941FC34E0");
        ep.addCapk(ec);
        // F3
        ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xF3);
        ec.setModul("98F0C770F23864C2E766DF02D1E833DFF4FFE92D696E" + "1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA012" + "4723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A3"
                + "50C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C" + "5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3");
        ec.setExponent("03");
        ec.setExpDate("491231");// YYMMDD
        ec.setCheckSum("A69AC7603DAF566E972DEDC2CB433E07E8B01A9A");
        ep.addCapk(ec);
        // F8
        ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xF8);
        ec.setModul("A1F5E1C9BD8650BD43AB6EE56B891EF7459C0A24FA8" + "4F9127D1A6C79D4930F6DB1852E2510F18B61CD354DB83A356BD19" + "0B88AB8DF04284D02A4204A7B6CB7C5551977A9B36379CA3DE1A08E"
                + "69F301C95CC1C20506959275F41723DD5D2925290579E5A95B0DF632" + "3FC8E9273D6F849198C4996209166D9BFC973C361CC826E1");
        ec.setExponent("03");
        ec.setExpDate("491231");// YYMMDD
        ec.setCheckSum("F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DD");
        ep.addCapk(ec);
        // FA
        ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xFA);
        ec.setModul("A90FCD55AA2D5D9963E35ED0F440177699832F49C6" + "BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA" + "74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDA"
                + "B5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E70135" + "36C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B10886" + "7DEC40FAAECD740C00E2B7A8852D");
        ec.setExponent("03");
        ec.setExpDate("491231");
        ec.setCheckSum("5BED4068D96EA16D2D77E03D6036FC7A160EA99C");
        ep.addCapk(ec);
        // EF
        ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xEF);
        ec.setModul("A191CB87473F29349B5D60A88B3EAEE0973AA6F1A08" + "2F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293" + "EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A1"
                + "3ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA" + "565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B" + "8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35"
                + "C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB65" + "1AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC" + "6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3B" + "7DEC40FAAECD740C00E2B7A8852D");
        ec.setExponent("03");
        ec.setExpDate("491231");// YYMMDD
        ec.setCheckSum("21766EBB0EE122AFB65D7845B73DB46BAB65427A");
        ep.addCapk(ec);
        // F1
        ec = new EMVCapk();
        ec.setRID("A000000004");
        ec.setKeyID((byte) 0xF1);
        ec.setModul("A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C" + "5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C5" + "0AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02D"
                + "D1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF9885" + "1FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E0906429" + "09C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1" + "B43B3D7835088A2FAFA7BE7");
        ec.setExponent("03");
        ec.setExpDate("491231");// YYMMDD
        ec.setCheckSum("D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB");
        ep.addCapk(ec);
    }

    private void loadVisaRevocs(EMVParam ep) {
        EMVRevoc er = new EMVRevoc();
        er.setUCRID("A000000003");
        er.setUCIndex((byte) 0x50);
        er.setUCCertSn("024455");
        ep.addRecov(er);
    }

    private void loadMasterCardRevocs(EMVParam ep) {
        EMVRevoc er = new EMVRevoc();
        er.setUCRID("A000000004");
        er.setUCIndex((byte) 0xFE);
        er.setUCCertSn("082355");
        ep.addRecov(er);
    }

    // 4442卡测试
    public void sle4442Test() {
        boolean nRet = _setting.sle4442Init();
        if (nRet)
            sendMessage("SLE4442 init successful!");
        else {
            sendMessage("SLE4442 init failure!");
            return;
        }
        sendMessage("RSC(before):" + _setting.sle4442RSC());
        sendMessage("CSC:" + _setting.sle4442CSC("FFFFFF"));
        sendMessage("RSC(after):" + _setting.sle4442RSC());
        sendMessage("RSTC:" + _setting.sle4442RSTC());
        sendMessage("SRD:" + _setting.sle4442SRD(0, 50));
        sendMessage("PRD:" + _setting.sle4442PRD());
    }

    // 4428卡测试
    public void sle4428Test() {
        boolean nRet = _setting.sle4428Init();
        if (nRet)
            sendMessage("SLE4428 init successful!");
        else {
            sendMessage("SLE4428 init failure!");
            return;
        }
        sendMessage("CSC:" + _setting.sle4428CSC("FFFF"));
        sendMessage("RSTC:" + _setting.sle4428RSTC());
        sendMessage("SRD:" + _setting.sle4428SRD(0, 50));
        sendMessage("PRD:" + _setting.sle4428PRD(0, 50));
    }

    // AT24卡测试
    public void at24Test() {
        sendMessage(_setting.icReset());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Apdu_Send send = new Apdu_Send();
                send.setCommand(StringUtils.convertHexToBytes("00840000"));
                send.setLE((short) 8);
                sendMessage(_setting.getDataWithAPDU(send));
            }
        }).start();
        // AT24卡
        // boolean nRet = _setting.at24Reset();
        // if (nRet)
        // sendMessage("AT24 reset successful!");
        // else {
        // sendMessage("AT24 reset failure!");
        // return;
        // }
        // sendMessage("AT24 R(0x00~0x0a):"
        // + _setting.at24Read(0, 10, Settings.AT_24C16));
        // nRet = _setting.at24Write(2, 8, Settings.AT_24C16,
        // "9988776655443322");
        // if (nRet)
        // sendMessage("AT24 write successful!");
        // else {
        // sendMessage("AT24 write failure!");
        // return;
        // }
        // UL卡
        // String tmp = _setting.ulRequest();
        // sendMessage("UL Request:"+ tmp);
        // sendMessage("UL Select:"+_setting.ulSelect(tmp));
        // sendMessage("UL Read:"+_setting.ulReadPage("05"));
        // sendMessage("UL Write:"+_setting.ulWritePage("05", "aabbccdd"));
        // TYPEB卡
        // sendMessage("typeBRequst:"+_setting.typeBRequst());
        // sendMessage("typeBATTRIB:"+_setting.typeBATTRIB());
        // Apdu_Send send = new Apdu_Send();
        // send.setCommand(new byte[]{0x00,(byte)0x84,0x00,0x00});
        // send.setLE((short)8);
        // sendMessage("typeBApdu:"+_setting.typeBApdu(send));
        // sendMessage("typeBHalt:"+_setting.typeBHalt());
        // des fire
        // sendMessage("dfSelect:"+_setting.dfSelect());
        // sendMessage("dfReset:"+_setting.dfReset());
        // sendMessage("dfPSE:"+_setting.dfPSE("100000"));
        // sendMessage("dfDelApp:"+_setting.dfDelApp("100000",
        // "00000000000000000000000000000000"));
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // sendMessage("dfCreatAID:"+_setting.dfCreatAID("100000", "ef", 01,
        // "00000000000000000000000000000000"));
    }

    public void sendMessage(String str) {
        _mHandler.sendMessage(str);
    }

    @Override
    protected void onDestroy() {
        _handler.onDestroy();
        // System.exit(0);
        super.onDestroy();
    }
}
