package nicelee.ui.item;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import nicelee.ui.Global;

public class JOptionPaneManager {

	private static List<Thread> promptThreads = new ArrayList<Thread>();

	public static void showMsg(String title, String msg) {
		if(Global.isAlertIfDownloded && promptThreads.size() < Global.maxAlertPrompt) {
			promptThreads.add(Thread.currentThread());
			
			Object[] options = { "关闭", "关闭所有" };
			int m = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, options, options[0]);
			synchronized (promptThreads) {
				if (m == 1) {
					interruptAllThread();
				} else {
					promptThreads.remove(Thread.currentThread());
				}
			}
		}
	}

	public static void showMsgWithNewThread(String title, String msg) {
		if(Global.isAlertIfDownloded && promptThreads.size() < Global.maxAlertPrompt) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					Object[] options = { "关闭", "关闭所有" };
					int m = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					// System.out.println(m);
					
					synchronized (promptThreads) {
						if (m == 1) {
							interruptAllThread();
						} else {
							promptThreads.remove(Thread.currentThread());
						}
					}
				}
			});
			promptThreads.add(t);
			t.start();
		}
	}

	private static void interruptAllThread() {
		// 不管怎样，先移除当前线程
		promptThreads.remove(Thread.currentThread());
		for (Thread t : promptThreads) {
			if (t.isAlive()) {
				t.interrupt();
			}
		}
	}
}
