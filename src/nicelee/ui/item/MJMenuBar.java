package nicelee.ui.item;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import nicelee.ui.item.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import nicelee.bilibili.API;
import nicelee.bilibili.INeedAV;
import nicelee.bilibili.enums.DownloadModeEnum;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.VersionManagerUtil;
import nicelee.ui.FrameAbout;
import nicelee.ui.Global;
import nicelee.ui.TabSettings;
import nicelee.ui.thread.BatchDownloadRbyRThread;
import nicelee.ui.thread.BatchDownloadThread;
import nicelee.ui.thread.CookieRefreshThread;
import nicelee.ui.thread.DownloadRunnable;
import nicelee.ui.thread.LoginThread;

public class MJMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -344077300590858072L;

	private JFrame frame;
	// int tabDownloadType; 	// 保存 从菜单栏批量下载的计划类型
	String qnQualityPri;	// 保存 从菜单栏批量下载的优先清晰度选项
	String batchDownloadFileName; // 保存 从菜单栏一键下载的配置文件选项
	
	public MJMenuBar(JFrame frame) {
		super();
		this.frame = frame;
		init();
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
		JMenuItem batchDownload = new JMenuItem("一键下载");
		JMenuItem batchDownloadRbyR = new JMenuItem("按计划周期下载");
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
		JMenuItem refreshCookie = new JMenuItem("刷新Cookie");
		loginRelated.add(qrLogin);
		loginRelated.add(pwdLogin);
		loginRelated.add(smsLogin);
		loginRelated.addSeparator();
		loginRelated.add(refreshCookie);
		loginRelated.addSeparator();
		loginRelated.add(logout);
		
		operMenu.add(batchDownload);
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
		operMenu.addSeparator();
		operMenu.add(batchDownloadRbyR);
		
		
		/**
		 * 创建二级 配置 子菜单
		 */
//		JMenu dTypeMenuItem = new MJMenuWithRadioGroupBuilder("下载策略", "仅第一", "全部") {
//			@Override
//			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
//				tabDownloadType = itemIndex;
//			}
//			
//			@Override
//			public void init(JRadioButtonMenuItem[] menuItems) {
//				menuItems[Global.menu_plan].setSelected(true);
//			}
//		}.build();
		
		JMenu dUseRepoMenuItem = new MJMenuWithRadioGroupBuilder("下载前先查询记录?", "查询", "不查询") {
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.useRepo = itemIndex == 0;
				Logger.println("仓库功能开启:" + Global.useRepo);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				int itemIndex = Global.useRepo ? 0 : 1;
				menuItems[itemIndex].setSelected(true);
			}
		}.build();
		
		JMenu dDashDownTypeMenuItem = new MJMenuWithRadioGroupBuilder("下载模式(DASH)", "全部", "仅视频","仅音频") {
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.downloadMode = DownloadModeEnum.getModeEnum(itemIndex);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				menuItems[Global.downloadMode.getMode()].setSelected(true);
			}
		}.build();
		JMenu dTypeReDownloadMenuItem = new MJMenuWithRadioGroupBuilder("下载重试策略", "使用之前的url", "重新查询url") {
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				if (Global.reloadDownloadUrl)
					menuItems[1].setSelected(true);
				else
					menuItems[0].setSelected(true);
			}

			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				if(itemIndex == 0) {
					Logger.println("重试下载时使用之前的url");
					Global.reloadDownloadUrl = false;
				}else {
					Logger.println("重试下载时重新查询url");
					Global.reloadDownloadUrl = true;
				}
			}
		}.build();
		JMenu dForceReplaceHostMenuItem = new MJMenuWithRadioGroupBuilder("音视频链接替换host?", "替换", "不替换") {
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.forceReplaceUposHost = itemIndex == 0;
				Logger.println("音视频链接强制替换host:" + Global.forceReplaceUposHost);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				int itemIndex = Global.forceReplaceUposHost ? 0 : 1;
				menuItems[itemIndex].setSelected(true);
			}
		}.build();
		
		List<String> qnSelections = new ArrayList<>();
		for (VideoQualityEnum item : VideoQualityEnum.values()) {
			qnSelections.add(item.getQuality());
		}
		JMenu dQNMenuItem = new MJMenuWithRadioGroupBuilder("优先清晰度", qnSelections) {
			
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				qnQualityPri = item.getText();
				Global.menu_qn = qnQualityPri;
				Logger.println("优先清晰度(菜单)为: " + qnQualityPri);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				for(JRadioButtonMenuItem item: menuItems) {
					if(item.getText().equals(Global.menu_qn)) {
						item.setSelected(true);
					}
				}
			}
		}.build();
		
		String[] straOptions = { "tryNormalTypeFirst", "judgeTypeFirst", "returnFixedValue" };
		String[] straOptionTips = { "先尝试普通类型，报错再尝试其它", "先判断类型再查询", "返回固定值" };
		JMenu dQNQueryStrategyMenuItem = new MJMenuWithRadioGroupBuilder("可用清晰度查询策略", straOptionTips) {
			
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.infoQueryStrategy = straOptions[itemIndex];
				Logger.println("可用清晰度查询策略为: " + straOptionTips[itemIndex]);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				for(int i = 0; i < menuItems.length; i++) {
					if(straOptions[i].equals(Global.infoQueryStrategy)) {
						menuItems[i].setSelected(true);
					}
				}
			}
		}.build();
		
		File configDir = new File(ResourcesUtil.baseDirectory(), "config");
		List<String> configFiles = new ArrayList<>();
		configFiles.add(Global.batchDownloadConfigName);
		if(configDir.exists()) {
			String[] list = configDir.list();
			if(list != null) {
				for(String fName: list) {
					Matcher m = Global.batchDownloadConfigNamePattern.matcher(fName);
					if(m.find() && !fName.equals(Global.batchDownloadConfigName)) {
						Logger.println(fName);
						configFiles.add(fName);
					}
				}
			}
		}
		JMenu dBatchDownMenuItem = new MJMenuWithRadioGroupBuilder("一键下载配置", configFiles) {
			
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				batchDownloadFileName = item.getText();
				Logger.println("一键下载配置: " + batchDownloadFileName);
			}
			
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				menuItems[0].setSelected(true);
			}
		}.build();
		JMenu dUpdateMenuItem = new MJMenuWithRadioGroupBuilder("更新源选择", Global.updateSourceAvailable.split("\\|")) {
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.updateSourceActive = item.getText();
				Logger.println("当前使用的更新源切换为：" + Global.updateSourceActive);
				
			}
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				for(JRadioButtonMenuItem item: menuItems) {
					if(item.getText().equals(Global.updateSourceActive)) {
						item.setSelected(true);
					}
				}
			}
		}.build();
		JMenu dFFmpegMenuItem = new MJMenuWithRadioGroupBuilder("FFMPEG源选择", Global.ffmpegSourceAvailable.split("\\|")) {
			@Override
			public void onItemSelected(int itemIndex, JRadioButtonMenuItem item) {
				Global.ffmpegSourceActive = item.getText();
				Logger.println("当前使用的ffmpeg源切换为：" + Global.ffmpegSourceActive);
			}
			@Override
			public void init(JRadioButtonMenuItem[] menuItems) {
				for(JRadioButtonMenuItem item: menuItems) {
					if(item.getText().equals(Global.ffmpegSourceActive)) {
						item.setSelected(true);
					}
				}
			}
		}.build();
		JMenuItem settingsMenuItem = new JMenuItem("打开配置页");
