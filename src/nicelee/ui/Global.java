package nicelee.ui;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nicelee.ui.item.DownloadInfoPanel;
import nicelee.util.HttpRequestUtil;

public class Global {
	//下载相关
	public final static int MP4 = 0;
	public final static int FLV = 1;
	
	public static int downloadFormat = MP4;
	public static String savePath = "./download/";
	public static ExecutorService downLoadThreadPool = Executors.newFixedThreadPool(3);  //下载线程池
	public static TabDownload downloadTab; //下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil>  downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil>();
	
	//登录相关
	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	public static FrameQRCode qr; //二维码图片显示界面
	public static TabIndex index; //主页界面
}
