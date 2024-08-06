package nicelee.ui.thread;

import java.awt.Component;
import java.awt.HeadlessException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.pushers.Push;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.custom.System;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

public class BatchDownloadRbyRThread extends BatchDownloadThread {

	private static ConcurrentHashMap<ClipInfo, TaskInfo> currentTaskList;
	private static long batchDownloadBeginTime;
	private static long batchDownloadEndTime;
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	final static SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy-MM-dd");
	static {
		TimeZone _8zone = TimeZone.getTimeZone("GMT+8:00");
		TimeZone.setDefault(_8zone);
		sdf.setTimeZone(_8zone);
		sdfToday.setTimeZone(_8zone);
	}
	
	final String plans[];
	public BatchDownloadRbyRThread(String configFileName) {
		super(configFileName);
		this.setName("Thread-BatchDownload round by round");
		this.setDaemon(true);
		//String plan = "06:00~02:00=>r(300,480); 02:00~04:00=>~06:00+r(0,360); 00:00~00:00=>r(30,30)";
		plans = Global.batchDownloadPlan.split(";");
	}
	
	public void runBatchDownloadOnce() {
		taskOrderNo = 0;
		batchDownloadBeginTime = System.currentTimeMillis();
		Logger.printf("--%s批量下载开始", sdf.format(batchDownloadBeginTime));
		currentTaskList = new ConcurrentHashMap<>();
		super.run();
		waitUtilAllTaskDone(false);
		waitUtilAllTaskDone(true);
		batchDownloadEndTime = System.currentTimeMillis();
		Logger.printf("--%s批量下载完毕", sdf.format(batchDownloadEndTime));
		// push消息
		new Push().push(currentTaskList, batchDownloadBeginTime, batchDownloadEndTime);
		// 将一切置空
		currentTaskList = null;
	}

