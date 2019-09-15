package nicelee.bilibili.parsers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.StoryClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.IInputParser;
import nicelee.bilibili.parsers.IParamSetter;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.MD5;

public abstract class AbstractBaseParser implements IInputParser {

	final static String appkey = "YvirImLGlLANCLvM";
	final static String appSecret = "JNlZNgfNGKZEpaDTkCdPQVXntXhuiJEM";

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
	 * 
	 * @param avId         字符串带av
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	protected VideoInfo getAVDetail(String avId, int videoFormat, boolean getVideoLink) {
		String avIdNum = avId.replace("av", "");
		return getAVDetail(avId, avIdNum, videoFormat, getVideoLink);
	}

	/**
	 * 
	 * @param avId
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	protected VideoInfo getAVDetail(long avId, int videoFormat, boolean getVideoLink) {
		String avIdNum = "" + avId;
		String avIdStr = "av" + avIdNum;
		return getAVDetail(avIdStr, avIdNum, videoFormat, getVideoLink);
	}

	/**
	 * 获取详细av信息
	 * 
	 * @param avId
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	private VideoInfo getAVDetail(String avId, String avIdNum, int videoFormat, boolean getVideoLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);
		String url = "https://api.bilibili.com/x/web-interface/view?aid=" + avIdNum;
		HashMap<String, String> headers_json = new HttpHeaders().getBiliJsonAPIHeaders(avId);
		String json = util.getContent(url, headers_json, HttpCookies.getGlobalCookies());
		Logger.println(url);
		Logger.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		JSONArray array = jObj.getJSONArray("pages");

		// 总体大致信息
		String videoName = jObj.getString("title");
		String brief = jObj.getString("desc");
		String author = jObj.getJSONObject("owner").getString("name");
		String authorId = String.valueOf(jObj.getJSONObject("owner").getLong("mid"));
		String videoPreview = jObj.getString("pic");
		viInfo.setVideoName(videoName);
		viInfo.setBrief(brief);
		viInfo.setAuthor(author);
		viInfo.setAuthorId(authorId);
		viInfo.setVideoPreview(videoPreview);

		// 判断是否是互动视频
		int videos = jObj.getInt("videos");
		long cid = array.getJSONObject(0).getLong("cid");
		if (videos > array.length() && array.length() == 1) {
			// 查询graph_version版本
			String url_graph_version = String.format("https://api.bilibili.com/x/player.so?id=cid:%d&aid=%s", cid,
					avIdNum);
//			HashMap<String, String> headers_gv = new HashMap<>();
//			headers_gv.put("Origin", "https://www.bilibili.com");
//			headers_gv.put("Referer", "https://www.bilibili.com/video/" + avId);
			String xml = util.getContent(url_graph_version, headers_json, HttpCookies.getGlobalCookies());
			Pattern p = Pattern.compile("<interaction>.*\"graph_version\" *: *([0-9]+).*</interaction>");
			Matcher matcher = p.matcher(xml);
			if (matcher.find()) {
				String graph_version = matcher.group(1);
				List<List<StoryClipInfo>> story_list = new ArrayList<>();
				List<StoryClipInfo> originStory = new ArrayList<StoryClipInfo>();
				StoryClipInfo storyClip = new StoryClipInfo(cid);
				originStory.add(storyClip);
				// 从根节点，一直遍历到子节点，找到所有故事线，放到story_list
				collectStoryList(avIdNum, "", graph_version, originStory, story_list);
				LinkedHashMap<Long, ClipInfo> clipMap = storyList2Map(avId, videoFormat, getVideoLink, viInfo,
						story_list);
				viInfo.setClips(clipMap);
				viInfo.print();
				return viInfo;
			}
		}

		// 非互动视频
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(avId);
			clip.setcId(clipObj.getLong("cid"));
			clip.setPage(clipObj.getInt("page"));
			clip.setTitle(clipObj.getString("part"));
			clip.setPicPreview(videoPreview);

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
				for (int qn : qnList) {
					if (getVideoLink) {
						String link = getVideoLink(avId, String.valueOf(clip.getcId()), qn, videoFormat);
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
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * 遍历所有故事线，找到所有故事片段并返回
	 * 
	 * @param avId
	 * @param videoFormat
	 * @param getVideoLink
	 * @param viInfo
	 * @param story_list
	 * @return
	 */
	private LinkedHashMap<Long, ClipInfo> storyList2Map(String avId, int videoFormat, boolean getVideoLink,
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
					clip.setAvId(avId);
					clip.setcId(cid);
					clip.setPage(clipMap.size());
					clip.setTitle(String.format("%d.%d-%s", i, j, obj.getOption())); // 故事线i的第j个片段
					clip.setPicPreview(viInfo.getVideoPreview());

					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					try {
						int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
						for (int qn : qnList) {
							if (getVideoLink) {
								String link = getVideoLink(avId, String.valueOf(clip.getcId()), qn, videoFormat);
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
					clip.setTitle(String.format("%s_%d.%d-%s", clip.getTitle(), i, j, obj.getOption()));
					if (j == 0)
						clip.setTitle("起始");
				}
			}
		}
		return clipMap;
	}

