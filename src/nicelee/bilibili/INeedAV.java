package nicelee.bilibili;

import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.model.ClipInfo;
import nicelee.model.VideoInfo;
import nicelee.util.HttpCookies;
import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;

public class INeedAV {

	HttpRequestUtil util = new HttpRequestUtil();

	public static void main(String[] args) {
		System.out.println("-------------------------------");
		System.out.println("输入av号, 下载当前cookie所能下载的最清晰链接");
		System.out.println("-------------------------------");
		INeedAV ina = new INeedAV();
		INeedLogin inl =new INeedLogin();
		// 0. 尝试读取cookie
		List<HttpCookie> cookies = null;
		String cookiesStr = inl.readCookies();
		//检查有没有本地cookie配置
		if(cookiesStr != null) {
			cookies = HttpCookies.convertCookies(cookiesStr);
		}
		HttpCookies.setGlobalCookies(cookies);
		
		//1. 获取av 信息, 并下载当前Cookies最清晰的链接
		try {
			VideoInfo avInfo =ina.getVideoDetail(args[0], true);
			
			//下载最清晰的链接
			for(ClipInfo clip: avInfo.getClips().values()) {
				ina.downloadClip(clip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  提取字符串里的avID
	 * @param origin
	 * @return
	 */
	public String getAvID(String origin) {
		int end = origin.indexOf("?");
		if(end > -1) {
			origin = origin.substring(0, end - 1);
		}
		String avId = origin.replaceAll("av[0-9]+$", "");
		avId = origin.replaceFirst(avId, "");
		System.out.println(avId);
		return avId;
	}
	/**
	 *  下载视频
	 * @param avClipInfo
	 * @return
	 */
	public boolean downloadClip(ClipInfo avClipInfo) {
		String url = getSuperHDAVLink(avClipInfo);
		String avId = avClipInfo.getAvId();
		String fileName = avId + "-p"+ avClipInfo.getPage() + ".flv";
		return util.download(url, fileName, 
				new HttpHeaders().getBiliWwwHeaders(avId));
	}
	/**
	 * 下载视频
	 * @param url
	 * @param avId
	 * @param page
	 * @return
	 */
	public boolean downloadClip(String url, String avId, String page) {
		String fileName = avId + "-p"+ page + ".flv";
		return util.download(url, fileName, 
				new HttpHeaders().getBiliWwwHeaders(avId));
	}
	/**
	 * 获取当前Cookie所能获得的资源连接
	 * @param avClipInfo
	 * @return
	 */
	public String getSuperHDAVLink(ClipInfo avClipInfo) {
		HashMap<Integer, String> links = avClipInfo.getLinks();
		// 对HashMap中的key 进行排序
		List<Integer> list = new LinkedList<Integer>(links.keySet());
        Collections.sort(list);
		return links.get(list.get(list.size() -1));
	}
	
	
	/**
	 * 获取AVid 视频的所有信息(全部)
	 * 
	 * @param avId
	 * @param isGetLink
	 * @return
	 */
	public VideoInfo getVideoDetail(String avId, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);

		String avIdNum = avId.replace("av", "");
		String url = "https://api.bilibili.com/x/web-interface/view?aid=" + avIdNum;
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId),
				HttpCookies.getGlobalCookies());
		System.out.println(json);
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

			int qnList[] = getVideoQNList(avId, String.valueOf(clip.getcId()));
			HashMap<Integer, String> links = new HashMap<Integer, String>();
			for (int qn : qnList) {
				if(isGetLink) {
					String link = getVideoFLVLink(avId, String.valueOf(clip.getcId()), qn);
					links.put(qn, link);
				}else {
					links.put(qn, "");
				}
			}
			clip.setLinks(links);
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
	public String getVideoFLVLink(String avId, String cid, int qn) {
		String avIdNum = avId.replace("av", "");
		String url = "https://api.bilibili.com/x/player/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
		url = String.format(url, avIdNum, cid, qn);
		// System.out.println(url);
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId),
				HttpCookies.getGlobalCookies());
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		int linkQN = jObj.getInt("quality");
		System.out.println("查询质量为:" + qn + "的链接, 得到质量为:" + linkQN + "的链接");
		return jObj.getJSONArray("durl").getJSONObject(0).getString("url");
	}

	/**
	 * 查询视频可提供的质量
	 * 
	 * @param avId
	 * @param cid
	 * @return
	 */
	public int[] getVideoQNList(String avId, String cid) {

		String avIdNum = avId.replace("av", "");
		String url = "https://api.bilibili.com/x/player/playurl?fnval=2&fnver=0&player=1&otype=json&avid=%s&cid=%s&qn=%s";
		url = String.format(url, avIdNum, cid, 32);
		// System.out.println(url);
		HttpHeaders headers = new HttpHeaders();
		String json = util.getContent(url, headers.getBiliJsonAPIHeaders(avId),
				HttpCookies.getGlobalCookies());
		JSONArray jArr = new JSONObject(json).getJSONObject("data").getJSONArray("accept_quality");
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
}
