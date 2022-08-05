package nicelee.ui;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.ui.item.MJMenuBar;
import nicelee.ui.thread.LoginThread;
import nicelee.ui.thread.MonitoringThread;

public class FrameMain_v3_4 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTabbedPane jTabbedpane;// 存放选项卡的组件

	public static void main(String[] args) {
		// System.getProperties().setProperty("file.encoding", "utf-8");
		ConfigUtil.initConfigs();
		//初始化主题
		initUITheme();
		
		//初始化UI
		FrameMain_v3_4 main = new FrameMain_v3_4();
		main.InitUI();
		
		//初始化监控线程，用于刷新下载面板
		MonitoringThread th = new MonitoringThread();
		th.start();

		//初始化 - 登录
		INeedLogin inl = new INeedLogin();
		if (inl.readCookies() != null) {
			Global.needToLogin = true;
		}
		LoginThread loginTh = new LoginThread();
		loginTh.start();

		//
		if(Global.saveToRepo) {
			RepoUtil.init(false);
		}
		//Logger.println(.);
//		FrameQRCode qr = new FrameQRCode("https://www.bilibili.com/");
//		qr.initUI();
//		qr.dispose();
	}

	/**
	 * 
	 */
	static void initUITheme() {
		try {
			if (!Global.themeDefault) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				Font font = new Font("Dialog", Font.PLAIN, 12);
				Enumeration<Object> keys = UIManager.getDefaults().keys();
				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					Object value = UIManager.get(key);
					if (value instanceof javax.swing.plaf.FontUIResource) {
						UIManager.put(key, font);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void InitUI() {

		this.setTitle("BiliBili Down~~" + Global.version);
		this.setSize(1200, 767);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		URL iconURL = this.getClass().getResource("/resources/favicon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		this.setIconImage(icon.getImage());

		//菜单栏
		MJMenuBar menu = new MJMenuBar(this);
		this.setJMenuBar(menu);
		
		jTabbedpane = new JTabbedPane();
		Global.tabs = jTabbedpane;
		jTabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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
		// 关闭窗口时
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				CmdUtil.deleteAllInactiveCmdTemp();
			}
		});
		this.setVisible(true);
		Logger.println(jTabbedpane.getWidth());
		Logger.println(jTabbedpane.getHeight());
	}

}
