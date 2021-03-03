package nicelee.ui.item;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import nicelee.ui.TabVideo;

// 关闭所有Tab
// 关闭左侧Tab
// 关闭右侧Tab
public class MJTabVideo extends TabVideo implements MouseListener, ActionListener {

	private static final long serialVersionUID = 10112L;

	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem closeAll = null, closeRight = null, closeLeft = null, closeThis = null; // 功能菜单
	private JLabel label = null;
	private JTabbedPane jTabbedpane = null;

	public MJTabVideo(JTabbedPane jTabbedpane, JLabel label) {
		super(label);
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
		closeThis.addActionListener(this);
		closeAll.addActionListener(this);
		closeRight.addActionListener(this);
		closeLeft.addActionListener(this);
		label.add(pop);
		label.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == label) {
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
		}else {
			super.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == label) {
			if(e.getButton() == MouseEvent.BUTTON3)
			pop.show(label, e.getX(), e.getY());
		}else {
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
		}else {
			super.actionPerformed(e);
		}
	}

}