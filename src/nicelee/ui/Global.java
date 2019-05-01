package nicelee.ui;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nicelee.bilibili.downloaders.IDownloader;
import nicelee.ui.item.DownloadInfoPanel;

public class Global {
	// 界面显示相关
	public static String version = "v3.5";
	public static boolean themeDefault = true;
	
	public static boolean isAlertIfDownloded = true;
	public static int maxAlertPrompt = 5;
	// 下载相关
	public final static int MP4 = 0;
	public final static int FLV = 1;

	public static int downloadFormat = MP4; //优先下载格式，如不存在该类型的源，那么将默认转为下载另一种格式
	public static String savePath = "./download/"; // 下载文件保存路径
	public static ExecutorService downLoadThreadPool;// 下载线程池
	public static ExecutorService queryThreadPool = Executors.newFixedThreadPool(1);// 查询线程池(同一时间并发不能太多, 为了保证任务面板的顺序，采用fixed(1))
	public static TabDownload downloadTab; // 下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, IDownloader> downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, IDownloader>();
	
	public static boolean useRepo = true; //从仓库判断是否需要下载
	public static boolean saveToRepo = true; //使用仓库保存下载成功的记录
	
	public static String formatStr = "avTitle-pDisplay-clipTitle-qn";
	public static boolean doRenameAfterComplete = true;
	/* 存在某一清晰度后, 在下载另一种清晰度时是否判断已完成*/
	public static boolean repoInDefinitionStrictMode = false; //
	//public static int totalTask = 0, activeTask = 0, pauseTask = 0, doneTask = 0, queuingTask = 0;//用于下载任务统计
	// 登录相关
	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	public static FrameQRCode qr; // 二维码图片显示界面
	public static TabIndex index; // 主页界面

	// 信息查询相关
	public static int pageSize = 5; // 当有分页时，每页显示个数
	public static String pageDisplay; // 当有分页时，每页显示个数
	
	// 临时文件相关
	public static boolean restrictTempMode = true;
	
}
