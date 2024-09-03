package nicelee.ui.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import nicelee.ui.item.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import nicelee.bilibili.util.Logger;
import nicelee.ui.FrameMain;
import nicelee.ui.Global;
import nicelee.ui.SysTray;


public class MJTitleBar extends JPanel  implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 7910189206713741389L;
	private JFrame frame;
	private JButton btnMin;
	private JButton btnClose;
	private Point pressedPoint;//记录鼠标按下时的位置
	private boolean setTitle;
	private boolean setMenuBar;
	private JLabel title;
	
	public MJTitleBar(JFrame frame) {
		this(frame, false, false);
	}
	
	public MJTitleBar(JFrame frame, boolean setTitle) {
		this(frame, setTitle, false);
	}
	
	public MJTitleBar(JFrame frame, boolean setTitle, boolean setMenuBar) {
		super();
		this.frame = frame;
		this.setTitle = setTitle;
		this.setMenuBar = setMenuBar;
		init();
	}
	
	private void init() {
		// frame 去掉标题栏
		frame.setUndecorated(true);
		// 根据父frame的宽度，设置大小
		this.setPreferredSize(new Dimension(frame.getWidth() -4, 25));
		// 设置layout
		int vgap = setMenuBar ? -3 : 3;
		Logger.println(vgap);
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, vgap));
		this.setOpaque(false);
		this.setBackground(Color.WHITE);
		
		if(setTitle) {
			initTitle();
		}
		if(setMenuBar) {
			initMenuBar();
		}
		initButton();
                    
	}

	/**
	 * 初始化标题Icon和文字
	 */
	private void initTitle() {
		// 添加icon
		JLabel icon = null;
		if(frame.getIconImage() != null) {
			Logger.println("不为null");
			ImageIcon iconImg = new ImageIcon(frame.getIconImage().getScaledInstance(25, 23, Image.SCALE_DEFAULT));
			icon = new JLabel(iconImg);
			this.add(icon);
		}
		// 添加title
		title = new JLabel();
		title.setText(frame.getTitle());
		if(setMenuBar) {
			//title.setPreferredSize(null);
			title.setPreferredSize(new Dimension(350, 25));
			this.add(title);
			JLabel blank = new JLabel();
			blank.setPreferredSize(new Dimension(542, 25));
			//blank.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.DARK_GRAY));
			this.add(blank);
		}else {
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setPreferredSize(new Dimension(frame.getWidth() - 60, 25));
			Font oldFont = title.getFont();
			title.setFont(new Font(oldFont.getName(), oldFont.getStyle(), 18));
			this.add(title);
		}
	}
	
	/**
	 * 初始化菜单栏
	 */
	private void initMenuBar() {
		MJMenuBar menu = new MJMenuBar(frame);
		menu.setPreferredSize(new Dimension(150, 25));
		//menu.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.DARK_GRAY));
		this.add(menu);
		// 占位,调整与最小化按钮的间隙
		JLabel blank = new JLabel();
		blank.setPreferredSize(new Dimension(50, 25));
		this.add(blank);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
	}
	/**
	 * 初始化最小化、关闭按钮
	 */
	private void initButton() {
		// 添加最小化按钮
		btnMin = new JButton();
        btnMin.setRolloverIcon(new ImageIcon(this.getClass().getResource("/resources/_h.jpg")));
        btnMin.setFocusPainted(false);
        btnMin.setBorderPainted(false);
        btnMin.setContentAreaFilled(false);
        ImageIcon img_ = new ImageIcon(this.getClass().getResource("/resources/_.jpg"));
        btnMin.setIcon(img_);
        btnMin.setPreferredSize(new Dimension(20, 20));
        this.add(btnMin);
		// 添加关闭按钮
        btnClose = new JButton();
        btnClose.setRolloverIcon(new ImageIcon(this.getClass().getResource("/resources/xh.jpg")));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        ImageIcon imgx = new ImageIcon(this.getClass().getResource("/resources/x.jpg"));
        btnClose.setIcon(imgx);
        btnClose.setPreferredSize(new Dimension(20, 20));
        //Logger.println(imgx.getIconWidth());
        this.add(btnClose);
        //Logger.println("标题栏");
		// 实现功能 - 拖拽
        // 实现功能 - 最小化
        // 实现功能 - 最大化
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
        btnMin.addMouseListener(this);
        btnClose.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == btnClose) {
			if(frame instanceof FrameMain && SysTray.isSysTrayInitiated() && Global.closeToSystray) {
				Logger.println("最小化到系统托盘");
				frame.setExtendedState(JFrame.ICONIFIED);
				frame.setVisible(false);
			}else {
				// 如果是用户想要关闭程序，先判断是否仍然有活动的任务
				if(frame instanceof FrameMain && Global.downloadTab.activeTask > 0) {
					Object[] options = { "我要退出", "我再想想" };
					String msg = String.format("当前仍有 %d 个任务在下载/转码，正在转码的文件退出后可能丢失或异常，确定要退出吗？", Global.downloadTab.activeTask);
					int m = JOptionPane.showOptionDialog(null, msg, "警告", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);
					Logger.println(m);
					if(m != 0) 	return;
				}
				Logger.println("closing...");
				WindowEvent event=new WindowEvent(frame,WindowEvent.WINDOW_CLOSING);
				frame.dispatchEvent(event);
			}
			
		}else if(e.getSource() == btnMin){
			frame.setExtendedState(JFrame.ICONIFIED);
			if (frame instanceof FrameMain && SysTray.isSysTrayInitiated() && Global.minimizeToSystray) {
				Logger.println("最小化到系统托盘");
				frame.setVisible(false);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == this) {
			pressedPoint = SwingUtilities.convertPoint(this, e.getPoint(), frame);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getSource() == this) {
			pressedPoint = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//实现拖拽
		if(pressedPoint != null &&e.getSource() != btnClose && e.getSource() != btnMin) {
			int newX = e.getXOnScreen() - pressedPoint.x;
			int newY = e.getYOnScreen() - pressedPoint.y;
			if( newX == frame.getX() && newY == frame.getY() )
				return;
			frame.setLocation(newX, newY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}
}
