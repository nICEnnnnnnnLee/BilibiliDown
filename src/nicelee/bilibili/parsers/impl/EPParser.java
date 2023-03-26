package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

//@Bilibili(name = "EPParser")
public class EPParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("(?!/cheese/play/ep[0-9]+)ep[0-9]+");
	private String epId;

	//public EPParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize)  {
	public EPParser(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			epId = matcher.group();
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return epId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getAVDetail(EpIdToBvId(epId), videoFormat, getVideoLink);
	}
	
	/**
	 * @see https://www.bilibili.com/bangumi/media/md134912
	 * 		https://api.bilibili.com/pgc/view/web/season?ep_id=250435
	 * @input HttpRequestUtil util
	 */
	private String EpIdToBvId(String epId) {
		HttpHeaders headers = new HttpHeaders();
		String epIdNumber = epId.replace("ep", "");
		String url = "https://api.bilibili.com/pgc/view/web/season?ep_id=" + epIdNumber;
		String json = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("result");
		JSONArray array = jObj.getJSONArray("episodes");
		for (int i = 0; i < array.length(); i++) {
			JSONObject ep = array.getJSONObject(i);
			if(epIdNumber.equals(ep.optString("id"))) {
				String bvid = ep.getString("bvid");
				Logger.println("bvId为: " + bvid);
				return bvid;
			}
		}
		throw new RuntimeException("No epId found in the page");
	}

}
