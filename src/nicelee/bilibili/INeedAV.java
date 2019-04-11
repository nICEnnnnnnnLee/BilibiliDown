package nicelee.bilibili;

import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.model.ClipInfo;
import nicelee.model.VideoInfo;
import nicelee.util.CmdUtil;
import nicelee.util.HttpCookies;
import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;

public class INeedAV {

	// 0为MP4, 1 为FLV
	int downFormat = 0;
	HttpRequestUtil util = new HttpRequestUtil();
	
	public INeedAV() {
	}
	public static void main(String[] args) {
		System.out.println("-------------------------------");
		System.out.println("输入av号, 下载当前cookie所能下载的最清晰链接");
		System.out.println("-------------------------------");
		INeedAV ina = new INeedAV();
		ina.setDownFormat(0);
		INeedLogin inl = new INeedLogin();
		// 0. 尝试读取cookie
		List<HttpCookie> cookies = null;
		String cookiesStr = inl.readCookies();
		// 检查有没有本地cookie配置
		if (cookiesStr != null) {
			cookies = HttpCookies.convertCookies(cookiesStr);
		}
		HttpCookies.setGlobalCookies(cookies);

		// 1. 获取av 信息, 并下载当前Cookies最清晰的链接
		try {
			VideoInfo avInfo = ina.getVideoDetail(args[0], true);

			// 下载最清晰的链接
			for (ClipInfo clip : avInfo.getClips().values()) {
				ina.downloadClip(clip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提取字符串里的avID
	 * 
	 * @param origin
	 * @return
	 */
	Pattern avPattern = Pattern.compile("av[0-9]+");//普通视频
	Pattern epPattern = Pattern.compile("ep[0-9]+");//番剧单集
	Pattern ssPattern = Pattern.compile("ss[0-9]+");//season合集
	Pattern mdPattern = Pattern.compile("md[0-9]+");//番剧合集
	
	Pattern spacePattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/video");//个人主页 - 全部视频
	Pattern sChannelPattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/channel/detail\\?cid=([0-9]+)");//个人主页 - 频道
	Pattern favPattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=([0-9]+)");//个人收藏夹
	Pattern fav4mlPattern = Pattern.compile("ml([0-9]+)");//个人收藏夹
	
	Pattern paramPattern = Pattern.compile("p=([0-9]+)$");//自定义参数, 目前只匹配个人主页视频的页码
	
	Pattern auPattern = Pattern.compile("au[0-9]+");//音频

	public String getAvID(String origin) {
		origin = origin.toLowerCase();
		Matcher avMatcher = avPattern.matcher(origin);
		Matcher epMatcher = epPattern.matcher(origin);
		Matcher ssMatcher = ssPattern.matcher(origin);
		Matcher mdMatcher = mdPattern.matcher(origin);
		Matcher spaceMatcher = spacePattern.matcher(origin);
		Matcher sChannelMatcher = sChannelPattern.matcher(origin);
		Matcher favMatcher = favPattern.matcher(origin);
		Matcher fav4mlMatcher = fav4mlPattern.matcher(origin);
		if (avMatcher.find()) {
			System.out.println("匹配av号");
			// 如果能提取出avID, 直接返回
			return avMatcher.group();
		} else if (epMatcher.find()) {
			// 如果不能提取, 例如番剧 :https://www.bilibili.com/bangumi/play/ep250435/
			System.out.println("匹配ep号");
			//String avID = EpIdToAvId(epMatcher.group());
			return epMatcher.group();
		}else if (ssMatcher.find()) {
			// 如果不能提取, 例如番剧 :https://www.bilibili.com/bangumi/play/ss26295/
			System.out.println("匹配ss号");
			//String avID = EpIdToAvId(ssMatcher.group());
			return ssMatcher.group();
		}else if (mdMatcher.find()) {
			// 如果不能提取, 例如番剧 :https://www.bilibili.com/bangumi/media/md134912/
			System.out.println("匹配md号");
			//String avID = EpIdToAvId(ssMatcher.group());
			return mdMatcher.group();
		}else if(spaceMatcher.find()) {
			//e.g. https://space.bilibili.com/5276/video
			System.out.println("匹配UP主主页,返回 av1 av2 av3 ...");
			Matcher matcher = paramPattern.matcher(origin);
			if(matcher.find()) {
				return getAVList4Space(spaceMatcher.group(1), Integer.parseInt(matcher.group(1)));
			}
			return getAVList4Space(spaceMatcher.group(1), 1);
		}else if(sChannelMatcher.find()) {
			//e.g. https://space.bilibili.com/378034/channel/detail?cid=188
			System.out.println("匹配UP主主页特定频道,返回 av1 av2 av3 ...");
			Matcher matcher = paramPattern.matcher(origin);
			if(matcher.find()) {
				return getAVList4Space(sChannelMatcher.group(1), Integer.parseInt(matcher.group(1)));
			}
			return getAVList4Channel(sChannelMatcher.group(1), sChannelMatcher.group(2), 1);
		}else if(favMatcher.find()) {
			//e.g. https://space.bilibili.com/xxx/favlist?fid=xxx&ftype=create
			System.out.println("匹配收藏夹,返回 av1 av2 av3 ...");
			Matcher matcher = paramPattern.matcher(origin);
			if(matcher.find()) {
				return getAVList4FaviList(favMatcher.group(1), favMatcher.group(2), Integer.parseInt(matcher.group(1)));
			}
			return getAVList4FaviList(favMatcher.group(1), favMatcher.group(2), 1);
		}else if(fav4mlMatcher.find()) {
			//e.g. mlXXX
			System.out.println("匹配收藏夹,返回 av1 av2 av3 ...");
			Matcher matcher = paramPattern.matcher(origin);
			if(matcher.find()) {
				return getAVList4FaviList(fav4mlMatcher.group(1), Integer.parseInt(matcher.group(1)));
			}
			return getAVList4FaviList(fav4mlMatcher.group(1), 1);
			
		}
		return "";
	}

	/**
	 * 已知epId, 求avId 目前没有抓到api哦... 暂时从网页里面爬
	 */
	public String EpIdToAvId(String epId) {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + epId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		int avId = jObj.getJSONObject("epInfo").getInt("aid");
		System.out.println("avId为: " + avId);
		return "av" + avId;
	}
	/**
	 * 已知MdId, 求SsId 目前没有抓到api哦... 暂时从网页里面爬
	 */
	public String MdIdToSsId(String mdId) {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/media/" + mdId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));
		
		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		int ssId = jObj.getJSONObject("mediaInfo").getJSONObject("param").getInt("season_id");
		System.out.println("avId为: " + ssId);
		return "ss" + ssId;
	}

	/**
	 * 下载视频
	 * 
	 * @param avClipInfo
	 * @return
	 */
	public boolean downloadClip(ClipInfo avClipInfo) {
		String url = getSuperHDLink(avClipInfo);
		String avId = avClipInfo.getAvId();
		return downloadClip(url, avId, String.valueOf(avClipInfo.getPage()));
	}

	/**
	 * 下载视频
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	public boolean downloadClip(String url, String avId, String page) {
		if (downFormat == 0 && url.contains(".m4s?")) {
			return downloadM4sClip(url, avId, page);
		} else {
			return downloadFLVClip(url, avId, page);
		}
	}

	/**
	 * 下载FLV/MP4视频(音视频一起)
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	boolean downloadFLVClip(String url, String avId, String page) {
		String suffix = url.contains(".mp4")? ".mp4" : ".flv";
		HttpHeaders header = new HttpHeaders();
		if(url.contains("#")) {
			String links[] = url.split("#");
			util.setTotalTask(links.length);
			Pattern numUrl = Pattern.compile("^([0-9]+)(http.*)$");
			for(int i = 0; i < links.length; i++) {
				util.setCurrentTask(i+1);
				Matcher matcher = numUrl.matcher(links[i]);
				matcher.find();
				String order = matcher.group(1);
				String tUrl = matcher.group(2);
				String fileName = avId + "-p" + page + "-part" +order +suffix;
				if(!util.download(tUrl, fileName, header.getBiliWwwFLVHeaders(avId))) {
					return false;
				}
			}
			//下载完毕后,进行合并
			util.setConverting(true);
			CmdUtil.convert(avId + "-p" + page +suffix, links.length);
			util.setConverting(false);
			return true;
		}else {
			String fileName = avId + "-p" + page + suffix;
			return util.download(url, fileName, header.getBiliWwwFLVHeaders(avId));
		}
	}

	/**
	 * 下载M4S视频(音视频分开隔离)
	 * 
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	boolean downloadM4sClip(String url, String avId, String page) {

		String links[] = url.split("#");
		String videoName = avId + "-p" + page + "_video.m4s";
		String audioName = avId + "-p" + page + "_audio.m4s";
		String dstName = avId + "-p" + page + ".mp4";

		util.setTotalTask(2);
		//util.setNextTask(audioName + "##" + links[1]);
		util.setCurrentTask(1);
		util.setCmd(CmdUtil.createConvertCmd(videoName, audioName, dstName));
		if (util.download(links[0], videoName, new HttpHeaders().getBiliWwwM4sHeaders(avId))) {
			//util.setNextTask(null);
			util.setCurrentTask(2);
			util.setTotal(0);
			if (util.download(links[1], audioName, new HttpHeaders().getBiliWwwM4sHeaders(avId))) {
				util.setConverting(true);
				CmdUtil.convert(videoName, audioName, dstName);
				util.setConverting(false);
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 获取当前Cookie所能获得的资源连接
	 * 
	 * @param avClipInfo
	 * @return
	 */
	public String getSuperHDLink(ClipInfo avClipInfo) {
		HashMap<Integer, String> links = avClipInfo.getLinks();
		// 对HashMap中的key 进行排序
		List<Integer> list = new LinkedList<Integer>(links.keySet());
		Collections.sort(list);
		return links.get(list.get(list.size() - 1));
	}
	
	int pageSize = 5;
	/**
	 *  获取up主个人上传的视频列表, 默认每页5个
	 * @param spaceID
	 * @param page
	 * @return
	 */
	String getAVList4Space(String spaceID, int page) {
		String urlFormat = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=%s&pagesize=%d&tid=0&page=%d&keyword=&order=pubdate";
		String url = String.format(urlFormat, spaceID, pageSize, page);
		String json = util.getContent(url, new HttpHeaders().getCommonHeaders("space.bilibili.com"));
		JSONObject jobj = new JSONObject(json);
		JSONArray arr = jobj.getJSONObject("data").getJSONArray("vlist");
		StringBuilder sb = new StringBuilder(); 
		for(int i = 0; i < arr.length(); i++) {
			sb.append(" av").append(arr.getJSONObject(i).getLong("aid"));
		}
		return sb.toString();
	}
	/**
	 * 获取up主个人上传的特定频道的视频列表, 默认每页5个
	 * @param spaceID
	 * @param cid
	 * @param page
	 * @return
	 */
	String getAVList4Channel(String spaceID, String cid, int page) {
		String urlFormat = "https://api.bilibili.com/x/space/channel/video?mid=%s&cid=%s&pn=%d&ps=%d&order=0";
		String url = String.format(urlFormat, spaceID, cid, page, pageSize);
		String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
		JSONObject jobj = new JSONObject(json);
		JSONArray arr = jobj.getJSONObject("data").getJSONObject("list").getJSONArray("archives");
		StringBuilder sb = new StringBuilder(); 
		for(int i = 0; i < arr.length(); i++) {
			sb.append(" av").append(arr.getJSONObject(i).getLong("aid"));
		}
		return sb.toString();
	}
	
	/**
	 * 获取up主收藏夹的视频列表, 默认每页5个
	 * @param personID
	 * @param favID
	 * @param page
	 * @return
	 */
	String getAVList4FaviList(String personID, String favID, int page) {
		try {
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String url = String.format(urlFormat, favID, page, pageSize);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(personID, favID), HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("medias");//.getJSONArray("archives");
			StringBuilder sb = new StringBuilder(); 
			for(int i = 0; i < arr.length(); i++) {
				sb.append(" av").append(arr.getJSONObject(i).getLong("id"));
			}
			return sb.toString();
		}catch (Exception e) {
			return "";
		}
		
	}
	
	/**
	 * 获取up主收藏夹的视频列表, 默认每页5个
	 * @param favID
	 * @param page
	 * @return
	 */
	String getAVList4FaviList(String favID, int page) {
		try {
			//原api需要 personID + favID，弃用 (personID用于构造header，否则没权限)
			//String urlFormat = "https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/detail?media_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, favID, page, pageSize);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(favID), HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONArray arr = jobj.getJSONObject("data").getJSONArray("medias");//.getJSONArray("archives");
			StringBuilder sb = new StringBuilder(); 
			for(int i = 0; i < arr.length(); i++) {
				sb.append(" av").append(arr.getJSONObject(i).getLong("id"));
			}
			return sb.toString();
		}catch (Exception e) {
			return "";
		}
		
	}
	/**
	 * 获取AVid 视频的所有信息(全部)
	 * 
	 * @param avId
	 * @param isGetLink
	 * @return
	 */
	public VideoInfo getVideoDetail(String avId, boolean isGetLink) {
		if(avId.startsWith("av")) {
			return getAVDetail(avId, isGetLink);
		}else if(avId.startsWith("ep")){
			return getAVDetail(EpIdToAvId(avId), isGetLink);
		}else if(avId.startsWith("ss")){
			return getSSDetail(avId, isGetLink);
		}else if(avId.startsWith("md")){
			return getSSDetail(MdIdToSsId(avId), isGetLink);
		}
		return getAVDetail(avId, isGetLink);
	}

	/**
	 * @param ssId
	 * @param isGetLink
	 * @return
	 */
	private VideoInfo getSSDetail(String ssId, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(ssId);
		
		HttpHeaders headers = new HttpHeaders();
		String url = "https://www.bilibili.com/bangumi/play/" + ssId;
		String html = util.getContent(url, headers.getCommonHeaders("www.bilibili.com"));

		int begin = html.indexOf("window.__INITIAL_STATE__=");
		int end = html.indexOf(";(function()", begin);
		String json = html.substring(begin + 25, end);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		viInfo.setVideoName(jObj.getJSONObject("mediaInfo").getString("title"));
		viInfo.setBrief(jObj.getJSONObject("mediaInfo").getString("evaluate"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview("https:" + jObj.getJSONObject("mediaInfo").getString("cover"));
		
		JSONArray array = jObj.getJSONArray("epList");
		HashMap<Integer, ClipInfo> clipMap = new HashMap<Integer, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvId("av" + clipObj.getInt("aid"));
			clip.setcId(clipObj.getLong("cid"));
			//clip.setPage(Integer.parseInt(clipObj.getString("index")));
			clip.setPage(clipObj.getInt("i") + 1);
			//clip.setTitle(clipObj.getString("index_title"));
			clip.setTitle(clipObj.getString("longTitle"));
			
			HashMap<Integer, String> links = new HashMap<Integer, String>();
			try {
				int qnList[] = getVideoQNList(clip.getAvId(), String.valueOf(clip.getcId()));
				for (int qn : qnList) {
					if (isGetLink) {
						String link = getVideoLink(clip.getAvId(), String.valueOf(clip.getcId()), qn);
						links.put(qn, link);
					} else {
						links.put(qn, "");
					}
				}
				clip.setLinks(links);
			}catch (Exception e) {
				clip.setLinks(links);
			}
			
			clipMap.put(clip.getPage(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * @param avId
	 * @param isGetLink
	 * @return
	 */
	private VideoInfo getAVDetail(String avId, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);

		String avIdNum = avId.replace("av", "");
		String url = "https://api.bilibili.com/x/web-interface/view?aid=" + avIdNum;
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
		// System.out.println(json);
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
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

		//
		JSONArray array = jObj.getJSONArray("pages");
		HashMap<Integer, ClipInfo> clipMap = new HashMap<Integer, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvId(avId);
			clip.setcId(clipObj.getLong("cid"));
			clip.setPage(clipObj.getInt("page"));
			clip.setTitle(clipObj.getString("part"));
			
			HashMap<Integer, String> links = new HashMap<Integer, String>();
			try {
				int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
				for (int qn : qnList) {
					if (isGetLink) {
						String link = getVideoLink(avId, String.valueOf(clip.getcId()), qn);
						links.put(qn, link);
					} else {
						links.put(qn, "");
					}
				}
				clip.setLinks(links);
			}catch (Exception e) {
				clip.setLinks(links);
			}
			
			clipMap.put(clip.getPage(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * 查询视频链接
	 * 
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return
	 */
	public String getVideoLink(String avId, String cid, int qn) {
		if (downFormat == 0) {
			return getVideoM4sLink(avId, cid, qn);
		} else {
			return getVideoFLVLink(avId, cid, qn);
		}
	}

	/**
	 * 查询视频链接(FLV)
	 * 
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn   112: hdflv2;80: flv; 64: flv720; 32: flv480; 16: flv360
	 * @return url or  1 url1#2 url2...
	 */
	String getVideoFLVLink(String avId, String cid, int qn) {
		System.out.println("正在查询FLV链接...");
		String avIdNum = avId.replace("av", "");

		HttpHeaders headers = new HttpHeaders();
		JSONObject jObj = null;
		try {
			String url = "https://api.bilibili.com/x/player/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("FLV链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}

		int linkQN = jObj.getInt("quality");
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		JSONArray urlList = jObj.getJSONArray("durl");
		return parseUrlJArray(urlList);
	}

	/**
	 * @param urlList
	 * @return
	 */
	private String parseUrlJArray(JSONArray urlList) {
		if(urlList.length() == 1) {
			return urlList.getJSONObject(0).getString("url");
			
		}else {
			StringBuilder link = new StringBuilder();
			for(int i = 0; i < urlList.length(); i++) {
				JSONObject obj = urlList.getJSONObject(i);
				link.append(obj.getInt("order"));
				link.append(obj.getString("url"));
				link.append("#");
			}
			System.out.println(link.substring(0, link.length() - 1));
			return link.substring(0, link.length() - 1);
		}
	}

	/**
	 * 查询视频链接(MP4)
	 * 
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
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&type=&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("data");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("MP4链接地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, qn);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jObj = new JSONObject(json).getJSONObject("result");
		}
		int linkQN = jObj.getInt("quality");
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
			//鉴于部分视频如 https://www.bilibili.com/video/av24145318 H5仍然是用的是Flash源,此处切为FLV
			
			return parseUrlJArray(jObj.getJSONArray("durl"));
		}

	}

	/**
	 * 查询视频可提供的质量 原https://api.bilibili.com/x/player/playurl接口逐渐在被弃用?
	 * 使用https://api.bilibili.com/pgc/player/web/playurl
	 * 
	 * @param avId
	 * @param cid
	 * @return
	 */
	public int[] getVideoQNList(String avId, String cid) {

		String avIdNum = avId.replace("av", "");
		HttpHeaders headers = new HttpHeaders();
		JSONArray jArr = null;
		try {
			String url = "https://api.bilibili.com/x/player/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			//System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("地址解析失败,使用另一种方式");
			String url = "https://api.bilibili.com/pgc/player/web/playurl?fnval=16&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
			url = String.format(url, avIdNum, cid, 32);
			String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId), HttpCookies.getGlobalCookies());
			System.out.println(json);
			jArr = new JSONObject(json).getJSONObject("result").getJSONArray("accept_quality");
		}
		int qnList[] = new int[jArr.length()];
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = jArr.getInt(i);
		}
		return qnList;
	}

	public HttpRequestUtil getUtil() {
		return util;
	}

	public void setUtil(HttpRequestUtil util) {
		this.util = util;
	}

	public int getDownFormat() {
		return downFormat;
	}

	public void setDownFormat(int downFormat) {
		this.downFormat = downFormat;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
