package nicelee.ui.thread;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.server.core.SocketServer;
import nicelee.ui.Global;

public class CookieRefreshThread extends Thread {

	static CookieRefreshThread instance;

	public static CookieRefreshThread newInstance() {
		instance = new CookieRefreshThread();
		return instance;
	}

	public static CookieRefreshThread currentInstance() {
		return instance;
	}

	volatile String refreshCsrf;
	SocketServer socketServer;

	private CookieRefreshThread() {
		this.setName("Thread-CookieRefresh");
	}

	@Override
	public void run() {
		// 判断有没有refresh_token
		if (HttpCookies.getRefreshToken() == null) {
			JOptionPane.showMessageDialog(null, "当前的登录凭证为旧版本生成，无法刷新cookie");
			return;
		}
		// 判断需不需要刷新
		String url = "https://passport.bilibili.com/x/passport-login/web/cookie/info?csrf=" + HttpCookies.getCsrf();
		HttpRequestUtil util = new HttpRequestUtil();
		HttpHeaders headers = new HttpHeaders();
		String csrfInfo = util.getContent(url, headers.getCommonHeaders(), HttpCookies.getGlobalCookies());
		Logger.println(csrfInfo);
		JSONObject csrfInfoObj = new JSONObject(csrfInfo).getJSONObject("data");
		boolean needRefresh = csrfInfoObj.getBoolean("refresh");
		if (!needRefresh) {
			 JOptionPane.showMessageDialog(null, "当前cookie无需刷新");
			 return;
		}
		// 打开server，等待返回 refresh_csrf
		new Thread(new Runnable() {
			@Override
			public void run() {
				socketServer = new SocketServer(Global.serverPort);
				socketServer.startServer();
			}
		}, "Cookie 刷新server").start();
		String browseUrl = String.format("http://localhost:%d/cookieRefresh/index.html?timestamp=%d", Global.serverPort,
				csrfInfoObj.optLong("timestamp"));
		goUrlOrShowTips(browseUrl);
		try {// 等待3min
			synchronized(this) {
				this.wait(180000);
			}
		} catch (Exception e) {
		}
		// 关闭server，判断情况
		shutDownServerAsyncDelay();
		if (getRefreshCsrf() == null) {
			Logger.println("没有收到浏览器传来的refreshCsrf参数");
			JOptionPane.showMessageDialog(null, "没有收到浏览器传来的refreshCsrf参数");
			return;
		}
		// 调用刷新Cookie的API
		INeedLogin inl = new INeedLogin();
		String tips = inl.refreshCookie(HttpCookies.getCsrf(), refreshCsrf, HttpCookies.getRefreshToken());
		if (tips != null) {
			JOptionPane.showMessageDialog(null, tips);
		} else {
			JOptionPane.showMessageDialog(null, "Cookie 刷新成功");
		}
		Logger.println("Cookie刷新运行完毕");
	}

	private void shutDownServerAsyncDelay() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
				}
				socketServer.stopServer();
			}
		}).start();
	}

	private void goUrlOrShowTips(String browseUrl) {
		final boolean browseSupported = Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
		if (browseSupported)
			try {
				Desktop.getDesktop().browse(new URI(browseUrl));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		else {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable trans = new StringSelection(browseUrl);
			clipboard.setContents(trans, null);
			JOptionPane.showMessageDialog(null, "请通过浏览器访问以下网址(已复制到剪贴板):\n" + browseUrl, "请注意",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public String getRefreshCsrf() {
		return refreshCsrf;
	}

	public void setRefreshCsrf(String refreshCsrf) {
		this.refreshCsrf = refreshCsrf;
	}

}
