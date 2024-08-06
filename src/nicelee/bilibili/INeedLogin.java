package nicelee.bilibili;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.custom.System;

public class INeedLogin {

	HttpRequestUtil util = new HttpRequestUtil();
	public List<HttpCookie> iCookies;
	public String refreshToken;
	public String qrCodeStr = "";
	public UserInfo user;

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
		String url = "https://passport.bilibili.com/x/passport-login/web/qrcode/generate?source=main-web";
		String json = util.getContent(url, genLoginHeader());
		JSONObject jObj = new JSONObject(json).getJSONObject("data");
		qrCodeStr = jObj.getString("url");
		return jObj.getString("qrcode_key");
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
		String url = "https://passport.bilibili.com/x/passport-login/web/qrcode/poll?source=main-web&qrcode_key=" + authKey;
		try {
			String json = util.getContent(url, genLoginHeader());

			System.out.println(json);
			JSONObject jObj = new JSONObject(json).getJSONObject("data");

			boolean succ = jObj.getInt("code") == 0;
			if (succ) {
				iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
				refreshToken = jObj.getString("refresh_token");
			}
			return succ;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("验证Auth返回超时, 或json解析错误");
			return false;
		}

	}

	/**
	 * 将Cookie 和 token 保存至本地
	 * 
	 */
	public void saveCookiesAndToken() {
		File file = ResourcesUtil.sourceOf("./config/cookies.config");
		try {
			OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter oos = new BufferedWriter(fileWriter);
			oos.write(iCookies.toString());
			if(refreshToken != null) {
				HttpCookies.setRefreshToken(refreshToken);
				oos.newLine();
				oos.write(refreshToken);
			}
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
		File file = ResourcesUtil.sourceOf("./config/cookies.config");
		return ResourcesUtil.readAll(file);
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
		String jsonStr = util.getContent(url, genLoginHeader());
		JSONObject json = new JSONObject(jsonStr).getJSONObject("data");
		return json;
	}

	HashMap<String, String> loginHeader = null;
	public HashMap<String, String> genLoginHeader(){
		if(loginHeader != null)
			return loginHeader;
		HashMap<String, String> headers = new HttpHeaders().getBiliLoginAuthHeaders();
		String cookie = API.getFingerprint();
		headers.put("Cookie", cookie);
		loginHeader = headers;
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
				refreshToken = response.getJSONObject("data").getString("refresh_token");
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
				refreshToken = response.getJSONObject("data").getString("refresh_token");
				return null;
			} else {
				return response.optString("message", "未知错误，返回信息中没有错误描述");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "未知错误，可能由网络引起";
		}
	}

	public String refreshCookie(String csrf, String refresh_csrf, String refresh_token) {
		HttpHeaders header = new HttpHeaders();
		String postUrl = "https://passport.bilibili.com/x/passport-login/web/cookie/refresh?csrf=%s&refresh_csrf=%s&source=main_web&refresh_token=%s";
		postUrl = String.format(postUrl, csrf, refresh_csrf, refresh_token);
		String result = util.postContent(postUrl, header.getCommonHeaders(), "", HttpCookies.getGlobalCookies());
		Logger.println(result);
		JSONObject json = new JSONObject(result);
		if(json.getInt("code") == 0) {
			JSONObject data = json.optJSONObject("data");
			if(data != null) {
				iCookies = HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies();
				refreshToken = data.getString("refresh_token");
				HttpCookies.setGlobalCookies(iCookies);
				saveCookiesAndToken();
				// 将原来的Cookie注销掉
				postUrl = "https://passport.bilibili.com/x/passport-login/web/confirm/refresh?csrf=%s&refresh_token=%s";
				postUrl = String.format(postUrl, HttpCookies.getCsrf(), refresh_token);
				result = util.postContent(postUrl, header.getCommonHeaders(), "", HttpCookies.getGlobalCookies());
				Logger.println("将原来的Cookie注销掉");
				Logger.println(result);
			}
			return null;
		}else {
			return json.optString("message", "未知错误，返回信息中没有错误描述");
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
