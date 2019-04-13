package nicelee.ui;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nicelee.ui.item.DownloadInfoPanel;
import nicelee.util.HttpRequestUtil;

public class Global {
	// 下载相关
	public final static int MP4 = 0;
	public final static int FLV = 1;

	public static int downloadFormat = MP4; //优先下载格式，如不存在该类型的源，那么将默认转为下载另一种格式
	public static String savePath = "./download/"; // 下载文件保存路径
	public static ExecutorService downLoadThreadPool;// 下载线程池
	public static ExecutorService queryThreadPool = Executors.newFixedThreadPool(1);// 查询线程池(同一时间并发不能太多, 为了保证任务面板的顺序，采用fixed(1))
	public static TabDownload downloadTab; // 下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil> downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, HttpRequestUtil>();
	public static LinkedHashMap<String, Integer> nameQnMap; // 清晰度名称转数值字典
	public static LinkedHashMap<Integer, String> qnNameMap; // 清晰度数值转名称字典
	
	//public static int totalTask = 0, activeTask = 0, pauseTask = 0, doneTask = 0, queuingTask = 0;//用于下载任务统计
	// 登录相关
	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	public static FrameQRCode qr; // 二维码图片显示界面
	public static TabIndex index; // 主页界面

	// 信息查询相关
	public static int pageSize = 5; // 当有分页时，每页显示个数

	// 界面显示相关
	public static boolean themeDefault = true;
	
	// 临时文件相关
	public static boolean restrictTempMode = true;
	
	static {
		nameQnMap = new LinkedHashMap<>();
		nameQnMap.put("1080P60", 116);
		nameQnMap.put("1080P+", 112);
		nameQnMap.put("1080P", 80);
		nameQnMap.put("720P60", 74);
		nameQnMap.put("720P", 64);
		nameQnMap.put("480P", 32);
		nameQnMap.put("320P", 16);
		qnNameMap = new LinkedHashMap<>();
		qnNameMap.put(116, "高清1080P60");
		qnNameMap.put(112, "高清1080P+");
		qnNameMap.put(80, "高清1080P");
		qnNameMap.put(74, "高清720P60");
		qnNameMap.put(64, "高清720P");
		qnNameMap.put(32, "清晰480P");
		qnNameMap.put(16, "流畅320P");
		for(Entry<String, Integer> entry: nameQnMap.entrySet()) {
			qnNameMap.put(entry.getValue(), entry.getKey());
		}
	}
}
