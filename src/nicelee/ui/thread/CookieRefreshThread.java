package nicelee.ui.thread;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import nicelee.ui.item.JOptionPane;
import nicelee.ui.item.JOptionPaneManager;

import org.json.JSONObject;

import nicelee.bilibili.API;
import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.server.core.SocketServer;
import nicelee.ui.Global;

public class CookieRefreshThread extends Thread {

	public static boolean showTips = true;
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
		this.setDaemon(true);
	}

	private static String encrypt(String origin) {
		try {
			// base64编码的公钥
			String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLgd2OAkcGVtoE3ThUREbio0EgUc/prcajMKXvkCKFCWhJYJcLkcM2DKKcSeFpD/j6Boy538YXnR6VhcuUJOhH2x71nzPjfdTcqMz7djHum0qSZA0AyCBDABUqCrfNgCiJ00Ra7GmRj+YCK1NJEuewlb40JNrRuoEUXpabUzGB8QIDAQAB";
			byte[] decoded = Base64.getDecoder().decode(publicKey);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(spec);
			// RSA加密
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
			OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"),
					PSource.PSpecified.DEFAULT);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey, oaepParams);
			byte[] resultBytes = cipher.doFinal(origin.getBytes("UTF-8"));
			char[] resultChars = new char[resultBytes.length * 2];
			char[] chars = "0123456789abcdef".toCharArray();
			for (int i = 0; i < resultBytes.length; i++) {
				byte b = resultBytes[i];
				int left = (b & 0xf0) >> 4;
				int right = b & 0x0f;
				resultChars[i * 2] = chars[left];
				resultChars[i * 2 + 1] = chars[right];
			}
			String outStr = new String(resultChars);
			return outStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void run() {
		Logger.println("---CookieRefreshThread---");
		if (HttpCookies.getGlobalCookies() == null) {
			if(showTips)
				JOptionPaneManager.showMsgWithNewThread("消息", "当前未登录");
			API.uploadFingerprint();
			Logger.println("Fingerprint上传完毕");
			return;
		}
		// 判断有没有refresh_token
		if (HttpCookies.getRefreshToken() == null) {
			if(showTips)
				JOptionPaneManager.showMsgWithNewThread("消息", "当前的登录凭证为旧版本生成，无法刷新cookie");
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
			if(showTips)
				JOptionPaneManager.showMsgWithNewThread("消息", "当前cookie无需刷新");
			API.uploadFingerprint();
			Logger.println("Fingerprint上传完毕");
			return;
		}
		if (Global.runWASMinBrowser) {
			// 打开server，等待返回 refresh_csrf
			new Thread(new Runnable() {
				@Override
				public void run() {
					socketServer = new SocketServer(Global.serverPort);
					socketServer.startServer();
				}
			}, "Cookie 刷新server").start();
			String browseUrl = String.format("http://localhost:%d/cookieRefresh/index.html?timestamp=%d",
					Global.serverPort, csrfInfoObj.optLong("timestamp"));
			goUrlOrShowTips(browseUrl);
			try {// 等待3min
				synchronized (this) {
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
		} else {
			try {
				// 生成 correspond路径
				String refresh_timestamp = "refresh_" + csrfInfoObj.optString("timestamp");
				Logger.println(refresh_timestamp);
				String path = "https://www.bilibili.com/correspond/1/" + encrypt(refresh_timestamp);
				// 获取 refresh_csrf
				String html = util.getContent(path, headers.getCommonHeaders(), HttpCookies.getGlobalCookies());
				Pattern p = Pattern.compile("<div +id=\"1-name\">(.+?)</div>");
				Matcher m = p.matcher(html);
				m.find();
				refreshCsrf = m.group(1).trim();
				Logger.println(refreshCsrf);
			} catch (Exception e) {
				e.printStackTrace();
				if(showTips)
					JOptionPaneManager.showMsgWithNewThread("消息", "刷新cookie出现错误");
				return;
			}
		}
		// 调用刷新Cookie的API
		INeedLogin inl = new INeedLogin();
		String tips = inl.refreshCookie(HttpCookies.getCsrf(), refreshCsrf, HttpCookies.getRefreshToken());
		if(showTips)
			if (tips != null) {
				Logger.println(tips);
				JOptionPaneManager.showMsgWithNewThread("消息", tips);
			} else {
				JOptionPaneManager.showMsgWithNewThread("消息", "Cookie 刷新成功");
			}
		Logger.println("Cookie刷新运行完毕");
		API.uploadFingerprint();
		Logger.println("Fingerprint上传完毕");
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
