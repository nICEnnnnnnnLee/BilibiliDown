package nicelee.bilibili;

import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.model.ClipInfo;
import nicelee.model.VideoInfo;
import nicelee.util.CmdUtil;
import nicelee.util.HttpCookies;
import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;

public class INeedAV {

	// 0为MP4, 1 为FLV
	int downFormat = 0;
	HttpRequestUtil util = new HttpRequestUtil();

	public static void main(String[] args) {
		System.out.println("-------------------------------");
		System.out.println("输入av号, 下载当前cookie所能下载的最清晰链接");
		System.out.println("-------------------------------");
		INeedAV ina = new INeedAV();
		ina.setDownFormat(0);
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
			VideoInfo avInfo = ina.getVideoDetail(args[0], true);

			// 下载最清晰的链接
			for (ClipInfo clip : avInfo.getClips().values()) {
				ina.downloadClip(clip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提取字符串里的avID
	 * 
	 * @param origin
	 * @return
	 */
	Pattern avPattern = Pattern.compile("av[0-9]+");
	Pattern epPattern = Pattern.compile("ep[0-9]+");

	public String getAvID(String origin) {
		int end = origin.indexOf("?");
		if (end > -1) {
			origin = origin.substring(0, end - 1).toLowerCase();
		}
		Matcher avMatcher = avPattern.matcher(origin);
		Matcher epMatcher = epPattern.matcher(origin);
		if (avMatcher.find()) {
			// 如果能提取出avID, 直接返回
			return avMatcher.group();
		} else if(epMatcher.find()){
			// 如果不能提取, 例如番剧 :https://www.bilibili.com/bangumi/play/ep250435/
			String avID = EpIdToAvId(epMatcher.group());
			return avID;
		}
		return "";
	}
	/**
	 * 已知epId, 求avId
	 * 目前没有抓到api哦... 暂时从网页里面爬
	 */
	public String EpIdToAvId(String epId) {
		HttpRequestUtil util = new HttpRequestUtil();
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + epId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));
		
		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		JSONObject jObj = new JSONObject(json);
		String avId = "av" + jObj.getJSONObject("epInfo").getInt("aid");
		return avId;
	}
	/**
	 * 下载视频
	 * 
	 * @param avClipInfo
	 * @return
	 */
	public boolean downloadClip(ClipInfo avClipInfo) {
		String url = getSuperHDLink(avClipInfo);
		String avId = avClipInfo.getAvId();
		return downloadClip(url, avId, String.valueOf(avClipInfo.getPage()));
	}

	/**
	 * 下载视频
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	public boolean downloadClip(String url, String avId, String page) {
		if (downFormat == 0) {
			return downloadM4sClip(url, avId, page);
		} else {
			return downloadFLVClip(url, avId, page);
		}
	}

	/**
	 * 下载FLV视频
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	boolean downloadFLVClip(String url, String avId, String page) {
		String fileName = avId + "-p" + page + ".flv";
		return util.download(url, fileName, new HttpHeaders().getBiliWwwFLVHeaders(avId));
	}

	/**
	 * 下载MP4视频
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	boolean downloadM4sClip(String url, String avId, String page) {

		String links[] = url.split("#");
		String videoName = avId + "-p" + page + "_video.m4s";
		String audioName = avId + "-p" + page + "_audio.m4s";
		String dstName = avId + "-p" + page + ".mp4";

		util.setTotalTask(2);
		util.setNextTask(audioName + "##" + links[1]);
		util.setCmd(CmdUtil.createConvertCmd(videoName, audioName, dstName));
		if (util.download(links[0], videoName, new HttpHeaders().getBiliWwwM4sHeaders(avId))) {

			util.setNextTask(null);
			util.setTotal(0);
			if (util.download(links[1], audioName, new HttpHeaders().getBiliWwwM4sHeaders(avId))) {
				CmdUtil.convert(videoName, audioName, dstName);
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 获取当前Cookie所能获得的资源连接
	 * 
	 * @param avClipInfo
	 * @return
	 */
	public String getSuperHDLink(ClipInfo avClipInfo) {
		HashMap<Integer, String> links = avClipInfo.getLinks();
		// 对HashMap中的key 进行排序
		List<Integer> list = new LinkedList<Integer>(links.keySet());
		Collections.sort(list);
		return links.get(list.get(list.size() - 1));
	}

	/**
	 * 获取AVid 视频的所有信息(全部)
	 * 
	 * @param avId
	 * @param isGetLink
	 * @return
	 */
	public VideoInfo getVideoDetail(String avId, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);

		String avIdNum = avId.replace("av", "");
		String url = "https://api.bilibili.com/x/web-interface/view?aid=" + avIdNum;
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
		// System.out.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		String videoName = jObj.getString("title");
		String brief = jObj.getString("desc");
		String author = jObj.getJSONObject("owner").getString("name");
		String authorId = String.valueOf(jObj.getJSONObject("owner").getLong("mid"));
		String videoPreview = jObj.getString("pic");
		viInfo.setVideoName(videoName);
		viInfo.setBrief(brief);
		viInfo.setAuthor(author);
		viInfo.setAuthorId(authorId);
		viInfo.setVideoPreview(videoPreview);

		//
		JSONArray array = jObj.getJSONArray("pages");
		HashMap<Integer, ClipInfo> clipMap = new HashMap<Integer, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvId(avId);
			clip.setcId(clipObj.getLong("cid"));
			clip.setPage(clipObj.getInt("page"));
			clip.setTitle(clipObj.getString("part"));

			int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
			HashMap<Integer, String> links = new HashMap<Integer, String>();
			for (int qn : qnList) {
				if (isGetLink) {
					// 如需获取HTML5 源, 请使用getVideoM4sLink(String, String, int)
					// 上面方法会返回视频链接#音频链接
					String link = getVideoFLVLink(avId, String.valueOf(clip.getcId()), qn);
					links.put(qn, link);
				} else {
					links.put(qn, "");
				}
			}
			clip.setLinks(links);
			clipMap.put(clip.getPage(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * 查询视频链接
	 * 
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	public String getVideoLink(String avId, String cid, int qn) {
		if (downFormat == 0) {
			return getVideoM4sLink(avId, cid, qn);
		} else {
			return getVideoFLVLink(avId, cid, qn);
		}
	}

	/**
	 * 查询视频链接(FLV)
	 * 
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	String getVideoFLVLink(String avId, String cid, int qn) {
		System.out.println("正在查询FLV链接...");
		String avIdNum = avId.replace("av", "");

		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		try {
			String url = "https://api.bilibili.com/x/player/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}
		
		
		int linkQN = jObj.getInt("quality");
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		return jObj.getJSONArray("durl").getJSONObject(0).getString("url");
	}

	/**
	 * 查询视频链接(MP4)
	 * 
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return 视频url + "#" + 音频url
	 */
	String getVideoM4sLink(String avId, String cid, int qn) {
		System.out.println("正在查询MP4链接...");
		String avIdNum = avId.replace("av", "");
		
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		try {
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&type=&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}
		int linkQN = jObj.getInt("quality");
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");

		StringBuilder link = new StringBuilder();
		// 获取视频链接
		JSONArray videos = jObj.getJSONObject("dash").getJSONArray("video");
		for (int i = 0; i < videos.length(); i++) {
			JSONObject video = videos.getJSONObject(i);
			if (video.getInt("id") == linkQN) {
				link.append(video.getString("baseUrl")).append("#");
				break;
			}
		}
		// 获取音频链接(默认第一个)
		JSONArray audios = jObj.getJSONObject("dash").getJSONArray("audio");
		link.append(audios.getJSONObject(0).getString("baseUrl"));

		return link.toString();
	}

	/**
	 * 查询视频可提供的质量 
	 * 原https://api.bilibili.com/x/player/playurl接口逐渐在被弃用?
	 * 使用https://api.bilibili.com/pgc/player/web/playurl
	 * 
	 * @param avId
	 * @param cid
	 * @return
	 */
	public int[] getVideoQNList(String avId, String cid) {

		String avIdNum = avId.replace("av", "");
		HttpHeaders headers = new HttpHeaders();
		JSONArray jArr = null;
		try {
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		}
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
		}
		return qnList;
	}

	public HttpRequestUtil getUtil() {
		return util;
	}

	public void setUtil(HttpRequestUtil util) {
		this.util = util;
	}

	public int getDownFormat() {
		return downFormat;
	}

	public void setDownFormat(int downFormat) {
		this.downFormat = downFormat;
	}
}
