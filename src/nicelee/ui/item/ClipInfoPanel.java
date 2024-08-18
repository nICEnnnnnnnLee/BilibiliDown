package nicelee.ui.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nicelee.bilibili.enums.AudioQualityEnum;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;
import nicelee.ui.TabVideo;
import nicelee.ui.thread.DownloadRunnable;

public class ClipInfoPanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -752743062676819403L;
	String avTitle;
	VideoInfo video;
	ClipInfo clip;

	private JLabel labelTitle;
	private long lastMousePressed;
	Dimension btnSize = new Dimension(100, 26);
	public ClipInfoPanel(VideoInfo video, ClipInfo clip) {
		this.video = video;
		this.clip = clip;
		this.avTitle = clip.getAvTitle();
		initUI();
	}

	void initUI() {
		this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.setPreferredSize(new Dimension(340, 170));
		// 分情况显示
		boolean isPic = ResourcesUtil.isPicture(clip);
		if(clip.getListName() != null || isPic) {
			labelTitle = new JLabel(clip.getRemark() + " - " + clip.getAvTitle()+ " " +clip.getTitle(), JLabel.CENTER);
		}else {
			labelTitle = new JLabel(clip.getRemark() + " - " + clip.getTitle(), JLabel.CENTER);
		}
		labelTitle.addMouseListener(this);
		//labelTitle.setBorder(BorderFactory.createLineBorder(Color.red));
		//labelTitle.setToolTipText("双击复制title文本 + avId，长按查看更换预览图片");
		labelTitle.setToolTipText(clip.getAvTitle() + clip.getTitle());
		labelTitle.setPreferredSize(new Dimension(250, 30));
		this.setOpaque(false);
		this.add(labelTitle);
		
		if(!isPic) {
			JButton btnDanmuku = new MJButton("弹幕");
			Dimension size = new Dimension(60, 26);
			btnDanmuku.setPreferredSize(size);
			btnDanmuku.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DownloadRunnable downThread = new DownloadRunnable(video, clip, 801);
					Global.queryThreadPool.execute(downThread);
				}
			});
			this.add(btnDanmuku);
		}
		
		for (final int qn : clip.getLinks().keySet()) {
			if(qn >= 800)
				continue;
			// JButton btn = new JButton("清晰度: " + qn);
			String qnName = VideoQualityEnum.getQualityDescript(qn);
			if (qnName == null)
				qnName = AudioQualityEnum.getQualityDescript(qn);
			JButton btn = null;
			if (qnName != null && !isPic) {
				btn = new MJButton(qnName);
			} else {
				btn = new MJButton("清晰度: " + qn);
			}
			initQnBtn(qn, btn);
		}
		if(!isPic) {
			JButton btn = new MJButton("字幕");
			initQnBtn(800, btn);
		}
	}

	/**
	 * @param qn
	 * @param btn
	 */
	private void initQnBtn(final int qn, JButton btn) {
		btn.setPreferredSize(btnSize);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DownloadRunnable downThread = new DownloadRunnable(video, clip, qn);
				// new Thread(downThread).start();
				Global.queryThreadPool.execute(downThread);
			}
		});
		this.add(btn);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String txtToCopy = null;
		if (e.getClickCount() == 1) {
			//txtToCopy = clip.getAvTitle() + clip.getTitle();
		} else {
			txtToCopy = clip.getAvTitle() + clip.getTitle() + " " +clip.getAvId();
		}
		// 获取系统剪贴板
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 封装文本内容
		Transferable trans = new StringSelection(txtToCopy);
		// 把文本内容设置到系统剪贴板
		clipboard.setContents(trans, null);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastMousePressed = System.currentTimeMillis();
		labelTitle.setBorder(BorderFactory.createLineBorder(Color.red));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		labelTitle.setBorder(null);
		long timeTouched = System.currentTimeMillis() - lastMousePressed;
		Logger.println("长按了" + timeTouched +"ms");
		if(timeTouched >= 500) {
			try {
				//获取父对象
				TabVideo tVideo = (TabVideo)this.getParent().getParent().getParent().getParent();
				//设置更换预览图片
				String toDisplay = clip.getPicPreview();
				if(toDisplay != null && !toDisplay.equals(tVideo.getCurrentDisplayPic())) {
					URL fileURL = new URL(toDisplay);
					ImageIcon imag1 = new ImageIcon(fileURL);
					imag1 = new ImageIcon(imag1.getImage().getScaledInstance(700, 460, Image.SCALE_SMOOTH) );
					tVideo.getLbAvPrivew().setText("");
					tVideo.getLbAvPrivew().setIcon(imag1);
					tVideo.setCurrentDisplayPic(toDisplay);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
