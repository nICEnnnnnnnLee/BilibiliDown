package nicelee.memory.url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class MemoryURLHandler extends URLStreamHandler {

	private static Map<String, byte[]> memory = new LinkedHashMap<String, byte[]>();

	private static boolean allowAddSource = true;

	public static boolean addSource(String identifier, byte[] data) {
		if (allowAddSource) {
			if (memory.containsKey(identifier)) {
				return false;
			} else {
				memory.put(identifier, data);
				return true;
			}
		}
		return false;
	}

	public static void lockMemory() {
		allowAddSource = false;
	}

	public static boolean memoryIsLocked() {
		return !allowAddSource;
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new MemoryURLConnection(u);
	}

	class MemoryURLConnection extends URLConnection {

		protected MemoryURLConnection(URL url) {
			super(url);
		}

		@Override
		public void connect() throws IOException {
//			System.out.println("---MemoryURLConnection connect---");
		}

		@Override
		public String getContentType() {
			if (url.getPath().endsWith(".html")) {
				return "text/html; charset=UTF-8";
			}
			return null;
//	        return guessContentTypeFromName(url.getPath());
		}

		@Override
		public InputStream getInputStream() throws IOException {
			// mem://file/a/b/c.txt -> /a/b/c.txt
			// mem://search/a/b -> /a/b
			String pathInJar = url.getPath().substring(1);
			boolean comparePrefix = "search".equals(url.getAuthority());
			for (byte[] resource : memory.values()) {
				ByteArrayInputStream in = new ByteArrayInputStream(resource);
				JarInputStream jin = new JarInputStream(in, false);
				JarEntry entry = jin.getNextJarEntry();
				while (entry != null) {
					if ((!comparePrefix && entry.getName().equals(pathInJar))
							|| (comparePrefix && entry.getName().startsWith(pathInJar))) {
						return jin;
					}
					entry = jin.getNextJarEntry();
				}
				jin.close();
			}
			throw new IOException("No such resource in memory: " + pathInJar);
		}

		public OutputStream getOutputStream() throws IOException {
			throw new RuntimeException("Not Implemented");
		}
	}
}
