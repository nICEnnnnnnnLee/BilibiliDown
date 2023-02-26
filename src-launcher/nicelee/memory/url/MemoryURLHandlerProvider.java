package nicelee.memory.url;

public class MemoryURLHandlerProvider {
}
// java 8 不支持 URLStreamHandlerProvider

//import java.net.URLStreamHandler;
//import java.net.spi.URLStreamHandlerProvider;
//
//public class MemoryURLHandlerProvider extends URLStreamHandlerProvider {
//
//	@Override
//	public URLStreamHandler createURLStreamHandler(String protocol) {
//		if ("mem".equals(protocol)) {
//			return new MemoryURLHandler();
//		}
//		return null;
//	}
//}
