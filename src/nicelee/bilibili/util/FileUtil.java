package nicelee.bilibili.util;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUtil {

	
	public static void copy(File source, File dest) {
		try {
			RandomAccessFile rSource = new RandomAccessFile(source, "r");
			RandomAccessFile rDest = new RandomAccessFile(dest, "rw");
			
			byte[] buffer = new byte[1024*1024];
			int size = rSource.read(buffer);
			while(size != -1) {
				rDest.write(buffer, 0, size);
				size = rSource.read(buffer);
			}
			rSource.close();
			rDest.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
