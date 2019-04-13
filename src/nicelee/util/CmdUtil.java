package nicelee.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
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
			System.out.println("此处堵塞, 直至process 执行完毕");
			process.waitFor();
			System.out.println("process 执行完毕");
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
			// 删除文件
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
				if (videoFile.length() >= fSize * 0.8) {
					for (File f : fList) {
						f.delete();
					}
					// new File(Global.savePath + dstName + ".txt").delete();
					deleteAllInactiveCmdTemp();
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
		String cmd[] = { "ffmpeg", "-f", "concat", "-safe", "0", "-i", Global.savePath + dstName + ".txt", "-c", "copy",
				Global.savePath + dstName };
		return cmd;
	}

	/**
	 * 删除已经生效过的临时cmd 文件
	 * 
	 * 类似于
	 * @ex1 av12345-64-p1.flv.txt
	 * @ex2  av12345-64-p2-part1.flv
	 * @ex3  av12345-64-p2-part1.flv.part
	 * @ex4  av12345-64-p3.mp4.part
	 * 
	 * @return
	 */
	final static Pattern cmdTxtPattern = Pattern.compile("^(av[0-9]+-[0-9]+-p[0-9]+\\.(flv|mp4))\\.txt$");
	final static Pattern cmdDonePartPattern = Pattern.compile("^av[0-9]+-[0-9]+-p[0-9]+-part[0-9]+\\.(flv|mp4)$");
	final static Pattern cmdPartPattern = Pattern.compile("^(.*)\\.part$");
	final static Pattern standardFileNamePattern = Pattern.compile("^av[0-9]+-[0-9]+-p[0-9]+\\.(flv|mp4)$");

	public static void deleteAllInactiveCmdTemp() {
		// 找到下载文件夹
		File folderDown = new File(Global.savePath);
		// 筛选下载文件夹
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// txt文件，如果已经存在转换完的对应视频，则可以删除
				Matcher matcherTxt = cmdTxtPattern.matcher(name);
				if (matcherTxt.find()) {
					File file = new File(dir, matcherTxt.group(1));
					if (file.exists()) {
						return true;
					}
					return false;
				}
				if(Global.restrictTempMode) {
					// .part文件，如果已经存在转换完的对应视频，则可以删除
					Matcher matcherPart = cmdPartPattern.matcher(name);
					if (matcherPart.find()) {
						String fName = matcherPart.group(1).replaceFirst("-part[0-9]+", "");
						if(standardFileNamePattern.matcher(fName).matches()) {
							File file = new File(dir, fName);
							if (file.exists()) {
								return true;
							}
						}
						return false;
					}
					// 部分完成了的flv|mp4文件，如果已经存在转换完的对应视频，则可以删除
					Matcher matcherDonePart = cmdDonePartPattern.matcher(name);
					if (matcherDonePart.find()) {
						File file = new File(dir, matcherDonePart.group());
						if (file.exists()) {
							return true;
						}
						return false;
					}
				}
				return false;
			}
		};
		// 删除下载文件
		for (File file : folderDown.listFiles(filter)) {
			System.out.println("尝试删除" + file.getName());
			file.delete();
		}
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
