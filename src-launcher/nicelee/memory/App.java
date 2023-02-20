package nicelee.memory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import nicelee.memory.classloader.MemoryClassLoader;
import nicelee.memory.url.MemoryURLHandler;

public class App {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		final ProtectionDomain pd = App.class.getProtectionDomain();
		final File launchJarFile = new File(pd.getCodeSource().getLocation().getPath());
		final File coreJarFile = new File(launchJarFile.getParentFile(), "INeedBiliAV.jar");
//		final File coreJarFile = new File("D:\\Workspace\\javaweb-springboot\\Demo\\test\\INeedBiliAV.jar");
//		final File coreJarFile = new File("D:\\Workspace\\javaweb-springboot\\Demo\\JarClassLoader-main\\INeedBiliAV.jar");
//		final File coreJarFile = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\INeedBiliAV.jar");

		MemoryURLHandler.addSource("INeedBiliAV.jar", readAllBytes(coreJarFile));
		MemoryURLHandler.lockMemory();

		System.out.println(pd.getCodeSource().getLocation().getPath());
		MemoryClassLoader mcl = new MemoryClassLoader(pd);
		try {
			Class<?> clazz = mcl.loadClass("nicelee.ui.FrameMain");
			Method method = clazz.getMethod("main", new Class<?>[] { String[].class });
			method.invoke(null, (Object) args);
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private static byte[] readAllBytes(File f) throws IOException {
		InputStream in = new FileInputStream(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = in.read(buf);
		while (len > 0) {
			out.write(buf, 0, len);
			len = in.read(buf);
		}
		in.close();
		return out.toByteArray();
	}

}
