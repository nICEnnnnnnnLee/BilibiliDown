package nicelee.ui.thread;

import java.awt.Component;
import java.awt.HeadlessException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;

public class BatchDownloadRbyRThread extends BatchDownloadThread {

	private static ConcurrentHashMap<ClipInfo, TaskInfo> currentTaskList;
	private static long batchDownloadBeginTime;
	private static long batchDownloadEndTime;
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	final static SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy-MM-dd");
	static {
		TimeZone _8zone = TimeZone.getTimeZone("GMT+8:00");
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
		batchDownloadBeginTime = System.currentTimeMillis();
		Logger.printf("--%s批量下载开始\n", sdf.format(batchDownloadBeginTime));
		currentTaskList = new ConcurrentHashMap<>();
		super.run();
		for (TaskInfo task : currentTaskList.values()) {
			while (task.getStatus() == null || "just put in download panel".equals(task.getStatus())) {
				try {
					sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}
		batchDownloadEndTime = System.currentTimeMillis();
		Logger.printf("--%s批量下载完毕\n", sdf.format(batchDownloadEndTime));
		// TODO push消息
		simplePush(currentTaskList, batchDownloadBeginTime, batchDownloadEndTime);
		// 将一切置空
		currentTaskList = null;
	}

	private void simplePush(Map<ClipInfo, TaskInfo> currentTaskList, long begin, long end) {
		int successCnt = 0, failCnt = 0;
		List<TaskInfo> successTasks = new ArrayList<TaskInfo>();
		List<TaskInfo> failTasks = new ArrayList<TaskInfo>();
		for (TaskInfo task : currentTaskList.values()) {
			if ("success".equals(task.getStatus())) {
				successCnt++;
				successTasks.add(task);
			} else {
				failCnt++;
				failTasks.add(task);
			}
		}
		System.out.println(String.format("任务总数:%d, 成功:%d，失败:%d", successCnt + failCnt, successCnt, failCnt));
		
		System.out.println("下载成功的任务有: ");
		for (TaskInfo task : successTasks) {
			System.out.println("\t" + task.getFileName());
		}
		System.out.println("下载失败的任务有: ");
		for (TaskInfo task : failTasks) {
			ClipInfo clip = task.getClip();
			System.out.println("\t" + task.getStatus() + "\t" + clip.getAvId() + "\t" + clip.getAvTitle() + " - " + clip.getTitle());
		}
	}

	@Override
	public void addTask(ClipInfo clip) {
		if (currentTaskList != null) {
			currentTaskList.put(clip, new TaskInfo(clip));
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
			while (true) {
				// TODO 把下载面板的所有任务清空
				// TODO 刷新cookie
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
	 * <p> 每天早上6点到第二天早上2点，随机300s~480s(5~8min) </p>
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
				return toTimeDelta + rollTime;
			}
		}
		throw new IllegalArgumentException("一键下载计划时间配置错误" + currentTime);
	}
}
