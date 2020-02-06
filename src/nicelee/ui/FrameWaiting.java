package nicelee.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JWindow;

public class FrameWaiting extends JWindow implements Runnable, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5501092942908580975L;

	int maxWidth = 50;
	int width = 0;
	boolean beBigger = true;
	Thread thDisplay;

	Point pressedPoint = null; // 记录鼠标按下时的位置
	long lastTimeClicked = 0; // 上一次鼠标点击的时间
	
	public FrameWaiting() {
		init();
		this.setVisible(true);
		thDisplay = new Thread(this, "Thread-Waiting-for-Main");
	}

	public void init() {
		this.setSize(maxWidth, maxWidth);
		// this.setUndecorated(true);
		// this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().setBackground(Color.BLACK);
		this.setAlwaysOnTop(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.WHITE);
		// g.fill3DRect((maxWidth - width)/2,(maxWidth - width)/2, width, width, true);
		g.fillRect((maxWidth - width) / 2, (maxWidth - width) / 2, width, width);
	}

	public void start() {
		thDisplay.start();
	}

	public void stop() {
		try {
			thDisplay.interrupt();
		} catch (Exception e) {
		}
	}

	public void animate() {
		try {
			while (true) {
				if (beBigger) {
					if (width > maxWidth) {
						beBigger = false;
						continue;
					}
					this.repaint();
					width += 4;
				} else {
					if (width < 0) {
						beBigger = true;
						continue;
					}
					this.repaint();
					width -= 4;
				}
				Thread.sleep(150);
			}
		} catch (InterruptedException e) {
			this.dispose();
		}

	}

	public static void main(String[] args) {
		FrameWaiting p = new FrameWaiting();
		p.start();
	}

	@Override
	public void run() {
		this.animate();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// 实现拖拽
		if (pressedPoint != null) {
			Point locationPoint = this.getLocation();
			Point point = e.getPoint();
			int x = locationPoint.x + point.x - pressedPoint.x;
			int y = locationPoint.y + point.y - pressedPoint.y;
			this.setLocation(x, y);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastTimeClicked < 500) {
			this.dispose();
		}
		lastTimeClicked = currentTime;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressedPoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressedPoint = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
