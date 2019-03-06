package nicelee.bilibili;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.List;


import org.json.JSONObject;

import nicelee.model.UserInfo;
import nicelee.util.HttpCookies;
import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;
import nicelee.util.QrCodeUtil;

public class INeedLogin {

	HttpRequestUtil util = new HttpRequestUtil();
	public List<HttpCookie> iCookies;
	public String qrCodeStr = "";
	public UserInfo user;

	public static void main(String[] args) throws Exception {
		System.out.println("-------------------------------");
		System.out.println("测试cookie:");
		System.out.println("输入参数 0");
		System.out.println("利用二维码扫码登录, 获取cookie:");
		System.out.println("输入参数 1");
		System.out.println("-------------------------------");
		if(args!=null && args.length == 1) {
			if ( args[0].equals("0") ){
				INeedLogin inl = new INeedLogin();
				String cookieStr = inl.readCookies();
				if(cookieStr == null) {
					System.out.println("不存在Cookie");
					return;
				}
				List<HttpCookie> cookies = HttpCookies.convertCookies(cookieStr);
				if(inl.getLoginStatus(cookies)) {
					System.out.println("该Cookie有效");
					System.out.println("用户名称: " + inl.user.getName());
					System.out.println("用户头像: " + inl.user.getPoster());
				}else {
					System.out.println("该Cookie无效");
				}
			}else if ( args[0].equals("1") ){
				INeedLogin inl = new INeedLogin();
				String authKey = inl.getAuthKey();
				//保存二维码
				File qrCode = new File("qrcode.jpg");
				QrCodeUtil.createQrCode(new FileOutputStream(qrCode),
						inl.qrCodeStr, 900, "JPEG");
				//打开二维码文件
				try {
					Thread.sleep(3000);
					Desktop.getDesktop().open(qrCode);
				} catch (Exception e1) {
					System.out.println("二维码已保存至当前目录, 请尽快扫描登录! ");
				}
				boolean isLogin = false;
				while (!isLogin) {
					try {
						isLogin = inl.getAuthStatus(authKey);
						System.out.println("请尽快扫描二维码!...");
						Thread.sleep(3000);
					} catch (Exception e) {
					} 
				}
				inl.saveCookies(inl.iCookies.toString());
				System.out.println("cookie已保存至当前目录! ");
			}
		}
		
	}
	
	/**
	 * 该方法返回用户登录状态
	 * 若已登录,将在当前实例更新用户信息
	 * 
	 * @return
	 */
	public boolean getLoginStatus(List<HttpCookie> iCookies) {
		HttpHeaders headers = new HttpHeaders();
		/**
		 * 
		 * https://api.bilibili.com/x/space/myinfo 可用(可查信息,登录状态)
		 * https://account.bilibili.com/home/userInfo 可用(可查信息,登录状态)
		 * https://passport.bilibili.com/web/site/user/info 可用(可查登录状态)
		 * https://api.bilibili.com/x/space/acc/info?mid=8741628&jsonp=jsonp 可查信息,可不登录,但需要ID
		 * ...
		 */
		String url = "https://account.bilibili.com/home/userInfo";
		String json = util.getContent(url, headers.getBiliUserInfoHeaders(), iCookies);
		//System.out.println(json);
		boolean isLogin;
		try {
			JSONObject jObj = new JSONObject(json);
			isLogin = jObj.getBoolean("status");
			if(isLogin) {
				user = new UserInfo();
				user.setName(jObj.getJSONObject("data").getString("uname"));
				user.setPoster(jObj.getJSONObject("data").getString("face"));
				this.iCookies = iCookies;
				//System.out.println(user.getName());
				//System.out.println(user.getPoster());
			}
		}catch (Exception e) {
			isLogin = false;
		}
		return isLogin;
	}
	
	/**
	 * 该方法返回oauthKey的同时, 更新的Cookie由HttpRequestUtil设定的CookieManager保管, 该实例的QRCodeStr也将被更新
	 * 
	 * @return
	 */
	public String getAuthKey() {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://passport.bilibili.com/qrcode/getLoginUrl";
		String json = util.getContent(url, headers.getBiliLoginAuthHeaders());
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		qrCodeStr = jObj.getString("url");
		return jObj.getString("oauthKey");
	}

	/**
	 * 查询oauthKey的验证状态, 验证成功后,  更新的Cookie由HttpRequestUtil设定的CookieManager保管, 
	 * 	if 认证成功, 该实例的List<HttpCookie> iCookies 也将被更新
	 * 
	 * @param authKey
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public boolean getAuthStatus(String authKey) throws UnsupportedEncodingException{
		HttpHeaders headers = new HttpHeaders();
		String url = "https://passport.bilibili.com/qrcode/getLoginInfo";
		String param = "oauthKey=" + URLEncoder.encode(authKey, "UTF-8");
		param += "&gourl=" + URLEncoder.encode("https://www.bilibili.com/", "UTF-8");
		String json = util.postContent(url, headers.getBiliLoginAuthVaHeaders(), param);

		//System.out.println(param);
		System.out.println(json);
		JSONObject jObj = new JSONObject(json);
		
		boolean succ = jObj.getBoolean("status");
		if(succ) {
			iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
			//saveCookies(iCookies.toString()); //这个交由外部判断
		}
//		System.out.println(jObj.getBoolean("status"));
//		System.out.println(jObj.getInt("data"));
//		System.out.println(jObj.getString("message"));
		return succ;
	}

	/**
	 * 将Cookie 保存至本地
	 * 
	 * @param iCookies
	 */
	public void saveCookies(String iCookies) {
		File file = new File("./config/cookies.config");
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter oos = new BufferedWriter(fileWriter);
			oos.write(iCookies);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从本地获取 Cookie
	 * 
	 * @return
	 */
	public String readCookies() {
		File file = new File("./config/cookies.config");
		String iCookie = null;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader ois = new BufferedReader(fileReader);
			iCookie = ois.readLine();
			while(ois.readLine() != null) {
				iCookie += ois.readLine();
			}
			ois.close();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iCookie;
	}

	public HttpRequestUtil getUtil() {
		return util;
	}

	public void setUtil(HttpRequestUtil util) {
		this.util = util;
	}
}
