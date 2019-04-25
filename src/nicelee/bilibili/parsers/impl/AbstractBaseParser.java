package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.IInputParser;
import nicelee.bilibili.parsers.IParamSetter;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public abstract class AbstractBaseParser implements IInputParser {

	protected Matcher matcher;
	protected HttpRequestUtil util;
	protected int pageSize = 20;
	protected IParamSetter paramSetter;

	public AbstractBaseParser(Object... obj) {
		this.util = (HttpRequestUtil)obj[0];
		this.paramSetter = (IParamSetter)obj[1];
		this.pageSize = (int)obj[2];
	}

	@Override
	public abstract boolean matches(String input) ;

	@Override
	public abstract String validStr(String input) ;

	@Override
	public abstract VideoInfo result(String input, int videoFormat, boolean getVideoLink) ;
	
	/**
	 * 
	 * @param avId 字符串带av
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	protected VideoInfo getAVDetail(String avId, int videoFormat, boolean getVideoLink) {
		String avIdNum = avId.replace("av", "");
		return getAVDetail(avId, avIdNum, videoFormat, getVideoLink);
	}
	
	/**
	 * 
	 * @param avId
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	protected VideoInfo getAVDetail(long avId, int videoFormat, boolean getVideoLink) {
		String avIdNum = "" +avId;
		String avIdStr = "av" +avIdNum;
		return getAVDetail(avIdStr, avIdNum, videoFormat, getVideoLink);
	}

	/**
	 * 获取详细av信息
	 * 
	 * @param avId
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	private VideoInfo getAVDetail(String avId, String avIdNum, int videoFormat, boolean getVideoLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);
		String url = "https://api.bilibili.com/x/web-interface/view?aid=" + avIdNum;
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
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
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(avId);
			clip.setcId(clipObj.getLong("cid"));
			clip.setPage(clipObj.getInt("page"));
			clip.setTitle(clipObj.getString("part"));
			clip.setPicPreview(videoPreview);

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
				for (int qn : qnList) {
					if (getVideoLink) {
						String link = getVideoLink(avId, String.valueOf(clip.getcId()), qn, videoFormat);
						links.put(qn, link);
					} else {
						links.put(qn, "");
					}
				}
				clip.setLinks(links);
			} catch (Exception e) {
				clip.setLinks(links);
			}

			clipMap.put(clip.getcId(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * 查询视频可提供的质量 原https://api.bilibili.com/x/player/playurl接口逐渐在被弃用?
	 * 使用https://api.bilibili.com/pgc/player/web/playurl
	 * 
	 * @external input HttpRequestUtil util
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
			// System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		}
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
		}
		return qnList;
	}

	/**
	 * 查询视频链接
	 * 
	 * @external input HttpRequestUtil util
	 * @external input downFormat
	 * @external output linkQN 保存返回链接的清晰度
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		if (downFormat == 0) {
			return getVideoM4sLink(avId, cid, qn);
		} else {
			return getVideoFLVLink(avId, cid, qn);
		}
	}

	/**
	 * 查询视频链接(FLV)
	 * 
	 * @external input HttpRequestUtil util
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return url or 1 url1#2 url2...
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
			// System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("FLV链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			// System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}

		int linkQN = jObj.getInt("quality");
		paramSetter.setRealQN(linkQN);
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		JSONArray urlList = jObj.getJSONArray("durl");
		return parseUrlJArray(urlList);
	}

	/**
	 * 查询视频链接(MP4)
	 * 
	 * @external input HttpRequestUtil util
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
			System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("MP4链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}
		int linkQN = jObj.getInt("quality");
		paramSetter.setRealQN(linkQN);
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		try {
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
		} catch (Exception e) {
			// 鉴于部分视频如 https://www.bilibili.com/video/av24145318 H5仍然是用的是Flash源,此处切为FLV
			return parseUrlJArray(jObj.getJSONArray("durl"));
		}
	}

	/**
	 * 
	 * @param urlList
	 * @return
	 */
	private String parseUrlJArray(JSONArray urlList) {
		if (urlList.length() == 1) {
			return urlList.getJSONObject(0).getString("url");

		} else {
			StringBuilder link = new StringBuilder();
			for (int i = 0; i < urlList.length(); i++) {
				JSONObject obj = urlList.getJSONObject(i);
				link.append(obj.getInt("order"));
				link.append(obj.getString("url"));
				link.append("#");
			}
			System.out.println(link.substring(0, link.length() - 1));
			return link.substring(0, link.length() - 1);
		}
	}

	@Override
	public int getVideoLinkQN() {
		return paramSetter.getRealQN();
	}

}
