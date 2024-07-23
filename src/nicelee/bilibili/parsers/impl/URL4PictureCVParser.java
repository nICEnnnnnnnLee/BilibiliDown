package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 下载专栏里面的图片
 * <p>https://www.bilibili.com/read/cv23435927/?from=readlist</p>
 * <p>https://www.bilibili.com/read/mobile?id=23435927</p>
 *
 */
@Bilibili(name = "URL4PictureCVParser", note = "图片解析 - 专栏")
public class URL4PictureCVParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("\\.bilibili\\.com/read/(mobile\\?id=|cv)([0-9]+)");
	private final static Pattern picSrcPattern = Pattern.compile("img (data-)?src=\"([^\"]+)\"");
	private String cvIdNumber;

	public URL4PictureCVParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			cvIdNumber = matcher.group(2);
			Logger.println("匹配URL4PictureCVParser: cv" + cvIdNumber);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return input;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getCVDetail(cvIdNumber);
	}

	final static protected HashMap<String, String> headers = new HttpHeaders().getCommonHeaders("www.bilibili.com");
	protected VideoInfo getCVDetail(String cvIdNumber) {
		Logger.println("URL4PictureCVParser正在获取结果: cv" + cvIdNumber);
		String cvIdStr = "cv" + cvIdNumber;
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(cvIdStr);

		String urlOpus = "https://www.bilibili.com/read/" + cvIdStr;
		String html = util.getContent(urlOpus, headers, HttpCookies.globalCookiesWithFingerprint());
		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		Logger.println(json);

		JSONObject jObj = new JSONObject(json).getJSONObject("readInfo");
		JSONObject jUp = jObj.getJSONObject("author");

		// 总体大致信息
		String videoName = jObj.getString("title");
		String brief = jObj.getString("summary");
		String author = jUp.getString("name");
		String authorId = jUp.optString("mid");
		String videoPreview = jObj.getJSONArray("image_urls").getString(0);
		String bannerUrl = jObj.optString("banner_url"); // 可能为空
		viInfo.setVideoName(videoName);
		viInfo.setBrief(brief);
		viInfo.setAuthor(author);
		viInfo.setAuthorId(authorId);
		viInfo.setVideoPreview(videoPreview);

		long cTime = jObj.optLong("ctime") * 1000;
		String listName = null, listOwnerName = null;
		JSONObject jList = jObj.optJSONObject("list");
		if (jList != null) {
			listName = jList.getString("name").replaceAll("[/\\\\]", "_");
			listOwnerName = author.replaceAll("[/\\\\]", "_");
		}

		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		int picIndex = 0;
		if (bannerUrl != null && !bannerUrl.isEmpty()) {
			ClipInfo clip = newCommonClip(cvIdStr, viInfo, author, authorId, cTime, listName, listOwnerName);
			setPicOfClip(clip, clipMap, picIndex, bannerUrl);
			picIndex++;
		}
		JSONObject opus = jObj.optJSONObject("opus");
		if (opus != null) {
			JSONArray jParas = jObj.getJSONObject("opus").getJSONObject("content").getJSONArray("paragraphs");
			for (int i = 0; i < jParas.length(); i++) {
				JSONObject para = jParas.getJSONObject(i);
				if (para.getInt("para_type") != 2) {
					continue;
				}
				JSONArray pics = para.getJSONObject("pic").getJSONArray("pics");
				for (int j = 0; j < pics.length(); j++) {
					String picUrl = pics.getJSONObject(j).getString("url");
					ClipInfo clip = newCommonClip(cvIdStr, viInfo, author, authorId, cTime, listName, listOwnerName);
					setPicOfClip(clip, clipMap, picIndex, picUrl);
					picIndex++;
				}
			}
		} else {
			String content = jObj.getString("content");
//			Logger.println(content);
			Matcher m = picSrcPattern.matcher(content);
			while (m.find()) {
				String picUrl = m.group(2);
				if (picUrl.startsWith("//"))
					picUrl = "http:" + picUrl;
				ClipInfo clip = newCommonClip(cvIdStr, viInfo, author, authorId, cTime, listName, listOwnerName);
				setPicOfClip(clip, clipMap, picIndex, picUrl);
				picIndex++;
			}
		}

		viInfo.setClips(clipMap);
//		viInfo.print();
		return viInfo;
	}

	protected void setPicOfClip(ClipInfo clip, LinkedHashMap<Long, ClipInfo> clipMap, int picIndex, String picUrl) {
		clip.setcId(picIndex);
		clip.setPage(picIndex);
		clip.setRemark(picIndex);
		clip.setTitle("第" + picIndex + "张");
		clip.setPicPreview(picUrl);
		LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
		links.put(0, picUrl);
		clip.setLinks(links);
		clipMap.put(clip.getcId(), clip);
	}

	protected ClipInfo newCommonClip(String cvIdStr, VideoInfo viInfo, String author, String authorId, long cTime,
			String listName, String listOwnerName) {
		ClipInfo clip = new ClipInfo();
		clip.setAvTitle(viInfo.getVideoName());
		clip.setAvId(cvIdStr);
		clip.setUpName(author);
		clip.setUpId(authorId);
		clip.setcTime(cTime);
		clip.setListName(listName);
		clip.setListOwnerName(listOwnerName);
		return clip;
	}

