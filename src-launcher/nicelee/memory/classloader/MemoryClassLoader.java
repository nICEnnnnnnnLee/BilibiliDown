package nicelee.memory.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import nicelee.memory.url.MemoryURLHandler;

public class MemoryClassLoader extends ClassLoader {

	private static boolean DEBUG = false;
//	private static boolean DEBUGLOADCLASS = false;
//	@SuppressWarnings("rawtypes")
//	private static ConcurrentHashMap<String, Class> cache = new ConcurrentHashMap<String, Class>();

	final private ProtectionDomain defaultPd;

	public MemoryClassLoader() {
		this(MemoryClassLoader.class.getProtectionDomain());
	}

	public MemoryClassLoader(ProtectionDomain defaultPd) {
		this.defaultPd = defaultPd;
	}

	@Override
	protected URL findResource(String sName) {
		if (DEBUG)
			System.out.printf("findResource: %s%n", sName);
		try {
			URL u = this.getParent().getResource(sName);
			if (u == null) {
				if (DEBUG)
					System.out.printf("try findResource from mem: %s%n", sName);
				String url = "mem://file/" + sName;
				u = new URL(null, url, new MemoryURLHandler());
			}
			return u;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	protected Enumeration<URL> findResources(String sName) throws IOException {
		List<URL> urls = new ArrayList<>();
		try {
			String url = "mem://search/" + sName;
			urls.add(new URL(null, url, new MemoryURLHandler()));
		} catch (MalformedURLException e) {
		}
		Enumeration<URL> enums = this.getParent().getResources(sName);
		while (enums.hasMoreElements()) {
			urls.add(enums.nextElement());
		}
		return Collections.enumeration(urls);
	}

	@Override
	protected String findLibrary(String libname) {
		throw new RuntimeException("Not Implemented");
	}

	// 没有必要重写 loadClass
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			return findMemoryClass(name);
		} catch (IOException e) {
			throw new ClassNotFoundException("No such class in memory", e);
		}
	}

//	@SuppressWarnings("rawtypes")
//	@Override
//	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//		if (DEBUG)
//			System.out.println("DEBUG:" + this + ".loadClass(" + name + ")");
//		Class c = null;
//		try {
//			c = Class.forName(name); // can it be loaded by normal means? and initialized?
//			return c;
//		} catch (Exception e) {
//			if (DEBUGLOADCLASS) {
//				System.out.println("DEBUG:" + this + ".loadClass Class.forName(" + name + ") exception " + e);
//				// e.printStackTrace();
//			}
//		}
//		try {
//			c = findSystemClass(name);
//		} catch (Exception e) {
//			if (DEBUGLOADCLASS) {
//				System.out.println("DEBUG:" + this + ".loadClass findSystemClass(" + name + ") exception " + e);
//				// e.printStackTrace();
//			}
//		}
//		if (c == null) {
//			c = cache.get(name);
//		} else {
//			if (DEBUG)
//				System.out.println("DEBUG:" + this + ".loadClass exit found sys class " + name + " resolve=" + resolve);
//			return c;
//		}
//		if (c == null) {
//			c = findLoadedClass(name);
//		} else {
//			if (DEBUG)
//				System.out.println(
//						"DBUG:" + this + ".loadClass exit cache hit:" + c + " for " + name + " resolve=" + resolve);
//			return c;
//		}
//		// this is our last chance, otherwise noClassDefFoundErr and we're screwed
//		if (c == null) {
//			try {
//				c = findMemoryClass(name);
//				if (DEBUG)
//					System.out.println("DEBUG:" + this + " Putting class " + name + " of class " + c + " to cache");
//				cache.put(name, c);
//			} catch (IOException e) {
//				e.printStackTrace();
//				if (e.getCause() == null) {
//					System.out.printf("Not found %s in JAR by %s: %s%n", name, getClass().getName(), e.getMessage());
//				} else {
//					System.out.printf("Error loading %s in JAR by %s: %s%n", name, getClass().getName(), e.getCause());
//				}
//				// end of the line
//				throw new ClassNotFoundException(
//						"The requested class: " + name + " can not be found on any resource path");
//			}
//		} else {
//			if (DEBUG)
//				System.out.println("DEBUG:" + this + ".loadClass exit found loaded " + name + " resolve=" + resolve);
//			return c;
//		}
//		if (resolve)
//			resolveClass(c);
//		if (DEBUG)
//			System.out.println("DEBUG:" + this + ".loadClass exit resolved " + name + " resolve=" + resolve);
//		return c;
//	}

	private Class<?> findMemoryClass(String sClassName) throws IOException {
		String url = "mem://file/" + sClassName.replace('.', '/') + ".class";
		byte[] bClass = readAllBytes(new URL(null, url, new MemoryURLHandler()));
		Class<?> c = defineClass(sClassName, bClass, 0, bClass.length, defaultPd);
		return c;
	}

	private byte[] readAllBytes(URL url) throws IOException {
		InputStream in = url.openStream();
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
