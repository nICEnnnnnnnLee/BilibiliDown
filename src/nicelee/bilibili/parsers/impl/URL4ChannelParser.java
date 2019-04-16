package nicelee.bilibili.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "URL4ChannelParser",
		note = "UP 某主题频道文件夹的视频解析")
public class URL4ChannelParser extends AVParser {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/channel/detail\\?cid=([0-9]+)");
	private String spaceID;
	private String cid;
	
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if(matcher.find()) {
			System.out.println("匹配UP主主页特定频道,返回 av1 av2 av3 ...");
			spaceID = matcher.group(1);
			cid = matcher.group(2);
			
			Matcher paramMatcher = paramPattern.matcher(input);
			if (paramMatcher.find()) {
				page = Integer.parseInt(paramMatcher.group(1));
			}
			return true;
		}else {
			return false;
		}
		
	}

	@Override
	public String validStr(String input) {
		return getAVList4Channel(spaceID, cid, page);
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4ChannelParser 解析器不支持该方法！！");
		return null;
	}
	
	/**
	 * 获取up主个人上传的特定频道的视频列表, 默认每页5个
	 * 
	 * @input HttpRequestUtil util
	 * @input int pageSize
	 * @param spaceID
	 * @param cid
	 * @param page
	 * @return
	 */
	public String getAVList4Channel(String spaceID, String cid, int page) {
		String urlFormat = "https://api.bilibili.com/x/space/channel/video?mid=%s&cid=%s&pn=%d&ps=%d&order=0";
		String url = String.format(urlFormat, spaceID, cid, page, pageSize);
		String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
		JSONObject jobj = new JSONObject(json);
		JSONArray arr = jobj.getJSONObject("data").getJSONObject("list").getJSONArray("archives");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length(); i++) {
			sb.append(" av").append(arr.getJSONObject(i).getLong("aid"));
		}
		return sb.toString();
	}
}
