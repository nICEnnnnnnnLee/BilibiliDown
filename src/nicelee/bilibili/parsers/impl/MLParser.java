package nicelee.bilibili.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;

@Bilibili(name = "ml-parser",
		note = "收藏夹解析器")
public class MLParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ml([0-9]+)");
	protected final int FAV_PMAX = 20; // 超过20， 将会返回空
	
	protected String mlIdNumber;
	protected VideoInfo video;

	public MLParser(Object... obj) {
		super(obj);
	}
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			mlIdNumber = matcher.group(1);
			return true;
		}
		return false;
	}

	@Override
	public String validStr(String input) {
		return "ml" + mlIdNumber + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("MLParser正在获取结果" + mlIdNumber);
		video = new VideoInfo();
		video.setClips(new LinkedHashMap<>());
		//getAVList4FaviListBySinglePage(videoFormat, getVideoLink, mlIdNumber, page, 1, 20);
		getAVList4FaviList(videoFormat, getVideoLink,  mlIdNumber, paramSetter.getPage());
		return video;
	}
	
	/**
	 * 获取up主收藏夹的视频列表
	 * @input HttpRequestUtil util
	 * @input int pageSize
	 * @param favID
	 * @param page
	 * @return
	 */
	boolean getAVList4FaviList(int videoFormat, boolean getVideoLink, String favID, int page) {
		// 获取第 begin 个到第 end 个视频
		int begin = (page - 1)*pageSize + 1;
		int end = page * pageSize;
		return getAVList4FaviList(videoFormat, getVideoLink, favID, begin, end);
	}
	
	/**
	 * 获取up主收藏夹的视频列表
	 *   获取第 begin 个到第 end 个视频
	 * @input HttpRequestUtil util
	 * @input int pageSize
	 * @param favID
	 * @param begin
	 * @param end
	 * @return
	 */
	private boolean getAVList4FaviList(int videoFormat, boolean getVideoLink, String favID, int begin, int end) {
		System.out.println("获取收藏夹视频" + begin + " - "  + end);
		// begin 属于第 (begin-1)/FAV_PMAX + 1 页
		// end 属于第(end-1)/FAV_PMAX + 1 页
		int pageBegin = (begin-1)/FAV_PMAX + 1;
		int minPointerinBegin = (begin-1) % FAV_PMAX + 1;
		int pageEnd = (end-1)/FAV_PMAX + 1;
		int maxPointerinEnd = (end-1) % FAV_PMAX + 1;
		// 如果一次请求可以搞定
		if(pageBegin == pageEnd) {
			return getAVList4FaviListBySinglePage(videoFormat, getVideoLink,favID, pageBegin, minPointerinBegin, maxPointerinEnd);
		}
		// 如果需要两次以上， 先请求一次， 
		boolean listNotEmpty = getAVList4FaviListBySinglePage(videoFormat, getVideoLink, favID, pageBegin, minPointerinBegin, FAV_PMAX);
		if(!listNotEmpty) {
			return false;
		}
		for(int i = pageBegin + 1; i <= pageEnd; i++) {
			if(i < pageEnd) {
				listNotEmpty = getAVList4FaviListBySinglePage(videoFormat, getVideoLink, favID, i, 1, FAV_PMAX);
			}else {
				listNotEmpty = getAVList4FaviListBySinglePage(videoFormat, getVideoLink, favID, i, 1, maxPointerinEnd);
			}
			if(!listNotEmpty) {
				break;
			}
		}
		return true;
	}
	
	/**
	 * 取分页里的第min到 第max个视频
	 * @param favID
	 * @param page
	 * @param min
	 * @param max
	 * @return
	 */
	private boolean getAVList4FaviListBySinglePage(int videoFormat, boolean getVideoLink, String favID, int page, int min, int max) {
		try {
			System.out.printf("--获取收藏夹视频： 第%d页 第%d 到 第%d\n", page, min, max);
			//String urlFormat = "https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/detail?media_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, favID, page, FAV_PMAX);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(favID),
					HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONObject jData = jobj.getJSONObject("data");
			JSONObject jInfo = jobj.getJSONObject("data").getJSONObject("info");
			if(video.getVideoName() == null) {
				video.setVideoId("ml" + favID);
				video.setVideoName(jInfo.getString("title"));
				video.setBrief(jInfo.getJSONObject("upper").getString("name") + " 的收藏夹");
				video.setAuthor(jInfo.getJSONObject("upper").getString("name"));
				video.setAuthorId(""+ jInfo.getJSONObject("upper").getLong("mid"));
				video.setVideoPreview(jInfo.getString("cover"));
			}
			
			LinkedHashMap<Long, ClipInfo> map = video.getClips();
			JSONArray arr = jData.getJSONArray("medias");// .getJSONArray("archives");
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				String avId = "av" + arr.getJSONObject(i).getLong("id");
				String avTitle =  arr.getJSONObject(i).getString("title");
				JSONArray jClips = jAV.getJSONArray("pages");
				for(int pointer = 0; pointer < jClips.length(); pointer++) {
					JSONObject jClip = jClips.getJSONObject(pointer);
					ClipInfo clip = new ClipInfo();
					clip.setAvId(avId);
					clip.setAvTitle(video.getVideoName());
					clip.setcId(jClip.getLong("id"));
					clip.setPage(jClip.getInt("page"));
					clip.setRemark((page-1)*FAV_PMAX + i + 1);
					clip.setTitle(avTitle + "-" +jClip.getString("title"));
					
					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					// TODO json这里的清晰度到底是什么鬼哦？？不知道怎么解析
					try {
						for(VideoQualityEnum VQ: VideoQualityEnum.values()) {
							links.put(VQ.getQn(), "");
							links.remove(116);
							links.remove(74);
						}
//						JSONArray jQN = jClip.getJSONArray("metas");
//						for (int tt = 0; tt < jQN.length(); tt++) {
//							int quality = jQN.getJSONObject(tt).getInt("quality");
//							if(VideoQualityEnum.contains(quality)) {
//								if (getVideoLink) {
//									String link = getVideoLink(avId, String.valueOf(clip.getcId()), quality, videoFormat);
//									links.put(quality, link);
//								} else {
//									links.put(quality, "");
//								}
//							}
//							if(links.size() > 6) {
//								links.remove(116);
//								links.remove(74);
//							}
//						}
					} catch (Exception e) {
					}
					clip.setLinks(links);
					
					map.put(clip.getcId(), clip);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
