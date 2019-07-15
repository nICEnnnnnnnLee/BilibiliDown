package nicelee.bilibili.util;

import java.io.BufferedReader;
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
		try {
			buReader = new BufferedReader(new FileReader("./config/app.config"));
			String config;
			while ((config = buReader.readLine()) != null) {
				Matcher matcher = patternConfig.matcher(config);
				if (matcher.find()) {
					System.setProperty(matcher.group(1), matcher.group(2).trim());
					System.out.printf("  key-->value:  %s --> %s\r\n", matcher.group(1), matcher.group(2));
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			try {
				buReader.close();
			} catch (Exception e) {
			}
		}
		System.out.println("----Config ini end...----");
		//下载设置相关
		int fixPool = Integer.parseInt(System.getProperty("bilibili.download.poolSize"));
		Global.downLoadThreadPool = Executors.newFixedThreadPool(fixPool);
		Global.downloadFormat = Integer.parseInt(System.getProperty("bilibili.format"));
		Global.savePath = System.getProperty("bilibili.savePath");
		Global.maxFailRetry = Integer.parseInt(System.getProperty("bilibili.download.maxFailRetry"));
		//查询或显示相关
		Global.pageSize = Integer.parseInt(System.getProperty("bilibili.pageSize"));
		Global.pageDisplay = System.getProperty("bilibili.pageDisplay");
		Global.themeDefault = "default".equals(System.getProperty("bilibili.theme"));
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
	}
}
