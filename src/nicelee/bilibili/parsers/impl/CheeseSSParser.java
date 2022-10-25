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
import nicelee.bilibili.util.convert.ConvertUtil;
import nicelee.ui.Global;

@Bilibili(name = "CheeseSSParser", note = "课程集合")
public class CheeseSSParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("/cheese/play/ss([0-9]+)");
	private final static Pattern idPattern = Pattern.compile("season([0-9]+)_([0-9]+)_([0-9]+)");
	private String ssID;
	private String epID;
	private String avID;

	public CheeseSSParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			ssID = matcher.group(1);
			return true;
		}
		matcher = idPattern.matcher(input);
		if (matcher.find()) {
			ssID = matcher.group(1);
			epID = matcher.group(2);
			avID = matcher.group(3);
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
		Logger.println(paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 10;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {

			HashMap<String, String> headers = new HashMap<>();
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
			headers.put("Accept", "application/json, text/plain, */*");
			headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			headers.put("Accept-Encoding", "gzip, deflate");
			headers.put("Origin", "https://www.bilibili.com");
			headers.put("Referer", "https://www.bilibili.com/cheese/play/ss" + ssID);

			// 设置av信息
			if (pageQueryResult.getVideoName() == null) {
				String url = "https://api.bilibili.com/pugv/view/web/season?season_id=" + ssID;
				String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
				Logger.println(url);
				Logger.println(json);
				JSONObject jobj = new JSONObject(json).getJSONObject("data");
				JSONObject jUp = jobj.getJSONObject("up_info");
				pageQueryResult.setVideoId("season_" + ssID);
				pageQueryResult.setAuthor(jUp.getString("uname"));
				pageQueryResult.setVideoName(jobj.getString("title"));
				pageQueryResult.setVideoPreview(jobj.getString("cover"));
				pageQueryResult.setAuthorId(jUp.optString("mid"));
				pageQueryResult.setBrief(jobj.getString("subtitle"));
			}

			String urlFormat = "https://api.bilibili.com/pugv/view/web/ep/list?season_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, ssID, page, API_PMAX);

			String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
			Logger.println(url);
			Logger.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("items");

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jContent = arr.getJSONObject(i);
				ClipInfo clip = new ClipInfo();
				clip.setAvTitle(pageQueryResult.getVideoName());
				// ss_ep_av
				String ep_id = jContent.optString("id");
				String av_id = jContent.optString("aid");
				String idFormat = "season%s_%s_%s";
				clip.setAvId(String.format(idFormat, ssID, ep_id, av_id));
				clip.setcId(jContent.optLong("cid"));
				clip.setPage(1);
				clip.setTitle(jContent.getString("title"));
				clip.setPicPreview(pageQueryResult.getVideoPreview());
				clip.setRemark((page - 1) * API_PMAX + i + 1);
				clip.setUpName(pageQueryResult.getAuthor());
				clip.setUpId(pageQueryResult.getAuthorId());

				LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
				int[] qnList = { 112, 80, 64, 32, 16 };
				for (int qn : qnList) {
					links.put(qn, "");
				}
//				// 目前为每个视频查询一次清晰度
//				try {
//					int[] qnList = getVideoQNList(av_id, "" + clip.getcId(), ep_id);
//					for (int qn : qnList) {
//						links.put(qn, "");
//					}
//				} catch (Exception e) {
//				}
				clip.setLinks(links);
				map.put(clip.getcId(), clip);

			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	public int[] getVideoQNList(String avId, String cid, String epId) {
		String urlFormat = "https://api.bilibili.com/pugv/player/web/playurl?avid=%s&cid=%s&bvid=&qn=%d&type=&otype=json&ep_id=%s&fourk=1&fnver=0&fnval=80";
		String url = String.format(urlFormat, avId, cid, 80, epId);
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Origin", "https://www.bilibili.com");
		headers.put("Referer", "https://www.bilibili.com/cheese/play/ss" + ssID);
		String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
		JSONArray jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
			// Logger.println(qnList[i]);
		}
		return qnList;
	}

	@Override
	public String getVideoLink(String ssId, String cid, int qn, int downFormat) {
		if(qn == 800) {
			return getVideoSubtitleLink(ConvertUtil.Av2Bv(avID), cid, qn);
		}else if(qn == 801) {
			paramSetter.setRealQN(qn);
			return "https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid;
		}
		String urlFormat = "https://api.bilibili.com/pugv/player/web/playurl?avid=%s&cid=%s&bvid=&qn=%d&type=&otype=json&ep_id=%s&fourk=1&fnver=0&fnval=80";
		String url = String.format(urlFormat, avID, cid, qn, epID);
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", Global.userAgent);
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Origin", "https://www.bilibili.com");
		headers.put("Referer", "https://www.bilibili.com/cheese/play/ss" + ssID);
		String json = util.getContent(url, headers, HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		int linkQN = jObj.getInt("quality");
		paramSetter.setRealQN(linkQN);
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");

		try {
			HashMap<String, String> headerDownload = new HttpHeaders().getBiliWwwM4sHeaders("av" + avID);
			headerDownload.put("Referer", "https://www.bilibili.com/cheese/play/ss" + ssID);
			return parseType1(jObj, linkQN, headerDownload);
		} catch (Exception e) {
			// e.printStackTrace();
			Logger.println("切换解析方式");
			// 鉴于部分视频如 https://www.bilibili.com/video/av24145318 H5仍然是用的是Flash源,此处切为FLV
			return parseType2(jObj);
		}
	}
}
