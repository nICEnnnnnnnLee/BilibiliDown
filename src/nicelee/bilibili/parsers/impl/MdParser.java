package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "MdParser")
public class MdParser extends SSParser {

	private final static Pattern pattern = Pattern.compile("md[0-9]+");
	private String mdId;

	public MdParser(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			mdId = matcher.group();
			System.out.println("匹配MdParser: " + mdId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return mdId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("MdParser正在获取结果" + mdId);
		return getSSDetail(MdIdToSsId(mdId), videoFormat, getVideoLink);
	}
	
	/**
	 * 已知MdId, 求SsId 目前没有抓到api哦... 暂时从网页里面爬
	 * 
	 * @input HttpRequestUtil util
	 * @param mdId
	 * @return
	 */
	private String MdIdToSsId(String mdId) {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/media/" + mdId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		int ssId = jObj.getJSONObject("mediaInfo").getJSONObject("param").getInt("season_id");
		System.out.println("ssId为: " + ssId);
		return "ss" + ssId;
	}
}
