package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "AudioAuParser", note = "单个音频解析", weight=22)
public class AudioAuParser extends AbstractBaseParser {
	// e.g. https://www.bilibili.com/audio/au3688627
	private final static Pattern pattern = Pattern.compile("au([0-9]+)");
	private String auId;
	private long auIdNum;

	public AudioAuParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			auId = matcher.group();
			auIdNum = Long.parseLong(matcher.group(1));
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return auId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(auId);

		HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();
		String detailUrl = "https://www.bilibili.com/audio/music-service-c/web/song/info?sid=" + auIdNum;
		String detailJson = util.getContent(detailUrl, headers, HttpCookies.getGlobalCookies());
		Logger.println(detailUrl);
		Logger.println(detailJson);
		JSONObject detailObj = new JSONObject(detailJson).getJSONObject("data");

		long ctime = detailObj.optLong("passtime") * 1000;
		viInfo.setVideoName(detailObj.getString("title"));
		viInfo.setBrief(detailObj.getString("intro"));
		viInfo.setAuthor(detailObj.getString("author"));
		viInfo.setAuthorId(detailObj.optString("uid"));
		viInfo.setVideoPreview(detailObj.getString("cover"));

		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		ClipInfo clip = new ClipInfo();
		clip.setAvTitle(viInfo.getVideoName());
		clip.setAvId(auId);
		clip.setcId(auIdNum);
		clip.setPage(1);
		clip.setTitle(viInfo.getVideoName());
		clip.setPicPreview(viInfo.getVideoPreview());
		clip.setUpName(viInfo.getAuthor());
		clip.setUpId(viInfo.getAuthorId());
		clip.setcTime(ctime);
		LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
		int[] qnList = { 3, 2, 1, 0 };
		for (int qn : qnList) {
			links.put(qn, "");
		}
		clip.setLinks(links);

		clipMap.put(clip.getcId(), clip);
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;

	}

}
