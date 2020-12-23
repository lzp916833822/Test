package com.face.sv;

public class SerialDM2016Native {
	private static SerialDM2016Native mNative = null;

	static {
		System.loadLibrary("SerialDM2016");
	}

	public static SerialDM2016Native getInstance() {
		if (mNative == null) {
			mNative = new SerialDM2016Native();
		}
		return mNative;
	}

	/**
	 * 配置设备参数
	 * @param device 设备地址
	 */
	public native void configDevice(String device);

	/**
	 * 使用DM2016加密数据
	 * @param key 要加密的数据
	 * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
	 */
	public native byte[] encodeKey(byte[] key);

	/**
	 * 读取DM2016上的设备序列号（默认值13）
	 * @return  (长度4并且为负数失败，返回整数失败状态。)
	 */
	public native byte[] readDeviceSerial();

	/**
	 * 写入设备序列号到DM2016
	 * @param devSerial 序列号（默认值13）
	 * @return  (0表示成功，其他表示失败。)
	 */
	public native int writeDeviceSerial(byte[] devSerial);
}
