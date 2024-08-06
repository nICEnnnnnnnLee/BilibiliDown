package nicelee.bilibili.pushers.impl;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.sun.mail.util.MailSSLSocketFactory;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.pushers.IPush;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;

/**
 * 以下为相关配置
 * @bilibili.download.push.type 	Mail
 * @bilibili.download.push.account 	发送的邮箱地址
 * @bilibili.download.push.token 	发送的邮箱凭证，需要注意的是并不一定是密码
 * @mail.smtp.to.addr				接收的邮箱地址，为空时等于发送的邮箱地址
 * @mail.smtp.host					选填，邮箱不为@sina.com @163.com @qq.com时，必填
 * @mail.smtp.port					选填，邮箱不为@sina.com @163.com @qq.com时，必填
 * @mail.smtp.ssl.enable			选填，邮箱不为@sina.com @163.com @qq.com时，必填  值为 true/false
 * @mail.smtp.starttls.enale		选填，需要starttls再填写 true/false
 * @mail.smtp.debug					选填，是否输出debug。值为 true/false，默认false
 *
 */
@Bilibili(name = "MailPush", type = "pusher", note = "将结果通过邮件发送")
public class MailPush implements IPush {

	@Override
	public String type() {
		return "Mail";
	}

	@Override
	public IPush newInstance() {
		return new MailPush();
	}

