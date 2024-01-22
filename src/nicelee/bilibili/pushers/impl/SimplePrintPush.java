package nicelee.bilibili.pushers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.pushers.IPush;
import nicelee.bilibili.util.Logger;

@Bilibili(name = "SimplePrintPush", type = "pusher", note = "将结果打印出来")
public class SimplePrintPush implements IPush{

	@Override
	public String type() {
		return "Print";
	}

	@Override
	public IPush newInstance() {
		return new SimplePrintPush();
	}

	@Override
	public void push(Map<ClipInfo, TaskInfo> currentTaskList, long begin, long end) {
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
		Logger.println(String.format("任务总数:%d, 成功:%d，失败:%d", successCnt + failCnt, successCnt, failCnt));
		
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

}
