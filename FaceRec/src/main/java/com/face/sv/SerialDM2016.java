/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.face.sv;

public class SerialDM2016 {
	private SerialDM2016Native mSerialNative;


	public SerialDM2016() {
		mSerialNative = SerialDM2016Native.getInstance();
		//mDetectNative = new FaceDetectNative();
	}

	/**
	 * 配置设备参数
	 * @param device 设备地址
	 */
	public void configDevice(String device) {
		mSerialNative.configDevice(device);
	}

	/**
	 * 使用DM2016加密数据
	 * @param key 要加密的数据
	 * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
	 */
	public byte[] encodeKey(byte[] key) {
		if (key == null) {
			return null;
		}
		return mSerialNative.encodeKey(key);
	}

	/**
	 * 读取DM2016上的设备序列号
	 * @return  (长度4并且为负数失败，返回整数失败状态。)
	 */
	public byte[] readDeviceSerial() {
		return mSerialNative.readDeviceSerial();
	}

	/**
	 * 写入设备序列号到DM2016
	 * @param devSerial 序列号
	 * @return  (0表示成功，其他表示失败。)
	 */
	public int writeDeviceSerial(byte[] devSerial) {
		if (devSerial == null || devSerial.length == 0) {
			return -1;
		}
		return mSerialNative.writeDeviceSerial(devSerial);
	}
}
