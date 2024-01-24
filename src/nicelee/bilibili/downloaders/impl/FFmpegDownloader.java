package nicelee.bilibili.downloaders.impl;

import java.util.HashMap;

import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Encrypt;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.SysUtil;

@Bilibili(name = "ffmpeg-downloader", weight = 22, type = "downloader", note = "ffmpeg下载")
public class FFmpegDownloader extends FLVDownloader {

	@Override
	public boolean matches(String url) {
		if (url.contains("ffmpeg")) {
			return true;
		}
		return false;
	}

	String getSHA1(String os_arch) {
		Logger.println(os_arch);
		switch (os_arch) {
		case "linux_amd64":
			return "c396604b9c58e7164de1b79a11148c1ece3f4799";
		case "linux_arm64":
			return "97f718893509d37810c7c6bf759ee16398fdfc59";
		case "win_amd64":
			return "2d53419420c7f3d473cbd2c6827ce9347436520f";
		case "win_arm64":
			return "fe49da6125ec5e10441f1eda2976cc5734d06d47";
		default:
			return "";
		}
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
		Logger.println("url:" + url);
		String ffmpegEXE = "ffmpeg" + SysUtil.getEXE_SUFFIX();
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		if (file == null) {
			file = ResourcesUtil.sourceOf(ffmpegEXE);
		}
		util.setSavePath(ResourcesUtil.baseDirectory());
		boolean succ = util.download(url, ffmpegEXE, new HashMap<>());
		if (succ) {
			String sha1 = Encrypt.SHA1(util.getFileDownload());
			Logger.println("文件sha1:" + sha1);
			String os_arch = String.format("%s_%s", SysUtil.getOS(), SysUtil.getARCH());
			String expectedSha1 = getSHA1(os_arch);
			Logger.println("期望的文件sha1:" + expectedSha1);
			if (!expectedSha1.equals(sha1)) {
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
