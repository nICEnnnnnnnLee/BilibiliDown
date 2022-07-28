package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 针对以下类型的url解析
 * https://space.bilibili.com/378034/channel/seriesdetail?sid=918669
 *
 * 分页查询的每页个数不宜过多，因为会为每一个BV进行一次查询。这样做是为了确保多P视频的完整
 * 请务必不要在短时间内新建大量此类Tab页，容易被BAN
 * 
 * 另一个思路是:
 * 	查信息
 * 	https://api.bilibili.com/x/v1/medialist/info?type=5&biz_id=918669&tid=0
 * 	查BV详情  但是这里分页以后，并不是根据page number来查的，而是根据oid(上一次查询最后的视频id)，这不好实现
 * 	https://api.bilibili.com/x/v2/medialist/resource/list?type=5&oid=57829969&otype=2&biz_id=918669&bvid=&with_current=true&mobi_app=web&ps=20&direction=false&sort_field=1&tid=0&desc=true
 */
//@Bilibili(name = "URL4ChannelSeriesParser", ifLoad = "listAll", note = "UP 某合集和视频列表的视频解析")
public class URL4ChannelSeriesParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)/channel/seriesdetail\\?sid=([0-9]+)");
	private String spaceID;
	private String sid;

	public URL4ChannelSeriesParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
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
				String url = "https://api.bilibili.com/x/series/series?series_id=" + sid;
				Logger.println(url);
				String json = util.getContent(url, headers);
				JSONObject jobj = new JSONObject(json).getJSONObject("data").getJSONObject("meta");
				pageQueryResult.setVideoId(spaceID + " - " + sid);
				pageQueryResult.setVideoName(jobj.getString("name") + paramSetter.getPage());
				pageQueryResult.setBrief(jobj.getString("description"));
				pageQueryResult.setAuthorId(spaceID);
				
//				url = "https://api.bilibili.com/x/space/acc/info?jsonp=jsonp&mid=" + spaceID;
//				Logger.println(url);
//				json = util.getContent(url, headers);
//				jobj = new JSONObject(json).getJSONObject("data");
//				pageQueryResult.setAuthor(jobj.getString("name"));
				
			}
			String urlFormat = "https://api.bilibili.com/x/series/archives?mid=%s&series_id=%s&only_normal=true&sort=desc&pn=%d&ps=%d";
			String url = String.format(urlFormat, spaceID, sid, page, API_PMAX);
			String json = util.getContent(url, headers);
			Logger.println(url);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("archives");

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				try {
					// 进行二次查询，增加请求次数为 pageSize
					map.putAll(convertVideoToClipMap(jAV.getString("bvid"), (page - 1) * API_PMAX + i + 1, videoFormat,
							getVideoLink));
				} catch (Exception e) {
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用此方法会产生许多请求，慎用
	 * @param bvId
	 * @param remark
	 * @param videoFormat
	 * @param getVideoLink
	 * @return 将所有avId的视频封装成Map
	 */
	private LinkedHashMap<Long, ClipInfo> convertVideoToClipMap(String bvId, int remark, int videoFormat,
			boolean getVideoLink) {
		LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
		VideoInfo video = getAVDetail(bvId, videoFormat, getVideoLink); // 耗时
		if (pageQueryResult.getAuthor() == null) {
			pageQueryResult.setAuthor(video.getAuthor());
			pageQueryResult.setVideoPreview(video.getVideoPreview());
		}
		for (ClipInfo clip : video.getClips().values()) {
			// clip.setTitle(clip.getAvTitle() + "-" + clip.getTitle());
			// clip.setAvTitle(pageQueryResult.getVideoName());
			// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
			clip.setListName(pageQueryResult.getVideoName().replaceAll("[/\\\\]", "_"));
			clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));

			clip.setRemark(remark);
			map.put(clip.getcId(), clip);
		}
		return map;
	}
}
