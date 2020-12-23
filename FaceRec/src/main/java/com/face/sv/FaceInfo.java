package com.face.sv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FaceInfo {
	private int ret;
	private FacePos[] facePos;

	public FaceInfo() {
		ret = 0;
		facePos = null;
	}

	public void parseFromByteArray(byte[] data) {
		if (data != null && data.length >= 4) {
			ByteBuffer buf = ByteBuffer.wrap(data);
			buf.order(ByteOrder.nativeOrder());
			int length  = data.length;
			int fSize = FacePos.SIZE;
			if (length >= fSize) {
				int size = length / fSize;
				ret = size;
				facePos = new FacePos[size];
				byte[] bts;
				for (int i = 0; i < size; i++) {
					bts = new byte[fSize];
					buf.get(bts);
					facePos[i] = new FacePos();
					facePos[i].praseFromByteArray(bts);
				}
			} else {
				if (4 == length) {
					ret = buf.getInt();
				} else {
					ret = 0;
				}
				facePos = null;
			}
		} else {
			ret = 0;
			facePos = null;
		}
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public FacePos[] getFacePos() {
		return facePos;
	}

	public FacePos getFacePos(int index) {
		return facePos[index];
	}

	public byte[] getFacePosData(int index) {
		FacePos pos = facePos[index];
		if (pos != null) {
			return pos.getData();
		} else {
			return null;
		}
	}
}
