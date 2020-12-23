package com.zcscombo;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.usb.UsbHandler;
import com.ivsign.android.IDCReader.UserIDCardInfo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IDCardTestAcitivty extends Activity {
	static String TAG = "IDCardActivity";
	private Handler _ui;

	ImageView iv_icon;
	Button bt_read;
	Button bt_reset;
	Button bt_off;
	TextView ett;
	UsbHandler _handler;
	Settings _settings;
	MessageHandler _msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_idcard);
		_msg = new MessageHandler((TextView) findViewById(R.id.textView1));
		ett = (TextView) findViewById(R.id.textView1);
		_handler = new UsbHandler(this);
		_settings = new Settings(_handler);
		_ui = new Handler(Looper.myLooper());
		iv_icon = (ImageView) findViewById(R.id.imageView1);
		try {
			int nRet = _handler.connect();
			if (nRet != 0)
				sendMessage("Usb设备连接失败....,status=" + nRet);
			else {
				sendMessage("Usb设备连接成功...");
				// sendMessage(_settings.readVersion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		_msg.sendMessage(message);
	}

	public void btnClick(View view) {
		int id = view.getId();
		if (id == R.id.btReset) {// step1.身份证复位
			String tmp = _settings.writeIDReset();
			if (!tmp.startsWith("00")) {
				sendMessage("ID复位失败!");
			} else {
				sendMessage("ID复位成功!");
				sendMessage("默认寻找路径:"
						+ Environment.getExternalStorageDirectory() + "/wltlib");
			}
		} else if (id == R.id.btread) {
			readIDCard();
		} else if (id == R.id.btclose) {
			_settings.writeIDOff();
		}


	}

	private void readIDCard() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				readCard();
			}
		}).start();
	}

	private void readCard() {
		// step2.身份证数据读取
		final UserIDCardInfo cardInfo = _settings.writeIDRead("/sdcard");
		_ui.post(new Runnable() {
			@Override
			public void run() {
				ett.setText("");
			}
		});
		_ui.post(new Runnable() {
			@Override
			public void run() {
				iv_icon.setImageResource(R.drawable.face);
			}
		});
		if (cardInfo == null)
			return;
		_ui.post(new Runnable() {
			@Override
			public void run() {
				ett.setText("姓名：" + cardInfo.getuName() + "\n" + "性别："
						+ cardInfo.getuSex() + "\n" + "民族："
						+ cardInfo.getuNation() + "\n" + "出生日期："
						+ cardInfo.getuBirthday() + "\n" + "地址："
						+ cardInfo.getuAddress() + "\n" + "身份号码："
						+ cardInfo.getuID() + "\n" + "签发机关："
						+ cardInfo.getuAuthor() + "\n" + "有效期限："
						+ cardInfo.getuExpDateStart() + "-"
						+ cardInfo.getuExpDateEnd());
			}
		});
		_ui.post(new Runnable() {
			@Override
			public void run() {
				iv_icon.setImageBitmap(cardInfo.getuImage());
			}
		});
	}

	@Override
	protected void onDestroy() {
		_handler.onDestroy();
		super.onDestroy();
	}
}
