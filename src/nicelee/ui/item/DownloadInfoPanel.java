package nicelee.ui.item;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.downloaders.Downloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.ui.Global;

public class DownloadInfoPanel extends JPanel implements ActionListener {

	String avTitle; // 原始av标题
	String clipTitle; // 原始clip标题
	String avid;
	String cid;
	int page;
	int remark;
	int qn;

	// 下载相关
	public INeedAV iNeedAV;
	public String url;
	public String avid_qn;
	public String formattedTitle;

	long lastCntTime = 0L;
	long lastCnt = 0L;
	/**
	 * 
	 */
	private static final long serialVersionUID = -752743062676819402L;
	String path;
	String fileName;
	long totalSize;
	long currentDown;
	boolean isdownloading = true;

	JButton btnRemove;
	JButton btnOpen;
	JButton btnOpenFolder;
	JButton btnControl;
	JLabel lbCurrentStatus;
	JLabel lbDownFile;
	JLabel lbFileName;
	JLabel lbavName;

	public DownloadInfoPanel(ClipInfo clip, int qn) {
		this.avTitle = clip.getAvTitle();
		this.clipTitle = clip.getAvTitle();
		this.avid = clip.getAvId();
		this.cid = Long.toString(clip.getcId());
		this.page = clip.getPage();
		this.remark = clip.getRemark();
		this.qn = qn;

		path = "D:\\bilibiliDown\\";
		fileName = "timg.gif";
		totalSize = 0L;
		currentDown = 0L;
		initUI();
	}

