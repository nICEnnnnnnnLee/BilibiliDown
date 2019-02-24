package nicelee.ui.thread;

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class DownloadRunnable implements Runnable{
	String avid;
	String cid;
	String page;
	int qn;
	
	public DownloadRunnable(String avid, String cid, String page, int qn) {
		this.avid = avid;
		this.cid = cid;
		this.page = page;
		this.qn = qn;
	}
	
	final static Pattern urlPattern = Pattern.compile("-([0-9]+)\\.flv\\?");
	@Override
	public void run() {
		System.out.println("你点击了一次下载按钮...");
		INeedAV iNeedAV = new INeedAV();
		try {
			String url = iNeedAV.getVideoFLVLink(avid, cid, qn);
			String avid_qn = avid;
			Matcher ma = urlPattern.matcher(url);
			if(ma.find()) {
				avid_qn += "-" + ma.group(1);
			}
			System.out.println(avid_qn);
			//新建下载部件
			DownloadInfoPanel downPanel = new DownloadInfoPanel();
			//将下载任务(HttpRequestUtil + DownloadInfoPanel)添加至全局列表, 让监控进程周期获取信息并刷新
			Global.downloadTaskList.put(downPanel, iNeedAV.getUtil());
			//根据信息初始化绘制下载部件
			JPanel jpContent = Global.downloadTab.getJpContent();
			jpContent.add(downPanel);
			jpContent.setPreferredSize(new Dimension(1100, 120 * Global.downloadTaskList.size()));
			//开始下载
			iNeedAV.downloadClip(url, avid_qn, page);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
