package nicelee.ui.item;

import java.util.concurrent.ConcurrentLinkedQueue;

import nicelee.ui.item.JOptionPane;

import nicelee.ui.Global;

public class JOptionPaneManager {

	static JOptionPaneManager instance4CommonMsg = new JOptionPaneManager();
	static JOptionPaneManager instance4ErrMsg = new JOptionPaneManager();

	public static void showMsgWithNewThread(String title, String msg) {
		instance4CommonMsg.showMsgWithNewThread0(title, msg, false);
	}

	public static void alertErrMsgWithNewThread(String title, String msg) {
		instance4ErrMsg.showMsgWithNewThread0(title, msg, true);
	}

//	public static void alertErrMsg(String title, String msg) {
//		instance4ErrMsg.showMsg0(title, msg, true);
//	}

	private ConcurrentLinkedQueue<Thread> promptThreads = new ConcurrentLinkedQueue<Thread>();

//	private void showMsg0(String title, String msg, boolean isErrMsg) {
//		if ((Global.isAlertIfDownloded || isErrMsg) && promptThreads.size() < Global.maxAlertPrompt) {
//			promptThreads.add(Thread.currentThread());
//
//			Object[] options = { "关闭", "关闭所有" };
//			int m = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
//					null, options, options[0]);
//			synchronized (promptThreads) {
//				if (m == 1) {
//					interruptAllThread();
//				} else {
//					promptThreads.remove(Thread.currentThread());
//				}
//			}
//		}
//	}

	private void showMsgWithNewThread0(String title, String msg, boolean isErrMsg) {
		if ((Global.isAlertIfDownloded || isErrMsg) && promptThreads.size() < Global.maxAlertPrompt) {
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

	private void interruptAllThread() {
		// 不管怎样，先移除当前线程
		promptThreads.remove(Thread.currentThread());
		for (Thread t : promptThreads) {
			if (t.isAlive()) {
				t.interrupt();
			}
		}
	}
}