	private void waitUtilAllTaskDone(boolean isStrictMode) {
		List<TaskInfo> tasks = new ArrayList<>(currentTaskList.values());
		Collections.sort(tasks, (a, b) ->{
			return a.getOrderNum() - b.getOrderNum();
		});
		for (TaskInfo task : tasks) {
			// 一直循环，等待结果
			int nullCnt = 0;
			while (isTaskRunning(task)) {
				try {
					if(task.getStatus() == null) {
						if(isStrictMode) {
							task.setStatus("not sent to download queue");
							break;
						} else {
							nullCnt ++;
							if(nullCnt > 2 && nullCnt * 10000 > Global.sleepAfterDownloadQuery) {
								break;
							}
						}
					}
					sleep(10000);
					Logger.printf("等待任务完毕%s %s %s", task.getClip().getAvId(),
							task.getClip().getAvTitle(),
							task.getClip().getTitle());
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private boolean isTaskRunning(TaskInfo task) {
		if(task.getStatus() != null) {
			if("just put in download panel".equals(task.getStatus())) {
				DownloadInfoPanel dp = new DownloadInfoPanel(task.getClip(), 0);
				return Global.downloadTaskList.containsKey(dp); // 这是防止下载面板里面人工删除了该任务
//				boolean isTaskInDownPanel = false;
//				for(DownloadInfoPanel dp : Global.downloadTaskList.keySet()) {
//					if(dp.getClipInfo().equals(task.getClip())) {
//						isTaskInDownPanel = true;
//						break;
//					}
//				}
//				return isTaskInDownPanel; 
			} else {
				return false;
			}
		}
		return true;
	}

	int taskOrderNo = 0;
	@Override
	public void addTask(ClipInfo clip) {
		if (currentTaskList != null) {
			currentTaskList.put(clip, new TaskInfo(clip, taskOrderNo));
			taskOrderNo ++;
		}
	}
	
	@Override
	public void showMessageDialog(Component parentComponent, String message, String title, int messageType)
			throws HeadlessException {
		Logger.println(message);
		Logger.println(title);
	}

	public static void taskSucceed(ClipInfo clip, String fileName, String fileSize, String qn) {
		if (currentTaskList != null) {
			TaskInfo task = currentTaskList.get(clip);
			if (task != null) {
				task.setFileName(fileName);
				task.setFileSize(fileSize);
				task.setQn(qn);
				task.setStatus("success");
			}
		}
	}

	public static void taskFail(ClipInfo clip, String status) {
		if (currentTaskList != null) {
			TaskInfo task = currentTaskList.get(clip);
			if (task != null) {
				task.setStatus(status);
			}
		}
	}

	@Override
	public void run() {
		try {
			long lastCookieRefreshTime = System.currentTimeMillis();
			while (true) {
				long currentTime = System.currentTimeMillis();
				// 必须确保当前没有下载任务
				if(Global.downloadTab.activeTask == 0) {
					// 把下载面板的所有任务清空(堆积过多可能引起内存溢出)
					for(DownloadInfoPanel dp : Global.downloadTaskList.keySet()) {
						dp.removeTask(true);
					}
					// 尝试刷新cookie (已登录且不需要浏览器交互且距离上次刷新间隔大于一天)
					if (Global.isLogin && !Global.runWASMinBrowser && currentTime - lastCookieRefreshTime > MILLI_SECONDS_OF_ONE_DAY) {
						CookieRefreshThread.showTips = false;
						CookieRefreshThread thCR = CookieRefreshThread.newInstance();
						thCR.start();
						try {
							thCR.join();
						} catch (InterruptedException e1) {
						}
						CookieRefreshThread.showTips = true;
						lastCookieRefreshTime = currentTime;
					}
				}
				runBatchDownloadOnce();
				try {
					sleep(timeToSleep(plans, System.currentTimeMillis()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] a) {
		try {
			SimpleDateFormat sdf_0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf_0.setTimeZone(TimeZone.getTimeZone("GMT"));
			// Wed, 29 Jan 2025 08:21:29 GMT
			long expTime = sdf_0.parse("2025-01-29 08:21:29").getTime();
			long nutTime = 1706602889L * 1000L;
			long delta = expTime- nutTime;
			System.out.println(expTime);
			System.out.println(nutTime);
			System.out.println(delta/1000/60/60/24);
			System.exit(1);
			BatchDownloadRbyRThread ooxx = new BatchDownloadRbyRThread("");
			String plan = "06:00~02:00=>r(300,480); 02:00~04:00=>~06:00+r(0,360); 00:00~00:00=>r(30,31)";
			String[] plans = plan.split(";");
			
			long curTime = sdf.parse("2024-01-21 21:01").getTime(); // 返回随机 [300,480) s
			for(int i=0;i<10; i++) {
				long tt = ooxx.timeToSleep(plans, curTime);
				System.out.println("sleep(s) " + tt/1000);
			}
			System.out.println("-----------------------------");
			curTime = sdf.parse("2024-01-21 02:01").getTime(); // 到2024-01-21 06:01 有239min 再加上 [0~360)s
			
			for(int i=0;i<10; i++) {
				long tt = ooxx.timeToSleep(plans, curTime);
				System.out.println("sleep(min) " + tt/1000/60);
			}
			
			System.out.println("-----------------------------");
			curTime = sdf.parse("2024-01-21 04:01").getTime(); // 返回 30s
			
			for(int i=0;i<10; i++) {
				long tt = ooxx.timeToSleep(plans, curTime);
				System.out.println("sleep(s) " + tt/1000);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	final static long MILLI_SECONDS_OF_ONE_DAY = 1000 * 60 * 60 * 24;
	final static Pattern rollTimePattern = Pattern.compile("r\\(([0-9]+),([0-9]+)\\)");
	final static Pattern toTimePattern = Pattern.compile("~([0-9]{2}:[0-9]{2})");
	/**
	 * <p> 06:00~02:00=>r(300,480); 02:00~04:00=>~06:00+r(0,360); 00:00~00:00=>r(30,30) </p>
	 * <p> 北京时间、时间都是闭区间、roll点是左闭右开、左边优先级最高 </p>
	 * <p> 每天早上6点到第二天早上2点(但实际只包括当天剩余时间，即到第二天零点为止)，随机300s~480s(5~8min) </p>
	 * <p> 每天早上2点到早上4点，sleep到早上6点再加一个随机时间 0s~480s(0~6min) </p>
	 * <p> 其它时间，固定30s </p>
	 * @param plans				字符串数组，其中字符串格式类似于06:00~02:00=>r(300,480) 或 02:00~04:00=>~06:00+r(0,360)
	 * @param currentTime		当前时间
	 * @return					休眠时间
	 * @throws ParseException
	 */
	public long timeToSleep(String[] plans, long currentTime) throws ParseException {
		Random random = new Random();
		String today = sdfToday.format(currentTime);
		for(String plan: plans) { 
			// 06:00~02:00:r(300,480)
			// 02:00~04:00:~06:00+r(0,360)
			String[] params = plan.split("=>");
			// 获取时间的左右区间
			String[] timePeriod = params[0].split("~");
			String lTimeStr = today + " " + timePeriod[0].trim();
			long lTime = sdf.parse(lTimeStr).getTime();
			String rTimeStr = today + " " + timePeriod[1].trim();
			long rTime = sdf.parse(rTimeStr).getTime();
			if(rTime <= lTime)
				rTime += MILLI_SECONDS_OF_ONE_DAY;
			if(currentTime >= lTime && currentTime <= rTime) {
				long rollTime = 0L, toTimeDelta = 0L;
				// 判断roll点的时间
				Matcher rm = rollTimePattern.matcher(params[1]);
				if(rm.find()) {
					int rLeft = Integer.parseInt(rm.group(1)) * 1000;
					int rRight = Integer.parseInt(rm.group(2)) * 1000;
					rollTime = rRight > rLeft? rLeft + random.nextInt(rRight - rLeft): rLeft;
				}
				// 判断到指定时刻需要多少时间
				Matcher tm = toTimePattern.matcher(params[1]);
				if(tm.find()) {
					long toTime = sdf.parse(today + " " + tm.group(1)).getTime();
					if(toTime < currentTime)
						toTime += MILLI_SECONDS_OF_ONE_DAY;
					toTimeDelta = toTime - currentTime;
				}
				long timeToSleep = toTimeDelta + rollTime;
				Logger.printf("现在时间是 %s, 将要等待%dmin", 
						sdf.format(System.currentTimeMillis()), timeToSleep/60000);
				return timeToSleep;
			}
		}
		throw new IllegalArgumentException("一键下载计划时间配置错误" + currentTime);
	}
}
