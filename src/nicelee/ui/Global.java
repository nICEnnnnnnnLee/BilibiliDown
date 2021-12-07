package nicelee.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import nicelee.bilibili.annotations.Config;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.bilibili.util.net.TrustAllCertSSLUtil;
import nicelee.ui.item.DownloadInfoPanel;

public class Global {
	// 界面显示相关
	@Config(key = "bilibili.version", defaultValue = "v6.8", warning = false)
	public static String version; // 一般情况下，我们不会设置这个标签，这个用于测试
	@Config(key = "bilibili.theme", note = "界面主题", defaultValue = "true", eq_true = "default", valids = { "default", "system" })
	public static boolean themeDefault;
	@Config(key = "bilibili.button.style", note = "Button样式", defaultValue = "true", eq_true = "design", valids = { "design", "default" })
	public static boolean btnStyle = true;
	@Config(key = "bilibili.lockCheck", note = "防止多开", defaultValue = "false", valids = { "true", "false" })
	public static boolean lockCheck = false; // 简单的防多开功能
	@Config(key = "bilibili.alert.isAlertIfDownloded", note = "提交已完成的视频时是否弹出提示", defaultValue = "true", valids = { "true", "false" })
	public static boolean isAlertIfDownloded;
	@Config(key = "bilibili.alert.maxAlertPrompt", note = "提交已完成的视频时弹出提示框的最大数量", defaultValue = "5")
	public static int maxAlertPrompt = 5;

	public static ImageIcon backgroundImg;
	public static FrameWaiting frWaiting;
	public static FrameQRCode qr; // 二维码图片显示界面
	public static TabIndex index; // 主页界面
	// 下载相关
	public final static int MP4 = 0;
	public final static int FLV = 1;
	@Config(key = "bilibili.download.thumbUp", note = "下载完成后给作品点赞", defaultValue = "false", valids = { "true", "false" })
	public volatile static boolean thumbUpAfterDownloaded; // 下载完成后是否给作品点赞
	@Config(key = "bilibili.download.playSound", note = "全部任务完成后播放提示音", defaultValue = "false", valids = { "true", "false" })
	public static boolean playSoundAfterMissionComplete; // 全部任务完成后是否播放提示音
	@Config(key = "bilibili.download.maxFailRetry", note = "下载失败后重试次数", defaultValue = "3")
	public static int maxFailRetry;
	@Config(key = "bilibili.format", defaultValue = "0", valids = { "0", "1" })
	public static int downloadFormat = MP4; // 优先下载格式，如不存在该类型的源，那么将默认转为下载另一种格式
	@Config(key = "bilibili.savePath", note = "保存路径", defaultValue = "./download/")
	public static String savePath = "./download/"; // 下载文件保存路径
	@Config(key = "bilibili.download.poolSize", note = "下载任务线程池大小", defaultValue = "1")
	public static int downloadPoolSize;// 下载线程池
	public static ExecutorService downLoadThreadPool;// 下载线程池
	// 查询线程池(同一时间并发不能太多,为了保证任务面板的顺序，采用fixed(1))
	public static ExecutorService queryThreadPool = Executors.newFixedThreadPool(1);//
//	public static ExecutorService ccThreadPool = Executors.newFixedThreadPool(1);// 用于字幕下载
	public static JTabbedPane tabs; // 下载显示界面
	public static TabDownload downloadTab; // 下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, IDownloader> downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, IDownloader>();

