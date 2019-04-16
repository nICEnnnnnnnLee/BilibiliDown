package nicelee.bilibili.downloaders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpRequestUtil;

public class Downloader implements IDownloader{
	
	private List<IDownloader> downloaders = null;
	private IDownloader downloader = null;
	private HttpRequestUtil util;
	
	@Override
	public boolean matches(String url) {
		return true;
	}

	@Override
	public void init(HttpRequestUtil util) {
		downloaders = new ArrayList<>();
		this.util = util;
		try {
			for (Class<?> clazz : PackageScanLoader.validDownloaderClasses) {
				IDownloader downloader = (IDownloader) clazz.newInstance();
				downloader.init(util);
				downloaders.add(downloader);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean download(String url, String avId, int qn, int page) {
		for (IDownloader downloader : downloaders) {
			if (downloader.matches(url)) {
				this.downloader = downloader;
				downloader.init(util);
				return downloader.download(url, avId, qn, page);
			}
		}
		System.out.println("未找到匹配当前url的下载器");
		return false;
	}

	@Override
	public void startTask() {
		if(downloader !=null) {
			downloader.startTask();
		}
	}

	@Override
	public void stopTask() {
		if(downloader !=null) {
			downloader.stopTask();
		}
	}

	@Override
	public File file() {
		if(downloader !=null) {
			return downloader.file();
		}
		return null;
	}

	@Override
	public StatusEnum currentStatus() {
		if(downloader !=null) {
			return downloader.currentStatus();
		}
		return null;
	}

	@Override
	public int totalTaskCount() {
		if(downloader !=null) {
			return downloader.totalTaskCount();
		}
		return 0;
	}

	@Override
	public int currentTask() {
		if(downloader !=null) {
			return downloader.currentTask();
		}
		return 0;
	}

	@Override
	public long sumTotalFileSize() {
		if(downloader !=null) {
			return downloader.sumTotalFileSize();
		}
		return 0;
	}

	@Override
	public long sumDownloadedFileSize() {
		if(downloader !=null) {
			return downloader.sumDownloadedFileSize();
		}
		return 0;
	}

	@Override
	public long currentFileDownloadedSize() {
		if(downloader !=null) {
			return downloader.currentFileDownloadedSize();
		}
		return 0;
	}

	@Override
	public long currentFileTotalSize() {
		if(downloader !=null) {
			return downloader.currentFileTotalSize();
		}
		return 0;
	}

}
