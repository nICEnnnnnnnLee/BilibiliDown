package nicelee.ui.item;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;
import nicelee.ui.TabVideo;
import nicelee.ui.thread.GetVideoDetailThread;

// 关闭当前Tab
// 关闭所有Tab
// 关闭左侧Tab
// 关闭右侧Tab
//---------------
// 下载当前Tab
// 下载全部Tab
public class MJTabVideo extends TabVideo {// implements MouseListener, ActionListener

	private static final long serialVersionUID = 10112L;

	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem closeAll = null, closeRight = null, closeLeft = null, closeThis = null; // 功能菜单
	private JMenuItem downloadAll = null, downloadThis = null;// 功能菜单
	private JLabel label = null;
	private JTabbedPane jTabbedpane = null;
	private String searchContent; // 用于保存当前页面的查询内容
	
	public MJTabVideo(JTabbedPane jTabbedpane, JLabel label, String searchContent) {
		super(label);
		this.searchContent = searchContent;
		this.jTabbedpane = jTabbedpane;
		this.label = label;
		this.initial();
	}

	public void initial() {
		pop = new JPopupMenu();
		pop.add(closeThis = new JMenuItem("关闭此标签"));
		pop.add(closeAll = new JMenuItem("关闭所有标签"));
		pop.add(closeLeft = new JMenuItem("关闭左侧标签"));
		pop.add(closeRight = new JMenuItem("关闭右侧标签"));
		pop.addSeparator();
		pop.add(downloadThis = new JMenuItem("批量下载此标签"));
		pop.add(downloadAll = new JMenuItem("批量下载全部标签"));
		closeThis.addActionListener(this);
		closeAll.addActionListener(this);
		closeRight.addActionListener(this);
		closeLeft.addActionListener(this);
		downloadThis.addActionListener(this);
		downloadAll.addActionListener(this);
		label.add(pop);
		label.addMouseListener(this);
		
		btnNextPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Pattern paramPattern = Pattern.compile("(.*)p=([0-9]+)$");
				int page = 1;
				String modifiedSearchContent = null;
				Matcher matcher = paramPattern.matcher(searchContent);
				if(matcher.find()) {
					page = Integer.parseInt(matcher.group(2));
					modifiedSearchContent = matcher.group(1) + "p=" + (page+1);
				}else {
					modifiedSearchContent = searchContent + " p=" + (page+1);
				}
				Logger.println(modifiedSearchContent);
				JLabel label = new JLabel("正在加载中...");
				final TabVideo tab = new MJTabVideo(jTabbedpane, label, modifiedSearchContent);
				jTabbedpane.addTab("作品页", tab);
				jTabbedpane.setTabComponentAt(jTabbedpane.indexOfComponent(tab), label);
				GetVideoDetailThread th = new GetVideoDetailThread(tab, modifiedSearchContent);
				th.start();
				jTabbedpane.setSelectedComponent(tab);
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == label) {
			System.out.println("MJTab label的点击事件");
			if (e.getButton() == MouseEvent.BUTTON1) {
				// 鼠标左键双击事件， 删除此tab页
				if (e.getClickCount() >= 2) {
					jTabbedpane.remove(this);
				} else {
					// 鼠标左键单击事件， 切换到此tab页
					jTabbedpane.setSelectedComponent(this);
				}
			}
		} else {
			super.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == label) {
			if (e.getButton() == MouseEvent.BUTTON3)
				pop.show(label, e.getX(), e.getY());
		} else {
			super.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() != label) {
			super.mouseReleased(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() != label) {
			super.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() != label) {
			super.mouseExited(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeThis) {
			jTabbedpane.remove(this);
		} else if (e.getSource() == closeAll) {
			for (int i = jTabbedpane.getTabCount() - 1; i >= 2; i--) {
				jTabbedpane.removeTabAt(i);
			}
		} else if (e.getSource() == closeLeft) {
			int currentTabIndex = jTabbedpane.indexOfComponent(this);
			for (int i = 0; i < currentTabIndex - 2; i++) {
				jTabbedpane.removeTabAt(2);
			}
		} else if (e.getSource() == closeRight) {
			int currentTabIndex = jTabbedpane.indexOfComponent(this);
			for (int i = jTabbedpane.getTabCount() - 1; i > currentTabIndex; i--) {
				jTabbedpane.removeTabAt(i);
			}
		} else if (e.getSource() == downloadThis) {
			int qn = VideoQualityEnum.getQN(Global.tab_qn);
			this.download(true, qn);
		} else if (e.getSource() == downloadAll) {
			int qn = VideoQualityEnum.getQN(Global.tab_qn);
			Global.index.downVideoTabs(true, qn);
		} else {
			super.actionPerformed(e);
		}
	}

}