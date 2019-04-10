package nicelee.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nicelee.bilibili.INeedAV;
import nicelee.ui.item.MJTextField;
import nicelee.ui.thread.GetVideoDetailThread;
import nicelee.ui.thread.LoginThread;

public class TabIndex extends JPanel implements ActionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5829023045158490349L;
	public ImageIcon imgIconHeaderDefault = new ImageIcon(this.getClass().getResource("/resources/header.png"));
	public ImageIcon backgroundIcon = new ImageIcon(this.getClass().getResource("/resources/background.jpg"));
	public JLabel jlHeader;
	JTextField txtSearch = new MJTextField("https://www.bilibili.com/video/av35296336");
	JButton btnSearch = new JButton("查找");
	
	
	JTextArea consoleArea = new JTextArea(20, 50);
	JTabbedPane jTabbedpane;

	public TabIndex(JTabbedPane jTabbedpane) {
		this.jTabbedpane = jTabbedpane;
		init();
	}

	public void init() {
		this.setPreferredSize(new Dimension(1150, 620));
		// 空白模块- 占位
		JLabel jpBLANK = new JLabel();
		jpBLANK.setPreferredSize(new Dimension(980, 80));
		this.add(jpBLANK);
		
		// 空白模块- 占位
		imgIconHeaderDefault = new ImageIcon(imgIconHeaderDefault.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
		jlHeader = new JLabel(imgIconHeaderDefault);
		jlHeader.addMouseListener(this);
		this.add(jlHeader);
		
		URL fileURL = this.getClass().getResource("/resources/title.png");
		ImageIcon imgIcon = new ImageIcon(fileURL);
		//imgIcon = new ImageIcon(imgIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH));
		JLabel jLabel = new JLabel(imgIcon);
		this.add(jLabel);
		
		// 查找模块
		JPanel jpSearch = new JPanel();
		txtSearch.setPreferredSize(new Dimension(700, 40));
		btnSearch.addActionListener(this);
		btnSearch.setPreferredSize(new Dimension(90, 40));
		jpSearch.add(txtSearch);
		jpSearch.add(btnSearch);
		jpSearch.setOpaque(false);
		this.add(jpSearch);
		this.setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
//		// super.paintComponent(g);
		g.drawImage(backgroundIcon.getImage(), 0, 0, this.getSize().width, this.getSize().height, this.getParent());
		this.setOpaque(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSearch) {
			String avId = txtSearch.getText();
			INeedAV iNeedAV = new INeedAV();
			iNeedAV.setDownFormat(Global.downloadFormat);
			iNeedAV.setPageSize(Global.pageSize);
			avId = iNeedAV.getAvID(avId);
			System.out.println("当前解析的id为：");
			System.out.println(avId);
			if(avId.contains(" ")) {
				String avs[] = avId.trim().split(" ");
				for(String av : avs) {
					popVideoInfoTab(av);
				}
			}else {
				popVideoInfoTab(avId);
			}
		}
	}

	/**
	 * @param avId
	 */
	private void popVideoInfoTab(String avId) {
		if("".equals(avId)) {
			JOptionPane.showMessageDialog(this, "解析链接失败!", "失败", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// 作品页
		JLabel label = new JLabel("正在加载中...");
		final TabVideo tab = new TabVideo(label);
		jTabbedpane.addTab("作品页", tab);
		jTabbedpane.setTabComponentAt(jTabbedpane.indexOfComponent(tab), label);
		GetVideoDetailThread th = new GetVideoDetailThread(tab, avId);
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					jTabbedpane.remove(tab);
				} else {
					jTabbedpane.setSelectedComponent(tab);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		th.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == jlHeader) {
			Global.needToLogin = true;
			LoginThread loginTh = new LoginThread();
			loginTh.start();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Global.index.jlHeader.setBorder(BorderFactory.createLineBorder(Color.red));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Global.index.jlHeader.setBorder(null);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
