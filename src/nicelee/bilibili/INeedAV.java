package nicelee.bilibili;

import java.net.HttpCookie;
import java.util.List;

import nicelee.bilibili.downloaders.Downloader;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.InputParser;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.HttpRequestUtilEx;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;

public class INeedAV {

	private HttpRequestUtil util;
	private InputParser inputParser = null;
	private Downloader downloader = null;

	public INeedAV() {
		util = new HttpRequestUtilEx();
		downloader = new Downloader();
		downloader.init(util);
		inputParser = new InputParser(util, Global.pageSize, Global.pageDisplay);
		//inputParser.init(util);
	}

	public static void main(String[] args) {
//		args = new String[]{"https://space.bilibili.com/8741628/favlist?fid=70263328"};
		System.out.println("-------------------------------");
		System.out.println("输入av号, 下载当前cookie所能下载的最清晰链接");
		System.out.println("-------------------------------");
		// 初始化配置
		ConfigUtil.initConfigs();
		
		INeedAV ina = new INeedAV();
		INeedLogin inl = new INeedLogin();
		// 0. 尝试读取cookie
		List<HttpCookie> cookies = null;
		String cookiesStr = inl.readCookies();
		// 检查有没有本地cookie配置
		if (cookiesStr != null) {
			cookies = HttpCookies.convertCookies(cookiesStr);
		}
		HttpCookies.setGlobalCookies(cookies);

		// 1. 获取av 信息, 并下载当前Cookies最清晰的链接
		try {
			VideoInfo avInfo = ina.getVideoDetail(args[0], Global.downloadFormat, false);

			// 下载最清晰的链接
			for (ClipInfo clip : avInfo.getClips().values()) {
				Logger.println(clip.getAvTitle());
				ina.downloadClip(clip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载视频
	 * 
	 * @external input downFormat
	 * @param avClipInfo
	 * @return
	 */
	public boolean downloadClip(ClipInfo avClip) {
		return downloadClip(
				inputParser.getVideoLink(avClip.getAvId(), "" + avClip.getcId(), 120, 1), 
				avClip.getAvId(), inputParser.getRealQN(), avClip.getPage());
	}

	/**
	 * 下载视频
	 * 
	 * @external input downFormat
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	public boolean downloadClip(String url, String avId, int qn, int page) {
		return downloader.download(url, avId, qn, page);
	}

	/**
	 * 获取AVid 视频的所有信息(全部)
	 * 
	 * @input HttpRequestUtil util
	 * @param avId
	 * @param isGetLink
	 * @return
	 */
	public VideoInfo getVideoDetail(String avId, int downFormat, boolean isGetLink) {
		return inputParser.result(avId, downFormat, isGetLink);
	}

	/**
	 * 获取类似于 avID/ssID这类合法id
	 * 
	 * @param 未经加工的 avId/ssId字符串
	 * @return
	 */
	public String getValidID(String avId) {
		return inputParser.validStr(avId);
	}

	public InputParser getInputParser(String avId) {
		return inputParser;
	}

	public Downloader getDownloader() {
		return downloader;
	}

	public HttpRequestUtil getUtil() {
		return util;
	}
}
