package nicelee.bilibili.util.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import nicelee.bilibili.util.HttpHeaders;

/**
 * TODO 直播弹幕
 *
 */
public class WebSocket extends WebSocketClient {

	public static void main(String[] args) {
		WebSocket wss;
		try {
			// 请求头部
			HashMap<String, String> headers = new HttpHeaders().getHeaders();
			//headers.put("Origin", "http://coolaf.com");
//			List<HttpCookie> cookies = HttpCookies.convertCookies("");
//			for (HttpCookie cookie : cookies) {
//				headers.put(cookie.getName(), cookie.getValue());
//			}
			
			// 请求连接
			// URI url = new URI("wss://broadcast.chat.bilibili.com:7823/sub");
			//URI url = new URI("ws://123.207.167.163:9010/ajaxchattest");
			URI url = new URI("ws://121.40.165.18:8800");
			
			// 实例化
			wss = new WebSocket(url, headers);
			//wss = new WebSocket(url);
			// SSL设置信任所有证书
			//wss.setSocketFactory(TrustAllCertSSLUtil.getFactory());
			// wss.connect();
			wss.connectBlocking();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String line = reader.readLine();
				if (line.equals("close")) {
					wss.closeBlocking();
				} else if (line.equals("open")) {
					wss.reconnect();
				} else {
					wss.send(line);
					//wss.sendPing();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebSocket(URI serverUri) {
		super(serverUri);
	}

	public WebSocket(URI serverUri, Map<String, String> httpHeaders) {
		super(serverUri, httpHeaders);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected");

	}

	@Override
	public void onMessage(String message) {
		System.out.println("got: " + message);

	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Disconnected");
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();

	}
}
