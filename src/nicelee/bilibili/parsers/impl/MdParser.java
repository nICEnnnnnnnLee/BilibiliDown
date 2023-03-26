package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

//@Bilibili(name = "MdParser")
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
	 * @see https://www.bilibili.com/bangumi/media/md134912
	 * 		https://api.bilibili.com/pgc/review/user?media_id=134912
	 *      https://api.bilibili.com/pgc/web/season/section?season_id=25617
	 * @input HttpRequestUtil util
	 * @param mdId
	 * @return
	 */
	private String MdIdToSsId(String mdId) {
		HttpHeaders headers = new HttpHeaders();
		String mdIdNumber = mdId.replace("md", "");
		String url = "https://api.bilibili.com/pgc/review/user?media_id=" + mdIdNumber;
		String result = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		System.out.println(result);
		JSONObject jObj = new JSONObject(result);
		int ssId = jObj.getJSONObject("result").getJSONObject("media").getInt("season_id");
		System.out.println("ssId为: " + ssId);
		return "ss" + ssId;
	}
}
