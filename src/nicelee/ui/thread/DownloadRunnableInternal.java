package nicelee.ui.thread;

import nicelee.bilibili.API;
import nicelee.bilibili.INeedAV;
import nicelee.bilibili.downloaders.Downloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.parsers.InputParser;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;
import nicelee.ui.item.JOptionPaneManager;

public class DownloadRunnableInternal implements Runnable {

	final DownloadInfoPanel downPanel;
	final long urlTimestamp;
	final boolean invokeByContinueTask;
	final int failCnt;
	// 下面这些值都可以从 downPanel 直接或者间接获取
	final String record;
	final INeedAV iNeedAV;
	final Downloader downloader;
	final String urlQuery, avid, cid, avid_qn, formattedTitle;
	final int qn, realQN, page;

	/**
	 * <p>一个有效的下载任务和下载面板的控件downPanel绑定。</p>
	 * <p>为了防止该任务在线程池的队列里面等太久，需要urlTimestamp来标定url的有效性，以判断是否重新查询url</p>
	 * 
	 * @param downPanel					下载任务绑定的UI控件，相关信息可以从这里获取
	 * @param urlTimestamp				当前下载链接的获取时间
	 * @param invokeByContinueTask		该线程是否是 “继续任务”， 而不是 “开始任务”
	 * @param failCnt					在此之前的任务失败次数
	 */
	public DownloadRunnableInternal(DownloadInfoPanel downPanel, long urlTimestamp, boolean invokeByContinueTask, int failCnt) {
		this.downPanel = downPanel;
		this.urlTimestamp = urlTimestamp;
		this.invokeByContinueTask = invokeByContinueTask;
		this.failCnt = failCnt;
		iNeedAV = downPanel.iNeedAV;
		downloader = iNeedAV.getDownloader();
		urlQuery = downPanel.url;
		avid = downPanel.getAvid();
		cid = downPanel.getCid();
		avid_qn = downPanel.avid_qn;
		formattedTitle = downPanel.formattedTitle;
		qn = downPanel.getQn();
		realQN = downPanel.getRealqn();
		page = downPanel.getClipInfo().getPage();
		record = avid_qn + "-p" + page;
	}

	@Override
	public void run() {
		try {
			if (downloader.currentStatus() == StatusEnum.NONE && downPanel.stopOnQueue) {
				Logger.println("已经删除等待队列,无需再下载");
				return;
			}
			if (downloader.currentStatus() == StatusEnum.STOP) {
				Logger.println("已经人工停止,无需再下载");
				return;
			}
			// 判断是不是需要重新获取url
			String validUrl = urlQuery;
			if (!ResourcesUtil.isPicture(avid)) { // 不是图片类型(该类型没啥好重新获取的)
				boolean shouldReQuery = false;
				// 如果是重新尝试下载，是否需要重新获取链接
				if (invokeByContinueTask && Global.reloadDownloadUrl) {
					Logger.printf("重试时，重新查询下载链接");
					shouldReQuery = true;
				} else {
					// 在线程池里等待的时间是否超过了urlValidPeriod
					long currentTime = System.currentTimeMillis();
					long deltaTime = currentTime - urlTimestamp;
					if (deltaTime > Global.urlValidPeriod) {
						Logger.printf("下载url距离上次查询已经过了超过%d min，重新查询下载链接", Global.urlValidPeriod / 60000L);
						shouldReQuery = true;
					}
				}
				if (shouldReQuery) {// 重新查询url
					InputParser parser = iNeedAV.getInputParser(avid);
					validUrl = parser.getVideoLink(avid, cid, qn, Global.downloadFormat);
					downPanel.url = validUrl;
					if (realQN != parser.getVideoLinkQN()) {
						Logger.println("清晰度链接已经改变，无法再重新下载");
						iNeedAV.getUtil().stopDownloadAsFail();
						return;
					}
				}
			}
			// 开始下载
			if (iNeedAV.downloadClip(validUrl, avid, iNeedAV.getInputParser(avid).getVideoLinkQN(), page)) {
				// 下载成功后保存到仓库
				if (Global.saveToRepo) {
					RepoUtil.appendAndSave(record);
				}
				if (Global.thumbUpAfterDownloaded && Global.isLogin && avid.startsWith("BV")) {
					API.like(avid);
				}
				CmdUtil.convertOrAppendCmdToRenameBat(avid_qn, formattedTitle, page);
			}
		} catch (BilibiliError e) {
			JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Global.sleepAfterDownloadComplete > 0) {
			try {
				Thread.sleep(Global.sleepAfterDownloadComplete);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
