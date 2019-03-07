package nicelee.ui.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nicelee.model.ClipInfo;
import nicelee.ui.thread.DownloadRunnable;

public class ClipInfoPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -752743062676819403L;
	String avTitle;
	ClipInfo clip;
	public ClipInfoPanel(ClipInfo clip, String avTitle) {
		this.clip = clip;
		this.avTitle = avTitle;
		initUI();
	}
	
	void initUI() {
		this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.setPreferredSize(new Dimension(340, 110));
		JLabel label = new JLabel(clip.getPage() + " - " + clip.getTitle(), JLabel.CENTER);
		label.setPreferredSize(new Dimension(300, 30));
		this.setOpaque(false);
		this.add(label);
		for(final int qn :clip.getLinks().keySet()) {
			JButton btn = new JButton("清晰度: " + qn);
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					DownloadRunnable downThread = new DownloadRunnable(avTitle+ "-" +clip.getTitle(),
							clip.getAvId(),
							String.valueOf(clip.getcId()),
							String.valueOf(clip.getPage()), qn);
					new Thread(downThread).start();
					//Global.downLoadThreadPool.execute(downThread);
				}
			});
			this.add(btn);
		}
	}
}
