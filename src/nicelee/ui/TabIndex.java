package nicelee.ui;

import java.awt.Color;
import java.awt.Component;
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
import nicelee.ui.item.OperationPanel;
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
	String placeHolder = "请在此输入B站 av/ep/ss/md/ml号或地址";
	JTextField txtSearch = new MJTextField(placeHolder);
	//new MJTextField("https://www.bilibili.com/video/av35296336");
	JButton btnSearch = new JButton("查找");
	
	
	JTextArea consoleArea = new JTextArea(20, 50);
	JTabbedPane jTabbedpane;

	public TabIndex(JTabbedPane jTabbedpane) {
		this.jTabbedpane = jTabbedpane;
		init();
	}

	public void init() {
		this.setPreferredSize(new Dimension(1150, 620));
		OperationPanel operPanel = new OperationPanel();
		this.add(operPanel);
		
		// 空白模块- 占位
		JLabel jpBLANK = new JLabel();
		jpBLANK.setPreferredSize(new Dimension(700, 80));
		this.add(jpBLANK);
		
		// 空白模块- 占位
		imgIconHeaderDefault = new ImageIcon(imgIconHeaderDefault.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
		jlHeader = new JLabel(imgIconHeaderDefault);
		jlHeader.addMouseListener(this);
		this.add(jlHeader);
		
		//头像模块
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
	
	/**
	 * 关闭所有视频Tab
	 */
	public void closeAllVideoTabs() {
		System.out.println("当前Tab数量： " + (jTabbedpane.getTabCount() - 2));
		System.out.println("正在关闭Tab标签页");
		for(int i = jTabbedpane.getTabCount() - 1; i >= 2 ; i--) {
			jTabbedpane.removeTabAt(i);
		}
		System.out.println("当前Tab数量： " + (jTabbedpane.getTabCount() - 2));
	}
	
	/**
	 * 根据需要下载所有打开的Tab页视频
	 * @param downAll
	 * @param qn
	 */
	public void downVideoTabs(boolean downAll, int qn) {
		for(int i = 0; i < jTabbedpane.getTabCount(); i++) {
			//判断是否为Video标签页, 是就下载
			System.out.printf("Tab 页共 %d 个，当前第 %d 个\r\n",
					jTabbedpane.getTabCount(),
					i);
			Component comp = jTabbedpane.getComponentAt(i);
			if(comp instanceof TabVideo ) {
				TabVideo tabVideo = (TabVideo) comp;
				tabVideo.download(downAll, qn);
			}
		}
	}
	@Override
	public void paintComponent(Graphics g) {
//		// super.paintComponent(g);
		g.drawImage(backgroundIcon.getImage(), 0, 0, this.getSize().width, this.getSize().height, this.getParent());
		this.setOpaque(false);
	}
	
	/**
	 * 对应 查找 按钮的点击事件
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSearch) {
			search();
		}
	}

	/**
	 * 根据输入查找 av信息，并弹出av信息的Tab页
	 */
	public void search() {
		String avId = txtSearch.getText();
		if(!placeHolder.equals(avId)) {
			INeedAV iNeedAV = new INeedAV();
			avId = iNeedAV.getValidID(avId);
			System.out.println("当前解析的id为：");
			System.out.println(avId);
			if(avId.contains(" ")) {
				String avs[] = avId.trim().split(" ");
				System.out.println("将弹出窗口个数： " + avs.length);
				for(String av : avs) {
					popVideoInfoTab(av);
				}
			}else {
				popVideoInfoTab(avId);
			}
		}
		
	}

	/**
	 * 弹出avId对应的Video 标签页
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
