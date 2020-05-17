package nicelee.bilibili.util.check;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RafWBuffered {

	byte buffer[];
	int pointer;
	RandomAccessFile raf;

	public RafWBuffered(File file, String mode) throws FileNotFoundException {
		this(file, mode, 1024 * 1024 * 2);
	}

	public RafWBuffered(File file, String mode, int buffSize) throws FileNotFoundException {
		buffer = new byte[buffSize];
		raf = new RandomAccessFile(file, mode);
		pointer = 0;
	}

	public void write(int b) throws IOException {
		if (pointer == buffer.length) {
			raf.write(buffer);
			buffer[0] = (byte) (b & 0xff);
			pointer = 1;
		} else {
			buffer[pointer] = (byte) (b & 0xff);
			pointer++;
		}
	}

	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int bufferRemainSize = buffer.length - pointer;
		if (bufferRemainSize >= len) {
			// 若buffer剩余空间足够大，那么直接写入缓存
			System.arraycopy(b, off, buffer, pointer, len);
			pointer += len;
		} else {
			// 如果buffer空间不够，那么先写满buffer后写入文件
			System.arraycopy(b, off, buffer, pointer, bufferRemainSize);
			raf.write(buffer);
			// 如果剩下的依然大于空buffer大小，那么直接写入文件，否则写入缓存
			int remain = len - bufferRemainSize;
			if (remain > buffer.length) {
				raf.write(b, off + bufferRemainSize, remain);
				pointer = 0;
			} else {
				System.arraycopy(b, off + bufferRemainSize, buffer, 0, remain);
				pointer = remain;
			}
		}
	}

	/**
	 * 该方法会执行buffer清空操作,需减少使用次数
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void seek(long pos) throws IOException {
		this.flush();
		raf.seek(pos);
	}

	public void close() throws IOException {
		this.flush();
		raf.close();
	}

	/**
	 * 该方法会执行buffer清空操作,需减少使用次数
	 * 
	 * @param newLength
	 * @throws IOException
	 */
	public void setLength(long newLength) throws IOException {
		this.flush();
		raf.setLength(newLength);
	}

	public long getFilePointer() throws IOException {
		return raf.getFilePointer() + pointer;
	}

	public void flush() throws IOException {
		if (pointer > 0) {
			raf.write(buffer, 0, pointer);
			pointer = 0;
		}
	}
}
