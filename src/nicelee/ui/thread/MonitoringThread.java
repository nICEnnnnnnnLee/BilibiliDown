package nicelee.ui.thread;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;
import nicelee.util.HttpRequestUtil;

public class MonitoringThread extends Thread {
	
	public MonitoringThread() {
		this.setName("Thread - Monitoring Download");
	}
	public void run() {
		ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil> map = Global.downloadTaskList;
		while (true) {
			//每一次while循环， 统计一次任务状态， 并在UI上更新
			int totalTask = 0, activeTask = 0, pauseTask = 0, doneTask = 0, queuingTask = 0;
			for (Entry<DownloadInfoPanel, HttpRequestUtil> entry : map.entrySet()) {
				DownloadInfoPanel dp = entry.getKey();
				HttpRequestUtil httpUtil = entry.getValue();
				try {
					dp.getBtnControl().setVisible(true);
					dp.getLbFileName().setText(httpUtil.getFileDownload().getAbsolutePath()
							.replaceFirst("_(video|audio)\\.m4s$", ".mp4")
							.replaceFirst("-part[0-9]+\\.flv$", ".flv"));
					String fileSize = getFileSize(httpUtil.getTotal());
					
					if (httpUtil.getStatus() == 1) {//下载已完成(包括转码中)
						doneTask ++;
						String tip = httpUtil.isConverting()? "正在转码中...":"转码已完成";
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("下载完成. " + tip);
							dp.getLbDownFile().setText("文件大小: "  + fileSize);
						}else {
							String txt = String.format("%d/%d 下载完成. " + tip,
									httpUtil.getCurrentTask(),
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
							dp.getLbDownFile().setText(String.format("文件%d大小: %s",
									httpUtil.getCurrentTask(),
									fileSize));
						}
						dp.getBtnControl().setEnabled(false);
						//map.remove(dp);
					} else if (httpUtil.getStatus() == 0) { //仍在下载
						activeTask++;
						//计算下载速度
						long currrentTime = System.currentTimeMillis();
						int period = (int) (currrentTime - dp.getLastCntTime()) ; //ms
						int downSize = (int) (httpUtil.getCnt() - dp.getLastCnt());//byte
						int speedKBPerSec = downSize / period;
						dp.setLastCnt(httpUtil.getCnt());
						dp.setLastCntTime(currrentTime);
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("正在下载中... " + speedKBPerSec + " kB/s");//k=1000,K=1024
						}else {
							String txt = String.format("%d/%d 正在下载中... %d kB/s",
									httpUtil.getCurrentTask(),
									httpUtil.getTotalTask(),
									speedKBPerSec);
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getLbDownFile().setText(
								String.format("当前已下载 %d %%: %s/%s", 
										 httpUtil.getCnt()*100/httpUtil.getTotal(),
										 getFileSize(httpUtil.getCnt()).replaceAll("[^0-9\\.]", ""),
										 fileSize));
						dp.getBtnControl().setText("暂停");
					} else if (httpUtil.getStatus() == -1) {// 下载异常
						pauseTask ++;
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("下载异常");
						}else {
							String txt = String.format("%d/%d 下载异常",
									httpUtil.getCurrentTask(),
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getBtnControl().setText("继续下载");
					}else if (httpUtil.getStatus() == -2) {// 下载暂停
						pauseTask ++;
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("人工停止");
						}else {
							String txt = String.format("%d/%d 人工停止",
									httpUtil.getCurrentTask(),
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getBtnControl().setText("继续下载");
					}else if (httpUtil.getStatus() == -3) {// 已经提交Task，但由于线程并发过多原因尚未进行下载
						queuingTask ++;
						dp.getLbFileName().setText(dp.getAvid() + "-" + dp.getQn());
						dp.getLbDownFile().setText("等待下载中..");
						dp.getBtnControl().setVisible(false);
					}
				}catch(Exception e) { //等待队列中
					queuingTask ++;
					//e.printStackTrace();
					dp.getLbFileName().setText(dp.getAvid() + "-" + dp.getQn());
					dp.getLbDownFile().setText("等待下载中..");
					dp.getBtnControl().setVisible(false);
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
	
	final long KB = 1024L;
	final long MB = KB * 1024L;
	final long GB = MB * 1024L;
	String getFileSize(long size) {
		double dSize;
		if(size >= GB) {
			dSize = size * 1.0 / GB;
			return String.format("%.2f GB", dSize);
		}else if(size >= MB) {
			dSize = size * 1.0 / MB;
			return String.format("%.2f MB", dSize);
		}else if(size >= KB) {
			dSize = size * 1.0 / KB;
			return String.format("%.2f KB", dSize);
		}
		return size + " Byte";
	}
}
