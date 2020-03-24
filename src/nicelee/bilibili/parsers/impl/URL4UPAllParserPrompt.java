package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Deprecated
//@Bilibili(name = "URL4UPAllParser", ifLoad = "promptAll", note = "个人上传的视频列表")
public class URL4UPAllParserPrompt extends AbstractPageQueryParser<StringBuilder> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)(/video|/? *$|\\?)");
	private String spaceID;

	public URL4UPAllParserPrompt(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页全部视频,返回 av1 av2 av3 ...");
			spaceID = matcher.group(1);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String validStr(String input) {
		//return getAVList4Space(spaceID, paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), spaceID).toString();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4UPAllParser 解析器不支持该方法！！");
		return null;
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new StringBuilder();
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {
			String urlFormat = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=%s&pagesize=%d&tid=0&page=%d&keyword=&order=pubdate";
			String url = String.format(urlFormat, spaceID, API_PMAX, page);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("space.bilibili.com"));
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("vlist");
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				pageQueryResult.append(" av").append(arr.getJSONObject(i).getLong("aid"));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