	void initUI() {
		// this.setOpaque(false);
		this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.setPreferredSize(new Dimension(1100, 120));

		lbFileName = new JLabel("尚未生成");
		lbFileName.setPreferredSize(new Dimension(600, 45));
		lbFileName.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(lbFileName);

		btnOpen = new JButton("打开文件");
		btnOpen.setPreferredSize(new Dimension(100, 45));
		btnOpen.addActionListener(this);
		this.add(btnOpen);

		btnOpenFolder = new JButton("打开文件夹");
		btnOpenFolder.setPreferredSize(new Dimension(100, 45));
		btnOpenFolder.addActionListener(this);
		this.add(btnOpenFolder);

		btnRemove = new JButton("删除任务");
		btnRemove.setPreferredSize(new Dimension(100, 45));
		btnRemove.addActionListener(this);
		this.add(btnRemove);

		JLabel blank = new JLabel();
		blank.setPreferredSize(new Dimension(100, 45));
		this.add(blank);

		lbavName = new JLabel(avTitle);
		lbavName.setPreferredSize(new Dimension(500, 45));
		lbavName.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(lbavName);

		lbCurrentStatus = new JLabel("正在下载...");
		lbCurrentStatus.setPreferredSize(new Dimension(300, 45));
		lbCurrentStatus.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(lbCurrentStatus);

		lbDownFile = new JLabel(currentDown + "/" + totalSize);
		lbCurrentStatus.setPreferredSize(new Dimension(250, 45));
		lbDownFile.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(lbDownFile);
		this.setBackground(new Color(204, 255, 255));

		btnControl = new JButton("暂停");
		btnControl.setPreferredSize(new Dimension(100, 45));
		btnControl.addActionListener(this);
		this.add(btnControl);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOpenFolder) {
			File file = new File(lbFileName.getText());
			try {
				if (file.exists()) {
					// 打开并选中
					String cmd[] = { "explorer", "/e,/select,", file.getAbsolutePath() };
					Runtime.getRuntime().exec(cmd);
				} else {
					Desktop desktop = Desktop.getDesktop();
					desktop.open(file.getParentFile());
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "打开文件夹失败!", "失败", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource() == btnOpen) {
			File file = new File(lbFileName.getText());
			try {
				Desktop.getDesktop().open(file);
			} catch (Exception e1) {
				// e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "打开文件失败!", "失败", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource() == btnRemove) {
//			if(Global.downloadTaskList.get(this).getStatus() == 0) {
//				JOptionPane.showMessageDialog(this, "当前正在文件下载中!", "警告", JOptionPane.WARNING_MESSAGE);
//			}
			removeTask(true);
		} else if (e.getSource() == btnControl) {
			StatusEnum status = iNeedAV.getDownloader().currentStatus();
			if (status == StatusEnum.DOWNLOADING) {
				stopTask();
			} else {
				continueTask();
			}
		}
	}

	/**
	 * 下载前的初始化工作
	 */
	public void initDownloadParams(INeedAV iNeedAV, String url, String avid_qn, String formattedTitle) {
		this.iNeedAV = iNeedAV;
		this.avid_qn = avid_qn;
		this.formattedTitle = formattedTitle;
		this.url = url;
		lbavName.setText(formattedTitle);
	}

	/**
	 * 停止任务(方法内包含状态判断)
	 */
	public void stopTask() {
		Downloader downloader = iNeedAV.getDownloader();
		downloader.stopTask();
	}

	/**
	 * 继续任务(方法内包含状态判断)
	 */
	public void continueTask() {
		String record = avid_qn + "-p" + page;
		Downloader downloader = iNeedAV.getDownloader();
		// 如果正在下载 或 下载完毕，则不需要下载
		StatusEnum status = downloader.currentStatus();
		if (status != StatusEnum.DOWNLOADING && status != StatusEnum.SUCCESS && status != StatusEnum.PROCESSING) {
			downloader.startTask();
			Global.downLoadThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					if (downloader.currentStatus() == StatusEnum.STOP) {
						Logger.println("已经人工停止,无需再下载");
						return;
					}
					// 开始下载
					if (downloader.download(url, avid, qn, page)) {
						// 下载成功后保存到仓库
						if (Global.saveToRepo) {
							RepoUtil.appendAndSave(record);
						}
						CmdUtil.convertOrAppendCmdToRenameBat(avid_qn, formattedTitle, page);
					}
				}
			});
		}
	}

	/**
	 * 删除任务
	 */
	public void removeTask(boolean deleteAll) {
		// 删除所有 或 删除已完成的任务
		// 0 正在下载; 1 下载完毕; -1 出现错误; -2 人工停止;-3队列中
		if (deleteAll || iNeedAV.getDownloader().currentStatus() == StatusEnum.SUCCESS) {
			// 停止下载
			Global.downloadTaskList.get(this).stopTask();
			// 全局监控撤销
			Global.downloadTaskList.remove(this);
			// 当前页面控件删除
			Global.downloadTab.getJpContent().remove(this);
			// 大小重新适配
			Global.downloadTab.getJpContent()
					.setPreferredSize(new Dimension(1100, 128 * Global.downloadTaskList.size()));
			Global.downloadTab.getJpContent().updateUI();
			Global.downloadTab.getJpContent().repaint();
			// 删除未完成的下载文件
			File file = new File(lbFileName.getText() + ".part");
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public JLabel getLbCurrentStatus() {
		return lbCurrentStatus;
	}

	public void setLbCurrentStatus(JLabel lbCurrentStatus) {
		this.lbCurrentStatus = lbCurrentStatus;
	}

	public JLabel getLbDownFile() {
		return lbDownFile;
	}

	public void setLbDownFile(JLabel lbDownFile) {
		this.lbDownFile = lbDownFile;
	}

	public JLabel getLbFileName() {
		return lbFileName;
	}

	public void setLbFileName(JLabel lbFileName) {
		this.lbFileName = lbFileName;
	}

	public JButton getBtnControl() {
		return btnControl;
	}

	public void setBtnControl(JButton btnControl) {
		this.btnControl = btnControl;
	}

	@Override
	public int hashCode() {
		return (avid + page).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// System.out.println("DownloadInfoPanel - equals:");
		if (obj instanceof DownloadInfoPanel) {
			DownloadInfoPanel down = (DownloadInfoPanel) obj;
			return (avid.equals(down.avid) && page == down.page);
		}
		return false;
	}

	public long getLastCntTime() {
		return lastCntTime;
	}

	public void setLastCntTime(long lastCntTime) {
		this.lastCntTime = lastCntTime;
	}

	public long getLastCnt() {
		return lastCnt;
	}

	public void setLastCnt(long lastCnt) {
		this.lastCnt = lastCnt;
	}

	public String getAvid() {
		return avid;
	}

	public void setAvid(String avid) {
		this.avid = avid;
	}

	public int getQn() {
		return qn;
	}

	public void setQn(int qn) {
		this.qn = qn;
	}

}
