package nicelee.ui.thread;

import java.awt.Dimension;

import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.RepoUtil;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;
import nicelee.ui.TabDownload;
import nicelee.ui.item.DownloadInfoPanel;
import nicelee.ui.item.JOptionPaneManager;

public class DownloadRunnable implements Runnable {
	
	VideoInfo avInfo;
	ClipInfo clip;
	String displayName;
	String avid;
	String cid;
	int page;
	int remark;
	String record;
	int qn; //想要申请的链接视频质量

	final static String MSG_VIDEO_DOWNLOADED = "您已经下载过视频 %s\n如果想继续下载:\n"
			+ "临时方案: 右上角[配置] -> [下载前先查询记录?] -> [不查询]\n"
			+ "持久化方案: 在配置页搜索并修改配置 bilibili.repo";
//	public DownloadRunnable(ClipInfo clip, int qn) {
//		this.displayName = clip.getAvTitle() + "p" + clip.getRemark() + "-" +clip.getTitle();
//		this.clip = clip;
//		this.avid = clip.getAvId();
//		this.cid = String.valueOf(clip.getcId());
//		this.page = clip.getPage();
//		this.remark = clip.getRemark();
//		this.qn = qn;
//		this.record = avid + "-" + qn  + "-p" + page;
//	}
	public DownloadRunnable(VideoInfo avInfo, ClipInfo clip, int qn) {
		this.avInfo = avInfo;
		this.displayName = clip.getAvTitle() + "p" + clip.getRemark() + "-" +clip.getTitle();
		this.clip = clip;
		this.avid = clip.getAvId();
		this.cid = String.valueOf(clip.getcId());
		this.page = clip.getPage();
		this.remark = clip.getRemark();
		this.qn = qn;
		this.record = avid + "-" + qn  + "-p" + page;
	}

	@Override
	public void run() {
		try {
			download();
		} catch (BilibiliError e) {
			JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
			BatchDownloadRbyRThread.taskFail(clip, ResourcesUtil.detailsOfException(e));
		} catch (Exception e) {
			BatchDownloadRbyRThread.taskFail(clip, ResourcesUtil.detailsOfException(e));
		}
	}

	public void download() {
		System.out.println("你点击了一次下载按钮...");
		// 如果点击了全部暂停按钮，而此时在队列中
		if(TabDownload.isStopAll()) {
			System.out.println("你点击了一次暂停按钮...");
			BatchDownloadRbyRThread.taskFail(clip, "stop manualy");
			return;
		}
		//判断是否已经下载过
		if(Global.useRepo && RepoUtil.isInRepo(record)) {
			JOptionPaneManager.showMsgWithNewThread("提示", String.format(MSG_VIDEO_DOWNLOADED, record));
			System.out.println("已经下载过 " + record);
			BatchDownloadRbyRThread.taskFail(clip, "already downloaded");
			return;
		}
		// 新建下载部件
		DownloadInfoPanel downPanel = new DownloadInfoPanel(clip, qn);
		// 判断是否在下载任务中
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			BatchDownloadRbyRThread.taskFail(clip, "already in download panel");
			return;
		}
		// 查询下载链接
		INeedAV iNeedAV = new INeedAV();
		String urlQuery;
		int realQN;
		if(!ResourcesUtil.isPicture(avid)){
			urlQuery = iNeedAV.getInputParser(avid).getVideoLink(avid, cid, qn, Global.downloadFormat); //该步含网络查询， 可能较为耗时
			realQN = iNeedAV.getInputParser(avid).getVideoLinkQN();
		}else {
			urlQuery = clip.getLinks().get(0);
			realQN = 0;
		}
		// 生成格式化名称
		String formattedTitle = CmdUtil.genFormatedName(avInfo,clip,realQN);
		String avid_qn = avid + "-" + realQN;
		this.record = avid_qn  + "-p" + page;
		//如果清晰度不符合预期，再判断一次记录
		//判断是否已经下载过
		if (qn != realQN && Global.useRepo && RepoUtil.isInRepo(record)) {
			JOptionPaneManager.showMsgWithNewThread("提示", String.format(MSG_VIDEO_DOWNLOADED, record));
			System.out.println("已经下载过 " + record);
			BatchDownloadRbyRThread.taskFail(clip, "already downloaded2");
			return;
		}
		//获取实际清晰度后，初始化下载部件参数
		downPanel.initDownloadParams(iNeedAV, urlQuery, avid_qn, formattedTitle, realQN);
		// 再进行一次判断，看下载列表是否已经存在相应任务(防止并发误判)
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			BatchDownloadRbyRThread.taskFail(clip, "already in download panel2");
			return;
		}
		// 将下载任务(HttpRequestUtil + DownloadInfoPanel)添加至全局列表, 让监控进程周期获取信息并刷新
		Global.downloadTaskList.put(downPanel, iNeedAV.getDownloader());
		BatchDownloadRbyRThread.taskFail(clip, "just put in download panel");
		// 根据信息初始化绘制下载部件
		JPanel jpContent = Global.downloadTab.getJpContent();
		jpContent.add(downPanel);
		jpContent.setPreferredSize(new Dimension(1100, 128 * Global.downloadTaskList.size()));
		if(!Global.downLoadThreadPool.isShutdown()){
			Global.downLoadThreadPool.execute(new DownloadRunnableInternal(downPanel, System.currentTimeMillis(), false, 0));
		}
		if(Global.sleepAfterDownloadQuery > 0) {
			try {
				Thread.sleep(Global.sleepAfterDownloadQuery);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
