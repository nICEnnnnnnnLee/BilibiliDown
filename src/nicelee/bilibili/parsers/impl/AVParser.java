package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;


import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.convert.ConvertUtil;

@Bilibili(name = "av")
public class AVParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("av[0-9]+");
	private String avId;

//	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
	public AVParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			avId = matcher.group();
			System.out.println("匹配AVParser: " + avId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return avId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("AVParser正在获取结果" + avId);
		return getAVDetail(AvIdToBvId(avId), videoFormat, getVideoLink);
	}

	/**
	 * 已知avId, 求BvId 
	 * 
	 * @input HttpRequestUtil util
	 */
	private String AvIdToBvId(String avId) {
//		HttpHeaders headers = new HttpHeaders();
//		String url = "https://www.bilibili.com/video/" + avId;
//		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));
//
//		int begin = html.indexOf("window.__INITIAL_STATE__=");
//		int end = html.indexOf(";(function()", begin);
//		String json = html.substring(begin + 25, end);
//		System.out.println(json);
//		JSONObject jObj = new JSONObject(json);
//		String bvId = jObj.getString("bvid");
//		if(bvId.isEmpty()) {
//			bvId = jObj.getJSONObject("videoData").getString("bvid");
//		}
		String bvId = ConvertUtil.Av2Bv(Long.parseLong(avId.replace("av", "")));
		System.out.println("bvId为: " + bvId);
		return bvId;
	}
}
