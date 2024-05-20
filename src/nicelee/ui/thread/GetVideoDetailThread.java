package nicelee.ui.thread;

import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.impl.AbstractPageQueryParser;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.ui.Global;
import nicelee.ui.TabVideo;
import nicelee.ui.item.ClipInfoPanel;
import nicelee.ui.item.JOptionPaneManager;

public class GetVideoDetailThread extends Thread{
	
	TabVideo video;
	String avId;
	public GetVideoDetailThread(TabVideo video, String avId) {
		this.video = video;
		this.avId = avId;
		//this.setName("Thread-GetVideoInfo");
	}
	public void run() {
		try {
			//获取当前av详细信息
			INeedAV avs = new INeedAV();
			//更新当前Tab页面
			VideoInfo avInfo =avs.getVideoDetail(avId, Global.downloadFormat, false);
			// 通过getVideoDetail方法已经调用过selectParser，且是去掉过分页参数的
			if (avs.getInputParser(avId).getParser() instanceof AbstractPageQueryParser) {
				Logger.println("当前为分页查询");
				video.displayNextPagePanel();
			}
			video.setAvInfo(avInfo);
			video.getLbAvID().setText(avInfo.getVideoId());
			Collection<ClipInfo> clips = avInfo.getClips().values();
			if(Global.autoDisplayPreviewPic) {
				if(clips.size() == 0)
					video.setCurrentDisplayPic(avInfo.getVideoPreview());
				else
					video.setCurrentDisplayPic(clips.iterator().next().getPicPreview());
				try {
					URL fileURL = new URL(video.getCurrentDisplayPic());
					ImageIcon imag1 = new ImageIcon(fileURL);
					imag1 = new ImageIcon(imag1.getImage().getScaledInstance(700, 460, Image.SCALE_SMOOTH));
					video.getLbAvPrivew().setIcon(imag1);
					video.getLbAvPrivew().setText("");
				}catch (Exception e) {
					video.getLbAvPrivew().setText("无效预览图");
				}
			} else {
				video.getLbAvPrivew().setText("不显示预览");
			}
			video.getLbBreif().setText(avInfo.getBrief());
			video.getLbBreif().setToolTipText(avInfo.getBrief());
			video.getLbVideoTitle().setText(avInfo.getVideoName());
			video.getLbVideoTitle().setToolTipText(avInfo.getVideoName());
			String title = avInfo.getVideoName();
			if(title.length() >= 12) {
				title = title.substring(0, 9) + "...";
			}
			video.getLbTabTitle().setText(title);
			
			JPanel jpContent = video.getJpContent();
			jpContent.setPreferredSize(new Dimension(340, 175 * avInfo.getClips().size()));
			for(ClipInfo cInfo:  avInfo.getClips().values()) {
				ClipInfoPanel cp = new ClipInfoPanel(avInfo, cInfo);
				jpContent.add(cp);
			}
			jpContent.updateUI();
			jpContent.repaint();
		} catch (BilibiliError e) {
			e.printStackTrace();
			JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
