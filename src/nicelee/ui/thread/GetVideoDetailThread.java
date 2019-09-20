package nicelee.ui.thread;

import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.ui.Global;
import nicelee.ui.TabVideo;
import nicelee.ui.item.ClipInfoPanel;

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
			video.setAvInfo(avInfo);
			video.getLbAvID().setText(avInfo.getVideoId());
			video.setCurrentDisplayPic(avInfo.getVideoPreview());
			try {
				URL fileURL = new URL(avInfo.getVideoPreview());
				ImageIcon imag1 = new ImageIcon(fileURL);
				imag1 = new ImageIcon(imag1.getImage().getScaledInstance(700, 460, Image.SCALE_DEFAULT));
				video.getLbAvPrivew().setIcon(imag1);
				video.getLbAvPrivew().setText("");
			}catch (Exception e) {
				video.getLbAvPrivew().setText("无效预览图");
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
			jpContent.setPreferredSize(new Dimension(340, 116 * avInfo.getClips().size()));
			for(ClipInfo cInfo:  avInfo.getClips().values()) {
				ClipInfoPanel cp = new ClipInfoPanel(cInfo);
				jpContent.add(cp);
			}
			jpContent.updateUI();
			jpContent.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
