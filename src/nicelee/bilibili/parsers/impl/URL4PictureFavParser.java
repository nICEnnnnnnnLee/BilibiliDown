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

@Bilibili(name = "URL4PictureFavParser", note = "个人收藏的相簿")
public class URL4PictureFavParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=albumfav");
	private String spaceID;

	public URL4PictureFavParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配个人收藏的相簿");
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
		Logger.println(paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 30;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {
			String urlFormat = "https://api.vc.bilibili.com/user_plus/v1/Fav/getMyFav?biz_type=2&page=%d&pagesize=%d&_=%d";
			String url = String.format(urlFormat, page, API_PMAX, System.currentTimeMillis());
			HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();
			headers.put("Origin", "https://space.bilibili.com");
			headers.put("Referer", "https://space.bilibili.com/"+ spaceID +"/favlist?fid=albumfav");
			String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("list");

			// 设置av信息
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor("我");
				pageQueryResult.setVideoName("我的收藏相簿"+ paramSetter.getPage());
				pageQueryResult.setVideoPreview(arr.getJSONObject(0).getJSONObject("content").getJSONObject("item").getJSONArray("pictures").getJSONObject(0).getString("img_src"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("相簿列表 - " + paramSetter.getPage());
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jContent = arr.getJSONObject(i).getJSONObject("content");
				JSONObject jAlbum = jContent.getJSONObject("item");
				JSONArray jPics = jAlbum.getJSONArray("pictures");
				String upperName = jContent.getJSONObject("user").getString("name");
				String upperId = "" + jContent.getJSONObject("user").getLong("uid");
				for (int j = 0; j < jPics.length(); j++) {
					JSONObject jPic = jPics.getJSONObject(j);
					ClipInfo clip = new ClipInfo();
					String title = jAlbum.getString("title");
					if(title.isEmpty()) {
						title = jAlbum.getString("description");
						if(title.length() > 15)
							title = title.substring(0, 15);
					}
					clip.setAvTitle(title);
					Logger.println(jAlbum.getString("title"));
					clip.setAvId("h" + jAlbum.getLong("doc_id"));
					clip.setcId(j);
					clip.setPage(j);
					clip.setTitle("第" + j + "张");
					clip.setPicPreview(jPic.getString("img_src"));
					clip.setRemark((page-1)*API_PMAX + i + 1);
					clip.setUpName(upperName);
					clip.setUpId(upperId);
					
					
					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					links.put(0, jPic.getString("img_src"));
					clip.setLinks(links);
					map.put((long) (clip.getRemark()*100 + j), clip);
				}
				
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}
}
