package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Encrypt;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.VersionManagerUtil;
import nicelee.ui.Global;

@Bilibili(name = "version-downloader", type = "downloader", note = "最新的版本下载")
public class VersionDownloader extends FLVDownloader {

	private static final Pattern pattern = Pattern.compile("BilibiliDown\\.v([0-9]+\\.[0-9]+).*\\.zip");
	protected static final File updateDir = new File(ResourcesUtil.baseDirectory(), "update");
	String downName;
	String version;
	
	@Override
	public boolean matches(String url) {
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			downName = matcher.group();
			version = matcher.group(1);
			return true;
		}
		return false;
	}

	/**
	 * 下载matches
	 * 
	 * @param url
	 * @param avId
	 * @param qn 
	 * @param page 
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		if (file == null) {
			file = new File(updateDir, downName);
		}
		try {
			util.setSavePath(updateDir.getCanonicalPath());
		} catch (IOException e1) {
		}
		boolean succ = util.download(url, downName, new HashMap<>());
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			try {
				String sha1 = Encrypt.SHA1(util.getFileDownload());
				Logger.println("文件sha1:" + sha1);
				String expectedSHA1 = util.getContent(getSHA1Url(version, downName + ".sha1"), null).trim();
				Logger.println("expectedSHA1:" + expectedSHA1);
				Object[] options = { "是", "否" };
				
				if(!expectedSHA1.isEmpty() && !sha1.equalsIgnoreCase(expectedSHA1)) {
					int m0 = JOptionPane.showOptionDialog(null, "下载的SHA1与文件不匹配，是否继续?", "成功！",
							JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (m0 != 0)
						return succ;
				}
				
				VersionManagerUtil.unzipTargetJar(downName);
				int m = JOptionPane.showOptionDialog(null, "已经下载成功，需要关闭程序才能更新，现在是否重启?", "成功！",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (m == 0) {
					VersionManagerUtil.RunCmdAndCloseApp("1");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return succ;
	}

	private String getSHA1Url(String version, String file) {
		Logger.println("当前使用的更新源为： " + Global.updateSourceActive);
		String key = "bilibili.download.update.patterns." + Global.updateSourceActive;
		String pattern = Global.settings.getOrDefault(key, "https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V{version}/{file}");
		return pattern.replace("{version}", version).replace("{file}", file);
	}
}
