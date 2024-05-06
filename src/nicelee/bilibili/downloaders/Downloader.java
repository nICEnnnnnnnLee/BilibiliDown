package nicelee.bilibili.downloaders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class Downloader implements IDownloader {

	private static List<IDownloader> downloaders = null;
	private IDownloader downloader = null;
	private HttpRequestUtil util;
	private StatusEnum status;

	@Override
	public boolean matches(String url) {
		return true;
	}

	@Override
	public void init(HttpRequestUtil util) {
		status = StatusEnum.NONE;
		this.util = util;
		
		if(downloaders == null) {
			synchronized (Downloader.class) {
				if(downloaders == null) {
					downloaders = new ArrayList<>();
					try {
						for (Class<?> clazz : PackageScanLoader.validDownloaderClasses) {
							IDownloader downloader = (IDownloader) clazz.newInstance();
							downloaders.add(downloader);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean download(String url, String avId, int qn, int page) {
		if(downloader == null) {
			for (IDownloader downloader : downloaders) {
				if (downloader.matches(url)) {
					try {
						this.downloader = downloader.getClass().newInstance();
						this.downloader.matches(url);
						break;
					} catch (InstantiationException | IllegalAccessException e) {
					}
				}
			}
		}
		if(downloader != null) {
			status = StatusEnum.DOWNLOADING;
			downloader.init(util);
			return downloader.download(url, avId, qn, page);
		} else {
			System.out.print("未找到匹配当前url的下载器:");
			System.out.println(url);
			status = StatusEnum.FAIL;
			return false;
		}
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
