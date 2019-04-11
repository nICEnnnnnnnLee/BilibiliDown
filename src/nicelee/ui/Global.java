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
	public static ExecutorService downLoadThreadPool;//下载线程池
	public static ExecutorService queryThreadPool = Executors.newFixedThreadPool(20);//查询线程池(同一时间并发不能太多)
	public static TabDownload downloadTab; //下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil>  downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil>();
	public static int activeTask = 0;
	//登录相关
	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	public static FrameQRCode qr; //二维码图片显示界面
	public static TabIndex index; //主页界面
	
	//信息查询相关
	public static int pageSize = 5; // 当有分页时，每页显示个数
	
	//界面显示相关
	
	
}
