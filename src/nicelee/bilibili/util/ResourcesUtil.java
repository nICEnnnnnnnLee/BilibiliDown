package nicelee.bilibili.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResourcesUtil {

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
	
	public static File search(String path) {
		File file = new File(path);
		if (file.exists())
			return file;
		System.out.printf("%s 路径不存在, 尝试以程序目录为基址进行查找\n", path);
		file = new File(baseDirectory(), path);
		if (file.exists())
			return file;
		return null;
	}

	public static String baseDirectory() {
		try {
			String path = ClassLoader.getSystemResource("").getPath();
			if (path == null || "".equals(path))
				return getProjectPath();
			return path;
		} catch (Exception ignored) {
			return getProjectPath();
		}
	}

	private static String getProjectPath() {
		java.net.URL url = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) {
			int lastIndex = filePath.lastIndexOf("/");
			if (lastIndex > -1) {
				filePath = filePath.substring(0, lastIndex + 1);
			} else {
				filePath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
			}
		}
		File file = new File(filePath);
		filePath = file.getAbsolutePath();
		return filePath;
	}

	public static void close(Object resource) throws IOException, NoSuchMethodException, SecurityException {
		if (resource == null)
			return;
		if (resource instanceof Closeable) {
			((Closeable) resource).close();
		} else {
			System.err.println(resource.getClass().getName() + ": 尝试利用反射调用close()方法");
			try {
				Method method = resource.getClass().getDeclaredMethod("close");
				try {
					method.invoke(resource);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException e1) {
				Method method = resource.getClass().getMethod("close");
				try {
					method.invoke(resource);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void closeQuietly(Object resource) {
		try {
			close(resource);
		} catch (NoSuchMethodException | SecurityException | IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
