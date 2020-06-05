package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
//import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.Global;

public class ConfigUtil {
	final static Pattern patternConfig = Pattern.compile("^[ ]*([0-9|a-z|A-Z|.|_]+)[ ]*=[ ]*([^ ]+.*$)");

	/**
	 * 根据.lock文件判断，程序是否在运行
	 * @return true/false
	 */
	public static boolean isRunning() {
		File lockFile = new File(baseDirectory(), "config/.lock");
		try {
			System.out.println(lockFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lockFile.isFile();
	}
	
	public static void createLock() {
		File configDir = new File(baseDirectory(), "config");
		if(!configDir.exists()) 
			configDir.mkdirs();
		File lockFile = new File(configDir, ".lock");
		try {
			lockFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteLock() {
		File lockFile = new File(baseDirectory(), "config/.lock");
		lockFile.delete();
	}
	
	public static void deleteLockOnExit() {
		File lockFile = new File(baseDirectory(), "config/.lock");
		lockFile.deleteOnExit();
	}
	
	public static void initConfigs() {
		// 先初始化默认值
		BufferedReader buReader = null;
		try {
			InputStream in = ConfigUtil.class.getResourceAsStream("/resources/app.config");
			buReader = new BufferedReader(new InputStreamReader(in));
			String config;
			while ((config = buReader.readLine()) != null) {
				Matcher matcher = patternConfig.matcher(config);
				if (matcher.find()) {
					System.setProperty(matcher.group(1), matcher.group(2).trim());
					// System.out.printf(" key-->value: %s --> %s\r\n", matcher.group(1),
					// matcher.group(2));
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 从配置文件读取
		buReader = null;
		System.out.println("----Config init begin...----");
		readConfig("config/app.config");
		readConfig("config/user.config");
		System.out.println("----Config ini end...----");
		//批量下载设置相关
		Global.menu_plan = Integer.parseInt(System.getProperty("bilibili.menu.download.plan"));
		Global.menu_qn = System.getProperty("bilibili.menu.download.qn");
		Global.tab_qn = System.getProperty("bilibili.tab.download.qn");
		//下载设置相关
		int fixPool = Integer.parseInt(System.getProperty("bilibili.download.poolSize"));
		Global.downLoadThreadPool = Executors.newFixedThreadPool(fixPool);
		Global.downloadFormat = Integer.parseInt(System.getProperty("bilibili.format"));
		String savePath = System.getProperty("bilibili.savePath");
		if(savePath.endsWith("\\")) {
			savePath = savePath.substring(0, savePath.length()-1) + "/";
		}else if(!savePath.endsWith("/")){
			savePath += "/";
		}
		System.out.println("savePath: " + savePath);
		Global.savePath = savePath;
		Global.maxFailRetry = Integer.parseInt(System.getProperty("bilibili.download.maxFailRetry"));
		//查询或显示相关
		Global.pageSize = Integer.parseInt(System.getProperty("bilibili.pageSize"));
		Global.pageDisplay = System.getProperty("bilibili.pageDisplay");
		Global.themeDefault = "default".equals(System.getProperty("bilibili.theme"));
		Global.btnStyle = "design".equals(System.getProperty("bilibili.button.style"));
		//临时文件
		Global.restrictTempMode = "on".equals(System.getProperty("bilibili.restrictTempMode"));
		//仓库功能
		Global.useRepo = "on".equals(System.getProperty("bilibili.repo"));
		boolean saveToRepo = "on".equals(System.getProperty("bilibili.repo.save"));
		Global.saveToRepo =  Global.useRepo || saveToRepo;
		Global.repoInDefinitionStrictMode = "on".equals(System.getProperty("bilibili.repo.definitionStrictMode"));
		//重命名配置
		Global.formatStr = System.getProperty("bilibili.name.format");
		Global.doRenameAfterComplete = "true".equals(System.getProperty("bilibili.name.doAfterComplete"));
		//弹出框设置
		Global.isAlertIfDownloded = "true".equals(System.getProperty("bilibili.alert.isAlertIfDownloded"));
		Global.maxAlertPrompt = Integer.parseInt(System.getProperty("bilibili.alert.maxAlertPrompt"));
		String version = System.getProperty("bilibili.version");
		if(version != null) {
			Global.version = version;
		}
		//FFMPEG 路径设置
		Global.ffmpegPath = System.getProperty("bilibili.ffmpegPath");
		Global.flvUseFFmpeg = "true".equals(System.getProperty("bilibili.flv.ffmpeg"));
		//简单的防多开功能
		Global.lockCheck = "true".equals(System.getProperty("bilibili.lockCheck"));
		//字幕优先语种
		Global.cc_lang = System.getProperty("bilibili.cc.lang");
		// 登录设置
		Global.userName = System.getProperty("bilibili.user.userName");
		Global.password = System.getProperty("bilibili.user.password");
		Global.deleteUserFile = !"false".equals(System.getProperty("bilibili.user.delete"));
		if(Global.deleteUserFile) {
			deleteUserConfig();
		}
		Global.pwdLogin = "pwd".equals(System.getProperty("bilibili.user.login"));
		Global.pwdAutoLogin = "auto".equals(System.getProperty("bilibili.user.login.pwd"));
		Global.pwdAutoCaptcha = "true".equals(System.getProperty("bilibili.user.login.pwd.autoCaptcha"));
	}

	private static void deleteUserConfig() {
		File user = new File("config/user.config");
		if(user.exists()) {
			user.delete();
		}else {
			user = new File(baseDirectory(), "config/user.config");
			user.delete();
		}
	}
	private static void readConfig(String file) {
		BufferedReader buReader = null;
		File configFile;
		try {
			configFile = new File(file);
			if(!configFile.exists()) {
				System.out.println("配置文件不存在： " + configFile.getCanonicalPath());
				configFile = new File(baseDirectory(), file);
				System.out.println("尝试路径： " + configFile.getCanonicalPath());
			}
			buReader = new BufferedReader(new FileReader(configFile));
			String config;
			while ((config = buReader.readLine()) != null) {
				Matcher matcher = patternConfig.matcher(config);
				if (matcher.find()) {
					System.setProperty(matcher.group(1), matcher.group(2).trim());
					System.out.printf("  key-->value:  %s --> %s\r\n", matcher.group(1), matcher.group(2));
				}
			}
		} catch (IOException e) {
			System.out.println("配置文件不存在! ");
			// e.printStackTrace();
		} finally {
			try {
				buReader.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static String baseDirectory() {
        try {
            String path = ClassLoader.getSystemResource("").getPath();
            if (path == null || "".equals(path))
                return getProjectPath();
            return path;
        } catch (Exception ignored) {
        	return getProjectPath();
        }
    }

	private static String getProjectPath() {
        java.net.URL url = ConfigUtil.class.getProtectionDomain().getCodeSource()
                .getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
        	int lastIndex = filePath.lastIndexOf("/");
        	System.out.println(lastIndex);
        	if(lastIndex > -1) {
        		filePath = filePath.substring(0, lastIndex + 1);
        	}else {
        		filePath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
        	}
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
}
