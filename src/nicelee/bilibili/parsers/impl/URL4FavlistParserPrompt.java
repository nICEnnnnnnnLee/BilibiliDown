package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;

@Deprecated
//@Bilibili(name = "URL4FavlistParser_PromptTab", ifLoad = "promptAll", note = "收藏夹 - 采取弹出式")
public class URL4FavlistParserPrompt extends AbstractPageQueryParser<StringBuilder> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=([0-9]+)");// 个人收藏夹
	private String mlIdNumber;
	
	public URL4FavlistParserPrompt(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配收藏夹,返回 av1 av2 av3 ...");
			mlIdNumber = matcher.group(2);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return result(pageSize, paramSetter.getPage(), mlIdNumber).toString();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4FavlistParser_notUse 解析器不支持该方法！！");
		return null;
	}
	
	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new StringBuilder();
	}
	
	/**
	 * 
	 * <p>
	 * 查询第p 页的结果，将第min 到 第max 的数据加入 result
	 * </p>
	 * (此处每页大小为固定设置，与配置文件不一定相符)
	 * 
	 * @param begin
	 * @param end
	 * @param obj
	 * @return 查询成功/ 失败
	 */
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		String favID = (String) obj[0];
		try {
			// String urlFormat =
			// 					"https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/detail?media_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, favID, page, API_PMAX);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(favID),
					HttpCookies.getGlobalCookies());
			// System.out.println(url);
			// System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("medias");// .getJSONArray("archives");
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				pageQueryResult.append(" av").append(arr.getJSONObject(i).getLong("id"));
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
