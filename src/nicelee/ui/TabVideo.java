package nicelee.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.ui.item.MJButton;
import nicelee.ui.thread.DownloadRunnable;

public class TabVideo extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -5829023045158490350L;
	// ImageIcon backgroundIcon = new
	// ImageIcon(this.getClass().getResource("/resources/background.jpg"));

	VideoInfo avInfo;// 保存当前Tab 的视频信息

	JLabel lbTabTitle;
	JLabel lbVideoTitle = new JLabel("Av标题");
	JLabel lbAvID = new JLabel("AvID");
	JLabel lbBreif = new JLabel("Av简介");
	JLabel lbAvPrivew;
	JPanel jpContent;
	JScrollPane jpScorll;
	JComboBox<String> cbQn; // 清晰度
	JButton btnDownAll; // 批量下载
	JButton btnDownCC; // 批量下载CC字幕
	String currentDisplayPic; // 当前预览图片路径
	JPanel nextPagePanel;  // 下一页面板
	JLabel jlNextPageTips; // 下一页文字提示
	protected JButton btnNextPage; // 下一页
	

	public TabVideo(JLabel lbTabTitle) {
		this.lbTabTitle = lbTabTitle;
		init();
	}

	public void init() {
		Dimension size = new Dimension(86, 26);
		this.setOpaque(false);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		// 空白模块- 占位
		JLabel jlBLANK = new JLabel();
		jlBLANK.setPreferredSize(new Dimension(1150, 50));
		this.add(jlBLANK);

		JLabel jlBLANK0 = new JLabel();
		jlBLANK0.setPreferredSize(new Dimension(100, 30));
		this.add(jlBLANK0);

		lbVideoTitle.setBorder(BorderFactory.createLineBorder(Color.red));
		lbVideoTitle.setPreferredSize(new Dimension(400, 30));
		lbVideoTitle.setBackground(Color.BLUE);
		lbVideoTitle.addMouseListener(this);
		this.add(lbVideoTitle);

		// 空白模块- 占位
		JLabel jpBLANK1 = new JLabel();
		jpBLANK1.setPreferredSize(new Dimension(30, 30));
		this.add(jpBLANK1);

		lbAvID.setBorder(BorderFactory.createLineBorder(Color.red));
		lbAvID.setPreferredSize(new Dimension(100, 30));
		lbAvID.addMouseListener(this);
		this.add(lbAvID);

		// 空白模块- 占位
		JLabel jlBLANK1 = new JLabel();
		jlBLANK1.setPreferredSize(new Dimension(40, 30));
		this.add(jlBLANK1);

		JLabel label1 = new JLabel("优先清晰度");
		this.add(label1);
		cbQn = new JComboBox<>();
		for (VideoQualityEnum item : VideoQualityEnum.values()) {
			cbQn.addItem(item.getQuality());
		}
		cbQn.setSelectedItem(Global.tab_qn);
		// cbQn.setSelectedIndex(2);
		btnDownAll = new MJButton("批量下载");
		btnDownAll.setPreferredSize(size);
		btnDownAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				download(true, VideoQualityEnum.getQN(cbQn.getSelectedItem().toString()));
			}
		});
		btnDownCC = new MJButton("字幕下载");
		btnDownCC.setPreferredSize(size);
		btnDownCC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadCC();
			}
		});
		this.add(cbQn);
		this.add(btnDownAll);
		this.add(btnDownCC);
		// 空白模块- 占位
		JLabel jlBLANK11 = new JLabel();
		jlBLANK11.setPreferredSize(new Dimension(90, 30));
		// jlBLANK11.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(jlBLANK11);

		// 空白模块- 占位
		JLabel jlBLANK2 = new JLabel();
		jlBLANK2.setPreferredSize(new Dimension(100, 60));
		// jlBLANK2.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(jlBLANK2);

		lbBreif.setBorder(BorderFactory.createLineBorder(Color.red));
		lbBreif.setPreferredSize(new Dimension(700, 60));
		lbBreif.addMouseListener(this);
		this.add(lbBreif);

		// 下一页panel
		nextPagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nextPagePanel.setPreferredSize(new Dimension(350, 60));
		nextPagePanel.setOpaque(false);
		this.add(nextPagePanel);
		// 空白模块- 占位
		JLabel jlBLANK3 = new JLabel();
		jlBLANK3.setPreferredSize(new Dimension(50, 60));
		nextPagePanel.add(jlBLANK3);
		jlNextPageTips = new JLabel("视频数量不对?试试这个-->");
		jlNextPageTips.setAlignmentX(50.0f);
		jlNextPageTips.setPreferredSize(new Dimension(150, 60));
		btnNextPage = new MJButton("下一页");
		btnNextPage.setPreferredSize(size); // new Dimension(86, 26);
		
		// 空白模块- 占位
		JLabel jlBLANK4 = new JLabel();
		jlBLANK4.setPreferredSize(new Dimension(100, 460));
		this.add(jlBLANK4);

		// ImageIcon imag1 = new
		// ImageIcon(this.getClass().getResource("/resources/loading.gif"));
		// imag1.setImage(imag1.getImage().getScaledInstance(700, 460,
		// Image.SCALE_DEFAULT));
		lbAvPrivew = new JLabel("加载中", JLabel.CENTER);
		lbAvPrivew.setToolTipText("单击获取图片链接");
		lbAvPrivew.setFont(new Font("黑体", Font.BOLD, 120));
		lbAvPrivew.setBorder(BorderFactory.createLineBorder(Color.red));
		lbAvPrivew.setPreferredSize(new Dimension(700, 460));
		lbAvPrivew.addMouseListener(this);
