package nicelee.bilibili;

import org.json.JSONObject;

import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

public class INeedAVbPhone {
	
	HttpRequestUtil util = new HttpRequestUtil();
	
	public VideoInfo getVideoFromMobileById(String avId) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);

		String url = String.format("https://m.bilibili.com/video/%s.html", avId);
		String html;
		try {
			html = util.getContent(url, new HttpHeaders().getBiliMHeaders(), null);
			int begin = html.indexOf("window.__INITIAL_STATE__=");
			int end = html.indexOf("</script>", begin);
			String jsonStr = html.substring(begin + 25, end);
			//System.out.println(jsonStr);
			JSONObject jObj = new JSONObject(jsonStr).getJSONObject("reduxAsyncConnect").getJSONObject("videoInfo");
			String videoName = jObj.getString("title");
			String brief = jObj.getString("desc");
			String author = jObj.getJSONObject("owner").getString("name");
			String authorId = String.valueOf(jObj.getJSONObject("owner").getLong("mid"));
			String videoPreview = jObj.getString("pic");
			String links = "https:" + jObj.getString("initUrl");
			viInfo.setVideoName(videoName);
			viInfo.setBrief(brief);
			viInfo.setAuthor(author);
			viInfo.setAuthorId(authorId);
			viInfo.setVideoPreview(videoPreview);
			viInfo.setVideoLink(links);
			viInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return viInfo;
	}
}
