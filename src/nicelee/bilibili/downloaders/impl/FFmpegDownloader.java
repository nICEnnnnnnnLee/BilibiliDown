package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;

@Bilibili(name = "ffmpeg-downloader", type = "downloader", note = "ffmpeg下载")
public class FFmpegDownloader extends FLVDownloader {

	@Override
	public boolean matches(String url) {
		if (url.endsWith("ffmpeg.exe")) {
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
			file = new File("ffmpeg.exe");
		}
		util.setSavePath(".");
		boolean succ = util.download(url, "ffmpeg.exe", new HashMap<>());
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			JOptionPane.showMessageDialog(null, "ffmpeg.exe已经下载完成, 转码功能可使用", "成功!!",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return succ;
	}

}
