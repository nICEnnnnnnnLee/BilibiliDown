package nicelee.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import nicelee.ui.item.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.json.JSONObject;

import javax.swing.JDialog;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.util.Logger;
import nicelee.server.core.SocketServer;

public class DialogLogin extends JDialog implements FocusListener, MouseListener, MouseMotionListener {

	public static DialogLogin Instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3741671572332334943L;

	public static void main(String[] args) {
		try {
//			System.setProperty("proxyHost", "127.0.0.1");
//			System.setProperty("proxyPort", "8888");
//			HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllCertSSLUtil.getFactory());
			DialogLogin dialog = new DialogLogin(new INeedLogin());
			dialog.init();
			Logger.println("-----------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final static String tips = "请输入手机号/邮箱";
	final static Color colorLostFocus = Color.getHSBColor(100, 100, 100);
	final static Color colorGainFocus = Color.white;

	private Point pressedPoint;// 记录鼠标按下时的位置
	private JButton btnClose;

	INeedLogin inl;
	JTextField jtUserName = new JTextField(tips);
	JPasswordField jtPassword = new JPasswordField("123456");
	JLabel lbLogin = new JLabel("点击过极验验证码");
	JLabel lbTips = new JLabel("");
	boolean isRefreshingCaptcha = false;
	boolean isLogging = false;

	SocketServer socketServer;

	public DialogLogin(INeedLogin inl) {
		this.inl = inl;
	}

	public void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				socketServer = new SocketServer(Global.serverPort);
				socketServer.startServer();
			}
		}, "密码登录server").start();
		Instance = this;
		setAlwaysOnTop(true);
		setResizable(false);
		setSize(522, 300);
		setUndecorated(true);
		this.setLocationRelativeTo(null);
		// setBounds(400, 100, DIALOG_WIDTH,DIALOG_HEIGHT);
		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		// panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
		panel.setBackground(Color.WHITE);

