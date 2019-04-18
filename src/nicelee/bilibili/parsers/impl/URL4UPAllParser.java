package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "URL4UPAllParser", ifLoad = "listAll", note = "个人上传的视频列表")
public class URL4UPAllParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)(/video|/? *$)");
	private String spaceID;

	public URL4UPAllParser(Object... obj) {
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
		return matcher.group().trim() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		Logger.println(paramSetter.getPage());
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
		int videoFormat = (int)obj[0];
		boolean getVideoLink = (boolean)obj[1];
		try {
			String urlFormat = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=%s&pagesize=%d&tid=0&page=%d&keyword=&order=pubdate";
			String url = String.format(urlFormat, spaceID, API_PMAX, page);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("space.bilibili.com"));
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("vlist");
			
			//设置av信息
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor(arr.getJSONObject(0).getString("author"));
				pageQueryResult.setVideoName(pageQueryResult.getAuthor() + "的视频列表");
				pageQueryResult.setVideoPreview("http:" + arr.getJSONObject(0).getString("pic"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("视频列表 - " + paramSetter.getPage());
			}
			
			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				map.putAll(convertVideoToClipMap(jAV.getLong("aid"), 
						(page -1)* API_PMAX + i+ 1,
						videoFormat,
						getVideoLink));
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用此方法会产生许多请求，慎用
	 * @param avId
	 * @param remark
	 * @param videoFormat
	 * @param getVideoLink
	 * @return 将所有avId的视频封装成Map
	 */
	private LinkedHashMap<Long, ClipInfo> convertVideoToClipMap(long avId, int remark, int videoFormat, boolean getVideoLink){
		LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
		VideoInfo video = getAVDetail(avId, videoFormat, getVideoLink); // 耗时
		for(ClipInfo clip: video.getClips().values()) {
			clip.setTitle(clip.getAvTitle() + "-" + clip.getTitle());
			clip.setAvTitle(pageQueryResult.getVideoName());
			clip.setRemark(remark);
			map.put(clip.getcId(), clip);
		}
		return map;
	}
}
