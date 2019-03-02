package nicelee.ui.thread;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;
import nicelee.util.HttpRequestUtil;

public class MonitoringThread extends Thread {
	
	public MonitoringThread() {
		this.setName("Threa - Monitoring Download");
	}
	public void run() {
		ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil> map = Global.downloadTaskList;

		while (true) {
			
			for (Entry<DownloadInfoPanel, HttpRequestUtil> entry : map.entrySet()) {
				DownloadInfoPanel dp = entry.getKey();
				HttpRequestUtil httpUtil = entry.getValue();
				try {
					dp.getLbFileName().setText(httpUtil.getFileDownload().getAbsolutePath().replaceFirst("_(video|audio)\\.m4s$", ".mp4"));
					if (httpUtil.getStatus() == 1) {
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("下载完成. ");
						}else {
							String txt = String.format("%d/%d 下载完成.",
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask());
							dp.getLbCurrentStatus().setText(txt);
						}
						dp.getLbDownFile().setText("文件大小: "  + httpUtil.getTotal()/1024/1024 + " MB");
						dp.getBtnControl().setEnabled(false);
					} else if (httpUtil.getStatus() == 0) {
						if(httpUtil.getTotalTask() == 1) {
							dp.getLbCurrentStatus().setText("正在下载中...");
						}else {
							String txt = String.format("%d/%d 正在下载中...",
									httpUtil.getNextTask() != null ? 1 : 2,
									httpUtil.getTotalTask());
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
					//e.printStackTrace();
				}
				
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