//		configMenu.add(dTypeMenuItem);
		configMenu.add(dUseRepoMenuItem);
		configMenu.add(dDashDownTypeMenuItem);
		configMenu.add(dTypeReDownloadMenuItem);
		configMenu.add(dForceReplaceHostMenuItem);
		configMenu.add(dQNMenuItem);
		configMenu.add(dQNQueryStrategyMenuItem);
		configMenu.add(dBatchDownMenuItem);
		configMenu.add(dUpdateMenuItem);
		configMenu.add(dFFmpegMenuItem);
		configMenu.addSeparator();
		configMenu.add(settingsMenuItem);
		/**
		 * 创建二级 关于 子菜单
		 */
		JMenuItem infoMenuItem = new JMenuItem("作品信息");
		JMenuItem updateMenuItem = new JMenuItem("检查更新");
		aboutMenu.add(infoMenuItem);
		aboutMenu.add(updateMenuItem);
		if(Global.githubToken != null && !Global.githubToken.isEmpty()) {
			JMenuItem updateBetaMenuItem = new JMenuItem("更新Beta版本");
			aboutMenu.add(updateBetaMenuItem);
			updateBetaMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					VideoInfo avInfo = new INeedAV().getVideoDetail("BilibiliDown.PreRelease", 0, false);
					DownloadRunnable downThread = new DownloadRunnable(avInfo, avInfo.getClips().get(1234L), 0);
					Global.queryThreadPool.execute(downThread);
				}
			});
		}
		// 一键下载
		batchDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new BatchDownloadThread(batchDownloadFileName).start();
			}
		});
		// 按计划一键下载
		batchDownloadRbyR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "我要继续", "我再想想" };
				String msg = "程序将会周期性的执行一键下载，是否继续";
				int m = JOptionPane.showOptionDialog(null, msg, "警告", JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				Logger.println(m);
				if (m == 0){
					new BatchDownloadRbyRThread(batchDownloadFileName).start();
					batchDownloadRbyR.setEnabled(false);
				}
			}
		});
		batchDownloadRbyR.setEnabled(!Global.batchDownloadRbyRRunOnStartup);
		// 打开设置面板
		settingsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabSettings.openSettingTab();
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
				File dir = new File(ResourcesUtil.baseDirectory(), "config");
				if(!dir.exists()) {
					dir.mkdirs();
				}
				File downloadingTasks = new File(dir, "tasks.config");
				// \r\n##\r\n 分隔每个任务
				// \r\n@@\r\n 分隔 ClipInfo属性和 Qn
				final String taskSep = "\r\n##\r\n";
				final String attrSep = "\r\n@@\r\n";
				try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(downloadingTasks), "utf-8"))){
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
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(downloadingTasks), "utf-8"))){
					String line;
					StringBuilder result = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						result.append(line).append("\r\n");
					}
					String[] tasks = result.toString().split(taskSep);
					for(String task : tasks) {
						if(task.isEmpty())
							continue;
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
				} catch (NullPointerException e0) {
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// 关闭Tab页
		closeAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Global.promptBeforeCloseAllTabs) {
					Object[] options = { "是", "否" };
					int m = JOptionPane.showOptionDialog(null, 
							"关闭所有Tab页面?\n\nps:想要取消此确认框，可在配置页搜索关键词\n【menu.tab】 或 【promptBeforeCloseAllTabs】 或 【弹出确认框】", 
							"请确认", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (m == 0) {
						Global.index.closeAllVideoTabs();
					}
				}else
					Global.index.closeAllVideoTabs();
			}
		});

		// 批量下载
		doMultiDownMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean downAll = true; // tabDownloadType != 0;
				Logger.println(qnQualityPri);
				int qn = VideoQualityEnum.getQN(qnQualityPri);
				Global.index.downVideoTabs(downAll, qn);
			}
		});
		
		// 各种登录
		qrLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.needToLogin = true;
				Global.loginType = "qr";
				new LoginThread().start();
			}
		});
		pwdLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.needToLogin = true;
				Global.loginType = "pwd";
				new LoginThread().start();
			}
		});
		smsLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.needToLogin = true;
				Global.loginType = "sms";
				new LoginThread().start();
			}
		});
		refreshCookie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CookieRefreshThread.newInstance().start();
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
					ResourcesUtil.sourceOf("./config/cookies.config").delete();
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
