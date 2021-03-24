package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "CheeseEPParser", note = "课程单集")
public class CheeseEPParser extends AbstractBaseParser{

	private final static Pattern pattern = Pattern.compile("/cheese/play/ep([0-9]+)");
	private String epId;

	public CheeseEPParser(Object... obj) {
		super(obj);
	}
	
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			epId = matcher.group(1);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		String validInput = "/cheese/play/ss" + EpIdToSSId(epId);
		return validInput;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		Logger.println("默认转化为seasonId,该部分未实现");
		return null;
	}
	
	private String EpIdToSSId(String epId) {
		String url = "https://api.bilibili.com/pugv/view/web/season?ep_id=" + epId;
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Origin", "https://www.bilibili.com");
		headers.put("Referer", "https://www.bilibili.com/cheese/play/ep" + epId);
		String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
		return new JSONObject(json).getJSONObject("data").optString("season_id");
	}
}
