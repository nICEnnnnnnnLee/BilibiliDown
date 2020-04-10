import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompileJarUtil {

	public static void main(String[] args) {

//		args = new String[] { "D:\\Workspace\\javaweb-springboot\\BilibiliDown\\", "nicelee.ui.FrameMain" };
		File rootFolder = new File(args[0]);
		String mainClass = args[1];

		File source = new File(rootFolder, "src");
		File libs = new File(rootFolder, "libs");
		File dst = new File(rootFolder, "targets");
		File jar = new File(rootFolder, "INeedBiliAV.jar");
		compileFolder(source, dst, libs, dst);
		compileLibs(libs, dst);
		jar(dst, jar, mainClass);
	}

	static void compileLibs(File libs, File dst) {
		if (!dst.exists()) {
			dst.mkdirs();
		}
		for (File file : libs.listFiles()) {
			if (file.getName().endsWith(".jar")) {
				unJar(file, dst);
			}
		}
	}

	static void compileFolder(File source, File dst, File libs, File root) {
		if (!dst.exists()) {
			dst.mkdirs();
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(new File(root.getParentFile(), "src").getCanonicalPath());
			for (File file : libs.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					sb.append(File.pathSeparator).append(file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String classpath = sb.toString();
		for (File file : source.listFiles()) {
			if (file.isDirectory()) {
				if(!file.getName().startsWith("test")) {
					File newDst = new File(dst, file.getName());
					compileFolder(file, newDst, libs, root);
				}
			} else {
				if (file.getName().endsWith(".java")) {
					if(!file.getName().equals("FrameMain.java") && !file.getParentFile().getName().equals("impl")) {
						continue;
					}
					System.out.println(file.getName());
					System.out.println(classpath);
					try {
						compiler.run(null, null, null, "-classpath", classpath, "-encoding", "UTF-8", "-d",
//						compiler.run(null, null, null,  "-encoding", "UTF-8", "-d",
								root.getCanonicalPath(), file.getCanonicalPath());
					} catch (IOException e) {
						// e.printStackTrace();
					}
				} else {
					copy(file, new File(dst, file.getName()));
				}
			}
		}
	}

	static void copy(File source, File dest) {
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

	/**
	 * 解压文件
	 * 
	 * @param jarFile 解压文件
	 * @param dst     输出解压文件路径
	 */
	public static void unJar(File jarFile, File dst) {
		byte[] buffer = new byte[1024];

		if (!dst.exists()) {
			dst.mkdir();
		}
		try {
			JarInputStream zis = new JarInputStream(new FileInputStream(jarFile));
			JarEntry ze = zis.getNextJarEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(dst, fileName);
				// System.out.println("file unzip : "+ newFile.getAbsoluteFile());
				if (ze.isDirectory()) {
					newFile.mkdirs();
				} else {
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				ze = zis.getNextJarEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param sourceFile 源文件路径
	 * @param jarFile    压缩后文件
	 * @param mainClass  入口类
	 */
	public static void jar(File sourceFile, File jarFile, String mainClass) {
		Manifest manifest = new Manifest();
		Attributes attrs = manifest.getMainAttributes();
		attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		attrs.put(Attributes.Name.CLASS_PATH, ".");
		attrs.put(Attributes.Name.MAIN_CLASS, mainClass);

		try (JarOutputStream zos = new JarOutputStream(new FileOutputStream(jarFile, false), manifest)) {
			for (File file : sourceFile.listFiles()) {
				writeJar(file, "", zos);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 遍历所有文件，压缩
	 * <p>
	 * 目录名称必须以“/”斜杠结尾。
	 * </p>
	 * <p>
	 * 路径必须使用'/'斜杠，而不是'\'
	 * </p>
	 * <p>
	 * 条目不能以“/”斜杠开头。
	 * </p>
	 *
	 * @param file       源文件目录
	 * @param parentPath 压缩文件目录
	 * @param zos        文件流
	 */
	public static void writeJar(File file, String parentPath, JarOutputStream zos) {
		if (file.isDirectory()) {
			// 目录
			parentPath += file.getName() + "/";
			JarEntry zipEntry = new JarEntry(parentPath);
			// System.out.println(parentPath);
			try {
				zos.putNextEntry(zipEntry);
			} catch (IOException e) {
				e.printStackTrace();
			}
			File[] files = file.listFiles();
			for (File f : files) {
				writeJar(f, parentPath, zos);
			}
		} else {
			// 文件
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
				// 指定jar文件夹
				JarEntry zipEntry = new JarEntry(parentPath + file.getName());
				// System.out.println(parentPath + file.getName());
				zos.putNextEntry(zipEntry);
				int len;
				byte[] buffer = new byte[1024 * 10];
				while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
					zos.write(buffer, 0, len);
					zos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e.getCause());
			}
		}
	}
}
