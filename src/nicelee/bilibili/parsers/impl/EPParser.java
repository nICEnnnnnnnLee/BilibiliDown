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
	 * 已知epId, 求bvId 目前没有抓到api哦... 暂时从网页里面爬
	 * 
	 * @input HttpRequestUtil util
	 */
	private String EpIdToBvId(String epId) {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + epId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("__NEXT_DATA__");
		begin = html.indexOf(">", begin);
		int end = html.indexOf("</script>", begin);
		String json = html.substring(begin + 1, end);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("props").getJSONObject("pageProps")
				.getJSONObject("dehydratedState").getJSONArray("queries").getJSONObject(0)
				.getJSONObject("state").getJSONObject("data").getJSONObject("mediaInfo");
		JSONArray array = jObj.getJSONArray("episodes");
		for (int i = 0; i < array.length(); i++) {
			JSONObject ep = array.getJSONObject(i);
			if(ep.getString("link").endsWith(epId)) {
				String bvid = ep.getString("bvid");
				Logger.println("bvId为: " + bvid);
				return bvid;
			}
		}
		throw new RuntimeException("No epId found in the page");
	}

}
