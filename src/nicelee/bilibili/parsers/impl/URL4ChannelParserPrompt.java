package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "URL4ChannelParser", ifLoad = "promptAll", note = "UP 某主题频道文件夹的视频解析")
public class URL4ChannelParserPrompt extends AbstractPageQueryParser<StringBuilder> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/channel/detail\\?cid=([0-9]+)");
	private String spaceID;
	private String cid;
	
	public URL4ChannelParserPrompt(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if(matcher.find()) {
			System.out.println("匹配UP主主页特定频道,返回 av1 av2 av3 ...");
			spaceID = matcher.group(1);
			cid = matcher.group(2);
			
			return true;
		}else {
			return false;
		}
		
	}

	@Override
	public String validStr(String input) {
		//return getAVList4Channel(spaceID, cid, paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), spaceID, cid).toString();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4ChannelParser 解析器不支持该方法！！");
		return null;
	}
	
	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new StringBuilder();
	}
	
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		String spaceID = (String) obj[0];
		String cid = (String) obj[1];
		try {
			String urlFormat = "https://api.bilibili.com/x/space/channel/video?mid=%s&cid=%s&pn=%d&ps=%d&order=0";
			String url = String.format(urlFormat, spaceID, cid, page, API_PMAX);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONObject("list").getJSONArray("archives");
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				pageQueryResult.append(" av").append(arr.getJSONObject(i).getString("bvid"));
			}
			return true;
		}catch (Exception e) {
			return false;
		}
	}
}
