package nicelee.bilibili.ccaption;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class ClosedCaptionDealer {

	
	/**
	 * 下载avId的所有字幕
	 * @param avId
	 * @param savePath
	 * @param saveName
	 * @return 是否存在字幕
	 */
	static Pattern pattern = Pattern.compile("window.__INITIAL_STATE__=(.*});");
	static HttpRequestUtil util = new HttpRequestUtil();
	public boolean getCC(String avId, String savePath, String saveName) {
		saveName = saveName.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		String url = "https://www.bilibili.com/video/" + avId;
		String html = util.getContent(url, new HttpHeaders().getCommonHeaders("www.bilibili.com"));
		Matcher ma = pattern.matcher(html);
		if(ma.find()) {
			JSONObject jObj = new JSONObject(ma.group(1));
			JSONObject subtitle = jObj.getJSONObject("videoData").optJSONObject("subtitle");
			if(subtitle == null) {
				return false;
			}
			JSONArray subList = subtitle.optJSONArray("list");
			if(subList == null || subList.length() ==0) {
				return false;
			}
			Logger.println("找到字幕数： " + subList.length());
			for(int i=0; i<subList.length(); i++) {
				JSONObject sub = subList.getJSONObject(i);
				String subUrl = sub.getString("subtitle_url");
				String subLang = sub.getString("lan_doc");
				File file = new File(savePath, String.format("%s-%d-%s.srt", saveName, i, subLang));
				Logger.println(file.getAbsolutePath());
				save2srt(subUrl, file);
			}
		}
		return false;
	}
	
	public void save2srt(String url, File file){
		if(file.exists()) {
			file.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String json = util.getContent(url, new HashMap<>());
			JSONArray lines = new JSONObject(json).getJSONArray("body");
			for(int i=0; i<lines.length(); i++) {
				JSONObject obj = lines.getJSONObject(i);		
				writer.append("" + i);
				writer.newLine();
				writer.append(formatTime(obj.getDouble("from")));
				writer.append(" --> ");
				writer.append(formatTime(obj.getDouble("to")));
				writer.newLine();
				writer.append(obj.getString("content"));
				writer.newLine();
				writer.newLine();
			}
			writer.flush();
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String formatTime(double value) {
		int second = (int)value;
		double mills = value - second;
		int minute = second/60;
		int hour = minute/60;
		minute = minute%60;
		second = second%60;
		mills += second;
		return String.format("%02d:%02d:%.2f", hour, minute, mills);
	}
}
