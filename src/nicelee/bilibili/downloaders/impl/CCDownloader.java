package nicelee.bilibili.downloaders.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.ui.Global;

@Bilibili(name = "ClosedCaption-downloader", type = "downloader", note = "字幕下载")
public class CCDownloader implements IDownloader {

	protected HttpRequestUtil util;
	protected File file = null;
	protected StatusEnum status = StatusEnum.NONE;
	long sumTotalFileSize = 0;
	
	@Override
	public boolean matches(String url) {
		if (url.contains("subtitle") || url.endsWith(".json")) {
			return true;
		}
		return false;
	}

	/**
	 * 下载弹幕
	 * 
	 * @param url
	 * @param avId
	 * @param qn
	 * @param page
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		status = StatusEnum.DOWNLOADING;
		String dstName = String.format("%s-%d-p%d.srt", avId, qn, page);
		if(file == null) {
			file = new File(Global.savePath, dstName);
		}
		boolean result = save2srt(url, file);
		if (result) {
			status = StatusEnum.SUCCESS;
			sumTotalFileSize = file.length();
			return true;
		}else {
			status = StatusEnum.FAIL;
			return false;
		}
	}

	@Override
	public void init(HttpRequestUtil util) {
		this.util = util;
	}

	@Override
	public void startTask() {
	}

	@Override
	public void stopTask() {
	}

	@Override
	public File file() {
		return file;
	}

	@Override
	public StatusEnum currentStatus() {
		return status;
	}

	@Override
	public int totalTaskCount() {
		return 1;
	}

	@Override
	public int currentTask() {
		return 1;
	}

	@Override
	public long sumTotalFileSize() {
		return sumTotalFileSize;
	}

	@Override
	public long sumDownloadedFileSize() {
		return sumTotalFileSize;
	}

	@Override
	public long currentFileDownloadedSize() {
		if(file != null &&file.exists()) {
			return file.length();
		}
		return 0;
	}

	@Override
	public long currentFileTotalSize() {
		if(file != null &&file.exists()) {
			return file.length();
		}
		return 0;
	}

	public boolean save2srt(String url, File file){
//		if(file.exists()) {
//			file.delete();
//		}
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
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
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
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