	@Config(key = "bilibili.download.multiThread.count", note = "单个下载任务开启线程数(0,1为不开启多线程)", defaultValue = "0")
	public static int multiThreadCnt; // 多线程下载开启的线程数 0为不开启多线程下载
	@Config(key = "bilibili.download.multiThread.minFileSize", note = "文件大小小于阈值(整数, 单位MB)，则不开启多线程下载", defaultValue = "0", multiply = 1024 * 1024)
	public static long multiThreadMinFileSize; // 文件大小小于阈值(MB)，则不开启开启多线程下载
	@Config(key = "bilibili.download.multiThread.singlePattern", note = "url匹配该正则，则不开启多线程下载", defaultValue = "github|ffmpeg|\\.jpg|\\.png|\\.webp|\\.xml")
	public static Pattern singleThreadPattern; // url匹配该正则，则不开启开启多线程下载
	@Config(key = "bilibili.repo", note = "创建下载任务前判断是否已下载(会自动保存下载的BV号)", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean useRepo; // 从仓库判断是否需要下载
	@Config(key = "bilibili.repo.save", note = "在不判断已下载时仍然保存下载的BV号", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean saveToRepo; // 使用仓库保存下载成功的记录
	@Config(key = "bilibili.name.format", note = "自定义下载文件名称", defaultValue = "(:listName listName-)avTitle-pDisplay-clipTitle-qn")
	public static String formatStr;
	@Config(key = "bilibili.name.date.favTime.pattern", note = "收藏时间格式化", defaultValue = "yyMMdd")
	public static String favTimeFormat;
	@Config(key = "bilibili.name.date.cTime.pattern", note = "发布/更新时间格式化", defaultValue = "yyMMdd")
	public static String cTimeFormat;
	@Config(key = "bilibili.name.doAfterComplete", note = "下载完成后自动重命名", defaultValue = "true", valids = { "true", "false" })
	public static boolean doRenameAfterComplete = true;
	@Config(key = "bilibili.repo.definitionStrictMode", note = "是否将同一视频不同清晰度看作不同任务", defaultValue = "false", eq_true = "on", valids = { "on",
			"off" }) /* 存在某一清晰度后, 在下载另一种清晰度时是否判断已完成 */
	public static boolean repoInDefinitionStrictMode; //

	// 登录相关
	@Config(key = "bilibili.user.userName", defaultValue = "", warning = false)
	public static String userName;
	@Config(key = "bilibili.user.password", defaultValue = "", warning = false)
	public static String password;
	@Config(key = "bilibili.user.delete", defaultValue = "true", valids = { "true", "false" })
	public static boolean deleteUserFile;
	@Config(key = "bilibili.user.login", note = "登录方式", defaultValue = "false", eq_true = "pwd", valids = { "pwd", "qr" })
	public static boolean pwdLogin; // 使用用户名密码登录方式
	@Config(key = "bilibili.user.login.pwd", defaultValue = "false", eq_true = "pwd", valids = { "auto", "manual" })
	public static boolean pwdAutoLogin; // 使用用户名密码自动登录方式
	@Config(key = "bilibili.user.login.pwd.autoCaptcha", note = "用户名密码登录时是否自动尝试解析验证码", defaultValue = "false", valids = { "true", "false" })
	public static boolean pwdAutoCaptcha; // 自动提取验证码

	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	// 信息查询相关
	@Config(key = "bilibili.pageSize", note = "分页查询时，每页显示av个数(单个av可能不止一个视频)", defaultValue = "5")
	public static int pageSize = 5; // 当有分页时，每页显示个数
	@Config(key = "bilibili.pageDisplay", defaultValue = "listAll")
	public static String pageDisplay = "listAll"; // 分页查询时，结果展示方式 listAll/promptAll(promptAll 逐渐弃用)
	// 临时文件相关
	@Config(key = "bilibili.restrictTempMode", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean restrictTempMode;
	// FFMPEG 路径
	@Config(key = "bilibili.ffmpegPath", note = "ffmpeg路径", defaultValue = "ffmpeg")
	public static String ffmpegPath;
	@Config(key = "bilibili.flv.ffmpeg", note = "FLV合并时是否调用ffmpeg", defaultValue = "false", valids = { "true", "false" })
	public static boolean flvUseFFmpeg = false;
	// 批量下载设置相关
	@Config(key = "bilibili.menu.download.plan", defaultValue = "1")
	public static int menu_plan; // 0 下载每个tab页的第一个视频； 1 下载每个Tab页的全部视频
	@Config(key = "bilibili.menu.download.qn", note = "菜单栏下载时的优先清晰度", defaultValue = "1080P")
	public static String menu_qn; // 菜单批量下载时, 优先下载清晰度 详见VideoQualityEnum
	@Config(key = "bilibili.tab.download.qn", note = "标签页下载时的优先清晰度", defaultValue = "1080P")
	public static String tab_qn; // 标签页批量下载时, 优先下载清晰度 详见VideoQualityEnum
	// 字幕弹幕相关
	@Config(key = "bilibili.cc.lang", note = "CC字幕优先语种", defaultValue = "zh-CN")
	public static String cc_lang; // 字幕优先语种,如zh-CN等, 详见 release/wiki/langs.txt
	// 代理
//	@Config(key = "http.proxyHost", note = "HTTP代理Host", defaultValue = "")
//	private static String httpProxyHost;
//	@Config(key = "http.proxyPort", note = "HTTP代理Port", defaultValue = "0")
//	private static int httpProxyPort;
//	@Config(key = "https.proxyHost", note = "HTTPS代理Host", defaultValue = "")
//	private static String httpsProxyHost;
//	@Config(key = "https.proxyPort", note = "HTTP代理Port", defaultValue = "0")
//	private static int httpsProxyPort;
	@Config(key = "proxyHost", note = "HTTP/HTTPS代理Host", defaultValue = "", warning = false)
	private static String proxyHost;
	@Config(key = "proxyPort", note = "HTTP/HTTPS代理Port", defaultValue = "", warning = false)
	private static String proxyPort;
	@Config(key = "socksProxyHost", note = "SOCKS 代理Host", defaultValue = "", warning = false)
	private static String socksProxyHost;
	@Config(key = "socksProxyPort", note = "SOCKS 代理Port", defaultValue = "", warning = false)
	private static String socksProxyPort;
	// https://github.com/nICEnnnnnnnLee/BilibiliDown/issues/77
	@Config(key = "bilibili.https.allowInsecure", note = "跳过证书验证", defaultValue = "false", valids = { "true", "false" })
	private static boolean allowInsecure;

	final public static HashMap<String, String> settings = new HashMap<>();

	// 根据Global.settings 初始化配置到具体属性值
	public static void init() {
		for (Field field : Global.class.getDeclaredFields()) {
			Config config = field.getAnnotation(Config.class);
			if (config != null) {
				// 先从设置取值
				String valueFromSettings = Global.settings.get(config.key());
				// 检查合法性, 合法则继续使用, 否则使用默认值
				if (checkValid(valueFromSettings, config.valids())) {
					setValue(field, valueFromSettings, false, config);
				} else {
					if (config.warning())
						System.err.printf("%s 值为 %s, 不在合法范围内 %s, 将使用默认值\n", config.key(), valueFromSettings,
								Arrays.asList(config.valids()));
					setValue(field, config.defaultValue(), true, config);
				}
			}
		}
		// 特殊处理
		downLoadThreadPool = Executors.newFixedThreadPool(downloadPoolSize);
		String savePath = Global.savePath;
		if (savePath.endsWith("\\")) {
			savePath = savePath.substring(0, savePath.length() - 1) + "/";
		} else if (!savePath.endsWith("/")) {
			savePath += "/";
		}
		System.out.println("savePath: " + savePath);
		Global.savePath = savePath;
		Global.saveToRepo = Global.useRepo || Global.saveToRepo;
		if (Global.deleteUserFile) {
			File user = ResourcesUtil.search("config/user.config");
			if (user != null)
				user.delete();
		}
		String[] imgSuffixs = { ".png", ".jpg" };
		for (String suffix : imgSuffixs) {
			File img = ResourcesUtil.search("config/background" + suffix);
			if (img != null) {
				Global.backgroundImg = new ImageIcon(img.getPath());
				break;
			}
		}
		if (Global.backgroundImg == null)
			Global.backgroundImg = new ImageIcon(Global.class.getResource("/resources/background.png"));
		if(proxyHost != null && proxyPort != null) {
			System.setProperty("proxyHost", proxyHost);
			System.setProperty("proxyPort", proxyPort);
		}
		if(socksProxyHost != null && socksProxyPort != null) {
			System.setProperty("socksProxyHost", socksProxyHost);
			System.setProperty("socksProxyPort", socksProxyPort);
		}
		// 跳过HTTPS证书验证
		try {
			System.out.println("allowInsecure:" + allowInsecure);
			if (allowInsecure)
				HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllCertSSLUtil.getFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean checkValid(String value, String[] valids) {
		if (value == null)
			return false;
		if (valids.length == 0)
			return true;
		for (String valid : valids) {
			if (valid.equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}

	private static void setValue(Field field, String value, boolean isDefaultValue, Config config) {
		try {
			if (field.getType().equals(String.class)) {
				if (isDefaultValue && value.isEmpty())
					field.set(null, null);
				else
					field.set(null, value);
			} else if (field.getType().equals(int.class) || field.getType().equals(long.class)) {
				if (isDefaultValue)
					field.set(null, Integer.parseInt(value));
				else
					field.set(null, Integer.parseInt(value) * config.multiply());
			} else if (field.getType().equals(boolean.class)) {
				if (isDefaultValue)
					field.set(null, "true".equalsIgnoreCase(value));
				else
					field.set(null, config.eq_true().equalsIgnoreCase(value));
			} else if (field.getType().equals(Pattern.class)) {
				field.set(null, Pattern.compile(value));
			} else {
				System.err.println(config.key() + " 配置未能生效!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	static {
//		init();
//		System.exit(1);
//	}
}
