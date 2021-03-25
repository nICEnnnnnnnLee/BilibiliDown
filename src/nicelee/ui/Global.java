package nicelee.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import nicelee.bilibili.annotations.Config;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.ui.item.DownloadInfoPanel;

public class Global {
	// 界面显示相关
	@Config(key = "bilibili.version", defaultValue = "v6.1", warning = false)
	public static String version; // 一般情况下，我们不会设置这个标签，这个用于测试
	@Config(key = "bilibili.theme", defaultValue = "true", eq_true = "default", valids = { "default", "system" })
	public static boolean themeDefault;
	@Config(key = "bilibili.button.style", defaultValue = "true", eq_true = "design", valids = { "design", "default" })
	public static boolean btnStyle = true;
	@Config(key = "bilibili.lockCheck", defaultValue = "false", valids = { "true", "false" })
	public static boolean lockCheck = false; // 简单的防多开功能
	@Config(key = "bilibili.alert.isAlertIfDownloded", defaultValue = "true", valids = { "true", "false" })
	public static boolean isAlertIfDownloded;
	@Config(key = "bilibili.alert.maxAlertPrompt", defaultValue = "5")
	public static int maxAlertPrompt = 5;

	public static ImageIcon backgroundImg;
	public static FrameWaiting frWaiting;
	public static FrameQRCode qr; // 二维码图片显示界面
	public static TabIndex index; // 主页界面
	// 下载相关
	public final static int MP4 = 0;
	public final static int FLV = 1;
	@Config(key = "bilibili.download.thumbUp", defaultValue = "false", valids = { "true", "false" })
	public volatile static boolean thumbUpAfterDownloaded; // 下载完成后是否给作品点赞
	@Config(key = "bilibili.download.playSound", defaultValue = "false", valids = { "true", "false" })
	public static boolean playSoundAfterMissionComplete; // 全部任务完成后是否播放提示音
	@Config(key = "bilibili.download.maxFailRetry", defaultValue = "3")
	public static int maxFailRetry;
	@Config(key = "bilibili.format", defaultValue = "0", valids = { "0", "1" })
	public static int downloadFormat = MP4; // 优先下载格式，如不存在该类型的源，那么将默认转为下载另一种格式
	@Config(key = "bilibili.savePath", defaultValue = "./download/")
	public static String savePath = "./download/"; // 下载文件保存路径
	@Config(key = "bilibili.download.poolSize", defaultValue = "1")
	public static ExecutorService downLoadThreadPool;// 下载线程池
	// 查询线程池(同一时间并发不能太多,为了保证任务面板的顺序，采用fixed(1))
	public static ExecutorService queryThreadPool = Executors.newFixedThreadPool(1);//
//	public static ExecutorService ccThreadPool = Executors.newFixedThreadPool(1);// 用于字幕下载
	public static TabDownload downloadTab; // 下载显示界面
	public static ConcurrentHashMap<DownloadInfoPanel, IDownloader> downloadTaskList = new ConcurrentHashMap<DownloadInfoPanel, IDownloader>();

