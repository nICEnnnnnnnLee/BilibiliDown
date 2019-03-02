package nicelee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import nicelee.ui.Global;

public class CmdUtil {

	public static void run(String cmd[]) {
		Process cmdProcess = null;
		BufferedReader reader = null;
		try {
			cmdProcess = Runtime.getRuntime().exec(cmd);
			// InputStream
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			// ErrorStream
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getErrorStream()));
			line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cmdProcess.destroy();
		}
	}
	
	public static void run(String cmd) {
		run(new String[] {cmd});
	}
	
	/**
	 * 转码
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static void convert(String videoName, String audioName, String dstName) {
		String cmd[] = createConvertCmd(videoName, audioName, dstName);
		File mp4File = new File(Global.savePath + dstName);
		File video = new File(Global.savePath + videoName);
		File audio = new File(Global.savePath + audioName);
		if(!mp4File.exists()) {
			System.out.println("下载完毕, 正在运行转码程序...");
			CmdUtil.run(cmd);
			if( mp4File.exists() && mp4File.length() > video.length()) {
				video.delete();
				audio.delete();
			}
			System.out.println("转码完毕");
		}else {
			System.out.println("下载完毕");
		}
	}
	public static String[] createConvertCmd(String videoName, String audioName, String dstName) {
		String cmd[] = {"ffmpeg",
				"-i", Global.savePath +videoName, 
				"-i", Global.savePath +audioName, 
				"-c", "copy", Global.savePath + dstName};
		return cmd;
	}
}
