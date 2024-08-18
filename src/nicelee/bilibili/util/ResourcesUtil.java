package nicelee.bilibili.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Random;

import nicelee.bilibili.model.ClipInfo;

public class ResourcesUtil {

	final static String dataDirPath;
	final static boolean isJarLaunch;
	static {
		String tmpdataDirPath = System.getProperty("bilibili.prop.dataDirPath");
		try {
			if (tmpdataDirPath != null) {
				Logger.println(tmpdataDirPath);
				File tmpFileDir = new File(tmpdataDirPath);
				tmpdataDirPath = tmpFileDir.getCanonicalPath();
				if (tmpFileDir.exists()) {
					Logger.println("指定数据目录：" + tmpdataDirPath);
				} else {
					Logger.println("目录不存在：" + tmpdataDirPath);
					tmpdataDirPath = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tmpdataDirPath = null;
		}
		dataDirPath = tmpdataDirPath;

		String[] mainCommand = System.getProperty("sun.java.command", "").split(" ");
		boolean tmpIsJarLaunch = false;
		StringBuilder sb = new StringBuilder();
		// 考虑路径中包含有空格的命令行
		for (String cmd : mainCommand) {
			if (cmd.endsWith(".jar")) {
				sb.append(cmd);
				String jarPath = sb.toString();
				Logger.println(jarPath);
				if (new File(jarPath).exists()) {
					tmpIsJarLaunch = true;
				}
				break;
			} else {
				sb.append(cmd).append(" ");
			}
		}
		isJarLaunch = tmpIsJarLaunch;
		// isJarLaunch =
		// System.getProperty("java.class.path").startsWith("INeedBiliAV.jar");
	}

	public static void write(File f, String content) {
		try {
			f.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(f);
			out.write(content.getBytes("utf-8"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readAll(File f) {
		try {
			FileInputStream in = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int len = 0; (len = in.read(buffer)) != -1;) {
				out.write(buffer, 0, len);
			}
			in.close();
			byte[] bytes = out.toByteArray();
			return new String(bytes, "utf-8");
		} catch (IOException e) {
			return null;
		}
	}

	public static String random(int length, String alphabet, long seed) {
		StringBuilder sb = new StringBuilder(length);
		Random random = new Random(seed);
		for (int j = 0; j < length; j++) {
			int m = random.nextInt(alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static String randomInt(int i) {
		StringBuilder sb = new StringBuilder(i);
		String alphabet = "0123456789";
		for (int j = 0; j < i; j++) {
			int m = (int) (Math.random() * alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static String randomLower(int i) {
		StringBuilder sb = new StringBuilder(i);
		String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
		for (int j = 0; j < i; j++) {
			int m = (int) (Math.random() * alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static String randomHex(int i) {
		StringBuilder sb = new StringBuilder(i);
		String alphabet = "ABCDEF0123456789";
		for (int j = 0; j < i; j++) {
			int m = (int) (Math.random() * alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static String randomUpper(int i) {
		StringBuilder sb = new StringBuilder(i);
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		for (int j = 0; j < i; j++) {
			int m = (int) (Math.random() * alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static String random(int i) {
		StringBuilder sb = new StringBuilder(i);
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		for (int j = 0; j < i; j++) {
			int m = (int) (Math.random() * alphabet.length());
			sb.append(alphabet.charAt(m));
		}
		return sb.toString();
	}

	public static void copy(File source, File dest) {
		try {
			RandomAccessFile rSource = new RandomAccessFile(source, "r");
			RandomAccessFile rDest = new RandomAccessFile(dest, "rw");

			byte[] buffer = new byte[1024 * 1024];
			int size = rSource.read(buffer);
			while (size != -1) {
				rDest.write(buffer, 0, size);
				size = rSource.read(buffer);
			}
			rSource.close();
			rDest.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File search(String relativePath) {
		File file = new File(baseDirectory(), relativePath);
		if (file.exists())
			return file;
		return null;
	}

	public static File sourceOf(String relativePath) {
		File file = new File(baseDirectory(), relativePath);
		if (!file.exists())
			file.getParentFile().mkdir();
		return file;
	}

	static String cacheBaseDir;
	static File cacheBaseDirFile;

	public static String resolve(String path) {
		if (!Paths.get(path).isAbsolute()) {
			try {
				File f = new File(baseDirectory(), path);
				return f.getCanonicalPath();
			} catch (IOException e) {
				return path;
			}
		}
		return path;
	}

	public static File baseDirFile() {
		if (cacheBaseDirFile == null)
			cacheBaseDirFile = new File(baseDirectory());
		return cacheBaseDirFile;
	}

	public static String baseDirectory() {
		if (cacheBaseDir == null) {
			// 由JVM传入参数 -Dbilibili.prop.dataDirPath={dataDirPath} 指定位置
			if (dataDirPath != null) {
				cacheBaseDir = dataDirPath;
				return cacheBaseDir;
			}
			if (isJarLaunch) {
				try {
					String path = ClassLoader.getSystemResource("").getPath();
					if (path == null || "".equals(path))
						cacheBaseDir = getProjectPath();
					else
						cacheBaseDir = java.net.URLDecoder.decode(path, "UTF-8");
				} catch (Exception ignored) {
					cacheBaseDir = getProjectPath();
				}
			} else {
				cacheBaseDir = System.getProperty("user.dir", "");
			}
		}
		return cacheBaseDir;
	}

	public static String canonicalPath(String path) {
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			return path;
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

	public static String detailsOfException(Throwable e) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
			pw.write("\n");
			e.printStackTrace(pw);
			pw.write("\n");
			String s = sw.toString();
			return s;
		} catch (IOException e1) {
			return e.getMessage();
		}
	}

	public static boolean isPicture(String avId) {
		return avId.startsWith("h") || avId.startsWith("cv") || avId.startsWith("opus");
	}

	public static boolean isPicture(ClipInfo clip) {
		return isPicture(clip.getAvId());
	}
}
