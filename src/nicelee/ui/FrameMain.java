package nicelee.ui;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import nicelee.bilibili.INeedLogin;
import nicelee.ui.thread.LoginThread;
import nicelee.ui.thread.MonitoringThread;

public class FrameMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTabbedPane jTabbedpane = new JTabbedPane();// 存放选项卡的组件

	public static void main(String[] args) {
		System.getProperties().setProperty("file.encoding", "utf-8");

		FrameMain main = new FrameMain();
		main.InitUI();
		MonitoringThread th = new MonitoringThread();
		th.start();

		INeedLogin inl = new INeedLogin();
		if (inl.readCookies() != null) {
			Global.needToLogin = true;
		}
		;

		LoginThread loginTh = new LoginThread();
		loginTh.start();

//		FrameQRCode qr = new FrameQRCode("https://www.bilibili.com/");
//		qr.initUI();
//		qr.dispose();
	}

	/**
	 * 
	 */
	public void InitUI() {

		this.setTitle("BiliBili~~~");
		this.setSize(1200, 727);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		URL iconURL = this.getClass().getResource("/resources/favicon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		this.setIconImage(icon.getImage());

		// Index Tab
		Global.index = new TabIndex(jTabbedpane);
		jTabbedpane.addTab("主页", Global.index);
		// 下载页
		Global.downloadTab = new TabDownload();
		jTabbedpane.addTab("下载页", Global.downloadTab);
		// 作品页
//		JLabel label = new JLabel("作品页");
//		TabVideo tab = new TabVideo(label);
//		jTabbedpane.addTab("作品页", tab);
//		jTabbedpane.setTabComponentAt(jTabbedpane.indexOfComponent(tab), label);

		this.setContentPane(jTabbedpane);
		//
		this.setVisible(true);
	}

}
