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

import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;
import nicelee.ui.TabVideo;
import nicelee.ui.thread.DownloadRunnable;

public class ClipInfoPanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -752743062676819403L;
	String avTitle;
	ClipInfo clip;

	private JLabel labelTitle;
	private long lastMousePressed;
	public ClipInfoPanel(ClipInfo clip) {
		this.clip = clip;
		this.avTitle = clip.getAvTitle();
		initUI();
	}

	void initUI() {
		this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.setPreferredSize(new Dimension(340, 110));
		labelTitle = new JLabel(clip.getRemark() + " - " + clip.getTitle(), JLabel.CENTER);
		labelTitle.addMouseListener(this);
		//labelTitle.setToolTipText("双击复制title文本 + avId，长按查看更换预览图片");
		labelTitle.setToolTipText(clip.getAvTitle() + clip.getTitle());
		labelTitle.setPreferredSize(new Dimension(300, 30));
		this.setOpaque(false);
		this.add(labelTitle);
		for (final int qn : clip.getLinks().keySet()) {
			// JButton btn = new JButton("清晰度: " + qn);
			String qnName = VideoQualityEnum.getQualityDescript(qn);
			JButton btn = null;
			if (qnName != null) {
				btn = new JButton(qnName);
			} else {
				btn = new JButton("清晰度: " + qn);
			}
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					DownloadRunnable downThread = new DownloadRunnable(clip, qn);
					// new Thread(downThread).start();
					Global.queryThreadPool.execute(downThread);
				}
			});
			this.add(btn);
		}
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