//		try {
//			ImageIcon imgIcon = new ImageIcon(QrCodeUtil.createQrCode("你好吗?我好呀", 900));
//			imgIcon = new ImageIcon(imgIcon.getImage().getScaledInstance(450, 450, Image.SCALE_SMOOTH));
//			lbAvPrivew.setIcon(imgIcon);
//		} catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.add(lbAvPrivew);

		jpContent = new JPanel();
		jpContent.setPreferredSize(new Dimension(340, 300));
		jpContent.setOpaque(false);

		jpScorll = new JScrollPane(jpContent);
		// 分别设置水平和垂直滚动条出现方式
		jpScorll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpScorll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jpScorll.setBorder(BorderFactory.createLineBorder(Color.red));
		jpScorll.setPreferredSize(new Dimension(350, 460));
		jpScorll.setOpaque(false);
		jpScorll.getViewport().setOpaque(false);
		this.add(jpScorll);

	}

	public void displayNextPagePanel() {
		nextPagePanel.add(jlNextPageTips);
		nextPagePanel.add(btnNextPage);
	}
	
	/**
	 * 用于批量下载视频
	 * 
	 * @param downAll
	 * @param qn
	 */
	public void download(boolean downAll, int qn) {
		int total = avInfo.getClips().values().size();
		download(0, qn);
		if (downAll) {
			for (int i = 1; i < total; i++) {
				download(i, qn);
			}
		}
	}

	/**
	 * 用于下载字幕
	 * 
	 */
	public void downloadCC() {
		int total = avInfo.getClips().values().size();
		for (int i = 0; i < total; i++) {
			download(i, 800);
		}
	}

	/**
	 * 下载第i个视频
	 * 
	 * @param i
	 * @param qn
	 */
	private void download(int i, int qn) {
		try {
			ClipInfo clip = (ClipInfo) avInfo.getClips().values().toArray()[i];
			DownloadRunnable downThread = new DownloadRunnable(avInfo, clip, qn);
			// new Thread(downThread).start();
			Global.queryThreadPool.execute(downThread);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public void paintComponent(Graphics g) {
////		// super.paintComponent(g);
//		g.drawImage(backgroundIcon.getImage(), 0, 0, this.getSize().width, this.getSize().height, this.getParent());
//		this.setOpaque(false);
//	}
	@Override
	public void actionPerformed(ActionEvent e) {

	}

	public JLabel getLbTabTitle() {
		return lbTabTitle;
	}

	public void setLbTabTitle(JLabel lbTabTitle) {
		this.lbTabTitle = lbTabTitle;
	}

	public JLabel getLbVideoTitle() {
		return lbVideoTitle;
	}

	public void setLbVideoTitle(JLabel lbVideoTitle) {
		this.lbVideoTitle = lbVideoTitle;
	}

	public JLabel getLbAvID() {
		return lbAvID;
	}

	public void setLbAvID(JLabel lbAvID) {
		this.lbAvID = lbAvID;
	}

	public JLabel getLbBreif() {
		return lbBreif;
	}

	public void setLbBreif(JLabel lbBreif) {
		this.lbBreif = lbBreif;
	}

	public JLabel getLbAvPrivew() {
		return lbAvPrivew;
	}

	public void setLbAvPrivew(JLabel lbAvPrivew) {
		this.lbAvPrivew = lbAvPrivew;
	}

	public JScrollPane getJpScorll() {
		return jpScorll;
	}

	public void setJpScorll(JScrollPane jpScorll) {
		this.jpScorll = jpScorll;
	}

	public JPanel getJpContent() {
		return jpContent;
	}

	public void setJpContent(JPanel jpContent) {
		this.jpContent = jpContent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel label = (JLabel) e.getSource();
		// 获取系统剪贴板
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String txtToCopy = null;
		if (label == lbAvPrivew) {
			txtToCopy = currentDisplayPic != null ? currentDisplayPic : avInfo.getVideoPreview();
		} else {
			txtToCopy = label.getText();
		}
		if (txtToCopy != null) {
			// 封装文本内容
			Transferable trans = new StringSelection(txtToCopy);
			// 把文本内容设置到系统剪贴板
			clipboard.setContents(trans, null);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JLabel label = (JLabel) e.getSource();
		label.setBorder(BorderFactory.createLineBorder(Color.black, 3));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JLabel label = (JLabel) e.getSource();
		label.setBorder(BorderFactory.createLineBorder(Color.red));

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public VideoInfo getAvInfo() {
		return avInfo;
	}

	public void setAvInfo(VideoInfo avInfo) {
		this.avInfo = avInfo;
	}

	public String getCurrentDisplayPic() {
		return currentDisplayPic;
	}

	public void setCurrentDisplayPic(String currentDisplayPic) {
		this.currentDisplayPic = currentDisplayPic;
	}
}
