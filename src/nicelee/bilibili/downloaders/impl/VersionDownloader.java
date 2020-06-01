package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.VersionManagerUtil;

@Bilibili(name = "version-downloader", type = "downloader", note = "最新的版本下载")
public class VersionDownloader extends FLVDownloader {

	private static final Pattern pattern = Pattern.compile("BilibiliDown\\.v([0-9]+\\.[0-9]+).*\\.zip");
	String downName;
	
	@Override
	public boolean matches(String url) {
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			downName = matcher.group();
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
		if(file == null) {
			file = new File("update/" + downName);
		}
		util.setSavePath("./update");
		boolean succ = util.download(url, downName, new HashMap<>());
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			try {
				VersionManagerUtil.unzipTargetJar(downName);
				Object[] options = { "是", "否" };
				int m = JOptionPane.showOptionDialog(null, "已经下载成功，需要关闭程序才能更新，现在是否重启?", "成功！",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				Logger.println(m);
				if (m == 0) {
					VersionManagerUtil.RunCmdAndCloseApp("1");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return succ;
	}

}