//	protected VideoInfo getCVDetailCounter352(String cvIdNumber) {
//		Logger.println("URL4PictureCVParser正在获取结果: cv" + cvIdNumber);
//		String cvIdStr = "cv" + cvIdNumber;
//		VideoInfo viInfo = new VideoInfo();
//		viInfo.setVideoId(cvIdStr);
//		// 容易被风控 {"code":-352,"message":"-352","ttl":1}
//		String url = "https://api.bilibili.com/x/article/view?gaia_source=main_web&web_location=333.976&id="
//				+ cvIdNumber;
//		url = API.encWbi(url);
////		HashMap<String, String> headers_json = new HttpHeaders().getCommonHeaders();
//		HashMap<String, String> headers_json = new HashMap<>();
//		headers_json.put("Host", "api.bilibili.com");
//		headers_json.put("User-Agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0");
//		headers_json.put("Connection", "keep-alive");
//		headers_json.put("Accept",
//				"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
//		headers_json.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
////		headers_json.put("Origin", "https://www.bilibili.com");
////		headers_json.put("Referer", "https://www.bilibili.com");
//		String json = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
//		Logger.println(url);
//		Logger.println(json);
//		JSONObject jObj = new JSONObject(json).getJSONObject("data");
//		JSONObject jUp = jObj.getJSONObject("author");
//		
//		// 总体大致信息
//		String videoName = jObj.getString("title");
//		String brief = jObj.getString("summary");
//		String author = jUp.getString("name");
//		String authorId = jUp.optString("mid");
//		String videoPreview = jObj.getJSONArray("image_urls").getString(0);
//		viInfo.setVideoName(videoName);
//		viInfo.setBrief(brief);
//		viInfo.setAuthor(author);
//		viInfo.setAuthorId(authorId);
//		viInfo.setVideoPreview(videoPreview);
//		
//		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
//		
//		JSONObject opus = jObj.optJSONObject("opus");
//		if (opus != null) {
//			JSONArray jParas = jObj.getJSONObject("opus").getJSONObject("content").getJSONArray("paragraphs");
//			for (int i = 0, picIndex = 0; i < jParas.length(); i++) {
//				JSONObject para = jParas.getJSONObject(i);
//				if (para.getInt("para_type") != 2) {
//					continue;
//				}
//				JSONArray pics = para.getJSONObject("pic").getJSONArray("pics");
//				for (int j = 0; j < pics.length(); j++) {
//					String picUrl = pics.getJSONObject(j).getString("url");
//					ClipInfo clip = new ClipInfo();
//					clip.setAvTitle(viInfo.getVideoName());
//					clip.setAvId(cvIdStr);
//					clip.setcId(picIndex);
//					clip.setPage(picIndex);
//					clip.setRemark(picIndex);
//					clip.setTitle("第" + picIndex + "张");
//					clip.setPicPreview(picUrl);
//					clip.setUpName(author);
//					clip.setUpId(authorId);
//					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
//					links.put(0, picUrl);
//					clip.setLinks(links);
//					clipMap.put(clip.getcId(), clip);
//					picIndex++;
//				}
//			}
//		} else {
//			String content = jObj.getString("content");
//			Logger.println(content);
//			Pattern picPattern = Pattern.compile("img src=\"([^\"]+)\"");
//			Matcher m = picPattern.matcher(content);
//			int picIndex = 0;
//			while(m.find()) {
//				String picUrl = m.group(1);
//				if(picUrl.startsWith("//"))
//					picUrl = "http:" + picUrl;
//				ClipInfo clip = new ClipInfo();
//				clip.setAvTitle(viInfo.getVideoName());
//				clip.setAvId(cvIdStr);
//				clip.setcId(picIndex);
//				clip.setPage(picIndex);
//				clip.setRemark(picIndex);
//				clip.setTitle("第" + picIndex + "张");
//				clip.setPicPreview(picUrl);
//				clip.setUpName(author);
//				clip.setUpId(authorId);
//				LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
//				links.put(0, picUrl);
//				clip.setLinks(links);
//				clipMap.put(clip.getcId(), clip);
//				picIndex++;
//			}
//		}
//		
//		viInfo.setClips(clipMap);
//		viInfo.print();
//		return viInfo;
//	}

}
