package nicelee.bilibili.util.check;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

//import nicelee.bilibili.util.Logger;

public class FlvMerger {

	public static void main(String[] args) throws IOException {
		List<File> flist = new ArrayList<File>();
		File f1 = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\BV1Ss411h7Ge-80-p7-part1.flv");
		File f2 = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\BV1Ss411h7Ge-80-p7-part2.flv");
		File f3 = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\BV1Ss411h7Ge-80-p7-part3.flv");
		flist.add(f1);
		flist.add(f2);
		flist.add(f3);
		
		File dst = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\test2.flv");
		new FlvMerger().merge(flist, dst);
	}

	// 用于统计时间戳
	protected int lastTimestamp = -1;
	// 用于缓冲
	private byte[] buffer;

	/**
	 * 校准文件位置
	 * 
	 * @param raf
	 * @throws IOException
	 */
	private void skip(RafRBuffered raf) throws IOException {
		raf.skipBytes(9); // 头部
		raf.skipBytes(4); // 前一个tag size
		// 读取tag
		while (true) {
			// tag 类型
			int tagType = raf.read();
			if (tagType == 18 || tagType == 0) { // 18 scripts
				//Logger.println("scripts");
				int dataSize = readBytesToInt(raf, 3);
				//Logger.println(" 当前tag data 长度为：" + dataSize);
				raf.skipBytes(4);
				raf.skipBytes(3 + dataSize + 4);
			} else {
				//Logger.println(tagType);
				raf.seek(raf.getFilePointer() - 1);
				break;
			}
		}
	}

	public void merge(List<File> srcFiles, File dst) throws IOException {
		if (buffer == null) {
			buffer = new byte[1024 * 1024 * 4];
		}
		RafWBuffered rafNew = new RafWBuffered(dst, "rw");

		// 复制第一个文件
		System.out.println("处理flv文件0");
		RafRBuffered raf = new RafRBuffered(srcFiles.get(0), "r");
		int len = raf.read(buffer);
		while (len > -1) {
			rafNew.write(buffer, 0, len);
			len = raf.read(buffer);
		}
		// 读取末尾时间戳
		{
			long position = raf.getFilePointer();
			raf.seek(position - 4);
			// 读取前一个tag size
			int predataSize = readBytesToInt(raf, 4);
			position -= (predataSize + 4);
			raf.seek(position);
			int tagType = raf.read();
			// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
			int dataSize = readBytesToInt(raf, 3);
			// Logger.print(" ,当前tag data 长度为：" + dataSize);
			// 时间戳 3+1
			int timestamp = readBytesToInt(raf, 3);
			int timestampEx = raf.read() << 24;
			timestamp += timestampEx;
			lastTimestamp = timestamp;
		}
		raf.close();

		// 处理其它文件
		for (int i = 1; i < srcFiles.size(); i++) {
			System.out.println("处理flv文件" + i);
			raf = new RafRBuffered(srcFiles.get(i), "r");
			// 跳至开始处理的地方
			skip(raf);
			// 获取基准时间戳
			int baseTimeStamp = lastTimestamp + 1;
			// 读取每个tag，处理时间戳后写入
			while (true) {
				// 读取tag
				// tag 类型
				int tagType = raf.read();
				//Logger.print("当前tag 类型为：" + tagType);
				if (tagType == 8 || tagType == 9 || tagType == 18) {// 8/9 audio/video
					// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
					int dataSize = readBytesToInt(raf, 3);
					// Logger.println(" ,当前tag data 长度为：" + dataSize);
					if(dataSize == 0)
						break;
					rafNew.write(tagType);
					rafNew.write(buffer, 0, 3);
					// 时间戳 3
					int timestamp = readBytesToInt(raf, 3);
					int timestampEx = raf.read() << 24;
					timestamp += timestampEx;
					dealTimestamp(rafNew, timestamp, baseTimeStamp);
					raf.read(buffer, 0, 3 + dataSize + 4);
					rafNew.write(buffer, 0, 3 + dataSize + 4);
				} else {
					//Logger.print("未知类型tagType：" + tagType);
					break;
				}
			}
			raf.close();
		}

		rafNew.close();
		// 修改长度
		changeDuration(dst.getAbsolutePath(), this.getDuration() / 1000);
	}

	/**
	 * 处理时间戳
	 * 
	 * @param raf
	 * @param timestamp
	 * @throws IOException
	 */
	protected void dealTimestamp(RafWBuffered raf, int timestamp, int base) throws IOException {
		lastTimestamp = base + timestamp;
		// 低于0xffffff部分
		int lowCurrenttime = lastTimestamp & 0xffffff;
		raf.write(int2Bytes(lowCurrenttime), 1, 3);
		// 高于0xffffff部分
		int highCurrenttime = lastTimestamp >> 24;
		raf.write(highCurrenttime);
		//Logger.print(" ,读取timestamps 为：" + timestamp);
		//Logger.print(" ,写入timestamps 为：" + lastTimestamp);
		//Logger.println();
	}

	/**
	 * @param raf
	 * @param byteLength
	 * @return
	 * @throws IOException
	 */
	protected int readBytesToInt(RafRBuffered raf, int byteLength) throws IOException {
		raf.read(buffer, 0, byteLength);
		return bytes2Int(buffer, byteLength);
	}

	protected byte[] int2Bytes(int value) {
		byte[] byteRet = new byte[4];
		for (int i = 0; i < 4; i++) {
			byteRet[3 - i] = (byte) ((value >> 8 * i) & 0xff);
			// Logger.printf("%x ",byteRet[3-i]);
		}
		return byteRet;
	}

	protected int bytes2Int(byte[] bytes, int byteLength) {
		int result = 0;
		for (int i = 0; i < byteLength; i++) {
			result |= ((bytes[byteLength - 1 - i] & 0xff) << (i * 8));
			// System.out.printf("%x ",(bytes[i] & 0xff));
		}
		return result;
	}

	protected byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		byte[] byteReverse = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteReverse[i] = byteRet[7 - i];
			// System.out.printf("%x ",byteReverse[i]);
		}
		//Logger.println();
		return byteReverse;
	}

	public double bytes2Double(byte[] arr) {
		byte[] byteReverse = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteReverse[i] = arr[7 - i];
		}

		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (byteReverse[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}

	byte[] durationHeader = { 0x08, 0x64, 0x75, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e };
	int pDurationHeader = 0;

	public void changeDuration(String path, double duration) throws IOException {
		// 08 64 75 72 61 74 69 6f 6e duration
		// 00 bytes x8
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		int lenRead = raf.read(buffer);
		int pDuration = checkBufferForDuration();
		boolean findDuration = false;
		while (lenRead > -1) {
			long offset = 0;
			if (pDuration != -1) {
				findDuration = true;
				raf.seek(offset + pDuration + 1);
				raf.write(0x00);
				raf.write(double2Bytes(duration));
				break;
			}
			// Logger.println("当前完成度: " + cnt*100/total + "%");
			lenRead = raf.read(buffer);
			if (!findDuration) {
				pDuration = checkBufferForDuration();
			}
			offset += lenRead;
		}
		raf.close();

	}

	/**
	 * 检查buffer 是否包含duration头部
	 * 
	 * @return duration末尾在 buffer中的位置
	 */
	int checkBufferForDuration() {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == durationHeader[pDurationHeader]) {
				pDurationHeader++;
				if (pDurationHeader == durationHeader.length) {
					pDurationHeader = 0;
					return i;
				}
			}
		}
		return -1;
	}

	public double getDuration() {
		return (double) lastTimestamp;
	}
}
