package nicelee.ui.item;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import nicelee.bilibili.API;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.VersionManagerUtil;
import nicelee.ui.FrameAbout;
import nicelee.ui.Global;
import nicelee.ui.TabSettings;
import nicelee.ui.thread.DownloadRunnable;
import nicelee.ui.thread.LoginThread;

public class MJMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -344077300590858072L;

	private JFrame frame;
	ButtonGroup btnTypeGroup; // 从菜单栏批量下载的计划类型
	ButtonGroup btnQnGroup;	// 从菜单栏批量下载的优先清晰度选项
	
	public MJMenuBar(JFrame frame) {
		super();
		this.frame = frame;
		init();
		setDefaultTypeGroup(Global.menu_plan);
		setDefaultQnGroup(Global.menu_qn);
	}
	
	public void setDefaultTypeGroup(int planType) {
		try {
			Enumeration<AbstractButton> btns = btnTypeGroup.getElements();
			int order = 0;
			while (btns.hasMoreElements()) {
				JRadioButtonMenuItem item = (JRadioButtonMenuItem) btns.nextElement();
				if(order == planType) {
					item.setSelected(true);
					break;
				}
				order++;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDefaultQnGroup(String qn) {
		Enumeration<AbstractButton> btns = btnQnGroup.getElements();
		while (btns.hasMoreElements()) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) btns.nextElement();
			if(item.getText().equals(qn)) {
				item.setSelected(true);
			}
		}
	}
	
	private void init() {
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		/*
		 * 创建一级菜单
		 */
		JMenu operMenu = new JMenu("操作");
		JMenu configMenu = new JMenu("配置");
		JMenu aboutMenu = new JMenu("关于");
//		Dimension dMenu = new Dimension(39, 21);
//		operMenu.setPreferredSize(dMenu);
//		configMenu.setPreferredSize(dMenu);
//		aboutMenu.setPreferredSize(dMenu);
		this.add(operMenu);
		this.add(configMenu);
		this.add(aboutMenu);
//		Dimension dMenuBar = new Dimension(137, 23);
//		this.setPreferredSize(dMenuBar);
		
		/**
		 * 创建二级 操作 子菜单
		 */
		JMenuItem convertRepo = new JMenuItem("开始转换仓库(慎点)");
		JMenuItem convertRepoBreak = new JMenuItem("停止转换仓库");
		JMenuItem reloadConfig = new JMenuItem("重新加载配置");
		JMenuItem reloadRepo = new JMenuItem("重新加载仓库");
		JMenuItem saveDownloading = new JMenuItem("保存下载任务");
		JMenuItem loadDownloading = new JMenuItem("加载下载任务");
		JMenuItem closeAllMenuItem = new JMenuItem("关闭全部Tab页");
		JMenuItem doMultiDownMenuItem = new JMenuItem("批量下载Tab页");
		
		JMenu loginRelated = new JMenu("登录相关");
		JMenuItem logout = new JMenuItem("退出登录");
		JMenuItem qrLogin = new JMenuItem("二维码登录");
		JMenuItem pwdLogin = new JMenuItem("用户名密码登录");
		JMenuItem smsLogin = new JMenuItem("短信验证登录");
		loginRelated.add(qrLogin);
		loginRelated.add(pwdLogin);
		loginRelated.add(smsLogin);
		loginRelated.addSeparator();
		loginRelated.add(logout);
		
		operMenu.add(convertRepo);
		operMenu.add(convertRepoBreak);
		operMenu.addSeparator();
		operMenu.add(reloadConfig);
		operMenu.add(reloadRepo);
		operMenu.addSeparator();
		operMenu.add(saveDownloading);
		operMenu.add(loadDownloading);
		operMenu.addSeparator();
		operMenu.add(closeAllMenuItem);
		operMenu.add(doMultiDownMenuItem);
		operMenu.addSeparator();
		operMenu.add(loginRelated);
		
		/**
		 * 创建二级 配置 子菜单
		 */
		JMenu dTypeMenuItem = new JMenu("下载策略");
		JMenu dQNMenuItem = new JMenu("优先清晰度");
		JMenuItem settingsMenuItem = new JMenuItem("打开配置页");
		configMenu.add(dTypeMenuItem);
		configMenu.add(dQNMenuItem);
		configMenu.addSeparator();
		configMenu.add(settingsMenuItem);
		/**
		 * 创建二级 关于 子菜单
		 */
		JMenuItem infoMenuItem = new JMenuItem("作品信息");
		JMenuItem updateMenuItem = new JMenuItem("检查更新");
		aboutMenu.add(infoMenuItem);
		aboutMenu.add(updateMenuItem);

		/**
		 * 创建三级 配置-下载策略
		 */
		final JRadioButtonMenuItem dType01 = new JRadioButtonMenuItem("仅第一");
		final JRadioButtonMenuItem dType02 = new JRadioButtonMenuItem("全部");
		btnTypeGroup = new ButtonGroup();
		btnTypeGroup.add(dType01);
		btnTypeGroup.add(dType02);
		dTypeMenuItem.add(dType01);
		dTypeMenuItem.add(dType02);
		dType01.setSelected(true);

		/**
		 * 创建三级 配置-优先清晰度
		 */
		btnQnGroup = new ButtonGroup();
		for (VideoQualityEnum item : VideoQualityEnum.values()) {
			final JRadioButtonMenuItem dQN = new JRadioButtonMenuItem(item.getQuality());
			dQNMenuItem.add(dQN);
			btnQnGroup.add(dQN);
			if (item.getQn() == 80) {
				dQN.setSelected(true);
			}
		}

		// 打开设置面板
		settingsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabSettings.openSettingTab();
			}
		});
		// 将repo中avid转换为bvid
		convertRepo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RepoUtil.convert();
			}
		});
		// 暂停将repo中avid转换为bvid
		convertRepoBreak.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RepoUtil.stopConvert();
			}
		});
		
		// 修改app.config后，重新加载配置使生效
		reloadConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigUtil.initConfigs();
			}
		});
		
		// 修改repo.config后，重新加载仓库使生效
		reloadRepo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RepoUtil.init(true);
			}
		});
		
		// 保存下载页的所有任务
		saveDownloading.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File dir = ResourcesUtil.search("config");
				if(dir == null) {
					dir = new File("config");
					dir.mkdirs();
				}
				File downloadingTasks = new File(dir, "tasks.config");
				// \r\n##\r\n 分隔每个任务
				// \r\n@@\r\n 分隔 ClipInfo属性和 Qn
				final String taskSep = "\r\n##\r\n";
				final String attrSep = "\r\n@@\r\n";
				try(BufferedWriter writer = new BufferedWriter(new FileWriter(downloadingTasks))){
					for(DownloadInfoPanel dp : Global.downloadTaskList.keySet()) {
						ClipInfo c = dp.getClipInfo();
						writer.append(c.getAvTitle());
						writer.append(attrSep);
						writer.append(Long.toString(c.getcId()));
						writer.append(attrSep);
						writer.append(c.getAvId());
						writer.append(attrSep);
						writer.append(Integer.toString(c.getPage()));
						writer.append(attrSep);
						writer.append(c.getTitle());
						writer.append(attrSep);
						writer.append(c.getListName());
						writer.append(attrSep);
						writer.append(c.getListOwnerName());
						writer.append(attrSep);
						writer.append(Long.toString(c.getFavTime()));
						writer.append(attrSep);
						writer.append(Long.toString(c.getcTime()));
						writer.append(attrSep);
						writer.append(c.getUpName());
						writer.append(attrSep);
						writer.append(c.getUpId());
						writer.append(attrSep);
						writer.append(Integer.toString(c.getRemark()));
						writer.append(attrSep);
						writer.append(Integer.toString(dp.getQn()));
						
						writer.append(taskSep);
						writer.flush();
					}
				}catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		// 加载保存的任务到下载页
		loadDownloading.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File downloadingTasks = ResourcesUtil.search("config/tasks.config");
				// \r\n##\r\n 分隔每个任务
				// \r\n@@\r\n 分隔 ClipInfo属性和 Qn
				final String taskSep = "\r\n##\r\n";
				final String attrSep = "\r\n@@\r\n";
				try(BufferedReader reader = new BufferedReader(new FileReader(downloadingTasks))){
					String line;
					StringBuilder result = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						result.append(line).append("\r\n");
					}
					String[] tasks = result.toString().split(taskSep);
					for(String task : tasks) {
						String[] attrs = task.split(attrSep);
						ClipInfo c = new ClipInfo();
						c.setAvTitle(attrs[0]);
						c.setcId(Long.parseLong(attrs[1]));
						c.setAvId(attrs[2]);
						c.setPage(Integer.parseInt(attrs[3]));
						c.setTitle(attrs[4]);
						// null判断
						if(!"null".equals(attrs[5]))
							c.setListName(attrs[5]);
						if(!"null".equals(attrs[6]))
							c.setListOwnerName(attrs[6]);
						c.setFavTime(Long.parseLong(attrs[7]));
						c.setcTime(Long.parseLong(attrs[8]));
						
						c.setUpName(attrs[9]);
						c.setUpId(attrs[10]);
						c.setRemark(Integer.parseInt(attrs[11]));
						
						int qn = Integer.parseInt(attrs[12]);
						Logger.println(c.toString());
						Logger.println(qn);
						DownloadRunnable downThread = new DownloadRunnable(null, c, qn);
						Global.queryThreadPool.execute(downThread);
					}
				}catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// 关闭Tab页
		closeAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.index.closeAllVideoTabs();
			}
		});

		// 批量下载
		doMultiDownMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean downAll = dType02.isSelected();
				Enumeration<AbstractButton> btns = btnQnGroup.getElements();
				while (btns.hasMoreElements()) {
					JRadioButtonMenuItem item = (JRadioButtonMenuItem) btns.nextElement();
					
					Logger.println(item.isSelected());
					if(item.isSelected()) {
						Logger.println(item.getText());
						int qn = VideoQualityEnum.getQN(item.getText());
						Global.index.downVideoTabs(downAll, qn);
						break;
					}
				}
			}
		});
		
		// 各种登录
		qrLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.loginType = "qr";
				new LoginThread().start();
			}
		});
		pwdLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.loginType = "pwd";
				new LoginThread().start();
			}
		});
		smsLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.loginType = "sms";
				new LoginThread().start();
			}
		});
		// 退出登录
		logout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Global.downloadTab.activeTask > 0) {
					JOptionPane.showMessageDialog(null, "当前仍然存在下载任务！", "请注意!!",
							JOptionPane.WARNING_MESSAGE);
				}else if(!Global.isLogin){
					JOptionPane.showMessageDialog(null, "当前没有登录！", "请注意!!",
							JOptionPane.WARNING_MESSAGE);
				}else {
					API.logout();
					new File("./config/cookies.config").delete();
					// 置空全局cookie
					HttpCookies.setGlobalCookies(null);
					// 更改登录状态
					Global.isLogin = false;
					// 初始化登录图标
					Global.index.jlHeader.setToolTipText("点击登录");
					Global.index.jlHeader.setIcon(Global.index.imgIconHeaderDefault);
					// 初始化收藏夹
					Global.index.cmbFavList.removeAllItems();
					Global.index.cmbFavList.addItem("---我的收藏夹---");
				}
			}
		});
		
		// 作品信息
		infoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameAbout.showAbout();
			}
		});
		// 更新版本
		updateMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						frame.setTitle(frame.getTitle() + " 版本更新中");
						try {
							if (VersionManagerUtil.queryLatestVersion()) {
								JOptionPane.showMessageDialog(null, "当前版本为 " + Global.version + " ，已是最新", "成功",
										JOptionPane.PLAIN_MESSAGE);
							} else {
								VersionManagerUtil.downloadLatestVersion();

							}
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "出现了异常，异常原因为：" + e1.toString(), "异常",
									JOptionPane.PLAIN_MESSAGE);
						}
						frame.setTitle(frame.getTitle().replace(" 版本更新中", ""));
					}
				}, "更新线程").start();
			}
		});

	}
}
