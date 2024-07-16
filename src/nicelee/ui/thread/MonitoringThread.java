package nicelee.ui.thread;

import java.awt.Color;
import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Audio;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class MonitoringThread extends Thread {
	
	public MonitoringThread() {
		this.setName("Thread - Monitoring Download");
		this.setDaemon(true);
	}
	public void run() {
		ConcurrentHashMap<DownloadInfoPanel, IDownloader> map = Global.downloadTaskList;
		Color lightGreen = new Color(153, 214, 92);
		Color lightRed = new Color(255, 71, 10);
		Color lightPink = new Color(255, 122, 122);
		Color lightOrange = new Color(255, 207, 61);
		if(Global.playSoundAfterMissionComplete)
			Audio.init();
		int lastActiveTaskCount = 0;
		while (true) {
			int MAX_FAIL_CNT = Global.maxFailRetry;
			//每一次while循环， 统计一次任务状态， 并在UI上更新
			int totalTask = 0, activeTask = 0, pauseTask = 0, pauseTaskCanRetry = 0,doneTask = 0, queuingTask = 0;
			for (Entry<DownloadInfoPanel, IDownloader> entry : map.entrySet()) {
				DownloadInfoPanel dp = entry.getKey();
				IDownloader downloader = entry.getValue();
				String formattedTitle = dp.formattedTitle.replace("\\", "\\\\");
				try {
					File file = downloader.file();
					if(file != null) {
						String path = file.getAbsolutePath();
						if(Global.doRenameAfterComplete && downloader.currentStatus() == StatusEnum.SUCCESS) {
							path = path.replaceFirst("(?:av|h|cv|opus|BV|season|au|edd_)[0-9a-zA-Z_]+-[0-9]+-p[0-9]+", formattedTitle);
						}
						dp.getLbFileName().setText(path);
						dp.getLbFileName().setToolTipText(path);
					}
					switch (downloader.currentStatus()) {
					case SUCCESS:
						doneTask ++;
						if(dp.getBackground() != lightGreen) {
							String fileSize = IDownloader.transToSizeStr(downloader.sumTotalFileSize());
							dp.getLbCurrentStatus().setText(genTips("%d/%d 下载完成. ", downloader));
							dp.getLbDownFile().setText("文件大小: "  + fileSize);
							dp.getBtnControl().setVisible(false);
							dp.setBackground(lightGreen);
							// TODO 将成功的任务状态记入
							BatchDownloadRbyRThread.taskSucceed(dp.getClipInfo(), formattedTitle, fileSize, "" + dp.getRealqn());
						}
						break;
					case FAIL:
						pauseTask ++;
						dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
						if(dp.getFailCnt() == MAX_FAIL_CNT) {
							if(!dp.getLbCurrentStatus().getText().endsWith("下载异常. ")) {
								dp.getLbCurrentStatus().setText(genTips("%d/%d 下载异常. ", downloader));
								dp.getBtnControl().setText("继续下载");
								dp.getBtnControl().setVisible(true);
								// TODO 将失败的任务状态记入
								BatchDownloadRbyRThread.taskFail(dp.getClipInfo(), "fail");
							}
						}else {
							pauseTaskCanRetry++;
							dp.getLbCurrentStatus().setText(String.format("下载异常. 尝试重连 %d ", dp.getFailCnt()));
							dp.setFailCnt(dp.getFailCnt() + 1);
							dp.continueTask();
						}
						dp.setBackground(lightRed);
						break;
					case STOP:
						pauseTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 人工停止. ", downloader));
						dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
						dp.getBtnControl().setText("继续下载");
						dp.getBtnControl().setVisible(true);
						if(dp.getBackground() != lightGreen && dp.getBackground() != lightPink) {
							BatchDownloadRbyRThread.taskFail(dp.getClipInfo(), "stop");
							dp.setBackground(lightPink);
						}
						break;
					case PROCESSING:
						activeTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 转码中... ", downloader));
						dp.getLbDownFile().setText("文件大小: "  + IDownloader.transToSizeStr(downloader.sumTotalFileSize()));
						dp.setBackground(lightOrange);
						dp.setBackground(null);
						Logger.println("转码中。。。");
						break;
					case NONE:
						if(dp.stopOnQueue) {
							pauseTask ++;
							dp.getLbCurrentStatus().setText(genTips("%d/%d 人工停止. ", downloader));
							dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
							dp.getBtnControl().setText("继续下载");
							dp.getBtnControl().setVisible(true);
							if(dp.getBackground() != lightGreen && dp.getBackground() != lightPink) {
								BatchDownloadRbyRThread.taskFail(dp.getClipInfo(), "stop");
								dp.setBackground(lightPink);
							}
						}else {
							queuingTask ++;
							dp.getLbCurrentStatus().setText("等待下载中..");
							dp.getLbDownFile().setText("等待下载中..");
							dp.getBtnControl().setText("暂停");
							dp.getBtnControl().setVisible(false);
							dp.setBackground(lightOrange);
						}
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
						dp.setBackground(null);
						break;
					default:
						break;
					}
				}catch(Exception e) { 
					//e.printStackTrace(); // L38 文件不存在 NullPointerException
					if(downloader.currentStatus() == StatusEnum.STOP) {
						pauseTask ++;
						dp.getLbCurrentStatus().setText("任务取消");
						dp.getLbDownFile().setText("任务取消");
						dp.getBtnControl().setText("继续下载");
						dp.getBtnControl().setVisible(true);
						if(dp.getBackground() != lightGreen && dp.getBackground() != lightPink) {
							BatchDownloadRbyRThread.taskFail(dp.getClipInfo(), "stop");
							dp.setBackground(lightPink);
						}
					}else if(downloader.currentStatus() == StatusEnum.PROCESSING) {
						activeTask ++;
						dp.getLbCurrentStatus().setText(genTips("%d/%d 转码中... ", downloader));
						dp.getLbDownFile().setText("文件大小: "  + IDownloader.transToSizeStr(downloader.sumTotalFileSize()));
						dp.setBackground(lightOrange);
						dp.setBackground(null);
						Logger.println("转码中。。。");
					}else {
						//等待队列中
						if(dp.stopOnQueue) {
							pauseTask ++;
							dp.getLbCurrentStatus().setText(genTips("%d/%d 人工停止. ", downloader));
							dp.getLbDownFile().setText(genSizeCntStr("文件%d进度： %s/%s", downloader));
							dp.getBtnControl().setText("继续下载");
							dp.getBtnControl().setVisible(true);
							if(dp.getBackground() != lightGreen && dp.getBackground() != lightPink) {
								BatchDownloadRbyRThread.taskFail(dp.getClipInfo(), "stop");
								dp.setBackground(lightPink);
							}
						}else {
							queuingTask ++;
							dp.getLbCurrentStatus().setText("等待下载中..");
							dp.getLbDownFile().setText("等待下载中..");
							dp.getBtnControl().setText("暂停");
							dp.getBtnControl().setVisible(false);
							dp.setBackground(lightOrange);
						}
					}
				}
			}
			totalTask = map.size();
			//System.out.println("当前map总任务数： " + totalTask);
			//totalTask = activeTask + pauseTask + doneTask + queuingTask;
			//System.out.println("当前计算总任务数： " + totalTask);
			Global.downloadTab.refreshStatus(totalTask, activeTask + pauseTaskCanRetry, pauseTask - pauseTaskCanRetry, doneTask, queuingTask);
			//Global.activeTask = activeTask;
			//Logger.printf("lastActiveTaskCount: %d, activeTask: %d\n", lastActiveTaskCount, activeTask);
			if(Global.playSoundAfterMissionComplete && lastActiveTaskCount > 0 && activeTask == 0 && pauseTaskCanRetry == 0)
				Audio.play();
			lastActiveTaskCount = activeTask;
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
