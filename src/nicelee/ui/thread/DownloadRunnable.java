package nicelee.ui.thread;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class DownloadRunnable implements Runnable {
	String title;
	String avid;
	String cid;
	String page;
	int qn;

	public DownloadRunnable(String title, String avid, String cid, String page, int qn) {
		this.title = title;
		this.avid = avid;
		this.cid = cid;
		this.page = page;
		this.qn = qn;
	}

	final static Pattern urlFLVPattern = Pattern.compile("-([0-9]+)\\.(flv|mp4)\\?");
	final static Pattern urlM4SPattern = Pattern.compile("-300([0-9]+)\\.m4s\\?");

	@Override
	public void run() {
		System.out.println("你点击了一次下载按钮...");
		// 新建下载部件
		DownloadInfoPanel downPanel = new DownloadInfoPanel(title + "-p" + page, avid, cid, page, qn);
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			return;
		}
		INeedAV iNeedAV = new INeedAV();
		iNeedAV.setDownFormat(Global.downloadFormat);
		String url = iNeedAV.getVideoLink(avid, cid, qn); //该步含网络查询， 可能较为耗时
		String avid_qn = avid;
		String title_qn = title;
		Matcher ma = urlM4SPattern.matcher(url);
		if (ma.find()) {
			avid_qn += "-" + ma.group(1);
			title_qn += "-" + ma.group(1);
		}else {
			ma = urlFLVPattern.matcher(url);
			if (ma.find()) {
				avid_qn += "-" + ma.group(1);
				title_qn += "-" + ma.group(1);
			}
		}
		
		System.out.println(avid_qn);
		final String avid_q = avid_qn;
		final String title_q = title_qn;
		downPanel.iNeedAV = iNeedAV;
		downPanel.avid_qn = avid_qn;
		downPanel.url = url;
		// 再进行一次判断，看下载列表是否已经存在相应任务(防止并发误判)
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			return;
		}
		// 将下载任务(HttpRequestUtil + DownloadInfoPanel)添加至全局列表, 让监控进程周期获取信息并刷新
		Global.downloadTaskList.put(downPanel, iNeedAV.getUtil());
		// 根据信息初始化绘制下载部件
		JPanel jpContent = Global.downloadTab.getJpContent();
		jpContent.add(downPanel);
		jpContent.setPreferredSize(new Dimension(1100, 128 * Global.downloadTaskList.size()));
		Global.downLoadThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(iNeedAV.getUtil().getStatus() == -2) {
						System.out.println("已经人工停止,无需再下载");
						return;
					}
					// 开始下载
					iNeedAV.downloadClip(url, avid_q, page);
					// 追加重命名文件
					File f = new File(Global.savePath, "rename.bat");
					boolean isExist = f.exists();
					System.out.println(f.getAbsolutePath() + "是否存在? " + f.exists());
					FileWriter fw = new FileWriter(f, true);
					if (!isExist) {
						// .bat切为UTF-8编码, 防止中文乱码
						fw.write("@echo off\r\nchcp 65001\r\n");
					}
					if (Global.downloadFormat >= 0) {
						String cmd = String.format("rename \"%s.mp4\" \"%s.mp4\"\r\n", avid_q + "-p" + page,
								title_q.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"]", "·") + "-p" + page);
						fw.write(cmd);
						// }else {
						cmd = String.format("rename \"%s.flv\" \"%s.flv\"\r\n", avid_q + "-p" + page,
								title_q.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"]", "·") + "-p" + page);
						fw.write(cmd);
					}
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
