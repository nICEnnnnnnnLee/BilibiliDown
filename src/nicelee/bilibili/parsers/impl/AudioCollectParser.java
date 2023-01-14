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

@Bilibili(name = "AudioCollectParser", note = "自己创建的歌单")
public class AudioCollectParser extends AbstractPageQueryParser<VideoInfo> {
	// https://www.bilibili.com/audio/mycollection/[0-9]+ (必须要登录)
	private final static Pattern pattern = Pattern.compile("/audio/mycollection/([0-9]+)");
	private String sid;

	public AudioCollectParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			sid = matcher.group(1);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return matcher.group() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		Logger.println(paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 10;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {

			HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();

			// 设置av信息
			if (pageQueryResult.getVideoName() == null) {
				String url = "https://www.bilibili.com/audio/music-service-c/web/collections/info?sid=" + sid;
				String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
				Logger.println(url);
				Logger.println(json);
				JSONObject jobj = new JSONObject(json).getJSONObject("data");
				pageQueryResult.setVideoId("auCollect" + sid);
				pageQueryResult.setAuthor(jobj.getString("uname"));
				pageQueryResult.setVideoName(jobj.getString("title"));
				pageQueryResult.setVideoPreview(jobj.getString("cover"));
				pageQueryResult.setAuthorId(jobj.optString("uid"));
				pageQueryResult.setBrief(jobj.getString("desc"));
			}

			String urlFormat = "https://www.bilibili.com/audio/music-service-c/web/song/of-coll?sid=%s&pn=%d&ps=%d";
			// https://www.bilibili.com/audio/music-service-c/web/song/of-coll?sid=6683938&pn=1&ps=10
			// https://www.bilibili.com/audio/music-service-c/web/song/of-coll??sid=6683938&pn=1&ps=10
			String url = String.format(urlFormat, sid, page, API_PMAX);

			String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
			Logger.println(url);
			Logger.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("data");

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jContent = arr.getJSONObject(i);
				ClipInfo clip = new ClipInfo();
				long ctime = jContent.optLong("passtime") * 1000;
				long auIdNum = jContent.optLong("id");
				clip.setAvTitle(jContent.getString("title"));
				clip.setAvId("au" + auIdNum);
				clip.setcId(auIdNum);
				clip.setPage(1);
				clip.setTitle(jContent.getString("title"));
				clip.setPicPreview(jContent.getString("cover"));
				clip.setRemark((page - 1) * API_PMAX + i + 1);
				clip.setUpName(jContent.getString("uname"));
				clip.setUpId(jContent.optString("uid"));

//				clip.setAvTitle(pageQueryResult.getVideoName());
//				clip.setTitle(avTitle + "-" +jClip.getString("title"));
				clip.setListName(pageQueryResult.getVideoName().replaceAll("[/\\\\]", "_"));
				clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
				clip.setcTime(ctime);
				LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
				int[] qnList = { 3, 2, 1, 0 };
				for (int qn : qnList) {
					links.put(qn, "");
				}
				clip.setLinks(links);
				map.put(clip.getcId(), clip);

			}
			return true;
		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}
}
