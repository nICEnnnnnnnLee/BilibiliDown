package nicelee.bilibili.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "URL4UPAllParser",
		note = "个人上传的视频列表")
public class URL4UPAllParser extends AVParser {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/video");
	private String spaceID;
	
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if(matcher.find()) {
			System.out.println("匹配UP主主页全部视频,返回 av1 av2 av3 ...");
			spaceID = matcher.group(1);
			
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
		return getAVList4Space(spaceID, page);
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4UPAllParser 解析器不支持该方法！！");
		return null;
	}
	
	/**
	 * 获取up主个人上传的视频列表, 默认每页5个
	 * 
	 * @input HttpRequestUtil util
	 * @input int pageSize
	 * @param spaceID
	 * @param page
	 * @return
	 */
	public String getAVList4Space(String spaceID, int page) {
		String urlFormat = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=%s&pagesize=%d&tid=0&page=%d&keyword=&order=pubdate";
		String url = String.format(urlFormat, spaceID, pageSize, page);
		String json = util.getContent(url, new HttpHeaders().getCommonHeaders("space.bilibili.com"));
		System.out.println(url);
		System.out.println(json);
		JSONObject jobj = new JSONObject(json);
		JSONArray arr = jobj.getJSONObject("data").getJSONArray("vlist");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length(); i++) {
			sb.append(" av").append(arr.getJSONObject(i).getLong("aid"));
		}
		return sb.toString();
	}
}
