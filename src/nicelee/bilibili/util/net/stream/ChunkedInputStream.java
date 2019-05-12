package nicelee.bilibili.util.net.stream;

import java.io.IOException;
import java.io.InputStream;

// 未使用
public class ChunkedInputStream extends InputStream {

	private InputStream in;
	private int len = -1; // 用于读取长度
	private int[] length = new int[8]; // 用于保存长度
	private int size = 0; // 用于只是length有效字位数
	int remain = 0;// 还剩多少

	public ChunkedInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		int value, cnt = 0;
		while ((value = in.read()) != -1) {
			//Logger.println(Integer.toHexString(value));
			System.out.print(Integer.toHexString(value));
			System.out.print(" ");
			if(cnt == 30) {
				System.out.print("\r\n");
				cnt = 0;
			}
			cnt ++;
		}
		return -1;
		// 如果没有获取长度, 那么先获取长度
//		if (len == -1) {
//			while (true) {
//				value = in.read();
//				Logger.println(Integer.toHexString(value));
//				if (value == -1) {
//					return -1;
//				} else if (value == 13) {// 回车键
//					value = in.read(); // 换行键
//					// 此时, 已经读完长度
//					len = getLen();
//					remain = len;
//					break;
//				} else {
//					length[size] = value;
//					size ++;
//				}
//			}
//		}
//		// 读取一个字节
//		remain --;
//		//重置
//		if(remain == 0) {
//			len = -1;
//			in.read();
//			in.read();
//		}
//		return in.read();
	}

	/*
	 * 获取读到的长度
	 */
	private int getLen() throws IOException {

		int sum = 0;
		int weight = 1;
		while (size > 0) {
			sum += (asciiToHex(length[size - 1]) << weight);
			weight += 4;
			size--;
		}
		return sum;
	}

	private int asciiToHex(int axcii) throws IOException {
		if (axcii >= 0x30 && axcii <= 0x39) {
			return axcii - 0x30;
		} else if (axcii >= 0x41 && axcii <= 0x46) {
			return axcii - 0x41 + 10;
		} else if (axcii >= 0x61 && axcii <= 0x66) {
			return axcii - 0x61 + 10;
		} else {
			throw new IOException("ascii 码转换错误");
		}
	}

}
