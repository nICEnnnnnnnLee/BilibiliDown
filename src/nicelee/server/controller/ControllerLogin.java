package nicelee.server.controller;

import java.io.BufferedWriter;
import java.io.OutputStream;

import nicelee.bilibili.annotations.Controller;
import nicelee.bilibili.annotations.Value;
import nicelee.server.core.PathDealer;
import nicelee.server.util.ResponseUtil;
import nicelee.ui.DialogLogin;
import nicelee.ui.DialogSMSLogin;

@Controller(path = "/geetest", note = "极验参数")
public class ControllerLogin {

	@Controller(path = "/login", matchAll = true, note = "根据传入的参数发起登录请求")
	public String login(BufferedWriter out, OutputStream outRaw, @Value(key = "postData") String param) {
		String token = PathDealer.getValue(param, "token");
		String challenge = PathDealer.getValue(param, "challenge");
		String seccode = PathDealer.getValue(param, "seccode");
		String validate = PathDealer.getValue(param, "validate");
		try {
			if (token != null && challenge != null && seccode != null && validate != null) {
				String result = DialogLogin.Instance.login(token, challenge, validate, seccode);
				ResponseUtil.response200OK(out);
				ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
				ResponseUtil.endResponseHeader(out);
				if (result == null) {
					out.write("{\"code\":0, \"message\": \"ok\"}");
				} else {
					out.write("{\"code\":777, \"message\": \"" + result + "\"}");
				}
				ResponseUtil.endResponse(out);
			} else {
				ResponseUtil.response200OK(out);
				ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
				ResponseUtil.endResponseHeader(out);
				out.write("{\"code\":233, \"message\": \"need params: token, challenge, validate, seccode\"}");
				ResponseUtil.endResponse(out);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	@Controller(path = "/sms", matchAll = true, note = "根据传入的参数请求发送短信验证码")
	public String sms(BufferedWriter out, OutputStream outRaw, @Value(key = "postData") String param) {
		String token = PathDealer.getValue(param, "token");
		String challenge = PathDealer.getValue(param, "challenge");
		String seccode = PathDealer.getValue(param, "seccode");
		String validate = PathDealer.getValue(param, "validate");
		try {
			if (token != null && challenge != null && seccode != null && validate != null) {
				DialogSMSLogin.Instance.sendSMS(token, challenge, validate, seccode);
				ResponseUtil.response200OK(out);
				ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
				ResponseUtil.endResponseHeader(out);
				out.write("{\"code\":0, \"message\": \"ok\"}");
				ResponseUtil.endResponse(out);
			} else {
				ResponseUtil.response200OK(out);
				ResponseUtil.responseHeader(out, "Content-Type", "application/json;charset=UTF-8");
				ResponseUtil.endResponseHeader(out);
				out.write("{\"code\":233, \"message\": \"need params: token, challenge, validate, seccode\"}");
				ResponseUtil.endResponse(out);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}
}
