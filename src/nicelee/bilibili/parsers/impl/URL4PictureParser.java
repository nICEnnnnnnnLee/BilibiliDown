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

@Bilibili(name = "URL4PictureParser", note = "相簿解析 draw-cos-daily")
public class URL4PictureParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("h\\.bilibili\\.com/([0-9]+)");
	private final static Pattern numberPattern = Pattern.compile("^h[0-9]+$");
	private String hId;

//	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
	public URL4PictureParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			hId = "h" + matcher.group(1);
			System.out.println("匹配URL4PictureParser: " + hId);
			return true;
		}

		matcher = numberPattern.matcher(input);
		matches = matcher.find();
		if (matches) {
			hId = matcher.group();
			System.out.println("匹配URL4PictureParser: " + hId);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return hId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		String hIdWithoutH = hId.replace("h", "");
		System.out.println("URL4PictureParser正在获取结果" + hId);
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(hId);
		String url = "https://api.vc.bilibili.com/link_draw/v1/doc/detail?doc_id=" + hIdWithoutH;
		HashMap<String, String> headers_json = new HttpHeaders().getCommonHeaders("https://h.bilibili.com");
		String json = util.getContent(url, headers_json, HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		JSONObject jUp = jObj.getJSONObject("user");
		JSONObject jItem = jObj.getJSONObject("item");
		JSONArray jPics = jItem.getJSONArray("pictures");

		// 总体大致信息
		String videoName = jItem.getString("title");
		if(videoName.isEmpty()) {
			videoName = jItem.getString("description");
			if(videoName.length() > 15)
				videoName = videoName.substring(0, 15);
		}
		String brief = jItem.getString("description");
		String author = jUp.getString("name");
		String authorId = String.valueOf(jUp.getLong("uid"));
		String videoPreview = jPics.getJSONObject(0).getString("img_src");
		viInfo.setVideoName(videoName);
		viInfo.setBrief(brief);
		viInfo.setAuthor(author);
		viInfo.setAuthorId(authorId);
		viInfo.setVideoPreview(videoPreview);

		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < jPics.length(); i++) {
			JSONObject clipObj = jPics.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(hId);
			clip.setcId(i);
			clip.setPage(i);
			clip.setTitle("第" + i + "张");
			clip.setPicPreview(clipObj.getString("img_src"));
			clip.setUpName(author);
			clip.setUpId(authorId);
			
			
			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			links.put(0, clipObj.getString("img_src"));
			clip.setLinks(links);

			clipMap.put(clip.getcId(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

}