		initSysButton(panel);
		ImageIcon icon = new ImageIcon(DialogLogin.class.getResource("/resources/banner.jpg"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(520, 110, Image.SCALE_SMOOTH));
		JLabel label = new JLabel(icon);
		label.setIcon(icon);
		label.setBounds(1, 1, 520, 90);
		panel.add(label);

		Font font = new Font(Font.SERIF, Font.PLAIN, 18); //"system"
		Insets insets = new Insets(2, 10, 2, 0);
		jtUserName.setBounds(50, 80, 420, 40);
		jtPassword.setBounds(50, 130, 420, 40);
		jtUserName.setBackground(colorLostFocus);
		jtPassword.setBackground(colorLostFocus);
		jtUserName.setMargin(insets);
		jtPassword.setMargin(insets);
		jtUserName.setFont(font);
		jtPassword.setFont(font);

		lbLogin.setBounds(50, 200, 420, 40);
		lbLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lbLogin.setBackground(Color.LIGHT_GRAY);
		// lbLogin.setForeground(Color.BLUE);
		lbLogin.setOpaque(true);
		lbLogin.setFont(font);
		// lbLogin.setBorder(BorderFactory.createLineBorder(Color.red));

		lbTips.setBounds(50, 250, 420, 40);
		lbTips.setHorizontalAlignment(SwingConstants.CENTER);
		lbTips.setForeground(Color.RED);
		// lbTips.setFont(font);
		panel.add(jtUserName);
		panel.add(jtPassword);
		panel.add(lbLogin);
		panel.add(lbTips);
		for(Component com: panel.getComponents()) {
			bindKeyEnterEvent((JComponent)com);
		}

		jtUserName.addFocusListener(this);
		jtPassword.addFocusListener(this);
		lbLogin.addMouseListener(this);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		// 根据参数初始化
		if (Global.userName != null) {
			jtUserName.setText(Global.userName);
		}
		if (Global.password != null) {
			jtPassword.setText(Global.password);
		}
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				lbLogin.requestFocus();
			}
		});

		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				new Thread(new Runnable() {
					@Override
					public void run() {
						Logger.println("socketServer 关闭中...");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
						if (socketServer != null)
							socketServer.stopServer();
					}
				}, "Thread to shutdown server").start();
			}
		});

		this.setModal(true);
		this.setVisible(true);
	}

	/**
	 * 初始化关闭按钮
	 */
	private void initSysButton(JPanel panel) {
		// 添加关闭按钮
		btnClose = new JButton();
		btnClose.setRolloverIcon(new ImageIcon(this.getClass().getResource("/resources/xh.jpg")));
		btnClose.setFocusPainted(false);
		btnClose.setBorderPainted(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setOpaque(true);
		ImageIcon imgx = new ImageIcon(this.getClass().getResource("/resources/x.jpg"));
		btnClose.setIcon(imgx);
		btnClose.setBounds(490, 5, 20, 20);
		panel.add(btnClose);
		// 实现功能 - 最大化
		btnClose.addMouseListener(this);
	}

	/**
	 * 刷新验证码
	 */
	private void queryCaptcha() {
		Logger.println("刷新验证码");
		isRefreshingCaptcha = true;
		lbTips.setText("");
		if (jtUserName.hasFocus()) {
			Global.userName = jtUserName.getText();
		} else if (jtPassword.hasFocus()) {
			Global.password = String.valueOf(jtPassword.getPassword());
		}
		if (Global.userName == null || Global.password == null || Global.userName.isEmpty()
				|| Global.password.isEmpty()) {
			lbTips.setText("输入不能为空！");
		} else {
			try {
				JSONObject geetest = inl.getGeetest();
				String token = geetest.getString("token");
				String gt = geetest.getJSONObject("geetest").getString("gt");
				String challenge = geetest.getJSONObject("geetest").getString("challenge");
				String url = String.format("http://localhost:%d/static/index.html?token=%s&gt=%s&challenge=%s", Global.serverPort,
						token, gt, challenge);
				final boolean browseSupported = Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
				if(browseSupported)
					Desktop.getDesktop().browse(new URI(url));
				else {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable trans = new StringSelection(url);
					clipboard.setContents(trans, null);
					JOptionPane.showMessageDialog(this, "请通过浏览器访问以下网址(已复制到剪贴板):\n" + url, "请注意", JOptionPane.WARNING_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		isRefreshingCaptcha = false;
	}

	/**
	 * 登录
	 */
	public String login(String token, String challenge, String validate, String seccode) {
		lbTips.setText("");
		isLogging = true;
		String tips = inl.login(Global.userName, Global.password, token, challenge, validate, seccode);
		if (tips == null) {
			lbTips.setText("登录成功");
			Global.isLogin = true;
			WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
			this.dispatchEvent(event);
		} else {
			lbTips.setText(tips);
		}
		isLogging = false;
		return tips;
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == jtUserName) {
			if (Global.userName != null) {
				jtUserName.setText(Global.userName);
			} else {
				jtUserName.setText("");
			}
		} else if (e.getSource() == jtPassword) {
			if (Global.password != null) {
				jtPassword.setText(Global.password);
			} else {
				jtPassword.setText("");
			}
		}
		JComponent target = (JComponent) e.getSource();
		target.setBackground(colorGainFocus);
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == jtUserName) {
			String content = jtUserName.getText();
			if (content.isEmpty())
				jtUserName.setText(tips);
			else
				Global.userName = content;
		} else if (e.getSource() == jtPassword) {
			String content = String.valueOf(jtPassword.getPassword());
			if (content.isEmpty())
				jtPassword.setText("123456");
			else
				Global.password = content;
		}
		JComponent target = (JComponent) e.getSource();
		target.setBackground(colorLostFocus);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == lbLogin) {
			if (!isLogging) {
				// login();
				queryCaptcha();
			}
		} else if (e.getSource() == btnClose) {
			Logger.println("closing...");
			WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
			this.dispatchEvent(event);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			pressedPoint = e.getPoint();
			// Logger.println(pressedPoint);
		} else {
			if (e.getSource() instanceof JLabel) {
				JLabel label = (JLabel) e.getSource();
				label.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true));
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() instanceof JLabel) {
			JLabel label = (JLabel) e.getSource();
			label.setBorder(null);
		}
		pressedPoint = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// 实现拖拽
		if (pressedPoint != null && e.getSource() != btnClose) {
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

	void bindKeyEnterEvent(JComponent com) {
		com.getInputMap().put(KeyStroke.getKeyStroke('\n'), "login");
		com.getActionMap().put("login", new LoginAction());
	}

	class LoginAction extends AbstractAction {
		private static final long serialVersionUID = 6932343950986413925L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isLogging) {
				queryCaptcha();
			}
		}
	}
}
