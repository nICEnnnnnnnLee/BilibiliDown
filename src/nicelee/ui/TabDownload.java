package nicelee.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TabDownload extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8714599826187286737L;
	ImageIcon backgroundIcon = new ImageIcon(this.getClass().getResource("/resources/background.jpg"));
	
	JPanel jpContent;
	JScrollPane jpScorll;
	
	public TabDownload() {
		initUI();
	}
	
	public void initUI() {
		jpContent = new JPanel();
		jpContent.setPreferredSize(new Dimension(1100, 300));
		jpContent.setOpaque(false);
		
//		DownloadInfoPanel downPan = new DownloadInfoPanel();
//		jpContent.add(downPan);
		jpScorll = new JScrollPane(jpContent);
		// 分别设置水平和垂直滚动条出现方式
		jpScorll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpScorll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//jpScorll.setBorder(BorderFactory.createLineBorder(Color.red));
		jpScorll.setPreferredSize(new Dimension(1150, 620));
		jpScorll.setOpaque(false);
		jpScorll.getViewport().setOpaque(false); 
		this.add(jpScorll);
	}
	
	@Override
	public void paintComponent(Graphics g) {
//		// super.paintComponent(g);
		g.drawImage(backgroundIcon.getImage(), 0, 0, this.getSize().width, this.getSize().height, this.getParent());
		this.setOpaque(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	public JPanel getJpContent() {
		return jpContent;
	}

	public void setJpContent(JPanel jpContent) {
		this.jpContent = jpContent;
	}

}
