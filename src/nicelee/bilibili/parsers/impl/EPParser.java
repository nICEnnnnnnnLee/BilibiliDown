package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "ep")
public class EPParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ep[0-9]+");
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
		return getAVDetail(EpIdToAvId(epId), videoFormat, getVideoLink);
	}
	
	/**
	 * 已知epId, 求avId 目前没有抓到api哦... 暂时从网页里面爬
	 * 
	 * @input HttpRequestUtil util
	 */
	private String EpIdToAvId(String epId) {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + epId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		int avId = jObj.getJSONObject("epInfo").getInt("aid");
		System.out.println("avId为: " + avId);
		return "av" + avId;
	}

}
