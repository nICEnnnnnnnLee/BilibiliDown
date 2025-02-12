package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
//	private final static Pattern picSrcPattern = Pattern.compile("img (data-)?src=\"([^\"]+)\"");
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
		JSONObject jObj = new JSONObject(json);
		jObj = jObj.getJSONObject("detail");

		// 判断动态的类型， 11 图文 12 专栏 1 UP主投稿了 17 直播开播了
//		JSONObject jBasic = jObj.getJSONObject("basic");
//		int type = jBasic.optInt("comment_type");
		JSONArray jParagraphs = null, jTopPics = null, jModules = jObj.getJSONArray("modules");
		JSONObject jUp = null;
		for (int i = 0; i < jModules.length(); i++) {
			JSONObject module = jModules.getJSONObject(i);
			String mType = module.getString("module_type");
			if (mType.equals("MODULE_TYPE_AUTHOR"))
				jUp = module.getJSONObject("module_author");
			else if (mType.equals("MODULE_TYPE_CONTENT"))
				jParagraphs = module.getJSONObject("module_content").getJSONArray("paragraphs");
			else if (mType.equals("MODULE_TYPE_TOP"))
				jTopPics = module.getJSONObject("module_top").getJSONObject("display").getJSONObject("album")
						.getJSONArray("pics");
			else if (mType.equals("MODULE_TYPE_TITLE"))
				viInfo.setVideoName(module.getJSONObject("module_title").getString("text"));
			if (jUp != null && jParagraphs != null && jTopPics != null && viInfo.getVideoName() != null)
				break;
		}
		// 总体大致信息
		String author = jUp.getString("name");
		String authorId = jUp.optString("mid");
		long cTime = jUp.optLong("pub_ts") * 1000;
//		viInfo.setVideoId(opusIdStr);
		viInfo.setAuthor(author);
		viInfo.setAuthorId(authorId);
		// 设置 brief videoName
		for (int i = 0; i < jParagraphs.length(); i++) {
			JSONObject jPara = jParagraphs.getJSONObject(i);
			int paraType = jPara.optInt("para_type");
			if (paraType == 1) {
				JSONArray nodes = jPara.getJSONObject("text").getJSONArray("nodes");
				for (int nIdx = 0; nIdx < nodes.length(); nIdx++) {
					JSONObject node = nodes.getJSONObject(nIdx);
					if ("TEXT_NODE_TYPE_WORD".equals(node.getString("type"))) {
						String text = node.getJSONObject("word").getString("words");
						viInfo.setBrief(text);
						if (viInfo.getVideoName() == null) {
							String videoName = text;
							if (videoName.length() > 15)
								videoName = videoName.substring(0, 15);
							viInfo.setVideoName(videoName);
						}
						break;
					}
				}
			}
		}
		if (viInfo.getVideoName() == null)
			viInfo.setVideoName("空");

		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		int picIndex = 0;
		// 先遍历 jTopPics
		if (jTopPics != null) {
			for (int i = 0; i < jTopPics.length(); i++) {
				String picUrl = jTopPics.getJSONObject(i).getString("url");
				ClipInfo clip = newCommonClip(cvIdStr, viInfo, author, authorId, cTime, null, null);
				setPicOfClip(clip, clipMap, picIndex, picUrl);
				if (viInfo.getVideoPreview() == null)
					viInfo.setVideoPreview(picUrl);
				picIndex++;
			}
		}
		// 再遍历 jParagraphs
		for (int i = 0; i < jParagraphs.length(); i++) {
			JSONObject jPara = jParagraphs.getJSONObject(i);
			int paraType = jPara.optInt("para_type");
			if (paraType == 2) {
				JSONArray pics = jPara.getJSONObject("pic").getJSONArray("pics");
				for (int nIdx = 0; nIdx < pics.length(); nIdx++) {
					String picUrl = pics.getJSONObject(nIdx).getString("url");
					ClipInfo clip = newCommonClip(cvIdStr, viInfo, author, authorId, cTime, null, null);
					setPicOfClip(clip, clipMap, picIndex, picUrl);
					if (viInfo.getVideoPreview() == null)
						viInfo.setVideoPreview(picUrl);
					picIndex++;
				}
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
