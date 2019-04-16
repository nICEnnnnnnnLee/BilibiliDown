package nicelee.ui.thread;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import nicelee.bilibili.downloaders.IDownloader;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class MonitoringThread extends Thread {
	
	public MonitoringThread() {
		this.setName("Thread - Monitoring Download");
	}
	public void run() {
		ConcurrentHashMap<DownloadInfoPanel, IDownloader> map = Global.downloadTaskList;
		while (true) {
			//每一次while循环， 统计一次任务状态， 并在UI上更新
			int totalTask = 0, activeTask = 0, pauseTask = 0, doneTask = 0, queuingTask = 0;
			for (Entry<DownloadInfoPanel, IDownloader> entry : map.entrySet()) {
				DownloadInfoPanel dp = entry.getKey();
				IDownloader downloader = entry.getValue();
				try {
					String path = downloader.file().getAbsolutePath();
					if(Global.doRenameAfterComplete) {
						path = path.replaceFirst("av[0-9]+-[0-9]+-p[0-9]+", dp.formattedTitle);
					}
					dp.getLbFileName().setText(path);
					switch (downloader.currentStatus()) {
					case SUCCESS:
						doneTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 下载完成. ", downloader));
						dp.getLbDownFile().setText("文件大小: "  + IDownloader.transToSizeStr(downloader.sumTotalFileSize()));
						dp.getBtnControl().setVisible(false);
						break;
					case FAIL:
						pauseTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 下载异常. ", downloader));
						dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
						dp.getBtnControl().setText("继续下载");
						dp.getBtnControl().setVisible(true);
						break;
					case STOP:
						pauseTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 人工停止. ", downloader));
						dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
						dp.getBtnControl().setText("继续下载");
						dp.getBtnControl().setVisible(true);
						break;
					case PROCESSING:
						doneTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 转码中... ", downloader));
						dp.getLbDownFile().setText("文件大小: "  + IDownloader.transToSizeStr(downloader.sumTotalFileSize()));
						dp.getBtnControl().setVisible(false);
						break;
					case NONE:
						queuingTask ++;
						dp.getLbCurrentStatus().setText("等待下载中..");
						dp.getLbDownFile().setText("等待下载中..");
						dp.getBtnControl().setText("暂停");
						dp.getBtnControl().setVisible(true);
						break;
					case DOWNLOADING:
						activeTask++;
						//计算下载速度
						long currrentTime = System.currentTimeMillis();
						int period = (int) (currrentTime - dp.getLastCntTime()) ; //ms
						int downSize = (int) (downloader.sumDownloadedFileSize() - dp.getLastCnt());//byte
						int speedKBPerSec = downSize / period;
						dp.setLastCnt(downloader.sumDownloadedFileSize());
						dp.setLastCntTime(currrentTime);
						String txt = String.format("%d/%d 正在下载中... %d kB/s",
								downloader.currentTask(),
								downloader.totalTaskCount(),
								speedKBPerSec);
						
						dp.getLbCurrentStatus().setText(txt);
						dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
						dp.getBtnControl().setText("暂停");
						dp.getBtnControl().setVisible(true);
						break;
					default:
						break;
					}
				}catch(Exception e) { //等待队列中
					queuingTask ++;
					dp.getLbCurrentStatus().setText("等待下载中..");
					dp.getLbDownFile().setText("等待下载中..");
					dp.getBtnControl().setText("暂停");
					dp.getBtnControl().setVisible(true);
				}
			}
			totalTask = map.size();
			//System.out.println("当前map总任务数： " + totalTask);
			//totalTask = activeTask + pauseTask + doneTask + queuingTask;
			//System.out.println("当前计算总任务数： " + totalTask);
			String info = String.format(" 总计: %d / 下载中 : %d / 暂停 : %d / 下载完 : %d / 队列中 : %d", 
					totalTask, activeTask, pauseTask, doneTask, queuingTask);
			Global.downloadTab.refreshStatus(info);
			//Global.activeTask = activeTask;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @param format
	 * @param downloader
	 * @return
	 */
	String genTips(String format, IDownloader downloader) {
		String tips;
		tips = String.format(format,
				downloader.currentTask(),
				downloader.totalTaskCount());
		return tips;
	}
	
	/**
	 * 
	 * @param format
	 * @param downloader
	 * @return
	 */
	String genSizeCntStr(String format, IDownloader downloader) {
		// 文件1进度： 32MB/43MB
		String tips = String.format(format,
				downloader.currentTask(),
				IDownloader.transToSizeStr(downloader.currentFileDownloadedSize()),
				IDownloader.transToSizeStr(downloader.currentFileTotalSize()));
		return tips;
	}
	
}
