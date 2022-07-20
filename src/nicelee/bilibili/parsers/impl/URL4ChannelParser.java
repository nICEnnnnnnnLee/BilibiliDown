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
/**
 * 这是老版的channel解析，针对的是如下类型的url
 * https://space.bilibili.com/378034/channel/detail?cid=189
 *	还能生效，但是从web端已经无法找到这类url了
 *
 */
@Bilibili(name = "URL4ChannelParser", ifLoad = "listAll", note = "UP 某主题频道文件夹的视频解析")
public class URL4ChannelParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern
			.compile("space\\.bilibili\\.com/([0-9]+)/channel/detail\\?cid=([0-9]+)");
	private String spaceID;
	private String cid;

	public URL4ChannelParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页特定频道,返回 av1 av2 av3 ...");
			spaceID = matcher.group(1);
			cid = matcher.group(2);

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
//		String spaceID = (String) obj[0];
//		String cid = (String) obj[1];
		int videoFormat = (int)obj[0];
		boolean getVideoLink = (boolean)obj[1];
		try {
			String urlFormat = "https://api.bilibili.com/x/space/channel/video?mid=%s&cid=%s&pn=%d&ps=%d&order=0";
			String url = String.format(urlFormat, spaceID, cid, page, API_PMAX);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("api.bilibili.com"));
			Logger.println(url);
			JSONObject jobj = new JSONObject(json);
			JSONObject jData = jobj.getJSONObject("data").getJSONObject("list");
			JSONArray arr = jData.getJSONArray("archives");

			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID + " - " + cid);
				pageQueryResult.setVideoName(jData.getString("name"));
				pageQueryResult.setBrief(jData.getString("intro"));
				pageQueryResult.setAuthor(arr.getJSONObject(0).getJSONObject("owner").getString("name"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setVideoPreview(jData.getString("cover"));
			}

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < arr.length() && i < max; i++) {
				JSONObject jAV = arr.getJSONObject(i);
				//String avId = "av" + jAV.getLong("aid");
				try {
					// try给包围，出现 稿件不可见等已失效视频 的异常跳过即可
					// 进行二次查询，增加请求次数为 pageSize
					map.putAll(convertVideoToClipMap(jAV.getString("bvid"), 
							(page -1)* API_PMAX + i + 1,
							videoFormat,
							getVideoLink));
				}catch (Exception e) {
				}
				// 不再二次查询，直接根据内容，av返回的只有第一p，且不知道清晰度
//				String avTitle = jAV.getString("title");
//				ClipInfo clip = new ClipInfo();
//				clip.setAvId(avId);
//				clip.setAvTitle(pageQueryResult.getVideoName());
//				clip.setcId(jAV.getLong("cid"));
//				clip.setPage(1);
//				clip.setRemark((page -1)* pageSize + i);
//				clip.setTitle(avTitle);
//
//				LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
//				try {
//					for (VideoQualityEnum VQ : VideoQualityEnum.values()) {
//						if (getVideoLink) {
//							String link = getVideoLink(avId, String.valueOf(clip.getcId()), VQ.getQn(), videoFormat);
//							links.put(VQ.getQn(), link);
//						} else {
//							links.put(VQ.getQn(), "");
//						}
//						links.remove(116);
//						links.remove(74);
//					}
//				} catch (Exception e) {
//				}
//				clip.setLinks(links);
//				map.put(clip.getcId(), clip);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 使用此方法会产生许多请求，慎用
	 * @param bvId
	 * @param remark
	 * @param videoFormat
	 * @param getVideoLink
	 * @return 将所有avId的视频封装成Map
	 */
	private LinkedHashMap<Long, ClipInfo> convertVideoToClipMap(String bvId, int remark, int videoFormat, boolean getVideoLink){
		LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
		VideoInfo video = getAVDetail(bvId, videoFormat, getVideoLink); // 耗时
		for(ClipInfo clip: video.getClips().values()) {
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
