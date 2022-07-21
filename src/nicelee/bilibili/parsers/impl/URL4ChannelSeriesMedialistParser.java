package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 针对以下类型的url解析
 * https://www.bilibili.com/medialist/play/378034?from=space&business=space_series&business_id=918669&desc=1
 *
 * 这种方法比URL4ChannelSeriesParser要好一点，因为不需要为每一个BV再进行一次查询
 * 
 * 查信息
 * 	https://api.bilibili.com/x/v1/medialist/info?type=5&biz_id=918669&tid=0
 * 	查BV详情  
 *  	先查询oid
 *  https://api.bilibili.com/x/series/archives?mid=%s&series_id=%s&only_normal=true&sort=desc&pn=%d&ps=%d
 *  	再查询所有
 *  https://api.bilibili.com/x/v2/medialist/resource/list?type=5&oid=1084862&otype=2&biz_id=918669&bvid=&with_current=true&mobi_app=web&ps=20&direction=false&sort_field=1&tid=0&desc=true
 */
@Bilibili(name = "URL4ChannelSeriesMedialistParser", weight = 78,ifLoad = "listAll", note = "UP 某视频列表的视频解析")
public class URL4ChannelSeriesMedialistParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("www\\.bilibili\\.com/medialist/play/([0-9]+)\\?.*&business_id=([0-9]+)");
	private final static Pattern pattern2 = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)/channel/seriesdetail\\?sid=([0-9]+)");
	private String spaceID;
	private String sid;
	private String seriesName;

	public URL4ChannelSeriesMedialistParser(Object... obj) {
		super(obj);
	}

	private boolean maches(String input, Pattern pattern) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主合集和视频列表 ...");
			spaceID = matcher.group(1);
			sid = matcher.group(2);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean matches(String input) {
		return maches(input, pattern) || maches(input, pattern2);
	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim() + "&p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {

		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			// 先获取合集信息
			HashMap<String, String> headers = new HttpHeaders().getCommonHeaders("api.bilibili.com");
			if (pageQueryResult.getVideoName() == null) {
				String url = "https://api.bilibili.com/x/v1/medialist/info?type=5&tid=0&biz_id=" + sid;
				Logger.println(url);
				String json = util.getContent(url, headers);
				JSONObject jobj = new JSONObject(json).getJSONObject("data");
				seriesName = jobj.getString("title");
				pageQueryResult.setVideoId(spaceID + " - " + sid);
				pageQueryResult.setVideoName(seriesName + paramSetter.getPage());
				pageQueryResult.setBrief(jobj.getString("intro"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setAuthor(jobj.getJSONObject("upper").getString("name"));
			}
			// 获取oid(返回结果的第一个视频id)
			String oid = "";
			if (page > 1) {
				int lastPageNumber = (page - 1) * API_PMAX + 1;
				String urlFormat = "https://api.bilibili.com/x/series/archives?mid=%s&series_id=%s&only_normal=true&sort=desc&pn=%d&ps=%d";
				String url = String.format(urlFormat, spaceID, sid, lastPageNumber, 1);
				Logger.println(url);
				String json = util.getContent(url, headers);
				oid = new JSONObject(json).getJSONObject("data").getJSONArray("archives").getJSONObject(0)
						.optString("aid");
				System.out.printf("page: %d, lastPageNumber: %d, oid: %s\n", page, lastPageNumber, oid);
			}

			// 查询详细信息
			String urlFormat = "https://api.bilibili.com/x/v2/medialist/resource/list?type=5&oid=%s&otype=2&biz_id=%s&with_current=true&mobi_app=web&ps=%s&direction=false&sort_field=1&tid=0&desc=true";
			String url = String.format(urlFormat, oid, sid, API_PMAX);
			Logger.println(url);
			String json = util.getContent(url, headers);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("media_list");

			if (pageQueryResult.getVideoPreview() == null) {
				pageQueryResult.setVideoPreview(arr.getJSONObject(0).getString("cover"));
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				String avId = jAV.getString("bv_id");
				String avTitle = jAV.getString("title");
				String upName = jAV.getJSONObject("upper").getString("name");
				String upId = jAV.getJSONObject("upper").optString("mid");
				long cTime = jAV.optLong("pubtime") * 1000;
				JSONArray jClips = jAV.optJSONArray("pages");
				if (jClips == null) {
					continue;
				}
				for (int pointer = 0; pointer < jClips.length(); pointer++) {
					JSONObject jClip = jClips.getJSONObject(pointer);
					ClipInfo clip = new ClipInfo();
					clip.setAvId(avId);
					clip.setcId(jClip.getLong("id"));
					clip.setPage(jClip.getInt("page"));
					clip.setRemark((page - 1) * API_PMAX + i + 1);
					clip.setPicPreview(jAV.getString("cover"));
					// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
					clip.setListName(seriesName.replaceAll("[/\\\\]", "_"));
					clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
					clip.setUpName(upName);
					clip.setUpId(upId);
					clip.setAvTitle(avTitle);
					clip.setTitle(jClip.getString("title"));
					clip.setcTime(cTime);

					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					try {
						for (VideoQualityEnum VQ : VideoQualityEnum.values()) {
							if (getVideoLink) {
								String link = getVideoLink(avId, String.valueOf(clip.getcId()), VQ.getQn(),
										videoFormat);
								links.put(VQ.getQn(), link);
							} else {
								links.put(VQ.getQn(), "");
							}
						}
					} catch (Exception e) {
					}
					clip.setLinks(links);

					map.put(clip.getcId(), clip);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
