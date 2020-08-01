package nicelee.bilibili.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResourcesUtil {

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
