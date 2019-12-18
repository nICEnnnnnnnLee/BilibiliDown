package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "ss")
public class SSParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ss[0-9]+");
	private String ssId;

	public SSParser(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			ssId = matcher.group();
			System.out.println("匹配SSParser: " + ssId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return ssId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("SSParser正在获取结果" + ssId);
		return getSSDetail(ssId, videoFormat, getVideoLink);
	}
	
	/**
	 * 
	 * @input HttpRequestUtil util
	 * @param ssId
	 * @param isGetLink
	 * @return
	 */
	protected VideoInfo getSSDetail(String ssId, int videoFormat, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(ssId);
		
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + ssId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		Logger.println(url);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json);
		viInfo.setVideoName(jObj.getJSONObject("mediaInfo").getString("title"));
		viInfo.setBrief(jObj.getJSONObject("mediaInfo").getString("evaluate"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview("https:" + jObj.getJSONObject("mediaInfo").getString("cover"));
		
		JSONArray array = jObj.getJSONArray("epList");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		ClipInfo lastClip = null;
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId("av" + clipObj.getInt("aid"));
			clip.setcId(clipObj.getLong("cid"));
			//clip.setPage(Integer.parseInt(clipObj.getString("index")));
			clip.setRemark(clipObj.getInt("i") + 1);
			clip.setPicPreview("https:" +clipObj.getString("cover"));
			//如果和前面avid一致，那么是前者page + 1, 否则为 1
			if(i > 0 && array.getJSONObject(i-1).getInt("aid") == clipObj.getInt("aid")) {
				clip.setPage(lastClip.getPage() + 1);
			}else {
				clip.setPage(1);
			}
			//clip.setTitle(clipObj.getString("index_title"));
			clip.setTitle(clipObj.getString("longTitle"));
			clip.setUpName(viInfo.getVideoName());
			clip.setUpId(ssId);
			
			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = getVideoQNList(clip.getAvId(), String.valueOf(clip.getcId()));
				for (int qn : qnList) {
					if (isGetLink) {
						String link = getVideoLink(clip.getAvId(), String.valueOf(clip.getcId()), qn, videoFormat);
						links.put(qn, link);
					} else {
						links.put(qn, "");
					}
				}
				clip.setLinks(links);
			}catch (Exception e) {
				//e.printStackTrace();
				clip.setLinks(links);
			}
			lastClip = clip;
			clipMap.put(clip.getcId(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

}
