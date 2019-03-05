package nicelee.util;

import java.io.File;

import nicelee.ui.Global;
import nicelee.ui.thread.StreamManager;

public class CmdUtil {

	public static void run(String cmd[]) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			StreamManager errorStream = new StreamManager(process, process.getErrorStream());
			StreamManager outputStream  = new StreamManager(process, process.getInputStream());
            errorStream.start();
            outputStream.start();
            while(process.isAlive()) {
            	System.out.println("此处堵塞, 直至process 执行完毕");
            	Thread.sleep(2000);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(String cmd) {
		run(new String[] { cmd });
	}

	/**
	 * 转码
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static void convert(String videoName, String audioName, String dstName) {
		String cmd[] = createConvertCmd(videoName, audioName, dstName);
		File mp4File = new File(Global.savePath + dstName);
		File video = new File(Global.savePath + videoName);
		File audio = new File(Global.savePath + audioName);
		if (!mp4File.exists()) {
			System.out.println("下载完毕, 正在运行转码程序...");
			run(cmd);
			if (mp4File.exists() && mp4File.length() > video.length()) {
				video.delete();
				audio.delete();
			}
			System.out.println("转码完毕");
		} else {
			System.out.println("下载完毕");
		}
	}

	public static String[] createConvertCmd(String videoName, String audioName, String dstName) {
		String cmd[] = { "ffmpeg", "-i", Global.savePath + videoName, "-i", Global.savePath + audioName, "-c", "copy",
				Global.savePath + dstName };
		String str = String.format("ffmpeg命令为: \r\nffmpeg -i %s -i %s -c copy %s", Global.savePath + videoName,
				Global.savePath + audioName, Global.savePath + dstName);
		System.out.println(str);
		return cmd;
	}
}
