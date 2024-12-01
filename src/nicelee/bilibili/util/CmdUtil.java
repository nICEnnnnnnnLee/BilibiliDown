package nicelee.bilibili.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.check.FlvMerger;
import nicelee.bilibili.util.convert.ConvertUtil;
import nicelee.ui.Global;

public class CmdUtil {

	public static String FFMPEG_PATH = "ffmpeg";
	public static File DEFAULT_WORKING_DIR = null;
	private static final File NULL_FILE = new File(
            (System.getProperty("os.name")
                    .startsWith("Windows") ? "NUL" : "/dev/null")
    );
	private static final Redirect DISCARD = Redirect.to(NULL_FILE); // 为了兼容 java8
	
	public static boolean run(String cmd[]) {
		return run(cmd, DEFAULT_WORKING_DIR);
	}
	public static boolean run(String cmd[], File workingDir) {
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd).directory(workingDir);
            if(Global.debugCmd) {
            	pb.redirectOutput(Redirect.INHERIT);
            	pb.redirectError(Redirect.INHERIT);
            }else {
            	pb.redirectOutput(DISCARD);
            	pb.redirectError(DISCARD);
            }
			process = pb.start();
			process.waitFor();
			System.out.println("process 执行完毕");
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			Logger.println(e.toString());
			return false;
		}
	}

	/**
	 * 音视频合并转码
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static boolean convert(String videoName, String audioName, String dstName) {
		String cmd[] = createConvertCmd(videoName, audioName, dstName);
		File mp4File = new File(Global.savePath + dstName);
		File video = new File(Global.savePath + videoName);
		File audio = new File(Global.savePath + audioName);
		if (!mp4File.exists()) {
			Logger.println("下载完毕, 正在运行转码程序...");
			run(cmd);
			if (mp4File.exists() && mp4File.length() > video.length()) {
				video.delete();
				audio.delete();
				return true;
			}
			Logger.println("转码完毕");
		} else {
			Logger.println("下载完毕");
			return true;
		}
		return false;
	}
	
	/**
	 * 片段合并转码
	 * 
	 * @param dstName
	 * @param part
	 */
	public static boolean convert(String dstName, int part) {
		
		File videoFile = new File(Global.savePath + dstName);
		if (!videoFile.exists()) {
			Logger.println("下载完毕, 正在运行转码程序...");
			if(Global.flvUseFFmpeg) {
				String cmd[] = createConvertCmd(dstName, part);
				run(cmd);
			}else {
				List<File> flist = new ArrayList<File>();
				Matcher matcher = filePattern.matcher(dstName);
				matcher.find();
				String prefix = matcher.group(1);
				String suffix = matcher.group(2);
				for (int i = 1; i <= part; i++) {
					File f = new File(Global.savePath, prefix + "-part" + i + suffix);
					flist.add(f);
				}
				try {
					new FlvMerger().merge(flist, videoFile);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			Logger.println("转码完毕");
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
				Logger.println("转码后文件大小: " + videoFile.length());
				Logger.println("转码前文件大小和: " + fSize);
				if (videoFile.length() >= fSize * 0.8) {
					for (File f : fList) {
						f.delete();
					}
					// new File(Global.savePath + dstName + ".txt").delete();
					deleteAllInactiveCmdTemp();
					return true;
				}
			}
		} else {
			Logger.println("下载完毕");
			return true;
		}
		return false;
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
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for (int i = 1; i <= part; i++) {
				// Windows下
				// 当-i的Global.savePath路径以/结尾时，会寻找路径Global.savePath + %file
				// 当-i的Global.savePath路径以\结尾时，会寻找路径 %file
				// 此处我们在txt里输入的%file均为文件名，
				// 故必须要确保Global.savePath 以/结尾
				bw.write("file '");
				bw.write(prefix + "-part" + i + suffix);
				bw.write("'\r\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String cmd[] = { FFMPEG_PATH, "-f", "concat", "-safe", "0", "-i", Global.savePath + dstName + ".txt", "-c",
				"copy", Global.savePath + dstName };
		return cmd;
	}

	/**
	 * 删除已经生效过的临时cmd 文件
	 * 
	 * 类似于
	 * 
	 * @ex1 av12345-64-p1.flv.txt
	 * @ex2 av12345-64-p2-part1.flv // 在转码判断里面删除，防止误删
	 * @ex3 av12345-64-p2-part1.flv.part
	 * @ex4 av12345-64-p3.mp4.part
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
				if (Global.restrictTempMode) {
					// .part文件，如果已经存在转换完的对应视频，则可以删除
					Matcher matcherPart = cmdPartPattern.matcher(name);
					if (matcherPart.find()) {
						String fName = matcherPart.group(1).replaceFirst("-part[0-9]+", "");
						if (standardFileNamePattern.matcher(fName).matches()) {
							File file = new File(dir, fName);
							if (file.exists()) {
								return true;
							}
						}
						return false;
					}
//					// 部分完成了的flv|mp4文件，如果已经存在转换完的对应视频，则可以删除
//					Matcher matcherDonePart = cmdDonePartPattern.matcher(name);
//					if (matcherDonePart.find()) {
//						File file = new File(dir, matcherDonePart.group().replaceFirst("-part[0-9]+", ""));
//						if (file.exists()) {
//							return true;
//						}
//						return false;
//					}
				}
				return false;
			}
		};
		if (folderDown.exists()) {
			// 删除下载文件
			for (File file : folderDown.listFiles(filter)) {
				System.out.println("尝试删除" + file.getName());
				file.delete();
			}
		}
	}

	private static String replParams(String pattern, String videoName, String audioName, String dstName) {
		if(audioName == null) audioName = "null";
		if(videoName == null) videoName = "null";
		return pattern.replace("{FFmpeg}", FFMPEG_PATH).replace("{SavePath}", Global.savePath)
				.replace("{VideoName}", videoName).replace("{AudioName}", audioName)
				.replace("{DstName}", dstName);
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
		String cmd[] = null;
		if (audioName == null) {
			cmd = new String[]{ FFMPEG_PATH, "-i", Global.savePath + videoName, "-c", "copy", Global.savePath + dstName };
		} else if (videoName == null) {
			// cmd = new String[]{ FFMPEG_PATH, "-i", Global.savePath + audioName, "-vn", "-c:a", "copy", Global.savePath + dstName };
			cmd = Global.ffmpegCmd4AudioOnly.clone();
			for(int i = 0; i < cmd.length; i++) {
				cmd[i] = replParams(cmd[i], videoName, audioName, dstName);
			}
		} else {
//			cmd = new String[]{ FFMPEG_PATH, "-i", Global.savePath + videoName, "-i", Global.savePath + audioName, "-c",
//					"copy", Global.savePath + dstName };
			cmd = Global.ffmpegCmd4Merge.clone();
			for(int i = 0; i < cmd.length; i++) {
				cmd[i] = replParams(cmd[i], videoName, audioName, dstName);
			}
		}
		String str = String.format("ffmpeg命令为: %s", Arrays.toString(cmd));
		Logger.println(str);
		return cmd;
	}

	/**
	 * 下载成功后重命名 或者 追加重命名文件
	 * 
	 * @param avid_q
	 * @param formattedTitle
	 * @throws IOException
	 */
	// public static boolean doRenameAfterComplete = true;
	final static Pattern suffixPattern = Pattern.compile("\\.[^.]+$");

	public synchronized static void convertOrAppendCmdToRenameBat(final String avid_q, final String formattedTitle,
			int page) {
		try {
			// 获取已完成文件
			File originFile = getFileByAvQnP(avid_q, page);
			String fName = originFile.getName();
			Matcher suffixM = suffixPattern.matcher(fName);
			suffixM.find();
			String tail = suffixM.group();

			if (Global.doRenameAfterComplete) {
				File file = new File(Global.savePath, formattedTitle + tail);
				File folder = file.getParentFile();
				if (!folder.exists())
					folder.mkdirs();
				if((!originFile.renameTo(file)) && Global.autoNumberWhenFileExists) {// 如果不成功，大概率是文件名重复，在后面加上序号，类似于(01)
					for(int i = 1; i < 100; i++) {
						File f = new File(Global.savePath, 
								String.format("%s(%02d)%s", formattedTitle, i, tail));
						Logger.println(f.getAbsolutePath());
						if(!f.exists()) {
							originFile.renameTo(f);
							break;
						}
					}
				}
			} else {
				File f = new File(Global.savePath, "rename.bat");
				boolean isExist = f.exists();
				System.out.println(f.getAbsolutePath() + "是否存在? " + f.exists());
				FileWriter fw;
				fw = new FileWriter(f, true);
				if (!isExist) {
					// .bat切为UTF-8编码, 防止中文乱码
					fw.write("@echo off\r\nchcp 65001\r\n");
				}
				String cmd = String.format("rename \"%s\" \"%s%s\"\r\n", fName, formattedTitle, tail);
				fw.write(cmd);
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件
	 * 
	 * @param avid_q
	 * @param page
	 * @return
	 */
	static String[] suffixs = {".mp4", ".flv", ".jpg", ".webp", ".png", ".srt", ".ass", ".m4a", ".flac"};
	public static File getFileByAvQnP(String avid_q, int page) {
		String name = avid_q + "-p" + page;
		Logger.println(name);
		for(String suffix: suffixs) {
			File f = new File(Global.savePath, name + suffix);
			if (f.exists())
				return f;
		}
		File f = new File(Global.savePath, name + Global.suffix4AudioOnly);
		if (f.exists())
			return f;
		return null;
	}

	// ## avId - bv号 e.g. BV1BJ411E7uM
	// ## numAvId - 老的数字av号 e.g. av1234567 中的1234567
	// ## pAv - av 的第几个视频 e.g. p1/p2
	// ## pDisplay - 合集的第几个视频 e.g. pn1/pn2
	// ## qn - 清晰度值 e.g. 32/64/80
	// ## avTitle - av标题
	// ## clipTitle - 视频小标题
	//
	// 以下可能不存在
	// 用法举例 (:listName 我在前面-listName-我在后面) ===> 我在前面-某收藏夹的名称-我在后面
	// ### listName - 集合名称 e.g. 某收藏夹的名称
	// ### listOwnerName - 集合的拥有者 e.g. 某某某 （假设搜索的是某人的收藏夹）
	// public static String formatStr = "avTitle-pDisplay-clipTitle-qn";
	static Pattern splitUnit = Pattern.compile(
			"avId|numAvId|pAv\\d?|pDisplay\\d?|qn|avTitle|clipTitle|UpName|UpId|listName|listOwnerName|favTime|cTime|" + 
			"\\((?<ifOrUnless0>[\\:!])(?<condition0>[^ ]+) (?<format0>[^\\)]*)\\)|" + 
			"\\[(?<ifOrUnless1>[\\:!])(?<condition1>[^ ]+) (?<format1>[^\\]]*)\\]");

	public static String genFormatedName(String avId, String pAv, String pDisplay, int qn, String avTitle,
			String clipTitle, String listName, String listOwnerName) {
		// 生成KV表
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("avId", avId);
		paramMap.put("pAv", pAv);
		paramMap.put("pDisplay", pDisplay);
		paramMap.put("qn", "" + qn);
		paramMap.put("avTitle", avTitle.replaceAll("[/\\\\]", "_"));
		paramMap.put("clipTitle", clipTitle.replaceAll("[/\\\\]", "_"));
		paramMap.put("listName", listName);
		paramMap.put("listOwnerName", listOwnerName);
		// paramMap.put("clipTitle", clipTitle);

		// 匹配格式字符串
		// avTitle-pDisplay-clipTitle-qn
		return genFormatedName(paramMap, Global.formatStr);
	}

	public static String genFormatedName(VideoInfo avInfo, ClipInfo clip, int realQN) {
		// 生成KV表
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("avId", clip.getAvId());
		paramMap.put("pAv", "" + clip.getPage());
		paramMap.put("pDisplay", "" + clip.getRemark());
		paramMap.put("qn", "" + realQN);
		String avTitle = clip.getAvTitle().replaceAll("[/\\\\]", "_");
		String clipTitle = clip.getTitle().replaceAll("[/\\\\]", "_");
		paramMap.put("avTitle", avTitle);
		if( !(Global.ctFormatAllowNull && avTitle.equals(clipTitle)) ) {
			paramMap.put("clipTitle", clipTitle);
		}
		paramMap.put("listName", clip.getListName()); // 已确保没有路径分隔符
		paramMap.put("listOwnerName", clip.getListOwnerName()); // 已确保没有路径分隔符
		paramMap.put("UpName", clip.getUpName().replaceAll("[/\\\\]", "_"));
		paramMap.put("UpId", clip.getUpId());
		long favTime = clip.getFavTime();
		if(favTime > 0) {
			SimpleDateFormat ctf = new SimpleDateFormat(Global.favTimeFormat);
			paramMap.put("favTime", ctf.format(new Date(favTime)));
		}
		long cTime = clip.getcTime();
		if(cTime > 0) {
			SimpleDateFormat ctf = new SimpleDateFormat(Global.cTimeFormat);
			paramMap.put("cTime", ctf.format(new Date(cTime)));
		}
		// 匹配格式字符串
		// avTitle-pDisplay-clipTitle-qn
		return genFormatedName(paramMap, Global.formatStr);
	}

	private static String genFormatedName(HashMap<String, String> paramMap, String formatStr) {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = splitUnit.matcher(formatStr);
		int pointer = 0;
		while (matcher.find()) {
			// 加入匹配单位前的字符串
			sb.append(formatStr.substring(pointer, matcher.start()));
			int c = 0;
			String ifOrUnless = matcher.group("ifOrUnless" + c);// 条件语句
			ifOrUnless = ifOrUnless != null? ifOrUnless : matcher.group("ifOrUnless" + (++c));
			if (ifOrUnless != null) {
				String condition = matcher.group("condition" + c);
				String format = matcher.group("format" + c);
				if (":".equals(ifOrUnless) && paramMap.get(condition) != null) {
					sb.append(genFormatedName(paramMap,format));
				}else if("!".equals(ifOrUnless) && paramMap.get(condition) == null) {
					sb.append(genFormatedName(paramMap,format));
				}
//				Logger.println();
			} else {
				if(matcher.group().startsWith("pAv")) {
					String expectLength = matcher.group().substring(3);
					String rawNumber = paramMap.get("pAv");
					String expectNumber = formatNumber(rawNumber, expectLength);
					sb.append("p" + expectNumber);
				}else if(matcher.group().startsWith("pDisplay")) {
					String expectLength = matcher.group().substring(8);
					String rawNumber = paramMap.get("pDisplay");
					String expectNumber = formatNumber(rawNumber, expectLength);
					sb.append("pn" + expectNumber);
				}else if("numAvId".equals(matcher.group())) {
					try {
						// 计算BVid对应的AVid，并加入
						String bvId = paramMap.get("avId");
						long numAvId = ConvertUtil.Bv2Av(bvId);
						sb.append(numAvId);
					}catch (Exception e) {
						Logger.println("您当前下载的并不能找到对应的数字av号，请正确设置");
						sb.append(0);
					}
					
				}else
					// 加入匹配单位对应的值
					sb.append(paramMap.getOrDefault(matcher.group(), "null"));
			}
			// 改变指针位置
			pointer = matcher.end();
		}
		// 加入最后不匹配单位的部分
		sb.append(formatStr.substring(pointer));
		// 去掉文件名称的非法字符 |:*?<>"$
		// 将路径分隔符统一替换为当前系统分隔符
		String result = sb.toString().replaceAll("[\t\b\\r\\n|:*?<>\"$]", "_")
				.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
		return result;
	}


	private static String formatNumber(String rawNumber, String expectLength) {
		if(!expectLength.isEmpty()) {
			// 不足位补零
			int zeroLen = Integer.parseInt(expectLength) - rawNumber.length();
			if(zeroLen > 0) {
				String pattern = new StringBuilder("%0").append(zeroLen).append("d%s").toString();
				return String.format(pattern, 0, rawNumber);
			}
		}
		return rawNumber;
	}
}
