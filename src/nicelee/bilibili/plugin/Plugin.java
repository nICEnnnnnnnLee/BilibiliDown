package nicelee.bilibili.plugin;

import java.io.File;
import java.io.IOException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import nicelee.bilibili.util.ResourcesUtil;

public class Plugin {

	File workingDir;
	String packageName;
	
	public Plugin() {
		workingDir = new File(ResourcesUtil.baseDirectory(), "./parsers");
		packageName = "nicelee.bilibili.parsers.impl";
	}

	public Plugin(String workingDir, String packageName) {
		this.setEnv(workingDir);
		this.packageName = packageName;
	}

	private void setEnv(String workingDirectory) {
		workingDir = new File(ResourcesUtil.baseDirectory(), workingDirectory);
	}

	/**
	 * 是否需要编译相应类
	 * @param clazzName 不带.java, 不带全路径的类名
	 * @return 是否需要编译
	 */
	public boolean isToCompile(String clazzName) {
		File classF = new File(workingDir, clazzName + ".class");
		if(classF.exists()) {
			long timeLastModifiedClass = classF.lastModified();
			File javaF = new File(workingDir, clazzName + ".java");
			long timeLastModifiedJava = javaF.lastModified();
			if(timeLastModifiedClass > timeLastModifiedJava)
				return false;
			else
				return true;
		}else {
			return true;
		}
	}

	/**
	 * 编译相应类
	 * @param clazzName 不带.java, 不带全路径的类名
	 * @return 编译是否成功
	 */
	public boolean compile(String clazzName) {
		try {
			File file = new File(workingDir, clazzName + ".java");
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			String classpath = System.getProperty("java.class.path", "");
			classpath = ResourcesUtil.sourceOf("INeedBiliAV.jar").getCanonicalPath() 
					+ File.pathSeparatorChar + classpath;
			int result = compiler.run(null, null, null, "-cp", classpath, 
					"-encoding", "UTF-8", file.getCanonicalPath());
			return result == 0;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 加载类
	 * @param ccloader 自定义的类加载器
	 * @param clazzName clazzName 不带.java, 不带全路径的类名
	 * @return 加载类
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public Class<?> loadClass(CustomClassLoader ccloader, String clazzName) throws IOException, ClassNotFoundException {
		try {
			File f = new File(workingDir, clazzName + ".class");
			return ccloader.findClass(f.getCanonicalPath(), packageName + "." + clazzName);
		} catch (NoClassDefFoundError e) {
			String classNotFound = e.getMessage();
			classNotFound = classNotFound.substring(classNotFound.lastIndexOf("/") + 1);
			System.out.printf("尝试加载未找到的类： %s\r\n", classNotFound);
			loadClass(ccloader, classNotFound);
			return loadClass(ccloader, clazzName);
		} catch (LinkageError e) {
			System.err.printf("加载时出现状况: %s \r\n" ,e.getMessage());
			return ccloader.findClass(packageName + "." + clazzName);
		}
	}
}
