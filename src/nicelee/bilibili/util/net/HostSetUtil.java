package nicelee.bilibili.util.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.util.ResourcesUtil;

/**
 * 只适用于JDK 1.8, 而且GitHub在使用特定host后会阻断一定时间，需要不断改变host文件才能保证的连通性，该解决方案不具备普适性
 *
 */
@Deprecated
public class HostSetUtil {

	private HostSetUtil() {
	}

	private static HashMap<String, String> _hostMap;

	private static void updateHosts(String host, String ip) {
		if (_hostMap == null) {
			_hostMap = new HashMap<>();
		}
		_hostMap.put(host, ip);
	}

	public static void injectHosts() {
		try {
			Field field = InetAddress.class.getDeclaredField("addressCache");
			field.setAccessible(true);
			Object addressCache = field.get(null);

			// 获取Map
			Field cacheMapField = addressCache.getClass().getDeclaredField("cache");
			cacheMapField.setAccessible(true);
			Map cacheMap = (Map) cacheMapField.get(addressCache);

			// 获取CacheEntry(内部类) Map 的 Value
			Class<?> cls = Class.forName("java.net.InetAddress$CacheEntry");
			Constructor<?> constructor = cls.getDeclaredConstructor(InetAddress[].class, long.class);
			constructor.setAccessible(true);

			// 设置host(永不过期)
			synchronized (addressCache) {
				if (_hostMap != null) {
					for (Entry<String, String> entry : _hostMap.entrySet()) {
						InetAddress[] ipAddr = getInetAddr(entry.getValue());
						Object value = constructor.newInstance(ipAddr, -1);
						cacheMap.put(entry.getKey(), value);
					}
				}
			}

			// 测试
			//InetAddress addresses = InetAddress.getByName("api.github.com");
			//System.out.println(addresses.getHostAddress());
			
			// 还原可见性
			constructor.setAccessible(false);
			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InetAddress[] getInetAddr(String ip) throws UnknownHostException {
		InetAddress[] ipAddr = new InetAddress[1];

		String[] ipStr = ip.split("\\.");
		byte[] ipBuf = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
		}
		ipAddr[0] = InetAddress.getByAddress(ipBuf);
		return ipAddr;
	}

	final static Pattern patternConfig = Pattern.compile("^[ ]*([0-9.]+)[ ]+(.+$)");

	public static boolean readHostsFromFile(String file) {
		BufferedReader buReader = null;
		File configFile;
		try {
			configFile = new File(file);
			if (!configFile.exists()) {
				System.out.println("Hosts文件不存在： " + configFile.getCanonicalPath());
				configFile = new File(ResourcesUtil.baseDirectory(), file);
				System.out.println("尝试路径： " + configFile.getCanonicalPath());
			}
			buReader = new BufferedReader(new FileReader(configFile));
			String config;
			System.out.println("---Hosts start--- ");
			while ((config = buReader.readLine()) != null) {
				Matcher matcher = patternConfig.matcher(config);
				if (matcher.find()) {
					updateHosts(matcher.group(2), matcher.group(1));
					System.out.printf("  %s --> %s\r\n", matcher.group(2), matcher.group(1));
				}
			}
			System.out.println("---Hosts end--- ");
			return true;
		} catch (IOException e) {
			System.out.println("Hosts文件不存在! ");
			// e.printStackTrace();
			return false;
		} finally {
			try {
				buReader.close();
			} catch (Exception e) {
			}
		}
	}
}
