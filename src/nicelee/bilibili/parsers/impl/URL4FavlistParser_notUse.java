package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "URL4FavlistParser_notUse", note = "收藏夹 - 采取弹出式")
public class URL4FavlistParser_notUse extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=([0-9]+)");// 个人收藏夹
	private String mlIdNumber;
	
	public URL4FavlistParser_notUse(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配收藏夹,返回 av1 av2 av3 ...");
			mlIdNumber = matcher.group(2);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return getAVList4FavList(mlIdNumber, paramSetter.getPage());
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4FavlistParser_notUse 解析器不支持该方法！！");
		return null;
	}
	
	/**
	 * 获取up主收藏夹的视频列表, 默认每页5个
	 * 
	 * @input HttpRequestUtil util
	 * @input int pageSize
	 * @param favID
	 * @param page
	 * @return
	 */
	public String getAVList4FavList(String favID, int page) {
		try {
			// String urlFormat =
			// 					"https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/detail?media_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, favID, page, pageSize);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(favID),
					HttpCookies.getGlobalCookies());
			// System.out.println(url);
			// System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("medias");// .getJSONArray("archives");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arr.length(); i++) {
				sb.append(" av").append(arr.getJSONObject(i).getLong("id"));
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
}
