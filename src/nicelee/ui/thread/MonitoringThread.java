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
		int activeTask;
		while (true) {
			activeTask = 0;
			for (Entry<DownloadInfoPanel, HttpRequestUtil> entry : map.entrySet()) {
				DownloadInfoPanel dp = entry.getKey();
				HttpRequestUtil httpUtil = entry.getValue();
				try {
					dp.getLbFileName().setText(httpUtil.getFileDownload().getAbsolutePath().replaceFirst("_(video|audio)\\.m4s$", ".mp4"));
					if (httpUtil.getStatus() == 1) {
						String tip = httpUtil.isConverting()? "正在转码中...":"转码已完成";
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("下载完成. " + tip);
							dp.getLbDownFile().setText("文件大小: "  + httpUtil.getTotal()/1024/1024 + " MB");
						}else {
							String txt = String.format("%d/%d 下载完成. " + tip,
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
							dp.getLbDownFile().setText(String.format("文件%d大小: %d MB",
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask()));
						}
						dp.getBtnControl().setEnabled(false);
						//map.remove(dp);
					} else if (httpUtil.getStatus() == 0) {
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
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask(),
									speedKBPerSec);
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getLbDownFile().setText("当前已下载  "+ httpUtil.getCnt()*100/httpUtil.getTotal() + " % :" + httpUtil.getCnt() + "/" + httpUtil.getTotal());
						dp.getBtnControl().setText("暂停");
					} else if (httpUtil.getStatus() == -1) {
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("下载异常");
						}else {
							String txt = String.format("%d/%d 下载异常",
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getBtnControl().setText("继续下载");
					}else if (httpUtil.getStatus() == -2) {
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("人工停止");
						}else {
							String txt = String.format("%d/%d 人工停止",
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getBtnControl().setText("继续下载");
					}
				}catch(Exception e) {
					dp.getLbFileName().setText(dp.getAvid() + "-" + dp.getQn());
					dp.getBtnControl().setText("等待下载中..");
				}
				
			}
			
			Global.activeTask = activeTask;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