	/**
	<table>
	<thead>
		<tr><th>视频标题</th><th>副标题</th><th>BV号</th><th>失败原因</th><tr>
	</thead>
	<tbody>
		<tr><td>dd</td> <td>dd</td>  <td>dd</td> <td>dd</td> <tr>
		<tr><td>dd</td> <td>dd</td>  <td>dd</td> <td>dd</td> <tr>
	</tbody>
	</table>
	
	<table>
	<thead>
		<tr><th>文件名称</th><th>文件大小</th><th>BV号</th><th>清晰度</th><tr>
	</thead>
	<tbody>
		<tr><td>dd</td> <td>dd</td>  <td>dd</td> <td>dd</td> <tr>
		<tr><td>dd</td> <td>dd</td>  <td>dd</td> <td>dd</td> <tr>
		<tr><td>dd</td> <td>dd</td>  <td>dd</td> <td>dd</td> <tr>
	</tbody>
	</table> 
	 */
	@Override
	public void push(Map<ClipInfo, TaskInfo> currentTaskList, long begin, long end) {

		int successCnt = 0, failCnt = 0;
		List<TaskInfo> successTasks = new ArrayList<TaskInfo>();
		List<TaskInfo> failTasks = new ArrayList<TaskInfo>();
		for (TaskInfo task : currentTaskList.values()) {
			if ("success".equals(task.getStatus())) {
				successCnt++;
				successTasks.add(task);
			} else {
				failCnt++;
				failTasks.add(task);
			}
		}
		String title = String.format("%1$tF %1$tR 新增视频:%2$d个, 成功:%3$d个，失败:%4$d个", new Date(System.currentTimeMillis()),
				successCnt + failCnt, successCnt, failCnt);

		StringBuilder html = new StringBuilder();
		String thLeft = "<th style='border-bottom:1px solid black; border-right:1px solid black;'>";
		String thRight = "</th>";
		String tdLeft = "<td style='border-bottom:1px solid black; border-right:1px solid black;'>";
		String tdRight = "</td>";
		String table = "<table align='center' cellpadding='0' cellspacing='0' width='900' style='border-top:1px solid black; border-left:1px solid black; margin:0 auto; font-size: 12px; font-family: Microsoft YaHei;'><thead><tr>";
		html.append("<html><body>");
		if (successTasks.size() > 0) {
			html.append("<div style='margin:0 auto; width: 900px; font-size: 14px;'><p>成功列表</p></div>");
			html.append(table);
			html.append(thLeft).append("文件名称").append(thRight);
			html.append(thLeft).append("文件大小").append(thRight);
			html.append(thLeft).append("BV号").append(thRight);
			html.append(thLeft).append("清晰度").append(thRight);
			html.append("</tr></thead><tbody>");
			for (TaskInfo task : successTasks) {
				html.append("<tr>");
				html.append(tdLeft).append(task.getFileName()).append(tdRight);
				html.append(tdLeft).append(task.getFileSize()).append(tdRight);
				String link = String.format("<a href='https://b23.tv/%1$s'>%1$s</a>", task.getClip().getAvId());
				html.append(tdLeft).append(link).append(tdRight);
				html.append(tdLeft).append(task.getQn()).append(tdRight);
				html.append("</tr>");
			}
			html.append("</tbody></table><br/>");
		}
		if (failTasks.size() > 0) {
			html.append("<div style='margin:0 auto; width: 900px; font-size: 14px;'><p>失败列表</p></div>");
			html.append(table);
			html.append(thLeft).append("视频标题").append(thRight);
			html.append(thLeft).append("副标题").append(thRight);
			html.append(thLeft).append("BV号").append(thRight);
			html.append(thLeft).append("失败原因").append(thRight);
			html.append("</tr></thead><tbody>");
			for (TaskInfo task : failTasks) {
				ClipInfo clip = task.getClip();
				html.append("<tr>");
				html.append(tdLeft).append(clip.getAvTitle()).append(tdRight);
				html.append(tdLeft).append(clip.getTitle()).append(tdRight);
				String link = String.format("<a href='https://b23.tv/%1$s'>%1$s</a>", clip.getAvId());
				html.append(tdLeft).append(link).append(tdRight);
				html.append(tdLeft).append(task.getStatus()).append(tdRight);
				html.append("</tr>");
			}
			html.append("</tbody></table><br/>");
		}
		html.append("</body></html>");
		try {
			sendEmail(title, html.toString());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	final static String SMTP_HOST_KEY = "mail.smtp.host";
	final static String SMTP_PORT_KEY = "mail.smtp.port";
	final static String SMTP_SSL_ENABLE_KEY = "mail.smtp.ssl.enable";
	final static String SMTP_STARTTLS_KEY = "mail.smtp.starttls.enale";
	final static String SMTP_DEBUG_KEY = "mail.smtp.debug";
	final static String SMTP_TO_EMAIL_KEY = "mail.smtp.to.addr"; // 这个是必须要设置的

	public static Properties getProps(String fromEmail) {
		fromEmail = fromEmail.toLowerCase();
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		if (fromEmail.endsWith("@sina.com")) {
			props.put(SMTP_HOST_KEY, "smtp.sina.com");
			props.put(SMTP_PORT_KEY, "465");
			props.put(SMTP_SSL_ENABLE_KEY, "true");
		} else if (fromEmail.endsWith("@163.com")) {
			props.put(SMTP_HOST_KEY, "smtp.163.com");
			props.put(SMTP_PORT_KEY, "465");
			props.put(SMTP_SSL_ENABLE_KEY, "true");
		} else if (fromEmail.endsWith("@qq.com")) {
			props.put(SMTP_HOST_KEY, "smtp.qq.com");
			props.put(SMTP_PORT_KEY, "465");
			props.put(SMTP_SSL_ENABLE_KEY, "true");
			MailSSLSocketFactory sf;
			try {
				sf = new MailSSLSocketFactory();
				sf.setTrustAllHosts(true);
				props.put("mail.smtp.ssl.socketFactory", sf);
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		} else {
			props.put(SMTP_HOST_KEY, Global.settings.get(SMTP_HOST_KEY));
			String port = Global.settings.getOrDefault(SMTP_PORT_KEY, "465");
			props.put(SMTP_PORT_KEY, port);

			String ssLDefault = "25".equals(port) ? "false" : "true";
			String sslEnable = Global.settings.getOrDefault(SMTP_SSL_ENABLE_KEY, ssLDefault);
			props.put(SMTP_SSL_ENABLE_KEY, sslEnable);

			String sttls = Global.settings.get(SMTP_STARTTLS_KEY);
			if (sttls != null)
				props.put(SMTP_STARTTLS_KEY, sttls);
		}
		return props;
	}

	public static void main(String[] a) throws MessagingException {
		String aa = String.format("<a href='https://b23.tv/%1$s'>%1$s</a>", "BVw13");
		System.out.println(aa);
		String time = String.format("%1$tF %1$tR%1$tz ", new Date(System.currentTimeMillis()));
		System.out.println(time);
	}

	public static void sendEmail(String title, String content) throws MessagingException {
		// 账号信息
		String fromEmail = Global.msgPushAccount, password = Global.msgPushToken,
				toEmail = Global.settings.getOrDefault(SMTP_TO_EMAIL_KEY, Global.msgPushAccount);
		// 获取SMTP配置
		Properties props = getProps(fromEmail);
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		// 控制台打印调试信息
		session.setDebug("true".equalsIgnoreCase(Global.settings.get(SMTP_DEBUG_KEY)));

		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		message.setSubject(title, "utf-8");
		// message.setText("<p>测试文本信息</p>", "utf-8");
		message.setContent(content, "text/html;charset=utf-8");
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipient(RecipientType.TO, new InternetAddress(toEmail));

		// 发送
		Transport.send(message);
	}
}
