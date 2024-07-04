package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.API;
import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 
 * https://www.bilibili.com/medialist/play/378034?from=space&business=space&sort_field=play&tid=3
 * https://space.bilibili.com/378034/
 * https://space.bilibili.com/378034/video
 * https://space.bilibili.com/378034/video?tid=3&keyword=&order=stow
 * https://space.bilibili.com/378034/search/video?tid=3&keyword=&order=pubdate	keyword必须为空
 * 
 * sort_field=           最新发布 pubtime, 最多播放 play, 最多收藏 fav
 * 对应api1 order=       最新发布 pubdate, 最多播放 click, 最多收藏 stow
 * 对应api2 sort_field=  最新发布 1      , 最多播放 2    , 最多收藏 3
 * tid	// 全部 0, 音乐 3...
 *
 */
@Bilibili(name = "URL4UPAllMedialistParser", weight = 70, ifLoad = "listAll", note = "个人上传的视频列表(Medialist解析方式)")
public class URL4UPAllMedialistParser extends AbstractPageQueryParser<VideoInfo> {
	// 针对 https://www.bilibili.com/medialist/play/378034?from=space&business=space&sort_field=play&tid=3
	private final static Pattern pattern = Pattern
			.compile("www\\.bilibili\\.com/medialist/play/([0-9]+)\\?.*&business=space&");
	private final static Pattern patternParams = Pattern.compile("(tid|sort_field)=([^=&]+)");