	/**
	 * 查询视频可提供的质量 原https://api.bilibili.com/x/player/playurl接口逐渐在被弃用?
	 * 使用https://api.bilibili.com/pgc/player/web/playurl
	 * 
	 * @external input HttpRequestUtil util
	 * @param avId
	 * @param cid
	 * @return
	 */
	public int[] getVideoQNList(String avId, String cid) {
		String avIdNum = avId.replace("av", "");
		HttpHeaders headers = new HttpHeaders();
		JSONArray jArr = null;
		try {
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&fourk=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			Logger.println(url);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			Logger.println(json);
			JSONObject result = new JSONObject(json);
			if (result.getInt("code") == 0) {// -10403 -404
				jArr = result.getJSONObject("data").getJSONArray("accept_quality");
			} else {
				System.out.println("版权限制，只能在app观看,使用另一种方式解析");
				String params = "actionkey=appkey&aid=%s&appkey=%s&build=5423000&cid=%s&device=android&expire=0&fnval=80&fnver=0&force_host=0&fourk=0&mid=0&mobi_app=android&npcybs=0&otype=json&platform=android&qn=%s&quality=3&ts="
						+ System.currentTimeMillis();
				params = String.format(params, avIdNum, appkey, cid, 32);

				String appUrl = "https://app.bilibili.com/x/playurl?" + params + "&sign=" + MD5.sign(params, appSecret);
				// Logger.println(appUrl);
				String appJson = util.getContent(appUrl, headers.getBiliAppJsonAPIHeaders(),
						HttpCookies.getGlobalCookies());
				Logger.println(appJson);
				jArr = new JSONObject(appJson).getJSONObject("data").getJSONArray("accept_quality");
			}
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
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		if (downFormat == 0) {
			return getVideoM4sLink(avId, cid, qn);
		} else {
			return getVideoFLVLink(avId, cid, qn);
		}
	}

	/**
	 * 查询视频链接(FLV)
	 * 
	 * @external input HttpRequestUtil util
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return url or 1 url1#2 url2...
	 */
	String getVideoFLVLink(String avId, String cid, int qn) {
		System.out.println("正在查询FLV链接...");
		String avIdNum = avId.replace("av", "");

		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		try {
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());

			// System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("FLV链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/x/player/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			JSONObject result = new JSONObject(json);

			if (result.getInt("code") == 0) {
				jObj = new JSONObject(json).getJSONObject("data");
			} else {
				System.out.println("版权限制，只能在app观看,使用另一种方式解析");
				String params = "actionkey=appkey&aid=%s&appkey=%s&build=5423000&cid=%s&device=android&expire=0&fnval=80&fnver=0&force_host=0&fourk=0&mid=0&mobi_app=android&npcybs=0&otype=json&platform=android&qn=%s&quality=3&ts="
						+ System.currentTimeMillis();
				params = String.format(params, avIdNum, appkey, cid, qn);

				String appUrl = "https://app.bilibili.com/x/playurl?" + params + "&sign=" + MD5.sign(params, appSecret);
				// Logger.println(appUrl);
				String appJson = util.getContent(appUrl, headers.getBiliAppJsonAPIHeaders(),
						HttpCookies.getGlobalCookies());
				Logger.println(appJson);
				jObj = new JSONObject(appJson).getJSONObject("data");
			}
		}

		int linkQN = jObj.getInt("quality");
		paramSetter.setRealQN(linkQN);
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		JSONArray urlList = jObj.getJSONArray("durl");
		return parseUrlJArray(urlList);
	}

