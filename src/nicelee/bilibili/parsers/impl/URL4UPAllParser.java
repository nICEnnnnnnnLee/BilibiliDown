package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.API;
import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

/**
 * 针对以下url类型
 * https://space.bilibili.com/378034/search/video?tid=3&keyword=葫芦丝&order=pubdate keyword必须不为空
 * 另外，以下类型也可以解析，但是优先级不如URL4UPAllMedialistParser，所以被拦截
 * https://space.bilibili.com/378034
 * https://space.bilibili.com/378034/
 * https://space.bilibili.com/378034?spm=xxx
 * https://space.bilibili.com/378034/?spm=xxx
 * https://space.bilibili.com/378034/video
 * https://space.bilibili.com/378034/video?tid=3&keyword=&order=stow
 *
 */
@Bilibili(name = "URL4UPAllParser", ifLoad = "listAll", note = "个人上传的视频列表", weight=69)
public class URL4UPAllParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)(/video|/search/video\\?|/? *$|\\?)");
	private final static Pattern patternTid = Pattern.compile("(tid|order|keyword)=([^=&]+)");
	private String spaceID;
	private HashMap<String, String> params;
//	private String tid = "0"; // 全部 0, 音乐 3...
//	private String order = "pubdate"; // 最新发布 pubdate 最多点击click 最多收藏stow
//	private String keyword = ""; // 
	
	public URL4UPAllParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页全部视频 URL4UPAllParser");
			spaceID = matcher.group(1);
			params = new HashMap<>();
			params.put("tid", "0");
			params.put("order", "pubdate");
			params.put("keyword", "");
			Matcher m = patternTid.matcher(input);
			while(m.find())
				params.put(m.group(1), m.group(2).trim());
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String validStr(String input) {
//		return matcher.group().trim() + "p=" + paramSetter.getPage();
		return input.trim()+ "p=" + paramSetter.getPage();
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

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			//String urlFormat = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=%s&pagesize=%d&tid=0&page=%d&keyword=&order=pubdate";
			//String urlFormat = "https://api.bilibili.com/x/space/arc/search?mid=%s&ps=%d&tid=%s&pn=%d&keyword=%s&order=%s&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/x/space/wbi/arc/search?mid=%s&ps=%d&tid=%s&pn=%d&keyword=%s&order=%s&platform=web"; // &web_location=1550101&order_avoided=true
			String keyword = API.encodeURL(params.get("keyword"));
			String url = String.format(urlFormat, spaceID, API_PMAX, params.get("tid"), page, keyword, params.get("order"));
			url += API.genDmImgParams();
			url = API.encWbi(url);
			HashMap<String, String> headersRefer = new HttpHeaders().getCommonHeaders("api.bilibili.com");
			headersRefer.put("Referer", "https://space.bilibili.com/");
			headersRefer.put("Origin", "https://space.bilibili.com/");
			String json = util.getContent(url, headersRefer, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(url);
			Logger.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONObject("list").getJSONArray("vlist");

			// 设置av信息
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor(arr.getJSONObject(0).getString("author"));
				// 防止联合投稿视频误报，寻找下一个作品的投稿人
				int is_union_video = arr.getJSONObject(0).optInt("is_union_video", 0);
				int pointer = 1;
				while(is_union_video == 1 && pointer < arr.length()) {
					pageQueryResult.setAuthor(arr.getJSONObject(pointer).getString("author"));
					is_union_video = arr.getJSONObject(pointer).optInt("is_union_video", 0);
					pointer ++;
				}
				pageQueryResult.setVideoName(pageQueryResult.getAuthor() + "的视频列表");
				String videoPreview = arr.getJSONObject(0).getString("pic");
				if(videoPreview.startsWith("//")) {
					videoPreview = "http:" + videoPreview;
				}
				pageQueryResult.setVideoPreview(videoPreview);
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("视频列表 - " + paramSetter.getPage());
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				// 跳过课程解析
				String jumpUrl = jAV.optString("jump_url", "");
				if(jumpUrl.startsWith("https://www.bilibili.com/cheese/"))
					continue;
				map.putAll(convertVideoToClipMap(jAV.getString("bvid"), (page - 1) * API_PMAX + i + 1, videoFormat,
						getVideoLink));
			}
			return true;
		} catch (Exception e) {
			 e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用此方法会产生许多请求，慎用
	 * 
	 * @param avId
	 * @param remark
	 * @param videoFormat
	 * @param getVideoLink
	 * @return 将所有avId的视频封装成Map
	 */
	private LinkedHashMap<Long, ClipInfo> convertVideoToClipMap(String bvid, int remark, int videoFormat,
			boolean getVideoLink) {
		LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
		VideoInfo video = getAVDetail(bvid, videoFormat, getVideoLink); // 耗时
		for (ClipInfo clip : video.getClips().values()) {
			//clip.setTitle(clip.getAvTitle() + "-" + clip.getTitle());
			//clip.setAvTitle(pageQueryResult.getVideoName());
			// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
			clip.setListName(pageQueryResult.getVideoName().replaceAll("[/\\\\]", "_"));
			clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
			
			clip.setRemark(remark);
			map.put(clip.getcId(), clip);
		}
		return map;
	}
}
