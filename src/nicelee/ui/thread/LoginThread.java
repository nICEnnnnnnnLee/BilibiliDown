package nicelee.ui.thread;

import java.awt.Image;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.model.FavList;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.ui.DialogLogin;
import nicelee.ui.DialogSMSLogin;
import nicelee.ui.FrameQRCode;
import nicelee.ui.Global;

public class LoginThread extends Thread {

	@Override
	public void run() {
		try {
			login();
		} catch (Exception e) {
			e.printStackTrace();
			stopFrameWaiting();
		}
	}

	private void stopFrameWaiting() {
		if(Global.frWaiting != null)
			Global.frWaiting.stop();
	}
	
	public void login() {
		System.out.println("登录线程被调用...");
//		System.out.println(Global.index.getParent().getParent().getParent().getParent().getParent().isVisible());
//		System.exit(1);
		// Global.index.jlHeader.removeMouseListener(Global.index);
		INeedLogin inl = new INeedLogin();
		/**
		 * 0. 登录状态查询
		 */
		if (Global.isLogin || !Global.needToLogin) {
			// Global.index.jlHeader.addMouseListener(Global.index);
			System.out.println("已经登录,或没有发起登录请求");
			stopFrameWaiting();
			return;
		}
		String cookiesStr = inl.readCookies();
		// 检查有没有本地cookie配置
		if (cookiesStr != null) {
			System.out.println("检查到存在本地Cookies...");
			List<HttpCookie> cookies = HttpCookies.convertCookies(cookiesStr);
			// 成功登录后即返回,不再进行二维码扫码工作
			if (inl.getLoginStatus(cookies)) {
				System.out.println("本地Cookies验证有效...");
				// 设置全局Cookie
				HttpCookies.setGlobalCookies(cookies);
				// 初始化用户数据显示
				initUserInfo(inl);
				System.out.println("成功登录...");
				Global.isLogin = true;
				stopFrameWaiting();
				return;
			} else {
				System.out.println("本地Cookies验证无效...");
				// 置空全局Cookie
				HttpCookies.setGlobalCookies(null);
			}
		}
		System.out.println("没有检查到本地Cookies...");
		stopFrameWaiting();
		//QRLogin(inl);
		switch (Global.loginType) {
		case "pwd":
			PwdLogin(inl);
			break;
		case "qr":
			QRLogin(inl);
			break;
		case "sms":
			DialogSMSLogin dialog = new DialogSMSLogin(inl);
			dialog.init();
			break;
		default:
			QRLogin(inl);
			break;
		}

		Logger.println("线程即将结束，当前登录状态： " + Global.isLogin);
		if (Global.isLogin) {
			// 保存cookie到本地
			inl.saveCookiesAndToken();
			// 设置全局Cookie
			HttpCookies.setGlobalCookies(inl.iCookies);
			// 获取用户信息
			inl.getLoginStatus(inl.iCookies);
			// 初始化用户数据显示
			initUserInfo(inl);
			System.out.println("成功登录...");
		} else {
			// Global.index.jlHeader.addMouseListener(Global.index);
			Global.needToLogin = true;
		}
	}

	/**
	 * 账户密码登录(人工)
	 * 
	 * @param inl
	 */
	private void PwdLogin(INeedLogin inl) {
		DialogLogin dialog = new DialogLogin(inl);
		dialog.init();
	}
	
	/**
	 * 二维码扫码登录
	 * 
	 * @param inl
	 */
	private void QRLogin(INeedLogin inl) {
		/**
		 * 1. 访问 Get 访问 https://passport.bilibili.com/qrcode/getLoginUrl 获取 oauthKey ==>
		 * 链接 ==> 二维码
		 */
		System.out.println("正在获取验证AuthKey以生成二维码...");
		String authKey = inl.getAuthKey();
		System.out.println("authKey: " + authKey);
		// 显示二维码图片
		FrameQRCode qr = new FrameQRCode(inl.qrCodeStr);
		qr.initUI();

		/**
		 * 2. 周期性Post 访问 https://passport.bilibili.com/qrcode/getLoginInfo 直至扫码成功
		 * 成功后保存Cookie
		 */
		long start = System.currentTimeMillis();
		while (!Global.isLogin && Global.needToLogin && System.currentTimeMillis() - start < 60 * 1000) {
			try {
				Global.isLogin = inl.getAuthStatus(authKey);
				System.out.println("------------");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		// 销毁图片
		System.out.println("登录线程结束...");
		qr.dispose();
	}

	/**
	 * 初始化index页面头像 初始化index页面收藏夹选项
	 * 
	 * @param inl
	 */
	public void initUserInfo(INeedLogin inl) {
		// 设置当前头像
		try {
			// System.out.println(inl.user.getPoster());
			URL fileURL = new URL(inl.user.getPoster());
			ImageIcon imag1 = new ImageIcon(fileURL);
			imag1 = new ImageIcon(imag1.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
			Global.index.jlHeader.setToolTipText("当前用户为: " + inl.user.getName());
			Global.index.jlHeader.setIcon(imag1);
			// Global.index.jlHeader.removeMouseListener(Global.index);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// 设置收藏夹
		try {
			String favUrl = "https://api.bilibili.com/medialist/gateway/base/created?pn=1&ps=100&is_space=0&jsonp=jsonp&up_mid="
					+ inl.user.getUid();
			HttpRequestUtil util = new HttpRequestUtil();
			String jsonStr = util.getContent(favUrl, new HttpHeaders().getAllFavListHeaders(inl.user.getUid()),
					HttpCookies.getGlobalCookies());
//			System.out.println(favUrl);
//			System.out.println(jsonStr);
			JSONArray list = new JSONObject(jsonStr).getJSONObject("data").getJSONArray("list");
			if (Global.index.cmbFavList.getItemCount() == 1) {
				Global.index.cmbFavList.addItem("稍后再看");
				for (int i = 0; i < list.length(); i++) {
					JSONObject favlist = list.getJSONObject(i);
					FavList fav = new FavList(favlist.getLong("mid"), favlist.getLong("id"),
							favlist.getInt("media_count"), favlist.getString("title"));
					Global.index.cmbFavList.addItem(fav);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
