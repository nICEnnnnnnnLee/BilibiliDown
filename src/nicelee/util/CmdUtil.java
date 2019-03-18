package nicelee.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.Global;
import nicelee.ui.thread.StreamManager;

public class CmdUtil {

	public static void run(String cmd[]) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			StreamManager errorStream = new StreamManager(process, process.getErrorStream());
			StreamManager outputStream = new StreamManager(process, process.getInputStream());
			errorStream.start();
			outputStream.start();
			while (process.isAlive()) {
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
	 * 音视频合并转码
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

	/**
	 * 片段合并转码
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static void convert(String dstName, int part) {
		String cmd[] = createConvertCmd(dstName, part);
		File videoFile = new File(Global.savePath + dstName);
		if (!videoFile.exists()) {
			System.out.println("下载完毕, 正在运行转码程序...");
			run(cmd);
			System.out.println("转码完毕");
			//删除文件
			if (videoFile.exists()) {
				Matcher matcher = filePattern.matcher(dstName);
				matcher.find();
				String prefix = matcher.group(1);
				String suffix = matcher.group(2);
				List<File> fList = new ArrayList<File>();
				long fSize = 0;
				for (int i = 1; i <= part; i++) {
					File file = new File(Global.savePath + prefix + "-part" + i + suffix);
					fList.add(file);
					fSize += file.length();
				}
				System.out.println("转码后文件大小: " + videoFile.length());
				System.out.println("转码前文件大小和: " + fSize);
				if(videoFile.length() >= fSize * 0.8) {
					for(File f: fList) {
						f.delete();
					}
					new File(Global.savePath + dstName + ".txt").delete();
				}
			}
		} else {
			System.out.println("下载完毕");
		}
	}

	/**
	 * 音视频合并转码命令
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 * @return
	 */
	final static Pattern filePattern = Pattern.compile("^(.*)(\\.(mp4|flv))$");

	public static String[] createConvertCmd(String dstName, int part) {
		try {
			Matcher matcher = filePattern.matcher(dstName);
			matcher.find();
			String prefix = matcher.group(1);
			String suffix = matcher.group(2);
			File folderDown = new File(Global.savePath);
			folderDown.mkdirs();
			File file = new File(folderDown, dstName + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 1; i <= part; i++) {
				bw.write("file '");
				bw.write(prefix + "-part" + i + suffix);
				bw.write("'\r\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String cmd[] = { "ffmpeg", "-f", "concat", "-safe", "0" , "-i", Global.savePath + dstName + ".txt", "-c", "copy",
				Global.savePath + dstName };
		return cmd;
	}

	/**
	 * 视频片段合并转码命令
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 * @return
	 */
	public static String[] createConvertCmd(String videoName, String audioName, String dstName) {
		String cmd[] = { "ffmpeg", "-i", Global.savePath + videoName, "-i", Global.savePath + audioName, "-c", "copy",
				Global.savePath + dstName };
		String str = String.format("ffmpeg命令为: \r\nffmpeg -i %s -i %s -c copy %s", Global.savePath + videoName,
				Global.savePath + audioName, Global.savePath + dstName);
		System.out.println(str);
		return cmd;
	}
}
