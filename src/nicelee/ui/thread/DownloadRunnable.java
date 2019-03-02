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

	final static Pattern urlFLVPattern = Pattern.compile("-([0-9]+)\\.flv\\?");
	final static Pattern urlMP4Pattern = Pattern.compile("-300([0-9]+)\\.m4s\\?");

	@Override
	public void run() {
		System.out.println("你点击了一次下载按钮...");
		// 新建下载部件
		DownloadInfoPanel downPanel = new DownloadInfoPanel(avid, cid, page, qn);
		if (Global.downloadTaskList.get(downPanel) != null) {
			return;
		}
		try {
			INeedAV iNeedAV = new INeedAV();
			iNeedAV.setDownFormat(Global.downloadFormat);
			String url = iNeedAV.getVideoLink(avid, cid, qn);
			String avid_qn = avid;
			String title_qn = title;
			Matcher ma = null;
			if (Global.downloadFormat == Global.MP4) {
				ma = urlMP4Pattern.matcher(url);
			} else {
				ma = urlFLVPattern.matcher(url);
			}
			if (ma.find()) {
				avid_qn += "-" + ma.group(1);
				title_qn += "-" + ma.group(1);
			}
			System.out.println(avid_qn);
			// 将下载任务(HttpRequestUtil + DownloadInfoPanel)添加至全局列表, 让监控进程周期获取信息并刷新
			Global.downloadTaskList.put(downPanel, iNeedAV.getUtil());
			// 根据信息初始化绘制下载部件
			JPanel jpContent = Global.downloadTab.getJpContent();
			jpContent.add(downPanel);
			jpContent.setPreferredSize(new Dimension(1100, 120 * Global.downloadTaskList.size()));
			// 开始下载
			iNeedAV.downloadClip(url, avid_qn, page);
			// 追加重命名文件
			File f = new File(Global.savePath, "rename.bat");
			boolean isExist = f.exists();
			System.out.println(f.getAbsolutePath() + "是否存在? " + f.exists());
			FileWriter fw = new FileWriter(f, true);
			if(!isExist) {
				//.bat切为UTF-8编码, 防止中文乱码
				fw.write("@echo off\r\nchcp 65001\r\n");
			}
			if(Global.downloadFormat == 0) {
				String cmd = String.format("rename \"%s.mp4\" \"%s.mp4\"\r\n",
						avid_qn + "-p"+ page,
						title_qn.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"]", "·") + "-p"+ page);
				fw.write(cmd);
			}else {
				String cmd = String.format("rename \"%s.flv\" \"%s.flv\"\r\n",
						avid_qn + "-p"+ page,
						title_qn.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"]", "·") + "-p"+ page);
				fw.write(cmd);
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