	// 针对 https://space.bilibili.com/378034/video?tid=3&keyword=&order=stow
	// (keyword必须为空)
	private final static Pattern pattern2 = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)(/video|/search/video\\?|/? *$|/?\\?)");
	public final static Pattern patternKeyNotEmpty = Pattern.compile("keyword=[^=&]+");
	private final static Pattern patternParams2 = Pattern.compile("(tid|order)=([^=&]+)");

	private final static String[][] paramDicts = new String[][] { { "pubtime", "pubdate" }, { "play", "click" },
			{ "fav", "stow" } };
	private String spaceID;
	private String listName;
	private HashMap<String, String> params;

	public URL4UPAllMedialistParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		// 尝试匹配1
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页全部视频(Medialist解析方式) ...");
			spaceID = matcher.group(1);
			params = new HashMap<>();
			params.put("tid", "0");
			params.put("sort_field", "pubtime");
			Matcher m = patternParams.matcher(input);
			while (m.find())
				params.put(m.group(1), m.group(2).trim());
			return true;
		}
		// 尝试匹配2
		matcher = pattern2.matcher(input);
		if (matcher.find()) {
			if (patternKeyNotEmpty.matcher(input).find())
				return false;
			System.out.println("匹配UP主主页全部视频(Medialist解析方式2) ...");
			spaceID = matcher.group(1);
			params = new HashMap<>();
			params.put("tid", "0");
			params.put("sort_field", "pubtime");
			Matcher m = patternParams2.matcher(input);
			while (m.find()) {
				switch (m.group(1)) {
				case "tid":
					params.put("tid", m.group(2).trim());
					break;
				case "order":
					String sortField = paramDicts[0][0];
					String sortFieldParam = m.group(2).trim();
					for (int i = 0; i < paramDicts.length; i++) {
						if (paramDicts[i][1].equals(sortFieldParam)) {
							sortField = paramDicts[i][0];
							break;
						}
					}
					params.put("sort_field", sortField);
					break;
				default:
					break;
				}
			}
			return true;
		}
		return false;

	}

	@Override
	public String validStr(String input) {
		return input.trim() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
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
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		String sortField = params.get("sort_field");
		try {
			// 先获取合集信息
			HashMap<String, String> headers = new HttpHeaders().getCommonHeaders("api.bilibili.com");
			HashMap<String, String> headersRefer = new HashMap<>(headers);
			headersRefer.put("Referer", "https://space.bilibili.com/");
			headersRefer.put("Origin", "https://space.bilibili.com/");
			if (pageQueryResult.getVideoName() == null) {
				String url = "https://api.bilibili.com/x/v1/medialist/info?type=1&tid=0&biz_id=" + spaceID;
				Logger.println(url);
				String json = util.getContent(url, headers);
				JSONObject jobj = new JSONObject(json).getJSONObject("data");
				listName = jobj.getString("title") + "的视频列表";
				pageQueryResult.setVideoId("space" + spaceID);
				pageQueryResult.setVideoName(listName + paramSetter.getPage());
				pageQueryResult.setBrief(jobj.getString("intro"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setAuthor(jobj.getJSONObject("upper").getString("name"));
			}
			// 获取oid(返回结果的第一个视频id)
			String sortFieldParam = paramDicts[0][1];
			for (int i = 0; i < paramDicts.length; i++) {
				if (paramDicts[i][0].equals(sortField)) {
					sortFieldParam = paramDicts[i][1];
					break;
				}
			}
			String firstOid = position2Oid((page - 1) * pageSize + 1, headersRefer, sortFieldParam);
			if(firstOid.equals("end"))
				return pageQueryResult;
			String lastOidPlus1 = position2Oid(page * pageSize + 1, headersRefer, sortFieldParam);
			
			// 根据oid查询分页的详细信息
			String urlFormat = "https://api.bilibili.com/x/v2/medialist/resource/list?type=1&oid=%s&otype=2&biz_id=%s&bvid=&with_current=%s&mobi_app=web&ps=%d&direction=false&sort_field=%d&tid=%s&desc=true";
			boolean withCurrent = true;
			int sortFieldIndex = 1;
			for (int i = 0; i < paramDicts.length; i++) {
				if (paramDicts[i][0].equals(sortField)) {
					sortFieldIndex = i + 1;
					break;
				}
			}
			boolean findLastOid = false;
			String currentOid = firstOid;
			int pageRemark = (page - 1) * pageSize;
			while(!findLastOid) {
				String url = String.format(urlFormat, currentOid, spaceID, withCurrent, API_PMAX, sortFieldIndex, params.get("tid"));
				Logger.println(url);
				withCurrent = false; // 接下来的查询不需要包括定位的 oid
				String json = util.getContent(url, headers);
				JSONObject jobj = new JSONObject(json);
				JSONArray arr = jobj.getJSONObject("data").getJSONArray("media_list");
				
				if (pageQueryResult.getVideoPreview() == null) {
					pageQueryResult.setVideoPreview(arr.getJSONObject(0).getString("cover"));
				}
				
				LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
				for (int i = 0; i < arr.length(); i++) {
					pageRemark++;
					JSONObject jAV = arr.getJSONObject(i);
					String oid = jAV.optString("id");
					if(oid.equals(lastOidPlus1)) {
						findLastOid = true;
						break;
					}
					currentOid = oid;
					String avId = jAV.getString("bv_id");
					String avTitle = jAV.getString("title");
					String upName = jAV.getJSONObject("upper").getString("name");
					String upId = jAV.getJSONObject("upper").optString("mid");
					long cTime = jAV.optLong("pubtime") * 1000;
					JSONArray jClips = jAV.optJSONArray("pages");
					if (jClips == null) {
						continue;
					}
					for (int pointer = 0; pointer < jClips.length(); pointer++) {
						JSONObject jClip = jClips.getJSONObject(pointer);
						ClipInfo clip = new ClipInfo();
						clip.setAvId(avId);
						clip.setcId(jClip.getLong("id"));
						clip.setPage(jClip.getInt("page"));
						clip.setRemark(pageRemark); //这个已经没法计算准确了
						clip.setPicPreview(jAV.getString("cover"));
						// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
						clip.setListName(listName.replaceAll("[/\\\\]", "_"));
						clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
						clip.setUpName(upName);
						clip.setUpId(upId);
						clip.setAvTitle(avTitle);
						clip.setTitle(jClip.getString("title"));
						clip.setcTime(cTime);
						
						LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
						try {
							for (VideoQualityEnum VQ : VideoQualityEnum.values()) {
								if (getVideoLink) {
									String link = getVideoLink(avId, String.valueOf(clip.getcId()), VQ.getQn(),
											videoFormat);
									links.put(VQ.getQn(), link);
								} else {
									links.put(VQ.getQn(), "");
								}
							}
						} catch (Exception e) {
						}
						clip.setLinks(links);
						
						map.put(clip.getcId(), clip);
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return pageQueryResult;
	}

	/**
	 * 返回 "" 表示开头
	 * 返回 "end" 表示结尾
	 * 返回 数字 表示具体id
	 * @param pageNumber
	 * @param headers
	 * @param sortFieldParam
	 */
	private String position2Oid(int pageNumber, HashMap<String, String> headers, String sortFieldParam) {
		if(pageNumber == 1)
			return "";
		// String urlFormat = "https://api.bilibili.com/x/space/arc/search?mid=%s&ps=%d&tid=%s&pn=%d&keyword=&order=%s&jsonp=jsonp";
		String urlFormat = "https://api.bilibili.com/x/space/wbi/arc/search?mid=%s&ps=%d&tid=%s&special_type=&pn=%d&keyword=&order=%s&platform=web"; // &web_location=1550101&order_avoided=true
		String url = String.format(urlFormat, spaceID, 1, params.get("tid"), pageNumber, sortFieldParam);
		url += API.genDmImgParams();
		url = API.encWbi(url);
		String json = util.getContent(url, headers, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(url);
		Logger.println(json);
		JSONArray vlist = new JSONObject(json).getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
		if(vlist.length() == 0) {
			Logger.printf("position: %d, oid: search till end", pageNumber);
			return "end";
		} else {
			String oid = vlist.getJSONObject(0).optString("aid");
			Logger.printf("position: %d, oid: %s", pageNumber, oid);
			return oid;
		}
	}
	
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		return false;
	}
}
