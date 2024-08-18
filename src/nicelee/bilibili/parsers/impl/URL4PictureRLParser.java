package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 下载专栏文集里面的图片
 * <p>https://www.bilibili.com/read/mobile-readlist/rl716664</p>
 * <p>https://www.bilibili.com/read/readlist/rl716666</p>
 *
 */
@Bilibili(name = "URL4PictureRLParser", note = "图片解析 - 专栏文集")
public class URL4PictureRLParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("\\.bilibili\\.com/read/[^/]*readlist/rl([0-9]+)");
	private String rlIdNumber;
	final private Object[] obj;

	public URL4PictureRLParser(Object... obj) {
		super(obj);
		this.obj = obj;
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			rlIdNumber = matcher.group(1);
			Logger.println("匹配URL4PictureRLParser: rl" + rlIdNumber);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

//	@Override
//	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
//		Logger.println("URL4PictureRLParser正在获取结果: rl" + rlIdNumber);
//		String rlIdStr = "rl" + rlIdNumber;
//
//		String url = "https://api.bilibili.com/x/article/list/web/articles?jsonp=jsonp&id=" + rlIdNumber;
//		HashMap<String, String> headers_json = new HttpHeaders().getCommonHeaders();
//		String json = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
//		Logger.println(url);
//		Logger.println(json);
//		JSONObject jObj = new JSONObject(json).getJSONObject("data");
//		JSONObject jList = jObj.getJSONObject("list");
//		JSONObject jUp = jObj.getJSONObject("author");
//		JSONArray jArts = jObj.getJSONArray("articles");
//
//		// 总体大致信息
//		VideoInfo viInfo = new VideoInfo();
//		viInfo.setVideoId(rlIdStr);
//		viInfo.setVideoName(jList.getString("name"));
//		viInfo.setBrief(jList.getString("summary"));
//		viInfo.setAuthor(jUp.getString("name"));
//		viInfo.setAuthorId(jUp.optString("mid"));
//		viInfo.setVideoPreview(jList.getString("image_url"));
//
//		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
//		URL4PictureCVParser cvParser = new URL4PictureCVParser(obj);
//		for (int i = 0; i < jArts.length(); i++) {
//			JSONObject article = jArts.getJSONObject(i);
//			String cvIdNumber = article.optString("id");
//			VideoInfo vArt = cvParser.getCVDetail(cvIdNumber); // 这一步会发生网络请求，比较耗时
//			for (ClipInfo clip : vArt.getClips().values()) {
//				clip.setRemark(i);
//				clipMap.put(Long.parseLong(cvIdNumber + clip.getPage()), clip);
//			}
//			if (i > 5)
//				break;
//
//		}
//		viInfo.setClips(clipMap);
//		return viInfo;
//	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 9999;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	/**
	 * 分页查询
	 * 
	 * @param pageSize
	 * @param page
	 * @param obj
	 * @return 以pageSize 进行分页查询，获取第page页的结果
	 */
	public VideoInfo result(int pageSize, int page, Object... obj) {
		initPageQueryParam();
		Logger.printf("pageSize: %d, page: %d", pageSize, page);
		try {
			// 总体大致信息
			Logger.printf("URL4PictureRLParser正在获取结果: rl%s, p=%d, pageSize=%d", rlIdNumber, page, pageSize);
			String rlIdStr = "rl" + rlIdNumber;

			String url = "https://api.bilibili.com/x/article/list/web/articles?jsonp=jsonp&id=" + rlIdNumber;
			HashMap<String, String> headers_json = new HttpHeaders().getCommonHeaders();
			String json = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(url);
			Logger.println(json);
			JSONObject jObj = new JSONObject(json).getJSONObject("data");
			JSONObject jList = jObj.getJSONObject("list");
			JSONObject jUp = jObj.getJSONObject("author");
			JSONArray jArts = jObj.getJSONArray("articles");

			pageQueryResult.setVideoId(rlIdStr);
			pageQueryResult.setVideoName(jList.getString("name")+ paramSetter.getPage());
			pageQueryResult.setBrief(jList.getString("summary"));
			pageQueryResult.setAuthor(jUp.getString("name"));
			pageQueryResult.setAuthorId(jUp.optString("mid"));
			pageQueryResult.setVideoPreview(jList.getString("image_url"));

			int start = (page - 1) * pageSize;
			int end = page * pageSize - 1;
			LinkedHashMap<Long, ClipInfo> clipMap = pageQueryResult.getClips();
			URL4PictureCVParser cvParser = new URL4PictureCVParser(this.obj);
			for (int i = start; i < jArts.length() && i <= end; i++) {
				JSONObject article = jArts.getJSONObject(i);
				String cvIdNumber = article.optString("id");
				VideoInfo vArt = cvParser.getCVDetail(cvIdNumber); // 这一步会发生网络请求，比较耗时
				for (ClipInfo clip : vArt.getClips().values()) {
					clip.setRemark(i);
					clipMap.put(Long.parseLong(cvIdNumber + clip.getPage()), clip);
				}
				Thread.sleep(100); // 通过抠html得到的，太频繁容易风控，除了给cookie外，只能先sleep试试
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageQueryResult;
	}

	@Override
	protected boolean query(int p, int min, int max, Object... obj) {
		return false;
	}

}
