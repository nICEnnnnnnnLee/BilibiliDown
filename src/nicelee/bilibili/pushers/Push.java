package nicelee.bilibili.pushers;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.TaskInfo;
import nicelee.bilibili.pushers.impl.SimplePrintPush;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;

public class Push {

	private static Map<String, IPush> pusherMap;

	public Push() {
		if (pusherMap == null) {
			synchronized (Push.class) {
				if (pusherMap == null) {
					pusherMap = new HashMap<String, IPush>();
					try {
						for (Class<?> clazz : PackageScanLoader.validPusherClasses) {
							@SuppressWarnings("unchecked")
							Constructor<IPush> con = (Constructor<IPush>) clazz.getConstructor();
							IPush push = con.newInstance();
							pusherMap.put(push.type(), push);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void push(Map<ClipInfo, TaskInfo> currentTaskList, long begin, long end) {
		Logger.println("Global.msgPushType:" + Global.msgPushType);
		IPush pusher = pusherMap.get(Global.msgPushType);
		pusher = pusher == null? new SimplePrintPush(): pusher.newInstance();
		Logger.println("pusher type:" + pusher.type());
		pusher.push(currentTaskList, begin, end);
	}

}
