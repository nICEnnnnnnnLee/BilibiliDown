package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;

/**
 * 下载图文动态里面的图片
 * <p>https://www.bilibili.com/opus/953619104940425225</p>
 * <p>https://m.bilibili.com/opus/953839801850658840</p>
 * <p>https://t.bilibili.com/953619104940425225</p>
 */
@Bilibili(name = "URL4PictureOpusParser", note = "图片解析 - 图文动态")
public class URL4PictureOpusParser extends URL4PictureCVParser {

	private final static Pattern opPattern = Pattern.compile("\\.bilibili\\.com/opus/([0-9]+)");
	private final static Pattern tPattern = Pattern.compile("t\\.bilibili\\.com/([0-9]+)");
	private String opusIdNumber;

	public URL4PictureOpusParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = opPattern.matcher(input);
		if (matcher.find()) {
			opusIdNumber = matcher.group(1);
			Logger.println("匹配URL4PictureOpusParser: opus" + opusIdNumber);
			return true;
		}
		matcher = tPattern.matcher(input);
		if (matcher.find()) {
			opusIdNumber = matcher.group(1);
			Logger.println("匹配URL4PictureOpusParser: opus" + opusIdNumber);
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
		return getOpusDetail(opusIdNumber);
	}

	protected VideoInfo getOpusDetail(String opusIdNumber) {
		Logger.println("URL4PictureOpusParser正在获取结果: opus" + opusIdNumber);
		String opusIdStr = "opus" + opusIdNumber;

		String urlOpus = "https://www.bilibili.com/opus/" + opusIdNumber;
		String html = util.getContent(urlOpus, headers, HttpCookies.globalCookiesWithFingerprint());
		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json);
		String cvIdNumber = jObj.optString("cvid");
		if (cvIdNumber != null && !cvIdNumber.isEmpty()) {
			return getCVDetail(cvIdNumber);
		}
		jObj = jObj.getJSONObject("detail");

		// 判断动态的类型， 11 图文 12 专栏 1 UP主投稿了 17 直播开播了
		JSONObject jBasic = jObj.getJSONObject("basic");
		int type = jBasic.optInt("comment_type");
		if (type == 12) {
			cvIdNumber = jBasic.optString("comment_id_str");
			return getCVDetail(cvIdNumber);
		}
		VideoInfo viInfo = new VideoInfo();
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
		viInfo.setVideoId(opusIdStr);
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
				ClipInfo clip = newCommonClip(opusIdStr, viInfo, author, authorId, cTime, null, null);
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
					ClipInfo clip = newCommonClip(opusIdStr, viInfo, author, authorId, cTime, null, null);
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

//	protected VideoInfo getOpusDetailWithLogin(String opusIdNumber) {
//		Logger.println("URL4PictureOpusParser正在获取结果: opus" + opusIdNumber);
//		String opusIdStr = "opus" + opusIdNumber;
//		// 这个API只有在登录的时候可以使用，所以只能从网页里面扣
//		String url = "https://api.bilibili.com/x/polymer/web-dynamic/v1/detail?timezone_offset=-480&platform=web&gaia_source=main_web&features=itemOpusStyle,opusBigCover,onlyfansVote,endFooterHidden,decorationCard,onlyfansAssetsV2&web_location=333.1368&x-bili-device-req-json=%7B%2522platform%2522:%2522web%2522,%2522device%2522:%2522pc%2522%7D&x-bili-web-req-json=%7B%2522spm_id%2522:%2522333.1368%2522%7D&id="
//				+ opusIdNumber;
//		url = API.encWbi(url);
//		HashMap<String, String> headers_json = new HttpHeaders().getCommonHeaders();
//		headers_json.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//		headers_json.put("Origin", "https://www.bilibili.com");
//		headers_json.put("Referer", "https://www.bilibili.com");
//		String json = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
//		Logger.println(url);
//		Logger.println(json);
//		JSONObject jObj = new JSONObject(json).getJSONObject("data").getJSONObject("item");
//
//		JSONObject jBasic = jObj.getJSONObject("basic");
//		// 判断动态的类型， 11 图文 12 专栏 1 UP主投稿了 17 直播开播了
//		int type = jBasic.optInt("comment_type");
//		if (type == 12) {
//			String cvIdNumber = jBasic.optString("comment_id_str");
//			return getCVDetail(cvIdNumber);
//		}
//		VideoInfo viInfo = new VideoInfo();
//		viInfo.setVideoId(opusIdStr);
//		JSONObject jModule = jObj.getJSONObject("modules");
//		JSONObject jUp = jModule.getJSONObject("module_author");
//		JSONObject jDynamic = jObj.getJSONObject("module_dynamic").getJSONObject("major").getJSONObject("opus");
//
//		// 总体大致信息
//		String brief = jDynamic.getJSONObject("summary").getString("text");
//		String videoName = jDynamic.optString("title", brief);
//		if (videoName.length() > 15)
//			videoName = videoName.substring(0, 15);
//		String author = jUp.getString("name");
//		String authorId = jUp.optString("mid");
//		String videoPreview = jObj.getJSONArray("image_urls").getString(0);
//		viInfo.setVideoName(videoName);
//		viInfo.setBrief(brief);
//		viInfo.setAuthor(author);
//		viInfo.setAuthorId(authorId);
//		viInfo.setVideoPreview(videoPreview);
//
//		JSONArray pics = jDynamic.getJSONArray("pics");
//		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
//		for (int i = 0, picIndex = 0; i < pics.length(); i++) {
//			String picUrl = pics.getJSONObject(i).getString("url");
//			ClipInfo clip = new ClipInfo();
//			clip.setAvTitle(viInfo.getVideoName());
//			clip.setAvId(opusIdStr);
//			clip.setcId(picIndex);
//			clip.setPage(picIndex);
//			clip.setRemark(picIndex);
//			clip.setTitle("第" + picIndex + "张");
//			clip.setPicPreview(picUrl);
//			clip.setUpName(author);
//			clip.setUpId(authorId);
//			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
//			links.put(0, picUrl);
//			clip.setLinks(links);
//			clipMap.put(clip.getcId(), clip);
//			picIndex++;
//		}
//		viInfo.setClips(clipMap);
//		viInfo.print();
//		return viInfo;
//	}

}
