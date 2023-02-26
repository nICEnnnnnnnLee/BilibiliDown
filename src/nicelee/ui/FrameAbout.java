package nicelee.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import nicelee.bilibili.util.Logger;
import nicelee.ui.item.MJTitleBar;
import nicelee.ui.item.impl.TextTransferHandler;

public class FrameAbout extends JFrame implements HyperlinkListener {

	private static final long serialVersionUID = -5017130575041108799L;
	private static FrameAbout frame;

	private FrameAbout() {
	}

	private void initUI() {
		// this.setBounds(300, 200, 500, 400);
		// this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setSize(800, 580);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setTitle("用爱发电 bilibili~~");
		//this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		
		// 作为内容容器
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		// 添加标题栏
		MJTitleBar titleBar = new MJTitleBar(this, true);
		pane.add(titleBar);
		
		// 添加内容
		JEditorPane editorPane = new JEditorPane();
		editorPane.putClientProperty("charset", "utf-8");
		editorPane.setEditable(false);
		editorPane.setTransferHandler(new TextTransferHandler());
		try {
			editorPane.setPage(this.getClass().getResource("/resources/about.html"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		editorPane.addHyperlinkListener(this);
		JScrollPane scrollPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(800, 540));
		pane.add(scrollPane);
		
		this.setContentPane(pane);
	}

	public static void showAbout() {
		if (frame == null) {
			frame = new FrameAbout();
			frame.initUI();
		}
		frame.setVisible(true);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			Logger.println(url.toString());
			//Logger.println(url.getProtocol());
			try {
				Desktop desktop = Desktop.getDesktop();
				if (url.getProtocol().startsWith("http")) {
					desktop.browse(url.toURI());
				}else {
					File file = new File(url.toString().substring(7));
					Logger.println(file.getAbsolutePath());
					desktop.open(file);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
