package nicelee.bilibili.downloaders.impl;

import java.util.HashMap;

import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Encrypt;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;

@Bilibili(name = "ffmpeg-downloader", weight = 22, type = "downloader", note = "ffmpeg下载")
public class FFmpegDownloader extends FLVDownloader {

	@Override
	public boolean matches(String url) {
		if (url.contains("ffmpeg")) {
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
			file = ResourcesUtil.sourceOf("ffmpeg.exe");
		}
		util.setSavePath(ResourcesUtil.baseDirectory());
		boolean succ = util.download(url, "ffmpeg.exe", new HashMap<>());
		if (succ) {
			String sha1 = Encrypt.SHA1(util.getFileDownload());
			Logger.println("url:" + url);
			Logger.println("文件sha1:" + sha1);
			if(!"8e633e3cedcc0ddd4c83509d959b70932400b591".equalsIgnoreCase(sha1)) {
				JOptionPane.showMessageDialog(null, "ffmpeg.exe已经下载完成, 但SHA1不匹配！！", "警告!!",
						JOptionPane.WARNING_MESSAGE);
				throw new RuntimeException("ffmpeg.exe SHA1不匹配");
			}
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			JOptionPane.showMessageDialog(null, "ffmpeg.exe已经下载完成, 转码功能在重启后可使用", "成功!!",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return succ;
	}

}
