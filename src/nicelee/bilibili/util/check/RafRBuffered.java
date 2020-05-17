package nicelee.bilibili.util.check;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RafRBuffered {

	byte buffer[];
	int pointer;
	int validBound;
	RandomAccessFile raf;

	public RafRBuffered(File file, String mode) throws FileNotFoundException {
		this(file, mode, 1024 * 1024 * 4);
	}

	public RafRBuffered(File file, String mode, int buffSize) throws FileNotFoundException {
		buffer = new byte[buffSize];
		raf = new RandomAccessFile(file, mode);
		pointer = 0;
		validBound = 0;
	}

	public int read() throws IOException {
		if (pointer < validBound) {
			int result = buffer[pointer];
			pointer++;
			return result;
		} else {
			validBound = raf.read(buffer);
			// System.out.println("I/O read ---");
			pointer = 1;
			return buffer[0];
		}
	}

	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int bufferRemainSize = validBound - pointer;
		// 若buffer里面内容充足，则直接从缓存读取
		if (bufferRemainSize >= len) {
			System.arraycopy(buffer, pointer, b, off, len);
			pointer += len;
			return len;
		} else {
			// buffer里面内容不足，先将缓存里的全部读取
			try {
				System.arraycopy(buffer, pointer, b, off, bufferRemainSize);
			}catch (Exception e) {
				return -1;
			}
			
			int remainToRead = len - bufferRemainSize;
			// 若要读的还剩很多，直接读取，否则先读入缓存
			if (remainToRead >= buffer.length) {
				int readSize = raf.read(b, off + bufferRemainSize, remainToRead);
//				System.out.println("I/O read ---");
				pointer = 0;
				validBound = 0;
				return bufferRemainSize + readSize;
			} else {
				int readSize = raf.read(buffer);
//				System.out.println("I/O read ---");
				if(readSize == -1) {
					pointer = 0;
					validBound = 0;
					if(bufferRemainSize == 0)
						return -1;
					else
						return bufferRemainSize;
				}
				
				if (readSize >= remainToRead) {
					System.arraycopy(buffer, 0, b, off + bufferRemainSize, remainToRead);
					validBound = readSize;
					pointer = remainToRead;
					return bufferRemainSize + remainToRead;
				} else {
					System.arraycopy(buffer, 0, b, off + bufferRemainSize, readSize);
					pointer = 0;
					validBound = 0;
					return bufferRemainSize + readSize;
				}
			}
		}

	}

	public int skipBytes(int n) throws IOException {
		int bufferRemainSize = validBound - pointer;
		// 若buffer里面内容充足，则直接从缓存跳过
		if (bufferRemainSize >= n) {
			pointer += n;
			return n;
		} else {
			// buffer里面内容不足，先将缓存里的全部跳过
			int remainToRead = n - bufferRemainSize;
			// 若要跳过的还剩很多，直接跳过，否则先读入缓存再跳过
			if (remainToRead >= buffer.length) {
				int readSize = raf.skipBytes(remainToRead);
//				System.out.println("I/O skip ---");
				pointer = 0;
				validBound = 0;
				return bufferRemainSize + readSize;
			} else {
				int readSize = raf.read(buffer);
//				System.out.println("I/O read ---");

				if (readSize >= remainToRead) {
					validBound = readSize;
					pointer = remainToRead;
					return bufferRemainSize + remainToRead;
				} else {
					pointer = 0;
					validBound = 0;
					return bufferRemainSize + readSize;
				}
			}
		}
	}

	public void seek(long pos) throws IOException {
		long min = raf.getFilePointer() - validBound;
		long max = raf.getFilePointer();
		if (pos >= min && pos < max) {
			pointer = (int) (pos - min);
		} else {
			raf.seek(pos);
			pointer = 0;
			validBound = 0;
		}
	}

	public void close() throws IOException {
		raf.close();
	}

	public long getFilePointer() throws IOException {
		return raf.getFilePointer() - validBound + pointer;
	}

}
