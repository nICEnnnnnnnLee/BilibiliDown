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

/**
 *  针对的是如下类型的url
 * https://space.bilibili.com/593987248/channel/collectiondetail?sid=508765
 *
 *
 */
//@Bilibili(name = "URL4ChannelCollectionParser", ifLoad = "listAll", note = "UP 某合集的视频解析")
public class URL4ChannelCollectionParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)/channel/collectiondetail\\?sid=([0-9]+)");
	private String spaceID;
	private String sid;

	public URL4ChannelCollectionParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("UP 某合集的视频解析 ...");
			spaceID = matcher.group(1);
			sid = matcher.group(2);

			return true;
		} else {
			return false;
		}

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
		API_PMAX = 20;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			String urlFormat = "https://api.bilibili.com/x/polymer/space/seasons_archives_list?mid=%s&season_id=%s&sort_reverse=false&page_num=%d&page_size=%d";
			String url = String.format(urlFormat, spaceID, sid, page, API_PMAX);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
			Logger.println(url);
			JSONObject jobj = new JSONObject(json).getJSONObject("data");
			JSONObject jData = jobj.getJSONObject("meta");
			JSONArray arr = jobj.getJSONArray("archives");

			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID + " - " + sid);
				pageQueryResult.setVideoName(jData.getString("name") + page);
				pageQueryResult.setBrief(jData.getString("description"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setVideoPreview(jData.getString("cover"));
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				try {
					// try给包围，出现 稿件不可见等已失效视频 的异常跳过即可
					// 进行二次查询，增加的网络请求次数为 pageSize * getAVDetail方法的请求数
					LinkedHashMap<Long, ClipInfo> avClips = convertVideoToClipMap(jAV.getString("bvid"),
							(page - 1) * API_PMAX + i + 1, videoFormat, getVideoLink);
					// >= V3.6, ClipInfo 增加可选ListXXX字段，将合集信息移入其中
					for (ClipInfo clip : avClips.values()) {
						clip.setListName(jData.getString("name").replaceAll("[/\\\\]", "_"));
						clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
					}
					map.putAll(avClips);
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
		}
		for (ClipInfo clip : video.getClips().values()) {
			clip.setRemark(remark);
			map.put(clip.getcId(), clip);
		}
		return map;
	}
}
