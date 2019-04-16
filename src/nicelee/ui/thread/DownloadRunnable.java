package nicelee.ui.thread;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.RepoUtil;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class DownloadRunnable implements Runnable {
	
	ClipInfo clip;
	String displayName;
	String avid;
	String cid;
	int page;
	int remark;
	String record;
	int qn; //想要申请的链接视频质量

	public DownloadRunnable(ClipInfo clip, int qn) {
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
		System.out.println("你点击了一次下载按钮...");
		//判断是否已经下载过
		if(Global.useRepo && RepoUtil.isInRepo(record)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "您已经下载过视频" + record, "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}).start();
			System.out.println("已经下载过 " + record);
			return;
		}
		// 新建下载部件
		DownloadInfoPanel downPanel = new DownloadInfoPanel(clip, qn);
		// 判断是否在下载任务中
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			return;
		}
		// 查询下载链接
		INeedAV iNeedAV = new INeedAV();
		String url = iNeedAV.getInputParser(avid).getVideoLink(avid, cid, qn, Global.downloadFormat); //该步含网络查询， 可能较为耗时
		int realQN = iNeedAV.getInputParser(avid).getVideoLinkQN();
		// 生成格式化名称
		String formattedTitle = CmdUtil.genFormatedName(
				avid, 
				"p" + page, 
				"pn" + remark, 
				realQN, 
				clip.getAvTitle(), 
				clip.getTitle());
		String avid_qn = avid + "-" + realQN;
		this.record = avid_qn  + "-p" + page;
		//如果清晰度不符合预期，再判断一次记录
		//判断是否已经下载过
		if (qn != realQN && Global.useRepo && RepoUtil.isInRepo(record)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "您已经下载过视频" + record, "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}).start();
			System.out.println("已经下载过 " + record);
			return;
		}
		//获取实际清晰度后，初始化下载部件参数
		downPanel.initDownloadParams(iNeedAV, url, avid_qn, formattedTitle);
		// 再进行一次判断，看下载列表是否已经存在相应任务(防止并发误判)
		if (Global.downloadTaskList.get(downPanel) != null) {
			System.out.println("已经存在相关下载");
			return;
		}
		// 将下载任务(HttpRequestUtil + DownloadInfoPanel)添加至全局列表, 让监控进程周期获取信息并刷新
		Global.downloadTaskList.put(downPanel, iNeedAV.getDownloader());
		// 根据信息初始化绘制下载部件
		JPanel jpContent = Global.downloadTab.getJpContent();
		jpContent.add(downPanel);
		jpContent.setPreferredSize(new Dimension(1100, 128 * Global.downloadTaskList.size()));
		Global.downLoadThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(iNeedAV.getDownloader().currentStatus() == StatusEnum.STOP) {
						System.out.println("已经人工停止,无需再下载");
						return;
					}
					// 开始下载
					if(iNeedAV.downloadClip(url, avid, iNeedAV.getInputParser(avid).getVideoLinkQN(), page)) {
						// 下载成功后保存到仓库
						if(Global.saveToRepo) {
							RepoUtil.appendAndSave(record);
						}
						CmdUtil.convertOrAppendCmdToRenameBat(avid_qn, formattedTitle, page);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
