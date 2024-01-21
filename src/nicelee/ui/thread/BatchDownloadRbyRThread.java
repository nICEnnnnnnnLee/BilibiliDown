package nicelee.ui.thread;

import java.awt.Component;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JMenuItem;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.util.Logger;

public class BatchDownloadRbyRThread extends BatchDownloadThread {

	private static ConcurrentHashMap<ClipInfo, TaskInfo> currentTaskList;
	private static long batchDownloadBeginTime;
	private static long batchDownloadEndTime;
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	public BatchDownloadRbyRThread(String configFileName) {
		super(configFileName);
		this.setName("Thread-BatchDownload round by round");
		this.setDaemon(true);
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
		while (true) {
			// TODO 把下载面板的所有任务清空
			runBatchDownloadOnce();
			try {
				sleep(timeToSleep());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public long timeToSleep() {
		// TODO
		return 1000 * 60 * 8;
	}
}
