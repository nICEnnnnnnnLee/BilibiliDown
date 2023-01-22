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
import nicelee.bilibili.util.Logger;

@Bilibili(name = "ml-parser",
		note = "收藏夹解析器")
public class MLParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("ml([0-9]+)");
	
	protected String mlIdNumber;

	
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
		result(pageSize, paramSetter.getPage(), mlIdNumber, videoFormat, getVideoLink);
		return pageQueryResult;
	}
	
	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}
	
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		//Logger.println(str);
		String favID = (String) obj[0];
		int videoFormat = (int) obj[1];
		boolean getVideoLink = (boolean) obj[2];
		
		try {
			System.out.printf("--获取收藏夹视频： 第%d页 第%d 到 第%d\n", page, min, max);
			//String urlFormat = "https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=%s&pn=%d&ps=%d&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp";
			String urlFormat = "https://api.bilibili.com/medialist/gateway/base/detail?media_id=%s&pn=%d&ps=%d";
			String url = String.format(urlFormat, favID, page, API_PMAX);
			String json = util.getContent(url, new HttpHeaders().getFavListHeaders(favID),
					HttpCookies.getGlobalCookies());
			Logger.println(url);
			Logger.println(json);
			JSONObject jobj = new JSONObject(json);
			JSONObject jData = jobj.getJSONObject("data");
			JSONObject jInfo = jobj.getJSONObject("data").getJSONObject("info");
			if(pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId("ml" + favID);
				pageQueryResult.setVideoName(jInfo.getString("title") + paramSetter.getPage());
				pageQueryResult.setBrief(jInfo.getJSONObject("upper").getString("name") + " 的收藏夹");
				pageQueryResult.setAuthor(jInfo.getJSONObject("upper").getString("name"));
				pageQueryResult.setAuthorId(""+ jInfo.getJSONObject("upper").getLong("mid"));
				pageQueryResult.setVideoPreview(jInfo.getString("cover"));
			}
			
			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			JSONArray arr = jData.getJSONArray("medias");// .getJSONArray("archives");
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				String avId = jAV.getString("bvid");
				String avTitle =  jAV.getString("title");
				String upName = jAV.getJSONObject("upper").getString("name");
				String upId = "" + jAV.getJSONObject("upper").getLong("mid");
				long favTime = jAV.optLong("fav_time") * 1000;
				long cTime = jAV.optLong("ctime") * 1000;
				JSONArray jClips = jAV.optJSONArray("pages");
				if(jClips == null) {
					String link = jAV.optString("link", "");
					// 添加针对音频的特殊情况
					if(link.startsWith("bilibili://music")) {
						long auIdNum = jAV.optLong("id");
						ClipInfo clip = new ClipInfo();
						clip.setAvTitle(avTitle);
						clip.setAvId("au" + auIdNum);
						clip.setcId(auIdNum);
						clip.setPage(1);
						clip.setTitle(avTitle);
						clip.setPicPreview(jAV.getString("cover"));
						clip.setRemark((page - 1) * API_PMAX + i + 1);
						clip.setListName(jInfo.getString("title").replaceAll("[/\\\\]", "_"));
						clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
						clip.setUpName(upName);
						clip.setUpId(upId);
						clip.setFavTime(favTime);
						clip.setcTime(cTime);
						LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
						int[] qnList = { 3, 2, 1, 0 };
						for (int qn : qnList) {
							links.put(qn, "");
						}
						clip.setLinks(links);
						map.put(clip.getcId(), clip);
						continue;
					}
					continue;
				}
				for(int pointer = 0; pointer < jClips.length(); pointer++) {
					JSONObject jClip = jClips.getJSONObject(pointer);
					ClipInfo clip = new ClipInfo();
					clip.setAvId(avId);
					clip.setcId(jClip.getLong("id"));
					clip.setPage(jClip.getInt("page"));
					clip.setRemark((page-1)*API_PMAX + i + 1);
					clip.setPicPreview(jAV.getString("cover"));
//					clip.setAvTitle(pageQueryResult.getVideoName());
//					clip.setTitle(avTitle + "-" +jClip.getString("title"));
					// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
					clip.setListName(jInfo.getString("title").replaceAll("[/\\\\]", "_"));
					clip.setListOwnerName(pageQueryResult.getAuthor().replaceAll("[/\\\\]", "_"));
					clip.setUpName(upName);
					clip.setUpId(upId);
					clip.setAvTitle(avTitle);
					clip.setTitle(jClip.getString("title"));
					clip.setFavTime(favTime);
					clip.setcTime(cTime);
					
					LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
					// TODO json这里的清晰度到底是什么鬼哦？？不知道怎么解析
					try {
						for(VideoQualityEnum VQ: VideoQualityEnum.values()) {
							if (getVideoLink) {
								String link = getVideoLink(avId, String.valueOf(clip.getcId()), VQ.getQn(), videoFormat);
								links.put(VQ.getQn(), link);
							} else {
								links.put(VQ.getQn(), "");
							}
							//links.remove(116);
							//links.remove(74);
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
			//e.printStackTrace();
			return false;
		}
	}
	
}
