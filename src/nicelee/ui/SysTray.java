package nicelee.ui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.util.Logger;

public class SysTray {
	private Image systemTrayImg;
	final private JFrame frame;

	private SystemTray systemTray;
	private TrayIcon trayIcon;

	private SysTray(JFrame frame, Image systemTrayImg) {
		this.frame = frame;
		this.systemTrayImg = systemTrayImg;
	}

	private static SysTray mSysTray;

	public static boolean shoudSysTrayEnabled() {
		return Global.isSysTrayEnabled && SystemTray.isSupported();
	}
	
	public static boolean isSysTrayInitiated() {
		return mSysTray != null;
	}

	public static void buildSysTray(JFrame frame, Image systemTrayImg) {
		if (mSysTray == null) {
			mSysTray = new SysTray(frame, systemTrayImg).build();
		}
	}

	private SysTray build() {
		if (!shoudSysTrayEnabled()) {
			Logger.println("托盘功能未启用");
			return null;
		}
		Logger.println("托盘功能开始启用");
		systemTray = SystemTray.getSystemTray();
		Dimension dim = systemTray.getTrayIconSize();
		double factor = System.getProperty("os.name").startsWith("Windows")? 1.0: 0.8;
		int width = (int) (dim.width * factor);
		Logger.println("Systray width: " + width);
		systemTrayImg = systemTrayImg.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
		PopupMenu popup = createPopupMenu();
		trayIcon = new TrayIcon(systemTrayImg, "BilibiliDown", popup);
		// trayIcon.setImageAutoSize(true);

		// 鼠标事件
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 双击鼠标左键托盘窗口再现
				if ((e.getButton() == 1) && (e.getClickCount() == 2)) {
					Logger.println("双击左键！");
					frame.setVisible(true);
					frame.setExtendedState(JFrame.NORMAL);
				}
			}
		});

		try {
			// 添加托盘图标到系统托盘
			systemTray.add(trayIcon);
			frame.setExtendedState(JFrame.ICONIFIED);
			return this;
		} catch (AWTException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * 初始化弹出式菜单
	 */
	private PopupMenu createPopupMenu() {
		// 创建弹出式菜单
		PopupMenu popup = new PopupMenu();

		// 构建系统托盘弹出菜单主界面选项和退出界面选项
		MenuItem mainMenuItem = createMainMenuItem();
		MenuItem exitMenuItem = createExitMenuItem();

		popup.add(mainMenuItem);
		popup.addSeparator();
		popup.add(exitMenuItem);

		return popup;
	}

	/**
	 * 构建系统托盘弹出菜单 主界面选项
	 */
	private MenuItem createMainMenuItem() {
		MenuItem mainMenuItem = new MenuItem("BilibiliDown~");
		mainMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(true);
				frame.setExtendedState(JFrame.NORMAL);
			}
		});

		return mainMenuItem;
	}

	/**
	 * 构建退出程序选项
	 */
	private MenuItem createExitMenuItem() {
		// 构建退出程序选项
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 如果是用户想要关闭程序，先判断是否仍然有活动的任务
				if (frame instanceof FrameMain && Global.downloadTab.activeTask > 0) {
					Object[] options = { "我要退出", "我再想想" };
					String msg = String.format("当前仍有 %d 个任务在下载/转码，正在转码的文件退出后可能丢失或异常，确定要退出吗？",
							Global.downloadTab.activeTask);
					int m = JOptionPane.showOptionDialog(null, msg, "警告", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					Logger.println(m);
					if (m != 0)
						return;
				}
				Logger.println("closing...");
				WindowEvent event = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
				frame.dispatchEvent(event);
			}
		});

		return exitMenuItem;
	}
}
