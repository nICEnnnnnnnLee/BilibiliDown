package nicelee.ui.thread;

import java.awt.Image;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;

import nicelee.bilibili.INeedLogin;
import nicelee.ui.FrameQRCode;
import nicelee.ui.Global;
import nicelee.util.HttpCookies;

public class LoginThread extends Thread {

	@Override
	public void run() {
		
		Global.index.jlHeader.removeMouseListener(Global.index);
		INeedLogin inl = new INeedLogin();
		/**
		 * 0. 登录状态查询
		 */
		if(Global.isLogin || !Global.needToLogin) {
			Global.index.jlHeader.addMouseListener(Global.index);
			return;
		}
		String cookiesStr = inl.readCookies();
		//检查有没有本地cookie配置
		if(cookiesStr != null) {
			List<HttpCookie> cookies = HttpCookies.convertCookies(cookiesStr);
			//成功登录后即返回,不再进行二维码扫码工作
			if( inl.getLoginStatus(cookies)) {
				//设置全局Cookie
				HttpCookies.setGlobalCookies(cookies);
				//设置当前头像
				try {
					//System.out.println(inl.user.getPoster());
					URL fileURL = new URL(inl.user.getPoster());
					ImageIcon imag1 = new ImageIcon(fileURL);
					imag1 = new ImageIcon(imag1.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
					Global.index.jlHeader.setToolTipText("当前用户为: " + inl.user.getName());
					Global.index.jlHeader.setIcon(imag1);
					Global.index.jlHeader.removeMouseListener(Global.index);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				System.out.println("成功登录...");
				return;
			}
		}
		
		/**
		 * 1. 访问 Get 访问 https://passport.bilibili.com/qrcode/getLoginUrl 获取
		 * oauthKey ==> 链接 ==> 二维码
		 */
		String authKey = inl.getAuthKey();
		System.out.println("正在获取验证...");
		//显示二维码图片
		FrameQRCode qr = new FrameQRCode(inl.qrCodeStr);
		qr.initUI();
		
		/**
		 * 2. 周期性Post 访问 https://passport.bilibili.com/qrcode/getLoginInfo 直至扫码成功
		 *   成功后保存Cookie
		 */
		while (!Global.isLogin && Global.needToLogin) {
			try {
				Global.isLogin = inl.getAuthStatus(authKey);
				System.out.println("------------");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(Global.isLogin) {
			//保存cookie到本地
			inl.saveCookies(inl.iCookies.toString());
			//设置全局Cookie
			HttpCookies.setGlobalCookies(inl.iCookies);
			System.out.println("成功登录...");
		}else {
			Global.index.jlHeader.addMouseListener(Global.index);
			Global.needToLogin = true;
		}
		//销毁图片
		qr.dispose();
		
	}
}
