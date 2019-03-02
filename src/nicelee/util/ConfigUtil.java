package nicelee.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		//从配置文件读取
		buReader = null;
		System.out.println("----Config init begin...----");
		try {
			buReader = new BufferedReader(new FileReader("app.config"));
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
		Global.downloadFormat = Integer.parseInt(System.getProperty("bilibili.format"));
		Global.savePath = System.getProperty("bilibili.savePath");
	}
}