	/**
	 * 查询视频链接(MP4)
	 * 
	 * @external input HttpRequestUtil util
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return 视频url + "#" + 音频url
	 */
	String getVideoM4sLink(String avId, String cid, int qn) {
		System.out.println("正在查询MP4链接...");
		String avIdNum = avId.replace("av", "");

		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		try {
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");

		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("MP4链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&type=&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());

			JSONObject result = new JSONObject(json);
			Logger.println(json);
			if (result.getInt("code") == 0) {
				jObj = new JSONObject(json).getJSONObject("data");
			} else {
				System.out.println("版权限制，只能在app观看,使用另一种方式解析");
				String params = "actionkey=appkey&aid=%s&appkey=%s&build=5423000&cid=%s&device=android&expire=0&fnval=80&fnver=0&force_host=0&fourk=0&mid=0&mobi_app=android&npcybs=0&otype=json&platform=android&qn=%s&quality=3&ts="
						+ System.currentTimeMillis();
				params = String.format(params, avIdNum, appkey, cid, qn);

				String appUrl = "https://app.bilibili.com/x/playurl?" + params + "&sign=" + MD5.sign(params, appSecret);
				// Logger.println(appUrl);
				String appJson = util.getContent(appUrl, headers.getBiliAppJsonAPIHeaders(),
						HttpCookies.getGlobalCookies());
				Logger.println(appJson);
				jObj = new JSONObject(appJson).getJSONObject("data");
			}
		}
		int linkQN = jObj.getInt("quality");
		paramSetter.setRealQN(linkQN);
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		try {
			StringBuilder link = new StringBuilder();
			// 获取视频链接
			JSONArray videos = jObj.getJSONObject("dash").getJSONArray("video");
			for (int i = 0; i < videos.length(); i++) {
				JSONObject video = videos.getJSONObject(i);
				if (video.getInt("id") == linkQN) {
					link.append(video.getString("baseUrl")).append("#");
					break;
				}
			}
			// 获取音频链接(默认第一个)
			JSONArray audios = jObj.getJSONObject("dash").getJSONArray("audio");
			link.append(audios.getJSONObject(0).getString("baseUrl"));

			return link.toString();
		} catch (Exception e) {
			// 鉴于部分视频如 https://www.bilibili.com/video/av24145318 H5仍然是用的是Flash源,此处切为FLV
			return parseUrlJArray(jObj.getJSONArray("durl"));
		}
	}

	/**
	 * 将avId currentStory故事线的所有子结局全部放入List
	 * @param avIdNum
	 * @param node
	 * @param graph_version
	 * @param currentStory
	 * @param story_list
	 */
	private void collectStoryList(String avIdNum, String node, String graph_version, List<StoryClipInfo> currentStory,
			List<List<StoryClipInfo>> story_list) {
		String url_node_format = "https://api.bilibili.com/x/stein/nodeinfo?aid=%s&node_id=%s&graph_version=%s&platform=pc&portal=0&screen=0";
		String url_node = String.format(url_node_format, avIdNum, node, graph_version);
		Logger.println(url_node);
		HashMap<String, String> headers_gv = new HashMap<>();
		headers_gv.put("Referer", "https://www.bilibili.com/video/av" + avIdNum);
		String str_nodeInfo = util.getContent(url_node, headers_gv, HttpCookies.getGlobalCookies());
		Logger.println(str_nodeInfo);
		JSONObject nodeInfo = new JSONObject(str_nodeInfo).getJSONObject("data");
		JSONObject edge = nodeInfo.optJSONObject("edges");
		if (node == null || "".equals(node)) { // 如果是第一个片段，补全信息
			StoryClipInfo sClip = currentStory.get(0);
			sClip.setNode_id("" + nodeInfo.optLong("node_id"));
			sClip.setOption(nodeInfo.getString("title"));
		}
		if (edge == null) { // 如果没有选择，则到达末尾，则故事线完整，可以收集
			story_list.add(currentStory);
		} else {
			JSONArray choices = edge.getJSONArray("choices");
			for (int i = 0; i < choices.length(); i++) {
				JSONObject choice = choices.getJSONObject(i);
				StoryClipInfo sClip = new StoryClipInfo(choice.optLong("cid"), "" + choice.optLong("node_id"),
						choice.getString("option"));
				List<StoryClipInfo> cloneStory = new ArrayList<StoryClipInfo>(currentStory); // 确保不会对传入的故事线产生影响，而是生成新的时间线
				cloneStory.add(sClip);
				collectStoryList(avIdNum, "" + choices.getJSONObject(i).getLong("node_id"), graph_version, cloneStory,
						story_list);
			}
		}
	}

	private String parseUrlJArray(JSONArray urlList) {
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

}
