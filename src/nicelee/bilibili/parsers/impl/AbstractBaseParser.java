package nicelee.bilibili.parsers.impl;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.API;
import nicelee.bilibili.enums.DownloadModeEnum;
import nicelee.bilibili.exceptions.ApiLinkQueryParseError;
import nicelee.bilibili.exceptions.NoSubtitleException;
import nicelee.bilibili.exceptions.QualityTooLowException;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.StoryClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.IInputParser;
import nicelee.bilibili.parsers.IParamSetter;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.convert.ConvertUtil;
import nicelee.ui.Global;

public abstract class AbstractBaseParser implements IInputParser {

	protected Matcher matcher;
	protected HttpRequestUtil util;
	protected int pageSize = 20;
	protected IParamSetter paramSetter;

	public AbstractBaseParser(Object... obj) {
		this.util = (HttpRequestUtil) obj[0];
		this.paramSetter = (IParamSetter) obj[1];
		this.pageSize = (int) obj[2];
	}

	@Override
	public abstract boolean matches(String input);

	@Override
	public abstract String validStr(String input);

	@Override
	public abstract VideoInfo result(String input, int videoFormat, boolean getVideoLink);

	/**
	 * 获取详细av信息
	 * 
	 * @param bvId
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	public VideoInfo getAVDetail(String bvId, int videoFormat, boolean getVideoLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(bvId);

		// 获取av下的视频列表
		String url = String.format("https://api.bilibili.com/x/player/pagelist?bvid=%s&jsonp=jsonp", bvId);
		HashMap<String, String> headers_json = new HttpHeaders().getBiliJsonAPIHeaders(bvId);
		String json = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(url);
		Logger.println(json);
		JSONArray array = new JSONObject(json).getJSONArray("data");

		// 根据第一个获取总体大致信息
		JSONObject jObj = array.getJSONObject(0);
		long cid = jObj.getLong("cid");
//		String detailUrl = String.format("https://api.bilibili.com/x/web-interface/view?cid=%d&bvid=%s", cid, bvId);
//		String detailJson = util.getContent(detailUrl, headers_json, HttpCookies.globalCookiesWithFingerprint());
//		JSONObject detailObj = new JSONObject(detailJson).getJSONObject("data");
		String detailUrl = "https://api.bilibili.com/x/web-interface/wbi/view/detail?platform=web&page_no=1&p=1&need_operation_card=1&web_rm_repeat=1&need_elec=1&bvid="
				+ bvId;
		detailUrl += API.genDmImgParams();
		detailUrl = API.encWbi(detailUrl);
		String detailJson = util.getContent(detailUrl, headers_json, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(detailUrl);
		Logger.println(detailJson);
		JSONObject detailRaw = new JSONObject(detailJson);
		long aid = ConvertUtil.Bv2Av(bvId);
		int videoCnt; long ctime;
		if(detailRaw.optInt("code") == -403) {
			detailUrl = String.format("https://api.bilibili.com/x/v3/fav/resource/infos?resources=%d:2&platform=web&folder_mid=&folder_id=", aid);
			detailJson = util.getContent(detailUrl, headers_json, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(detailUrl);
			Logger.println(detailJson);
			JSONObject detailObj = new JSONObject(detailJson).getJSONArray("data").getJSONObject(0);
			ctime = detailObj.optLong("ctime") * 1000;
			videoCnt = detailObj.optInt("page");
			viInfo.setVideoName(detailObj.getString("title"));
			viInfo.setBrief(detailObj.getString("intro"));
			viInfo.setAuthor(detailObj.getJSONObject("upper").getString("name"));
			viInfo.setAuthorId(String.valueOf(detailObj.getJSONObject("upper").getLong("mid")));
			viInfo.setVideoPreview(detailObj.getString("cover"));
		} else {
			JSONObject detailObj = detailRaw.getJSONObject("data").getJSONObject("View");
			ctime = detailObj.optLong("ctime") * 1000;
			videoCnt = detailObj.optInt("videos");
			viInfo.setVideoName(detailObj.getString("title"));
			viInfo.setBrief(detailObj.getString("desc"));
			viInfo.setAuthor(detailObj.getJSONObject("owner").getString("name"));
			viInfo.setAuthorId(String.valueOf(detailObj.getJSONObject("owner").getLong("mid")));
			viInfo.setVideoPreview(detailObj.getString("pic"));
		}

		// 判断是否是互动视频
		if (videoCnt > 1 && array.length() == 1) {
			// 查询graph_version版本
			String url_graph_version = String.format("https://api.bilibili.com/x/player/wbi/v2?aid=%d&cid=%d&isGaiaAvoided=false", aid, cid);
			url_graph_version += API.genDmImgParams();
			url_graph_version = API.encWbi(url_graph_version);
			String result = util.getContent(url_graph_version, headers_json, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(url_graph_version);
			Logger.println(result);
			String graph_version = new JSONObject(result).getJSONObject("data").getJSONObject("interaction").optString("graph_version");
			Logger.println(graph_version);
			List<List<StoryClipInfo>> story_list = new ArrayList<>();
			List<StoryClipInfo> originStory = new ArrayList<StoryClipInfo>();
			StoryClipInfo storyClip = new StoryClipInfo(cid);
			originStory.add(storyClip);
			HashSet<StoryClipInfo> node_list = new HashSet<>();
			// 从根节点，一直遍历到子节点，找到所有故事线，放到story_list
			collectStoryList(bvId, "", graph_version, originStory, story_list, node_list);
			LinkedHashMap<Long, ClipInfo> clipMap = storyList2Map(bvId, videoFormat, getVideoLink, viInfo,
					story_list);
			viInfo.setClips(clipMap);
			viInfo.print();
			return viInfo;
		} else {
			LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
			int[] qnListDefault = null;
			if (array.length() > 5) {
				qnListDefault = new int[] { 120, 116, 112, 80, 74, 64, 32, 16 };
			}
			for (int i = 0; i < array.length(); i++) {
				jObj = array.getJSONObject(i);
				cid = jObj.getLong("cid");
				ClipInfo clip = new ClipInfo();
				clip.setAvTitle(viInfo.getVideoName());
				clip.setAvId(bvId);
				clip.setcId(cid);
				clip.setPage(jObj.getInt("page"));
				clip.setTitle(jObj.getString("part"));
				clip.setPicPreview(viInfo.getVideoPreview());
				clip.setUpName(viInfo.getAuthor());
				clip.setUpId(viInfo.getAuthorId());
				clip.setcTime(ctime);
				LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
				try {
					int qnList[] = qnListDefault != null ? qnListDefault
							: getVideoQNList(bvId, String.valueOf(clip.getcId()));
					for (int qn : qnList) {
						if (getVideoLink) {
							String link = getVideoLink(bvId, String.valueOf(clip.getcId()), qn, videoFormat);
							links.put(qn, link);
						} else {
							links.put(qn, "");
						}
					}
					clip.setLinks(links);
				} catch (Exception e) {
					e.printStackTrace();
					clip.setLinks(links);
				}

				clipMap.put(clip.getcId(), clip);
			}
			viInfo.setClips(clipMap);
		}
		viInfo.print();
		return viInfo;
	}

	public int[] getVideoQNList(String bvId, String cid) {
		switch (Global.infoQueryStrategy) {
		case "tryNormalTypeFirst":
			return getVideoQNList_TryNormalTypeFirst(bvId, cid);
		case "judgeTypeFirst":
			return getVideoQNList_JudgeTypeFirst(bvId, cid);
		default:
			return new int[] { 120, 116, 112, 80, 74, 64, 32, 16 };
		}
	}
	
	private int[] getVideoQNList_JudgeTypeFirst(String bvId, String cid) {
		HttpHeaders headers = new HttpHeaders();
		JSONArray jArr = null;
		// 先判断类型
		// https://api.bilibili.com/x/web-interface/wbi/view/detail?platform=web&bvid=%s&&need_operation_card=1&web_rm_repeat=1&need_elec=1
		String url = "https://api.bilibili.com/x/web-interface/wbi/view/detail?platform=web&bvid="
				+ bvId;
		url += API.genDmImgParams();
		url = API.encWbi(url);
		HashMap<String, String> header = headers.getBiliJsonAPIHeaders(bvId);
		String callBack = util.getContent(url, header, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(callBack);
		JSONObject infoObj = new JSONObject(callBack).getJSONObject("data")
				.getJSONObject("View");
		Long aid = infoObj.optLong("aid");

		if (infoObj.optString("redirect_url").isEmpty()) {
			// 普通类型
			url = "https://api.bilibili.com/x/player/playurl?cid=%s&bvid=%s&qn=%d&type=&otype=json&fnver=0&fnval=4048&fourk=1";
			url = String.format(url, cid, bvId, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(bvId), HttpCookies.globalCookiesWithFingerprint());
			Logger.println(json);
			jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		} else {
			// 非普通类型
			url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=4048&fnver=0&fourk=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, aid, cid, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders("av" + aid),
					HttpCookies.globalCookiesWithFingerprint());
			Logger.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		}
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
			// Logger.println(qnList[i]);
		}
		return qnList;
	}
	
	private int[] getVideoQNList_TryNormalTypeFirst(String bvId, String cid) {
		HttpHeaders headers = new HttpHeaders();
		JSONArray jArr = null;
		try {
			// 普通类型
			String url = "https://api.bilibili.com/x/player/playurl?cid=%s&bvid=%s&qn=%d&type=&otype=json&fnver=0&fnval=4048&fourk=1";
			url = String.format(url, cid, bvId, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(bvId), HttpCookies.globalCookiesWithFingerprint());
			Logger.println(json);
			jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		} catch (Exception e) {
			// 非普通类型
			long aid = ConvertUtil.Bv2Av(bvId);
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=4048&fnver=0&fourk=1&otype=json&avid=%d&cid=%s&qn=%s";
			url = String.format(url, aid, cid, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders("av" + aid),
					HttpCookies.globalCookiesWithFingerprint());
			Logger.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		}
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
			// Logger.println(qnList[i]);
		}
		return qnList;
	}

	/**
	 * 查询视频链接
	 * 
	 * @external input HttpRequestUtil util
	 * @external input downFormat
	 * @external output linkQN 保存返回链接的清晰度
	 * @param bvId 视频的bvId
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	@Override
	public String getVideoLink(String bvId, String cid, int qn, int downFormat) {
		if (bvId.startsWith("au") && (qn > 3 || qn < 0)) {
			qn = 3;
		}
		switch (qn) {
		case 800:
			return getVideoSubtitleLink(bvId, cid, qn);
		case 801:
			paramSetter.setRealQN(qn);
			return "https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid;
		case 3:
		case 2:
		case 1:
		case 0:
			return getAudioLink(bvId, cid, qn);
		default:
			return getVideoLinkByFormat(bvId, cid, qn, downFormat);
		}
	}

	protected String getAudioLink(String auId, String _auId, int qn) {
		String auIdNum = auId.substring(2);
//		String url = String.format("https://www.bilibili.com/audio/music-service-c/web/url?sid=%s&privilege=2&quality=2", auIdNum);
		String url = String.format("https://www.bilibili.com/audio/music-service-c/url?songid=%s&privilege=2&quality=%d&mid=&platform=web", auIdNum, qn);
		Logger.println(url);
		HashMap<String, String> headers = new HttpHeaders().getCommonHeaders();
		String r = util.getContent(url, headers, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(r);
		JSONObject data = new JSONObject(r).getJSONObject("data");
		int realQn = data.optInt("type");
		Logger.printf("预期下载清晰度：%d, 实际清晰度：%d", qn, realQn);
		paramSetter.setRealQN(realQn);
		String link = data.getJSONArray("cdns").getString(0);
		Logger.println(link);
		return link;
	}
	
	protected String getVideoSubtitleLink(String bvId, String cid, int qn) {
		String url = String.format("https://api.bilibili.com/x/player/wbi/v2?bvid=%s&cid=%s&isGaiaAvoided=false", bvId, cid);
		url += API.genDmImgParams();
		url = API.encWbi(url);
		Logger.println(url);
		HashMap<String, String> headers_json = new HttpHeaders().getBiliJsonAPIHeaders(bvId);
		String result = util.getContent(url, headers_json, HttpCookies.globalCookiesWithFingerprint());
		paramSetter.setRealQN(qn);
		try {
			JSONArray subList = new JSONObject(result).getJSONObject("data").getJSONObject("subtitle").getJSONArray("subtitles");
			for (int i = 0; i < subList.length(); i++) {
				JSONObject sub = subList.getJSONObject(i);
				String subLang = sub.getString("lan");
				if (Global.cc_lang.equals(subLang)) {
					return "https:" + sub.getString("subtitle_url");
				}
			}
			
			return "https:" + subList.getJSONObject(0).getString("subtitle_url");
		} catch (Exception e) {
			String tips = Global.isLogin? "未能找到字幕 " + bvId : "未能找到字幕，这可能是没有登录造成的。" + bvId;
			throw new NoSubtitleException(tips, e);
		}
	}

	/**
	 * 查询视频链接(MP4)
	 * 
	 * @external input HttpRequestUtil util
	 * @param bvId 视频的bvId
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @param downloadFormat   0 MP4/ 1 FLV
	 * @return 视频url + "#" + 音频url for mp4 / 视频url + "#" + 视频url + "#" + ... for flv
	 */
	String getVideoLinkByFormat(String bvId, String cid, int qn, int downloadFormat) {
		System.out.println("正在查询MP4/FLV链接...");
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		// 根据downloadFormat确定fnval
		String fnval = (downloadFormat & 0x01) == Global.MP4? "4048" : "2";
		// 先判断类型
		Long aid; boolean isNormalType;
		String url = "https://api.bilibili.com/x/web-interface/wbi/view/detail?platform=web&bvid="
				+ bvId;
		url += API.genDmImgParams();
		url = API.encWbi(url);
		HashMap<String, String> header = headers.getBiliJsonAPIHeaders(bvId);
		String callBack = util.getContent(url, header, HttpCookies.globalCookiesWithFingerprint());
		JSONObject detailRaw = new JSONObject(callBack);
		if(detailRaw.optInt("code") == -403) {
			aid = ConvertUtil.Bv2Av(bvId);
			String detailUrl = String.format("https://api.bilibili.com/x/v3/fav/resource/infos?resources=%d:2&platform=web&folder_mid=&folder_id=", aid);
			String detailJson = util.getContent(detailUrl, header, HttpCookies.globalCookiesWithFingerprint());
			Logger.println(detailUrl);
			Logger.println(detailJson);
			JSONObject detailObj = new JSONObject(detailJson).getJSONArray("data").getJSONObject(0);
			isNormalType = detailObj.getInt("attr") != 2; // 0 普通 16 互动视频
		} else {
			JSONObject infoObj = detailRaw.getJSONObject("data")
					.getJSONObject("View");
			aid = infoObj.optLong("aid");
			isNormalType = infoObj.optString("redirect_url").isEmpty();
		}
		if (isNormalType) {
			String trylookTail = Global.isLogin ? "" : "&try_look=1";
			// 普通类型
			url = downloadFormat == 2 ? 
					// 下面这个API清晰度没法选择，编码方式没法选择，固定返回1080P? mp4
					// https://api.bilibili.com/x/player/wbi/playurl?avid=857672756&bvid=BV1HV4y1p7ce&cid=1084157816&qn=80&fnver=0&fnval=4048&fourk=1
					"https://api.bilibili.com/x/player/wbi/playurl?cid=%s&bvid=%s&qn=%d":
					"https://api.bilibili.com/x/player/wbi/playurl?cid=%s&bvid=%s&qn=%d&type=&otype=json&fnver=0&fnval=%s&fourk=1";
			url += trylookTail;
			url = String.format(url, cid, bvId, qn, fnval);
			url = API.encWbi(url);
			Logger.println(url);
//			List cookie = downloadFormat == 2 ? null : HttpCookies.globalCookiesWithFingerprint();
			List<HttpCookie> cookie = HttpCookies.globalCookiesWithFingerprint();
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(bvId), cookie);
			Logger.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} else {
			// 非普通类型
			url = "https://api.bilibili.com/pgc/player/web/playurl?fnver=0&fourk=1&otype=json&avid=%s&cid=%s&qn=%s&fnval=%s";
			url = String.format(url, aid, cid, qn, fnval);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders("av" + aid),
					HttpCookies.globalCookiesWithFingerprint());
			Logger.println(url);
			Logger.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}
		int linkQN = jObj.getInt("quality");
		if(qn != linkQN) { // 只有和预期不符才会去判断
			// 有时候，api返回的列表中含有比指定清晰度更高的内容
			JSONObject dash = jObj.optJSONObject("dash");
			if(dash != null) {
				JSONArray videos = dash.getJSONArray("video");
				int firstLinkQN = videos.getJSONObject(0).getInt("id");
				if(linkQN < firstLinkQN) {
					linkQN = firstLinkQN > qn? qn: firstLinkQN;
				}
			}
		}
		paramSetter.setRealQN(linkQN);
		String tips = String.format("%s:%s - 查询质量为: %d的链接, 得到质量为: %d的链接", bvId, cid, qn, linkQN);
		Logger.println(tips);
		if(Global.alertIfQualityUnexpected && linkQN < 64 && qn > linkQN && Global.isLogin) {
			String notes = tips + "\n该视频的最高画质清晰度较低，请更换相匹配的优先清晰度之后再进行尝试。\n" 
					+ "如果你认为此处应当继续下载，而不是报错，请在配置页搜索 qualityUnexpected 并进行配置\n";
			throw new QualityTooLowException(notes);
		}
		try {
			return parseType1(jObj, linkQN, headers.getBiliWwwM4sHeaders(bvId));
		} catch (Exception e) {
			// e.printStackTrace();
			Logger.println("切换解析方式");
			try {
				// 鉴于部分视频如 https://www.bilibili.com/video/av24145318 H5仍然是用的是Flash源,此处切为FLV
				return parseType2(jObj);
			}catch (Exception e1) {
				e.printStackTrace();
				e1.printStackTrace();
				throw new ApiLinkQueryParseError("查询下载链接时api解析失败", e);
			}
		}
	}


	/**
	 * 将avId currentStory故事线的所有子结局全部放入List
	 * 
	 * @param avIdNum
	 * @param node
	 * @param graph_version
	 * @param currentStory 当前故事线
	 * @param story_list  故事线集合
	 * @param node_list  时间节点集合
	 */
	private void collectStoryList(String bvid, String node, String graph_version, List<StoryClipInfo> currentStory,
			List<List<StoryClipInfo>> story_list, HashSet<StoryClipInfo> node_list) {
		// String url_node_format =
		// "https://api.bilibili.com/x/stein/nodeinfo?aid=%s&node_id=%s&graph_version=%s&platform=pc&portal=0&screen=0";
		String url_node_format = "https://api.bilibili.com/x/stein/edgeinfo_v2?bvid=%s&edge_id=%s&graph_version=%s&platform=pc&portal=0&screen=0";
		String url_node = String.format(url_node_format, bvid, node, graph_version);
		Logger.println(url_node);
		HashMap<String, String> headers_gv = new HashMap<>();
		headers_gv.put("Referer", "https://www.bilibili.com/video/" + bvid);
		String str_nodeInfo = util.getContent(url_node, headers_gv, HttpCookies.globalCookiesWithFingerprint());
		Logger.println(str_nodeInfo);
		JSONObject nodeInfo = new JSONObject(str_nodeInfo).getJSONObject("data");
		JSONArray questions = nodeInfo.optJSONObject("edges").optJSONArray("questions");
		if (node == null || "".equals(node)) { // 如果是第一个片段，补全信息
			StoryClipInfo sClip = currentStory.get(0);
			sClip.setNode_id(nodeInfo.optLong("node_id"));
			sClip.setOption(nodeInfo.getString("title"));
			node_list.add(sClip);
		}
		if (questions == null) { // 如果没有选择，则到达末尾，则故事线完整，可以收集
			story_list.add(currentStory);
		} else {
			JSONArray choices = questions.getJSONObject(0).getJSONArray("choices");
			boolean is2Added = false;
			for (int i = 0; i < choices.length(); i++) {
				JSONObject choice = choices.getJSONObject(i);
				StoryClipInfo sClip = new StoryClipInfo(choice.optLong("cid"), choice.getLong("id"),
						choice.getString("option"));
//				if (currentStory.contains(sClip)) {
//					// 如果当前选择出现回退选项，说明已经到达末尾。故事线完整，可以收集
//					story_list.add(currentStory);
//					break;
//				} else 
				if (node_list.contains(sClip)) {
					// 如果当前选择出现回退选项，说明已经到达末尾。故事线完整，可以收集
					// 如果当前选择在其它故事线中出现过，说明将与其他内容重复。故事线虽不完整，但可以收集
					// 存在某些选择在其它故事线中出现过，但其它选择仍然可以开启新分支的情况，此处不能break
					is2Added = true;
					continue;
				} else {
					List<StoryClipInfo> cloneStory = new ArrayList<StoryClipInfo>(currentStory); // 确保不会对传入的故事线产生影响，而是生成新的时间线
					cloneStory.add(sClip);
					node_list.add(sClip);
					collectStoryList(bvid, "" + choice.getLong("id"), graph_version, cloneStory, story_list, node_list);
				}
			}
			if(is2Added)
				story_list.add(currentStory);

		}
	}

	String getUrlOfMedia(JSONObject media, boolean checkValid, HashMap<String, String> headerForValidCheck) {
		String baseUrl = media.getString("base_url");
		if(!checkValid) {
			return baseUrl;
		}else {
			if (util.checkValid(baseUrl, headerForValidCheck, null)) {
				return baseUrl;
			} else {
				JSONArray backup_urls = media.getJSONArray("backup_url");
				for (int j = 0; j < backup_urls.length(); j++) {
					String backup_url = backup_urls.getString(j);
					if (util.checkValid(backup_url, headerForValidCheck, null)) {
						return backup_url;
					}
				}
				return null;
			}
		}
	}
	JSONObject findMediaByPriList(List<JSONObject> medias, int[] priorities, int mediaType) {
		for(int priority: priorities) {
			JSONObject media = findMediaByPri(medias, priority, mediaType);
			if(media != null)
				return media;
		}
		return medias.get(0);
	}
	JSONObject findMediaByPri(List<JSONObject> medias, int priority, int mediaType) {
		for(JSONObject media: medias) {
			if(-1 == priority || (mediaType == 0 &&media.getInt("codecid") == priority)
					|| (mediaType == 1 &&media.getInt("id") == priority))
				return media;
		}
		return null;
	}
	// All 			视频链接#音频链接
	// VideoOnly 	视频链接#
	// AudioOnly	#音频链接
	protected String parseType1(JSONObject jObj, int linkQN, HashMap<String, String> headerForValidCheck) {
		JSONObject dash = jObj.getJSONObject("dash");
		StringBuilder link = new StringBuilder();
		// 获取视频链接
		if(Global.downloadMode == DownloadModeEnum.AudioOnly) {
			link.append("#");
		}else {
			JSONArray videos = dash.getJSONArray("video");
			// 获取所有符合清晰度要求的视频
			List<JSONObject> qnVideos = new ArrayList<>(3);
			for (int i = 0; i < videos.length(); i++) {
				JSONObject video = videos.getJSONObject(i);
				if (video.getInt("id") == linkQN) {
					qnVideos.add(video);
				}
			}
			// 如果没有找到对应的清晰度 eg. BV1K14y1g7iU 无cookie
			//（API 返回的链接里并没有对应实际清晰度的链接）
			// 那么只能随便加一个了
			if(qnVideos.size() == 0) {
				JSONObject v = videos.getJSONObject(0);
				paramSetter.setRealQN(v.getInt("id"));
				System.out.println("API返回质量为:" + linkQN + "的链接, 实际上只有质量为:" + paramSetter.getRealQN() + "的链接");
				qnVideos.add(v);
			}
			// 根据清晰度选择合适的编码优先级
			Integer rQN = Integer.valueOf(paramSetter.getRealQN());
			int[] videoCodecPriority = Global.videoCodecPriorityMap.getOrDefault(rQN, Global.videoCodecPriority);
			// 根据需求选择编码合适的视频
			JSONObject video = findMediaByPriList(qnVideos, videoCodecPriority, 0);
			// 选择可以连通的链接
			String videoLink = getUrlOfMedia(video, Global.checkDashUrl, headerForValidCheck);
			link.append(videoLink).append("#");
		}
		
		// 获取音频链接
		if(Global.downloadMode != DownloadModeEnum.VideoOnly) {
			// 获取所有音频
			List<JSONObject> listAudios = new ArrayList<>(5);
			JSONArray audios = dash.optJSONArray("audio");// 普通
			if (audios != null) {
				for (int i = 0; i < audios.length(); i++) {
					listAudios.add(audios.getJSONObject(i));
				}
			}
			JSONObject dolby = dash.optJSONObject("dolby");// 杜比
			if (dolby != null && linkQN == 126) {
				audios = dolby.optJSONArray("audio");
				if (audios != null) {
					for (int i = 0; i < audios.length(); i++) {
						listAudios.add(audios.getJSONObject(i));
					}
				}
			}
			JSONObject flac = dash.optJSONObject("flac");// flac
			JSONObject flacAudio = flac == null? null: flac.optJSONObject("audio");// audio
			if (flacAudio != null) {
				listAudios.add(flacAudio);
			}
			if(listAudios.size() > 0) { // 存在没有音频的投稿
				JSONObject audio = findMediaByPriList(listAudios, Global.audioQualityPriority, 1);
				String audioLink = getUrlOfMedia(audio, Global.checkDashUrl, headerForValidCheck);
				link.append(audioLink);
			}
		}
//		Logger.println(link);
		return link.toString();
	}
	
	protected String parseType2(JSONObject jObj) {
		JSONArray urlList = jObj.getJSONArray("durl");
		if (urlList.length() == 1) {
			return urlList.getJSONObject(0).getString("url");

		} else {
			StringBuilder link = new StringBuilder();
			for (int i = 0; i < urlList.length(); i++) {
				JSONObject obj = urlList.getJSONObject(i);
				link.append(obj.getInt("order"));
				link.append(obj.getString("url"));
				link.append("#");
			}
			System.out.println(link.substring(0, link.length() - 1));
			return link.substring(0, link.length() - 1);
		}
	}

	@Override
	public int getVideoLinkQN() {
		return paramSetter.getRealQN();
	}

	/**
	 * 遍历所有故事线，找到所有故事片段并返回
	 * 
	 * @param bvId
	 * @param videoFormat
	 * @param getVideoLink
	 * @param viInfo
	 * @param story_list
	 * @return
	 */
	private LinkedHashMap<Long, ClipInfo> storyList2Map(String bvId, int videoFormat, boolean getVideoLink,
			VideoInfo viInfo, List<List<StoryClipInfo>> story_list) {
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		// 遍历故事线，找到所有片段，放到clipMap
		for (int i = 0; i < story_list.size(); i++) {
			List<StoryClipInfo> story_clips = story_list.get(i);
			for (int j = 0; j < story_clips.size(); j++) {
				StoryClipInfo obj = story_clips.get(j);
				long cid = obj.getCid();
				Object clip_t = clipMap.get(cid);
				// 如果还没找到该片段
				if (clip_t == null) {
					ClipInfo clip = new ClipInfo();
					clip.setAvTitle(viInfo.getVideoName());
					clip.setAvId(bvId);
					clip.setcId(cid);
					clip.setPage(clipMap.size());
					clip.setTitle(String.format("%d.%d-%s", i, j, obj.getOption())); // 故事线i的第j个片段
					clip.setPicPreview(viInfo.getVideoPreview());
					clip.setUpName(viInfo.getAuthor());
					clip.setUpId(viInfo.getAuthorId());

					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					try {
						int qnList[] = getVideoQNList(bvId, String.valueOf(clip.getcId()));
						for (int qn : qnList) {
							if (getVideoLink) {
								String link = getVideoLink(bvId, String.valueOf(clip.getcId()), qn, videoFormat);
								links.put(qn, link);
							} else {
								links.put(qn, "");
							}
						}
						clip.setLinks(links);
					} catch (Exception e) {
						clip.setLinks(links);
					}
					clipMap.put(clip.getcId(), clip);
				} else {
					ClipInfo clip = (ClipInfo) clip_t;
					if (i == 0)
						clip.setTitle(String.format("%s_%d.%d-%s", clip.getTitle(), i, j, obj.getOption()));
					else
						clip.setTitle(String.format("%s_%d.%d", clip.getTitle(), i, j));
					if (j == 0)
						clip.setTitle("起始");
				}
			}
		}
		return clipMap;
	}
}
