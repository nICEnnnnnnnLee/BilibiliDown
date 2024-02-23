package nicelee.bilibili.pushers;

import java.util.Map;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;

public interface IPush {

	public String type();

	public IPush newInstance();

	public void push(Map<ClipInfo, TaskInfo> currentTaskList, long begin, long end);

}
