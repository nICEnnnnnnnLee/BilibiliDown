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

/**
 * 下载号主自己的图文收藏（需要登录）
 * <p>https://space.bilibili.com/{spaceID}/favlist?fid=opus</p>
 * <p>https://space.bilibili.com/{spaceID}/favlist?fid=albumfav</p>
 *
 */
@Bilibili(name = "URL4PictureFavParser", note = "个人收藏的图文")
public class URL4PictureFavParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=(albumfav|opus)");
	private String spaceID;
	final private Object[] obj;

	public URL4PictureFavParser(Object... obj) {
		super(obj);
		this.obj = obj;
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			Logger.println("匹配个人收藏的图文");
			spaceID = matcher.group(1);
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
		API_PMAX = 10;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {
			String urlFormat = "https://api.bilibili.com/x/polymer/web-dynamic/v1/opus/favlist?page=%d&page_size=%d&timezone_offset=-480";
			String url = String.format(urlFormat, page, API_PMAX);
			HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();
			String json = util.getContent(url, headers, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(url);
			Logger.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray items = jobj.getJSONObject("data").getJSONArray("items");

			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor("我");
				pageQueryResult.setVideoName("我的收藏图文" + paramSetter.getPage());
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("收藏图文列表 - " + paramSetter.getPage());
			}
			if (items.length() == 0)
				return false;

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			URL4PictureOpusParser opusParser = new URL4PictureOpusParser(this.obj);
			for (int i = min - 1; i < items.length() && i < max; i++) {
				JSONObject item = items.getJSONObject(i);
				if (item.optBoolean("is_expired"))
					continue;
				String opusIdNumber = item.getString("opus_id");
				VideoInfo vArt = opusParser.getOpusDetail(opusIdNumber); // 这一步会发生网络请求，比较耗时
				for (ClipInfo clip : vArt.getClips().values()) {
					if (pageQueryResult.getVideoPreview() == null)
						pageQueryResult.setVideoPreview(clip.getPicPreview());
					// 如果是普通图文动态，那么设置ListName、ListOwnerName
					if(clip.getListName() == null) {
						clip.setListName("我的收藏图文");
						clip.setListOwnerName("我");
					}
					clip.setRemark(i);
					String uniqeNumber = opusIdNumber + clip.getPage();
					uniqeNumber = uniqeNumber.substring(2); // 防止超出long的范围
					map.put(Long.parseLong(uniqeNumber), clip);
				}
				Thread.sleep(100); // 通过抠html得到的，太频繁容易风控，除了给cookie外，只能先sleep试试了
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
