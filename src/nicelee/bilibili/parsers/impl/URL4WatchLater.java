package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "URL4WatchLater", note = "稍后再看")
public class URL4WatchLater extends AbstractBaseParser{

	private final static Pattern pattern = Pattern
			.compile("www.bilibili.com/watchlater/#/list");

	public URL4WatchLater(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId("WatchLater");
		try {
			String url = "https://api.bilibili.com/x/v2/history/toview/web?jsonp=jsonp";
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"), HttpCookies.getGlobalCookies());
			Logger.println(url);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("list");

			viInfo.setVideoName("稍后再看");
			viInfo.setBrief("稍后再看");
			viInfo.setAuthor("稍后再看");
			viInfo.setAuthorId("稍后再看");
			viInfo.setVideoPreview(arr.getJSONObject(0).getString("pic"));

			LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jAV = arr.getJSONObject(i);
				String avId = jAV.getString("bvid");
				String avTitle =  jAV.getString("title");
				String upName = jAV.getJSONObject("owner").getString("name");
				String upId = "" + jAV.getJSONObject("owner").getLong("mid");
				JSONArray jClips = jAV.getJSONArray("pages");
				for(int pointer = 0; pointer < jClips.length(); pointer++) {
					JSONObject jClip = jClips.getJSONObject(pointer);
					ClipInfo clip = new ClipInfo();
					clip.setAvId(avId);
					clip.setcId(jClip.getLong("cid"));
					clip.setPage(jClip.getInt("page"));
					clip.setRemark(i);
					clip.setPicPreview(jAV.getString("pic"));
					clip.setUpName(upName);
					clip.setUpId(upId);
					clip.setAvTitle(avTitle);
					clip.setTitle(jClip.getString("part"));
					clip.setListName("稍后再看");
					
					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					try {
						for(VideoQualityEnum VQ: VideoQualityEnum.values()) {
							if (getVideoLink) {
								String link = getVideoLink(avId, String.valueOf(clip.getcId()), VQ.getQn(), videoFormat);
								links.put(VQ.getQn(), link);
							} else {
								links.put(VQ.getQn(), "");
							}
							//links.remove(116);
							//links.remove(74);
						}
					} catch (Exception e) {
					}
					clip.setLinks(links);
					
					map.put(clip.getcId(), clip);
				}
			}
			viInfo.setClips(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return viInfo;
	}
}
