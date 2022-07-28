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
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;

import org.json.JSONObject;

import nicelee.bilibili.model.UserInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.QrCodeUtil;
import nicelee.bilibili.util.ResourcesUtil;

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
		if (args != null && args.length == 1) {
			if (args[0].equals("0")) {
				INeedLogin inl = new INeedLogin();
				String cookieStr = inl.readCookies();
				if (cookieStr == null) {
					System.out.println("不存在Cookie");
					return;
				}
				List<HttpCookie> cookies = HttpCookies.convertCookies(cookieStr);
				if (inl.getLoginStatus(cookies)) {
					System.out.println("该Cookie有效");
					System.out.println("用户名称: " + inl.user.getName());
					System.out.println("用户头像: " + inl.user.getPoster());
				} else {
					System.out.println("该Cookie无效");
				}
			} else if (args[0].equals("1")) {
				INeedLogin inl = new INeedLogin();
				String authKey = inl.getAuthKey();
				// 保存二维码
				File qrCode = new File("qrcode.jpg");
				QrCodeUtil.createQrCode(new FileOutputStream(qrCode), inl.qrCodeStr, 900, "JPEG");
				// 打开二维码文件
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
	 * 该方法返回用户登录状态 若已登录,将在当前实例更新用户信息
	 * 
	 * @return
	 */
	public boolean getLoginStatus(List<HttpCookie> iCookies) {
		HttpHeaders headers = new HttpHeaders();
		boolean isLogin;
		try {
			/**
			 * https://api.bilibili.com/x/web-interface/nav?build=0&mobi_app=web
			 * https://api.bilibili.com/x/space/myinfo 可用(可查信息,登录状态)
			 * https://account.bilibili.com/home/userInfo 可用(可查信息,登录状态)
			 * https://passport.bilibili.com/web/site/user/info 可用(可查登录状态) 可查信息,可不登录,但需要ID
			 * ...
			 */
			String url = "https://api.bilibili.com/x/web-interface/nav?build=0&mobi_app=web";
			String json = util.getContent(url, headers.getBiliUserInfoHeaders(), iCookies);
			// System.out.println(json);

			JSONObject jObj = new JSONObject(json).getJSONObject("data");
			isLogin = jObj.getBoolean("isLogin");
			if (isLogin) {
				user = new UserInfo();
				user.setName(jObj.getString("uname"));
				user.setPoster(jObj.getString("face"));
				user.setUid(jObj.getJSONObject("wallet").getLong("mid"));
				this.iCookies = iCookies;
				// System.out.println(user.getName());
				// System.out.println(user.getPoster());
			}
		} catch (Exception e) {
			e.printStackTrace();
			isLogin = false;
		}
		return isLogin;
	}

	/**
	 * 该方法返回oauthKey的同时, 更新的Cookie由HttpRequestUtil设定的CookieManager保管,
	 * 该实例的QRCodeStr也将被更新
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
	 * 查询oauthKey的验证状态, 验证成功后, 更新的Cookie由HttpRequestUtil设定的CookieManager保管, if 认证成功,
	 * 该实例的List<HttpCookie> iCookies 也将被更新
	 * 
	 * @param authKey
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean getAuthStatus(String authKey) throws UnsupportedEncodingException {
		HttpHeaders headers = new HttpHeaders();
		String url = "https://passport.bilibili.com/qrcode/getLoginInfo";
		String param = "oauthKey=" + URLEncoder.encode(authKey, "UTF-8");
		param += "&gourl=" + URLEncoder.encode("https://www.bilibili.com/", "UTF-8");
		try {
			String json = util.postContent(url, headers.getBiliLoginAuthVaHeaders(), param);

			// System.out.println(param);
			System.out.println(json);
			JSONObject jObj = new JSONObject(json);

			boolean succ = jObj.getBoolean("status");
			if (succ) {
				iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
				// saveCookies(iCookies.toString()); //这个交由外部判断
			}
//			System.out.println(jObj.getBoolean("status"));
//			System.out.println(jObj.getInt("data"));
//			System.out.println(jObj.getString("message"));
			return succ;
		} catch (Exception e) {
			System.out.println("验证Auth返回超时, 或json解析错误");
			return false;
		}

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
			while (ois.readLine() != null) {
				iCookie += ois.readLine();
			}
			ois.close();
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
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

	/**
	 * 获取极验信息
	 */
	public JSONObject getGeetest() throws IOException {
		String url = "https://passport.bilibili.com/x/passport-login/captcha?source=main_mini";
		HttpHeaders headers = new HttpHeaders();
		//util.getContent("https://passport.bilibili.com/login", headers.getBiliLoginAuthHeaders());
		String jsonStr = util.getContent(url, headers.getBiliLoginAuthHeaders());
		JSONObject json = new JSONObject(jsonStr).getJSONObject("data");
		return json;
	}

	HashMap<String, String> loginHeader = null;
	HashMap<String, String> genLoginHeader(){
		if(loginHeader != null)
			return loginHeader;
		HashMap<String, String> headers = new HttpHeaders().getBiliLoginAuthHeaders();
		StringBuilder sb = new StringBuilder();
		
		sb.append("_uuid=")
			.append(ResourcesUtil.randomUpper(8)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(18)).append("infoc")
			.append("; ");
		sb.append("b_lsid=")
			.append(ResourcesUtil.randomUpper(8)).append("_")
			.append(ResourcesUtil.randomUpper(11))
			.append("; ");
		sb.append("b_nut=")
			.append(System.currentTimeMillis()/1000)
			.append("; ");
		sb.append("b_timer=")
			.append("%7B%22ffp%22%3A%7B%22333.130.fp.risk_")
			.append(ResourcesUtil.randomUpper(8))
			.append("%22%3A%22")
			.append(ResourcesUtil.randomInt(10))
			.append("A%22%7D%7D; ");
		sb.append("buvid3=")
			.append(ResourcesUtil.randomUpper(8)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(17)).append("infoc")
			.append("; ");
		sb.append("buvid4=")
			.append(ResourcesUtil.randomInt(8)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(17)).append("-")
			.append(ResourcesUtil.randomInt(9)).append("-")
			.append(ResourcesUtil.randomLower(4)).append("/")
			.append(ResourcesUtil.randomUpper(4)).append("+")
			.append(ResourcesUtil.randomUpper(12)).append("%3D%3D")
			.append("; ");
		sb.append("buvid_fp=")
			.append(ResourcesUtil.randomUpper(8)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(4)).append("-")
			.append(ResourcesUtil.randomUpper(17)).append("infoc")
			.append("; ");
		sb.append("fingerprint=").append(ResourcesUtil.randomLower(32));
		headers.put("Cookie", sb.toString());
		return headers;
	}
	
	/**
	 * 登录
	 * 
	 */
	public String login(String userName, String pwd, String token, String challenge, String validate, String seccode) {
		try {
			if (!userName.contains("%40"))
				userName = URLEncoder.encode(userName, "UTF-8"); // 账号没有特殊字符，OK
			String url = "https://passport.bilibili.com/x/passport-login/web/key?_=" + System.currentTimeMillis();
			HashMap<String, String> headers = genLoginHeader();
			String result = util.getContent(url, headers);
			Logger.println(result);
			JSONObject obj = new JSONObject(result).getJSONObject("data");
			String hash = obj.optString("hash", "");
			if (hash.isEmpty()) {
				return "服务器繁忙，登录失败";
			}
			String pubKey = obj.getString("key").replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "").replace("\n", "");

			String encryptPwd = encrypt(hash + pwd, pubKey);
			encryptPwd = URLEncoder.encode(encryptPwd, "UTF-8");
			url = "https://passport.bilibili.com/x/passport-login/web/login";
			String param = String.format(
					"username=%s&password=%s&keep=0&source=main_mini&token=%s&go_url=https://www.bilibili.com&challenge=%s&validate=%s&seccode=%s",
					userName, encryptPwd, token, challenge, validate, seccode);
			result = util.postContent(url, headers, param);
			Logger.println(result);
			JSONObject response = new JSONObject(result);
			if (response.optInt("code") == 0) {
				JSONObject data = response.getJSONObject("data");
				if (data.optInt("status") == 2) {
					return data.optString("message", "未知错误，返回信息中没有错误描述");
				}
				iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
				return null;
			} else {
				return response.optString("message", "未知错误，返回信息中没有错误描述");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "未知错误，可能由网络引起";
		}
	}
	
	public JSONObject sendSMS(String countryCode, String phoneNumber, String token, String challenge, String validate, String seccode) {
		try {
			String url = "https://passport.bilibili.com/x/passport-login/web/sms/send";
			HashMap<String, String> headers = genLoginHeader();
			String param = String.format(
					"cid=%s&tel=%s&source=main_mini&token=%s&challenge=%s&validate=%s&seccode=%s",
					countryCode, phoneNumber, token, challenge, validate, seccode);
			String result = util.postContent(url, headers, param);
			Logger.println(result);
			return new JSONObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String loginSMS(String countryCode, String phoneNumber, String code, String captchaKey) {
		try {
			String url = "https://passport.bilibili.com/x/passport-login/web/login/sms";
			HashMap<String, String> headers = genLoginHeader();
			String param = String.format(
					"cid=%s&tel=%s&code=%s&captcha_key=%s&source=main_mini&go_url=&keep=true",
					countryCode, phoneNumber, code, captchaKey);
			String result = util.postContent(url, headers, param);
			Logger.println(result);
			JSONObject response = new JSONObject(result);
			if (response.optInt("code") == 0) {
				JSONObject data = response.getJSONObject("data");
				if (data.optInt("status") == 2) {
					return data.optString("message", "未知错误，返回信息中没有错误描述");
				}
				iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
				return null;
			} else {
				return response.optString("message", "未知错误，返回信息中没有错误描述");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "未知错误，可能由网络引起";
		}
	}

	/**
	 * RSA加密
	 * 
	 * @param origin    待加密文本
	 * @param publicKey 公钥
	 * @return
	 */
	private static String encrypt(String origin, String publicKey) {
		try {
			// base64编码的公钥
			byte[] decoded = Base64.getDecoder().decode(publicKey);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(spec);
			// RSA加密
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(origin.getBytes("UTF-8")));
			return outStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
