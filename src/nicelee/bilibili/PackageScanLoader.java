package nicelee.bilibili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.annotations.Controller;
import nicelee.bilibili.plugin.CustomClassLoader;
import nicelee.bilibili.plugin.Plugin;
import nicelee.bilibili.util.ResourcesUtil;

public abstract class PackageScanLoader {

	private ClassLoader classLoader;
	private List<Class<?>> validClazzList;
	
	public static List<Class<?>> validPusherClasses;
	public static List<Class<?>> validParserClasses;
	public static List<Class<?>> validDownloaderClasses;
	public static List<java.lang.Class<?>> controllerClazzes;
	static {
		validPusherClasses = new ArrayList<Class<?>>();
		validParserClasses = new ArrayList<Class<?>>();
		validDownloaderClasses = new ArrayList<Class<?>>();
		// 扫描parsers文件夹，加载自定义类名
		Plugin parserPlg = new Plugin("parsers", "nicelee.bilibili.parsers.impl");
		CustomClassLoader ccloader = new CustomClassLoader();
		File parserFolder = new File(ResourcesUtil.baseDirectory(), "parsers");
		// 如果parsers.ini存在, 逐行读取类名, 按照顺序进行扫描
		// 这是为了在jar包里的类加载生效之前使用, 替换原来的功能
		// 大多数情况下不需要用到
		File parserInit = new File(parserFolder, "parsers.ini");
		loadTargetFolder(parserPlg, ccloader, parserFolder, parserInit);
		
		// 扫描pushers文件夹，加载自定义类名
		Plugin pusherPlg = new Plugin("pushers", "nicelee.bilibili.pushers.impl");
		File pusherFolder = new File(ResourcesUtil.baseDirectory(), "pushers");
		File pusherInit = new File(pusherFolder, "pushers.ini");
		loadTargetFolder(pusherPlg, ccloader, pusherFolder, pusherInit);
		
		// 扫描包，加载 parser 类、downloader类、pusher类
		PackageScanLoader pLoader = new PackageScanLoader() {
			@Override
			public boolean isValid(Class<?> klass) {
				Bilibili bili = klass.getAnnotation(Bilibili.class);
				if (null != bili) {
					if("parser".equals(bili.type())){
						validParserClasses.add(klass);
					}else if("downloader".equals(bili.type())){
						validDownloaderClasses.add(klass);
					}else if("pusher".equals(bili.type())){
						validPusherClasses.add(klass);
					}
				}
				return false;
			}
		};
		pLoader.scanRoot("nicelee.bilibili");
		// 按权重排序,越大越优先
		Comparator<Class<?>> comparator = new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				int bili1 = o1 == null? 0 : o1.getAnnotation(Bilibili.class).weight();
				int bili2 = o2 == null? 0 : o2.getAnnotation(Bilibili.class).weight();
				return bili2 - bili1;
			}
		};
		Collections.sort(validPusherClasses, comparator);
		Collections.sort(validParserClasses, comparator);
		Collections.sort(validDownloaderClasses, comparator);

		// 扫描包，加载 controller 类
		controllerClazzes = new ArrayList<java.lang.Class<?>>();
		pLoader = new PackageScanLoader() {
			@Override
			public boolean isValid(java.lang.Class<?> clazz) {
				if (clazz.getAnnotation(Controller.class) != null) {
					// System.out.println(clazz.getName());
					controllerClazzes.add(clazz);
				}
				return true;
			}
		};
		pLoader.scanRoot("nicelee.server.controller");
	}

	private static void loadTargetFolder(Plugin plug, CustomClassLoader ccloader, File jFileFolder,
			File initConfFile) {
		if(initConfFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(initConfFile), "utf-8"));
				String clazzName = reader.readLine();
				while(clazzName != null) {
					compileAndLoad(plug, ccloader, clazzName);
					clazzName = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(jFileFolder.exists()){
			// 遍历文件进行扫描
			for(File file: jFileFolder.listFiles()) {
				String fileName = file.getName();
				if(fileName.endsWith(".java")) {
					String clazzName = fileName.substring(0, fileName.length() - 5);
					compileAndLoad(plug, ccloader, clazzName);
				}
			}
		}
	}

	/**
	 *  编译并加载指定类
	 * @param parserPlg
	 * @param ccloader
	 * @param clazzName
	 */
	private static void compileAndLoad(Plugin parserPlg, CustomClassLoader ccloader, String clazzName) {
		// 编译类
		if(parserPlg.isToCompile(clazzName)) {
			parserPlg.compile(clazzName);
		}
		try {
			// 加载类
			System.out.printf("尝试加载自定义类: %s\r\n", clazzName);
			Class<?> klass = parserPlg.loadClass(ccloader, clazzName);
			Bilibili bili = klass.getAnnotation(Bilibili.class);
			if (null != bili) {
				if("parser".equals(bili.type())){
					validParserClasses.add(klass);
				}else if("pusher".equals(bili.type())){
					validPusherClasses.add(klass);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Class 类型是否符合预期，如果是，则加入列表
	 * 
	 * @param klass
	 * @return
	 */
	public abstract boolean isValid(Class<?> klass);

	public List<Class<?>> scanRoot(String packNameWithDot) {
		validClazzList = new ArrayList<Class<?>>();
		classLoader = this.getClass().getClassLoader();
		String packNameWithFileSep = packNameWithDot.replace("\\", "/").replace(".", "/");
		packNameWithDot = packNameWithDot.replace("/", ".");

		try {
			Enumeration<URL> url = classLoader.getResources(packNameWithFileSep);
			while (url.hasMoreElements()) {
				URL currentUrl = url.nextElement();
				String type = currentUrl.getProtocol();
				if (type.equals("jar")) { // jar 包
					dealWithJar(currentUrl, packNameWithFileSep);
				} else if (type.equals("file")) { // file
					File file = new File(currentUrl.toURI());
					if (file.isDirectory()) { // 目录
						dealWithFolder(packNameWithDot, file);
					} else if (file.getName().endsWith(".class")) {
						deaWithJavaClazzFile(packNameWithDot, file);
					}
				} else if (type.equals("mem")) {
					dealWithMemoryJar(currentUrl, packNameWithFileSep);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return validClazzList;
	}

	// 处理目录文件
	private void dealWithFolder(String packNameWithDot, File file) {
		if (file.exists()) {
			// file一定是目录型文件所以得到该目录下所有文件遍历它们
			File[] files = file.listFiles();
			for (File childfile : files) {
				// 如果子文件是目录，则递归处理，调用本方法递归。
				if (childfile.isDirectory()) {
					// 注意递归时候包名字要加上".文件名"后为新的包名
					// 因为后面反射时需要类名，也就是com.mec.***
					dealWithFolder(packNameWithDot + "." + childfile.getName(), childfile);
				} else {
					// 如果该文件不是目录。
					String name = childfile.getName();
					// 该文件是class类型
					if (name.contains(".class")) {
						deaWithJavaClazzFile(packNameWithDot, childfile);
					} else {
						continue;
					}
				}
			}
		} else {
			return;
		}
	}

	private void deaWithJavaClazzFile(String packNameWithDot, File file) {
		int index = file.getName().lastIndexOf(".class");
		String filename = file.getName().substring(0, index);
		Class<?> klass = null;
		try {
			klass = Class.forName(packNameWithDot + "." + filename);
			if (isValid(klass)) {
				validClazzList.add(klass);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 处理jar包类型
	private void dealWithJar(URL url, String packNameWithFileSep) {
		JarURLConnection jarURLConnection;
		try {
			jarURLConnection = (JarURLConnection) url.openConnection();
			JarFile jarFile = jarURLConnection.getJarFile();
			Enumeration<JarEntry> jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jar = jarEntries.nextElement();
				if (jar.isDirectory() || !jar.getName().endsWith(".class") || !jar.getName().startsWith(packNameWithFileSep)) {
					continue;
				}
				// 处理class类型
				String jarName = jar.getName();
				int dotIndex = jarName.indexOf(".class");
				String className = jarName.substring(0, dotIndex).replace("/", ".");
				Class<?> klass = Class.forName(className);
				if (isValid(klass)) {
					validClazzList.add(klass);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	// 处理自定义内存加载类型
	private void dealWithMemoryJar(URL url, String packNameWithFileSep) {
		try {
			JarInputStream jin = (JarInputStream) url.openStream();
			JarEntry entry = jin.getNextJarEntry();
			while (entry != null) {
				if (entry.isDirectory() || !entry.getName().endsWith(".class") || !entry.getName().startsWith(packNameWithFileSep)) {
					entry = jin.getNextJarEntry();
					continue;
				}
				// 处理class类型
				String jarName = entry.getName();
				int dotIndex = jarName.indexOf(".class");
				String className = jarName.substring(0, dotIndex).replace("/", ".");
				Class<?> klass = Class.forName(className, true, classLoader);
				if (isValid(klass)) {
					validClazzList.add(klass);
				}
				entry = jin.getNextJarEntry();
			}
			jin.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
