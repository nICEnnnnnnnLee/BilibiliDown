package nicelee.bilibili.parsers.impl;

//public class URL4ChannelCollectionExParser{
//	
//}
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

/**
 *  针对的是如下类型的url
 * https://space.bilibili.com/593987248/channel/collectiondetail?sid=508765
 * https://space.bilibili.com/{spaceID}/favlist?fid=405855&ftype=collect&ctype=21
 *
 *		- https://api.bilibili.com/x/space/fav/season/list?season_id=405855&pn=2&ps=20&jsonp=jsonp
 */
@Bilibili(name = "URL4ChannelCollectionParserEx", weight = 77, ifLoad = "listAll", note = "UP 某合集的视频解析")
public class URL4ChannelCollectionParserEx extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("space\\.bilibili\\.com/[0-9]+/channel/collectiondetail\\?sid=([0-9]+)");
	private final static Pattern pattern2 = Pattern.compile("space\\.bilibili\\.com/[0-9]+/favlist\\?.*fid=([0-9]+).*ftype=collect");
	private String sid;

	public URL4ChannelCollectionParserEx(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("UP 某合集的视频解析(优化版) ...pattern");
			sid = matcher.group(1);
			return true;
		}
		matcher = pattern2.matcher(input);
		if (matcher.find()) {
			System.out.println("UP 某合集的视频解析(优化版) ...pattern2");
			sid = matcher.group(1);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {

		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 9999;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			// 先找到部分信息和第一个BV
			// 第一个BV的页面里面有所有的信息
			String urlFormat = "https://api.bilibili.com/x/polymer/space/seasons_archives_list?mid=%s&season_id=%s&sort_reverse=false&page_num=%d&page_size=%d";
			String url = String.format(urlFormat, "1234567", sid, 1, 1);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
			Logger.println(url);
			JSONObject jobj = new JSONObject(json).getJSONObject("data");
			JSONObject jData = jobj.getJSONObject("meta");
			String firstBV = jobj.getJSONArray("archives").getJSONObject(0).getString("bvid");

			// 在第一部BV里面补全up的信息，并得到所有合集
			String urlBV = "https://www.bilibili.com/video/" + firstBV;
			String html = util.getContent(urlBV, new HttpHeaders().getCommonHeaders("www.bilibili.com"), HttpCookies.globalCookiesWithFingerprint());
			int begin = html.indexOf("window.__INITIAL_STATE__=");
			int end = html.indexOf(";(function()", begin);
			String result = html.substring(begin + 25, end);
			// Logger.println(result);
			JSONObject obj1 = new JSONObject(result);
			JSONObject author = obj1.getJSONObject("upData");
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId("sid - " + sid);
				pageQueryResult.setVideoName(jData.getString("name") + page);
				pageQueryResult.setBrief(jData.getString("description"));
				pageQueryResult.setAuthorId(author.optString("mid"));
				pageQueryResult.setAuthor(author.optString("name"));
				pageQueryResult.setVideoPreview(jData.getString("cover"));
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			JSONArray sections = obj1.optJSONArray("sections");
			if(sections == null) {
				sections = obj1.getJSONObject("sectionsInfo").getJSONArray("sections");
			}
			boolean stop = false;
			for (int order = 0, sIndex = 0; sIndex < sections.length(); sIndex++) {
				JSONArray episodes = sections.getJSONObject(sIndex).getJSONArray("episodes");
				for (int eIndex = 0; eIndex < episodes.length(); eIndex++) {
					if (order + 1 < min) {
						order++;
						continue;
					}
					if (order + 1 > max) {
						stop = true;
						break;
					}
					JSONObject episode = episodes.getJSONObject(eIndex);
					JSONObject pageJ = episode.getJSONObject("page");
					JSONObject arc = episode.getJSONObject("arc");
					ClipInfo clip = new ClipInfo();
					clip.setAvId(episode.getString("bvid"));
					clip.setcId(episode.optLong("cid"));
					clip.setPage(pageJ.getInt("page"));
					clip.setRemark(order + 1);
					clip.setPicPreview(arc.getString("pic"));
					clip.setListName(jData.getString("name").replaceAll("[/\\\\]", "_"));
					clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
					clip.setUpName(pageQueryResult.getAuthor());
					clip.setUpId(pageQueryResult.getAuthorId());
					clip.setAvTitle(episode.getString("title"));
					clip.setTitle(pageJ.getString("part"));
					clip.setcTime(arc.optLong("ctime") * 1000);

					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					try {
						for (VideoQualityEnum VQ : VideoQualityEnum.values()) {
							if (getVideoLink) {
								String link = getVideoLink(clip.getAvId(), String.valueOf(clip.getcId()), VQ.getQn(),
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
					order++;
				}
				if (stop)
					break;
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}
}
