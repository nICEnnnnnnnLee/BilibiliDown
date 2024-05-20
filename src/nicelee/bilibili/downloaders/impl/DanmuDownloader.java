package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.danmuku.Danmuku;
import nicelee.bilibili.util.danmuku.Xml2Ass;
import nicelee.ui.Global;

@Bilibili(name = "danmuku-downloader", type = "downloader", note = "弹幕下载")
public class DanmuDownloader implements IDownloader {

	protected HttpRequestUtil util;
	protected File file = null;
	protected StatusEnum status = StatusEnum.NONE;

	// http://comment.bilibili.com/85464878.xml
	// https://api.bilibili.com/x/v1/dm/list.so?oid=85464878
	@Override
	public boolean matches(String url) {
		if (url.contains("dm/list.so") || url.contains(".xml")) {
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
		String result = util.getContent(url, new HttpHeaders().getDanmuHeaders());
		String dstName = String.format("%s-%d-p%d", avId, qn, page);
		File xmlfile = new File(Global.savePath, dstName + ".xml");
		if(file == null) {
			file = new File(Global.savePath, dstName + ".ass");
		}
		if ("".equals(result)) {
			status = StatusEnum.FAIL;
			return false;
		}
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlfile),Charset.forName("UTF-8"));
			out.write(result);
			status = StatusEnum.SUCCESS;
			//Logger.println(result);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			status = StatusEnum.FAIL;
			return false;
		} finally {
			ResourcesUtil.closeQuietly(out);
			Xml2Ass xml2AssTool = new Xml2Ass();
			try {
				// 从xml读取弹幕
				List<Danmuku> danmuList = xml2AssTool.readXml(xmlfile);
				// 写入ass文件
				xml2AssTool.writeAss(danmuList, file);
				xmlfile.delete();
			}catch (Exception e) {
				e.printStackTrace();
			}
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
		if(file != null &&file.exists()) {
			return file.length();
		}
		return 0;
	}

	@Override
	public long sumDownloadedFileSize() {
		if(file != null &&file.exists()) {
			return file.length();
		}
		return 0;
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

}
