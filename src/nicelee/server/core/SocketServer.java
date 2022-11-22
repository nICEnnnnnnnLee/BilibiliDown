package nicelee.server.core;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

	public static void main(String[] args) {
		SocketServer ss = new SocketServer(8081);
		ss.startServer();
	}

	int portServerListening;

	boolean isRun = true;
	public static ExecutorService httpThreadPool;
	ServerSocket serverSocket;

	public SocketServer(int portServerListening) {
		this.portServerListening = portServerListening;
		httpThreadPool = Executors.newFixedThreadPool(20);
	}

	public SocketServer(int portServerListening, int threadPoolSize) {
		this.portServerListening = portServerListening;
		httpThreadPool = Executors.newFixedThreadPool(threadPoolSize);
	}

	/**
	 *  关闭服务器
	 */
	public void stopServer() {
		try {
			isRun = false;
			serverSocket.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		System.out.println("正在关闭 SocketServer: 服务器... ");
	}

	/**
	 *  打开服务器
	 */
	public void startServer() {
		Socket socket = null;
		System.out.println("SocketServer: 服务器监听开始... ");
		try {
			serverSocket = new ServerSocket(portServerListening, 30, Inet4Address.getLoopbackAddress());
			// serverSocket.setSoTimeout(300000);

			while (isRun) {
				try {
					socket = serverSocket.accept();
				} catch (SocketTimeoutException e) {
					continue;
				} catch (SocketException e) {
					break;
				}
				SocketDealer dealer = new SocketDealer(socket);
				httpThreadPool.execute(dealer);
				// 只接受本地或局域网请求
//				String addr = socket.getInetAddress().getHostAddress();
//				if (addr.equals("127.0.0.1") || addr.startsWith("192.168.")) {
//					SocketDealer dealer = new SocketDealer(socket);
//					httpThreadPool.execute(dealer);
//				} else
//					socket.close();

			}
			httpThreadPool.shutdownNow();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}
		System.out.println("SocketServer: 服务器已经关闭... ");
	}
}
