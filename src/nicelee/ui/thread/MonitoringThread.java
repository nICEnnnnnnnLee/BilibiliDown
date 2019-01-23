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
					dp.getLbFileName().setText(httpUtil.getFileDownload().getAbsolutePath());
					if (httpUtil.getStatus() == 1) {
						dp.getLbCurrentStatus().setText("下载完成. ");
						dp.getLbDownFile().setText("文件大小: "  + httpUtil.getTotal()/1024/1024 + " MB");
					} else if (httpUtil.getStatus() == 0) {
						dp.getLbCurrentStatus().setText("正在下载中...");
						dp.getLbDownFile().setText("当前已下载  "+ httpUtil.getCnt()*100/httpUtil.getTotal() + " % :" + httpUtil.getCnt() + "/" + httpUtil.getTotal());
					} else if (httpUtil.getStatus() == -1) {
						dp.getLbCurrentStatus().setText("下载异常");
					}else if (httpUtil.getStatus() == -2) {
						dp.getLbCurrentStatus().setText("人工停止");
					}
				}catch(Exception e) {
					e.printStackTrace();
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
