package nicelee.server.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketDealer extends PathDealer implements Runnable {

	// 与客户端之间的联系
	BufferedReader in;
	BufferedWriter out;
	OutputStream outRaw;

	public SocketDealer(Socket socketClient) {
		super(socketClient);
	}

	final static Pattern urlPattern = Pattern.compile("^(?:GET|POST) ([^ \\?]+)\\??([^ \\?]*) HTTP.*$");
	final static Pattern contentLengthPattern = Pattern.compile("^content-length *: *([0-9]+)$");
	final static Pattern headersPattern = Pattern.compile("^([^:]+) *: *(.+)$");
	@Override
	public void run() {
		String path = null, param = null, data = null;
		HashMap<String, String> headersMap = new HashMap<>(16, 0.999f);
		int dataLen = -1;
		try {
			in = new BufferedReader(new InputStreamReader(socketClient.getInputStream(), "utf-8"));
			outRaw = socketClient.getOutputStream();
			out = new BufferedWriter(new OutputStreamWriter(outRaw, "utf-8"));
			
			// 读取url请求
			String line = null;
			while ((line = in.readLine()) != null) {
				// 处理Path
				Matcher matcher = urlPattern.matcher(line);
				if(path == null && matcher.find()) {
					// System.out.println("正在处理请求: " + line);
					path = matcher.group(1);
					param = matcher.group(2);
					continue;
				}
				
				// 处理Content-Length
				matcher = contentLengthPattern.matcher(line.toLowerCase());
				if(dataLen<0 && matcher.find()) {
					dataLen = Integer.parseInt(matcher.group(1));
				}
				
				// 处理headers
				matcher = headersPattern.matcher(line.toLowerCase());
				if(matcher.find()) {
					headersMap.put(matcher.group(1), matcher.group(2));
					//System.out.printf("header-%s : %s\r\n", matcher.group(1), matcher.group(2));
				}
				
				// 处理结尾
				if(line.length() == 0) {
					if(dataLen > 0) {
						char[] buffer = new char[dataLen];
						in.read(buffer);
						data = new String(buffer);
						//System.out.println(data);
					}
					break;
				}
			}
			
			// 处理请求并返回内容
			dealRequest(out, outRaw, path, param, data, headersMap);
			
		} catch (SocketException e) {
		} catch (IOException e) {
		} catch (IndexOutOfBoundsException e) {
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			//System.out.println(path + " -线程结束...");
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
			try {
				socketClient.close();
			} catch (Exception e) {
			}
		}
	}
	
}
