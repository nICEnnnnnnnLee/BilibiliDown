package nicelee.bilibili.downloaders;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class Downloader implements IDownloader {

	private List<IDownloader> downloaders = null;
	private IDownloader downloader = null;
	private HttpRequestUtil util;
	private StatusEnum status;

	@Override
	public boolean matches(String url) {
		return true;
	}

	@Override
	public void init(HttpRequestUtil util) {
		downloaders = new ArrayList<>();
		status = StatusEnum.NONE;
		this.util = util;

		try {
			for (Class<?> clazz : PackageScanLoader.validDownloaderClasses) {
				IDownloader downloader = (IDownloader) clazz.newInstance();
				downloader.init(util);
				downloaders.add(downloader);
			}
			// 按权重排序,越大越优先
			Collections.sort(downloaders, new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					int bili1 = o1 == null? 0 : o1.getClass().getAnnotation(Bilibili.class).weight();
					int bili2 = o2 == null? 0 : o2.getClass().getAnnotation(Bilibili.class).weight();
					return bili2 - bili1;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean download(String url, String avId, int qn, int page) {
		for (IDownloader downloader : downloaders) {
			if (downloader.matches(url)) {
				this.downloader = downloader;
				status = StatusEnum.DOWNLOADING;
				downloader.init(util);
				return downloader.download(url, avId, qn, page);
			}
		}
		System.out.print("未找到匹配当前url的下载器:");
		System.out.println(url);
		status = StatusEnum.FAIL;
		return false;
	}

	@Override
	public void startTask() {
		if (downloader != null) {
			status = StatusEnum.DOWNLOADING;
			downloader.startTask();
		} else {
			Logger.println(StatusEnum.NONE.toString());
			status = StatusEnum.NONE;
		}
	}

	@Override
	public void stopTask() {
		if (downloader != null) {
			downloader.stopTask();
		}
		status = StatusEnum.STOP;
	}

	@Override
	public File file() {
		if (downloader != null) {
			return downloader.file();
		}
		return null;
	}

	@Override
	public StatusEnum currentStatus() {
		// 如果有downloader， 以downloader为准
		if (downloader != null) {
			return downloader.currentStatus();
		} else {
			return status;
		}
	}

	@Override
	public int totalTaskCount() {
		if (downloader != null) {
			return downloader.totalTaskCount();
		}
		return 0;
	}

	@Override
	public int currentTask() {
		if (downloader != null) {
			return downloader.currentTask();
		}
		return 0;
	}

	@Override
	public long sumTotalFileSize() {
		if (downloader != null) {
			return downloader.sumTotalFileSize();
		}
		return 0;
	}

	@Override
	public long sumDownloadedFileSize() {
		if (downloader != null) {
			return downloader.sumDownloadedFileSize();
		}
		return 0;
	}

	@Override
	public long currentFileDownloadedSize() {
		if (downloader != null) {
			return downloader.currentFileDownloadedSize();
		}
		return 0;
	}

	@Override
	public long currentFileTotalSize() {
		if (downloader != null) {
			return downloader.currentFileTotalSize();
		}
		return 0;
	}

}
