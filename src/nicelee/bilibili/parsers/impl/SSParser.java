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

//@Bilibili(name = "SSParser")
public class SSParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("(?!/cheese/play/ss[0-9]+)ss[0-9]+");
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
	 * @see https://www.bilibili.com/bangumi/media/md134912
	 * 		https://api.bilibili.com/pgc/review/user?media_id=139252&ts=...
	 *      https://api.bilibili.com/pgc/view/web/season?ep_id=250479
	 * @input HttpRequestUtil util
	 * @param ssId
	 * @param isGetLink
	 * @return
	 */
	protected VideoInfo getSSDetail(String ssId, int videoFormat, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(ssId);
		
		String ssIdNumber = ssId.replace("ss", "");
		HttpHeaders headers = new HttpHeaders();
		String url = "https://api.bilibili.com/pgc/view/web/season?season_id=" + ssIdNumber;
		String json = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		Logger.println(url);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("result");
		viInfo.setVideoName(jObj.getString("title"));
		viInfo.setBrief(jObj.getString("evaluate"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview(jObj.getString("cover"));
		
		JSONArray array = jObj.getJSONArray("episodes");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		ClipInfo lastClip = null;
		int[] qnListDefault = null;
		if (array.length() > 20) {
			qnListDefault = new int[] { 120, 116, 112, 80, 74, 64, 32, 16 };
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(clipObj.getString("bvid"));
			clip.setcId(clipObj.getLong("cid"));
			//clip.setPage(Integer.parseInt(clipObj.getString("index")));
			clip.setRemark(i + 1);
			clip.setPicPreview(clipObj.getString("cover"));
			//如果和前面avid一致，那么是前者page + 1, 否则为 1
			if(i > 0 && array.getJSONObject(i-1).getString("bvid").equals(clipObj.getString("bvid"))) {
				clip.setPage(lastClip.getPage() + 1);
			}else {
				clip.setPage(1);
			}
			//clip.setTitle(clipObj.getString("index_title"));
			clip.setTitle(clipObj.getString("long_title"));
			clip.setUpName(viInfo.getVideoName());
			clip.setUpId(ssId);
			
			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = qnListDefault != null ? qnListDefault
						: getVideoQNList(clip.getAvId(), String.valueOf(clip.getcId()));
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
