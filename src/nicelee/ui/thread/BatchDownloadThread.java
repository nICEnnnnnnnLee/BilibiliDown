package nicelee.ui.thread;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.batchdownload.BatchDownload;
import nicelee.bilibili.util.batchdownload.BatchDownload.BatchDownloadsBuilder;
import nicelee.ui.Global;
import nicelee.ui.item.JOptionPaneManager;

public class BatchDownloadThread extends Thread {

	String configFileName;
	String configFilePath;

	public BatchDownloadThread(String configFileName) {
		this.setName("Thread-BatchDownload");
		this.configFileName = configFileName;
		configFilePath = "config/" + configFileName;
	}

	final Pattern pagePattern = Pattern.compile("p=[0-9]+$");

	@Override
	public void run() {
		try {
			Logger.println("一键下载进行中");
			File f = ResourcesUtil.search(configFilePath);
			checkValid(f);
			List<BatchDownload> bds = new BatchDownloadsBuilder(new FileInputStream(f)).Build();
			Logger.println("一键下载进行中。。。。。");
			Logger.println(bds);
			for (BatchDownload batch : bds) {
				Logger.printf("[url:%s] 任务开始", batch.getUrl());
				INeedAV ina = new INeedAV();
				String validStr = ina.getValidID(batch.getUrl());
				Logger.println(validStr);
				Matcher m = pagePattern.matcher(validStr);
				boolean isPageable = true;
				if (!m.find())
					isPageable = false;
				else
					validStr = validStr.replaceFirst("p=[0-9]+$", "");
				int page = batch.getStartPage();
				boolean stopFlag = false;
				while (!stopFlag) {
					if(!isPageable && page >= 2)
						break;
					String sp = validStr + " p=" + page;
					VideoInfo avInfo = null;
					try {
						avInfo = ina.getVideoDetail(sp, Global.downloadFormat, false);
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
					Collection<ClipInfo> clips = avInfo.getClips().values();
					if (clips.size() == 0)
						break;
					Logger.printf("当前url: %s ,page: %d, 分页查询开始进行", batch.getUrl(), page);
					for (ClipInfo clip : clips) {
						// 判断是否要停止[url:{url}] 对应的任务
						if (batch.matchStopCondition(clip, page)) {
							// 判断边界BV是否要下载
							if (batch.isIncludeBoundsBV() && batch.matchDownloadCondition(clip, page)) {
								addTask(clip);
								DownloadRunnable downThread = new DownloadRunnable(avInfo, clip,
										VideoQualityEnum.getQN(Global.menu_qn));
								Global.queryThreadPool.execute(downThread);
							}
							stopFlag = true;
							break;
						}
						// 判断是否要下载
						if (batch.matchDownloadCondition(clip, page)) {
							addTask(clip);
							DownloadRunnable downThread = new DownloadRunnable(avInfo, clip,
									VideoQualityEnum.getQN(Global.menu_qn));
							Global.queryThreadPool.execute(downThread);
						}
					}
					Logger.printf("当前url: %s ,page: %d, 分页查询完毕", batch.getUrl(), page);
					page++;
					Thread.sleep(1500);
				}
				Thread.sleep(1000);
				Logger.printf("[url:%s] 任务完毕", batch.getUrl());
				if (batch.isAlertAfterMissionComplete()) {
					showMessageDialog(null, "url:" + batch.getUrl(), "任务完毕!! " + batch.getRemark(),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
			showMessageDialog(null, "一键下载完毕", "OK", JOptionPane.PLAIN_MESSAGE);
		} catch (BilibiliError e) {
			JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Logger.println("一键下载运行完毕");
	}

	public void checkValid(File f) throws IOException, URISyntaxException {
		if (f == null || !f.exists()) {
			String docsUrl = "https://nICEnnnnnnnLee.github.io/BilibiliDown/guide/advanced/quick-batch-download";
			String warning = "批量下载配置不存在`" + configFilePath + "`!\r\n请参考配置" + docsUrl;
			Object[] options = { "确认", "前往参考文档" };
			int m = JOptionPane.showOptionDialog(null, warning, "错误", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (m == 1) {
				if (Desktop.isDesktopSupported())
					Desktop.getDesktop().browse(new URI(docsUrl));
				else {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable trans = new StringSelection(docsUrl);
					clipboard.setContents(trans, null);
					JOptionPane.showMessageDialog(null, "相关网页链接已复制到剪贴板");
				}
			}
			throw new RuntimeException("配置文件`" + configFilePath + "`不存在");
		}
	}

	public void addTask(ClipInfo clip) {
	}
	
	public void showMessageDialog(Component parentComponent, String message, String title, int messageType)
			throws HeadlessException {
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}
}