	@Config(key = "bilibili.download.multiThread.count", defaultValue = "0")
	public static int multiThreadCnt; // 多线程下载开启的线程数 0为不开启多线程下载
	@Config(key = "bilibili.download.multiThread.minFileSize", defaultValue = "0", multiply = 1024 * 1024)
	public static long multiThreadMinFileSize; // 文件大小小于阈值(MB)，则不开启开启多线程下载
	@Config(key = "bilibili.download.multiThread.singlePattern", defaultValue = "github|ffmpeg|\\.jpg|\\.png|\\.webp|\\.xml")
	public static Pattern singleThreadPattern; // url匹配该正则，则不开启开启多线程下载
	@Config(key = "bilibili.repo", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean useRepo; // 从仓库判断是否需要下载
	@Config(key = "bilibili.repo.save", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean saveToRepo; // 使用仓库保存下载成功的记录
	@Config(key = "bilibili.name.format", defaultValue = "(:listName listName-)avTitle-pDisplay-clipTitle-qn")
	public static String formatStr;
	@Config(key = "bilibili.name.doAfterComplete", defaultValue = "true", valids = { "true", "false" })
	public static boolean doRenameAfterComplete = true;
	@Config(key = "bilibili.repo.definitionStrictMode", defaultValue = "false", eq_true = "on", valids = { "on",
			"off" }) /* 存在某一清晰度后, 在下载另一种清晰度时是否判断已完成 */
	public static boolean repoInDefinitionStrictMode; //

	// 登录相关
	@Config(key = "bilibili.user.userName", defaultValue = "", warning = false)
	public static String userName;
	@Config(key = "bilibili.user.password", defaultValue = "", warning = false)
	public static String password;
	@Config(key = "bilibili.user.delete", defaultValue = "true", valids = { "true", "false" })
	public static boolean deleteUserFile;
	@Config(key = "bilibili.user.login", defaultValue = "false", eq_true = "pwd", valids = { "pwd", "qr" })
	public static boolean pwdLogin; // 使用用户名密码登录方式
	@Config(key = "bilibili.user.login.pwd", defaultValue = "false", eq_true = "pwd", valids = { "auto", "manual" })
	public static boolean pwdAutoLogin; // 使用用户名密码自动登录方式
	@Config(key = "bilibili.user.login.pwd.autoCaptcha", defaultValue = "false", valids = { "true", "false" })
	public static boolean pwdAutoCaptcha; // 自动提取二维码

	public static boolean needToLogin = false;
	public static boolean isLogin = false;
	// 信息查询相关
	@Config(key = "bilibili.pageSize", defaultValue = "5")
	public static int pageSize = 5; // 当有分页时，每页显示个数
	@Config(key = "bilibili.pageDisplay", defaultValue = "listAll")
	public static String pageDisplay = "listAll"; // 分页查询时，结果展示方式 listAll/promptAll(promptAll 逐渐弃用)
	// 临时文件相关
	@Config(key = "bilibili.restrictTempMode", defaultValue = "true", eq_true = "on", valids = { "on", "off" })
	public static boolean restrictTempMode;
	// FFMPEG 路径
	@Config(key = "bilibili.ffmpegPath", defaultValue = "ffmpeg")
	public static String ffmpegPath;
	@Config(key = "bilibili.flv.ffmpeg", defaultValue = "false")
	public static boolean flvUseFFmpeg = false;
	// 批量下载设置相关
	@Config(key = "bilibili.menu.download.plan", defaultValue = "1")
	public static int menu_plan; // 0 下载每个tab页的第一个视频； 1 下载每个Tab页的全部视频
	@Config(key = "bilibili.menu.download.qn", defaultValue = "1080P")
	public static String menu_qn; // 菜单批量下载时, 优先下载清晰度 详见VideoQualityEnum
	@Config(key = "bilibili.tab.download.qn", defaultValue = "1080P")
	public static String tab_qn; // 标签页批量下载时, 优先下载清晰度 详见VideoQualityEnum
	// 字幕弹幕相关
	@Config(key = "bilibili.cc.lang", defaultValue = "zh-CN")
	public static String cc_lang; // 字幕优先语种,如zh-CN等, 详见 release/wiki/langs.txt

	// 根据System.getProperties()初始化配置
	public static void init() {
		for (Field field : Global.class.getDeclaredFields()) {
			Config config = field.getAnnotation(Config.class);
			if (config != null) {
//				if (!checkValid(config.defaultValue(), config.valids())) {
//					System.err.printf("%s 值为 %s, 不在合法范围内 %s, 将使用默认值\n", config.key(), config.defaultValue(),
//							Arrays.asList(config.valids()));
//				}
				// 先从设置取值
				String valueFromSettings = System.getProperty(config.key());
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
			} else if (field.getType().equals(ExecutorService.class)) {
				int poolSize = Integer.parseInt(value);
				field.set(null, Executors.newFixedThreadPool(poolSize));
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
