package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nicelee.bilibili.util.custom.System;

import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.util.convert.ConvertUtil;
import nicelee.ui.Global;

public class RepoUtil {
	// avinfo 必须符合avId-qn-p的形式
	// av1234-60-p2
	static Pattern standardAvPattern;
	// 存在某一清晰度后, 在下另一种清晰度时是否判断已完成
	// true : 同一视频两种清晰度算不同文件
	// false : 同一视频两种清晰度算相同文件
	static boolean definitionStrictMode;

	static File fRepo; // 持久化文件，存放于config/repo.config
	static Set<String> downRepo; // 已下载完成的av集合

	public static void init(boolean refresh) {
		definitionStrictMode = Global.repoInDefinitionStrictMode;
		if (fRepo == null || refresh) {
			fRepo = ResourcesUtil.sourceOf("config/repo.config");
			if(!fRepo.exists())
				try {
					fRepo.createNewFile();
				} catch (IOException e1) {}
			standardAvPattern = Pattern.compile("^((?:av|h|cv|opus|BV|season|au|edd_)[0-9a-zA-Z_]+)-([0-9]+)(-p[0-9]+)$");
			int initialSize = (int) (233 / 0.75f);
			try {
				long fSize = fRepo.length();
				long lines = fSize / 19 + 233;
				initialSize = (int) (lines / 0.75f);
			} catch (Exception e) {
			}
			downRepo = Collections.newSetFromMap(new ConcurrentHashMap<>(initialSize));
		}
		// 先初始化downRepo
		BufferedReader buReader = null;
		try {
			buReader = new BufferedReader(new InputStreamReader(new FileInputStream(fRepo), "utf-8"));
			String avRecord;
			while ((avRecord = buReader.readLine()) != null) {
				Matcher matcher = standardAvPattern.matcher(avRecord);
				if (matcher.find()) {
					if (avRecord.startsWith("av")) {
						String bv = ConvertUtil.Av2Bv(matcher.group(1));
						StringBuilder sb = new StringBuilder(bv);
						if (definitionStrictMode) {
							sb.append("-").append(matcher.group(2)).append(matcher.group(3));
						} else {
							sb.append(matcher.group(3));
						}
						downRepo.add(sb.toString());
						System.out.println(sb.toString());
					} else {
						if (definitionStrictMode) {
							downRepo.add(avRecord);
						} else {
							downRepo.add(matcher.group(1) + matcher.group(3));
						}
					}
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 仓库里是否存在该条记录
	 * @param avRecord
	 * @return
	 */
	public static boolean isInRepo(String avRecord) {
		System.out.println("查询记录" + avRecord);
		if (avRecord.contains("-800-") || avRecord.contains("-801-") || avRecord.startsWith("FFmpeg")) {
			return false;
		}
		if (definitionStrictMode) {
			return downRepo.contains(avRecord);
		} else {
			Matcher matcher = standardAvPattern.matcher(avRecord);
			if (matcher.find()) {
//				Logger.println(matcher.group(1));
//				Logger.println(matcher.group(3));
				return downRepo.contains(matcher.group(1) + matcher.group(3));
			}
		}
		return false;
	}

	/**
	 * 加入并持久化保存到记录仓库
	 * <p> avRecord 必须符合avId-p-qn的形式 </p> 
	 * @param avinfo
	 */
	public static void appendAndSave(String avRecord) {
		System.out.println("已完成下载： " + avRecord);
		if (avRecord.contains("-800-") || avRecord.contains("-801-")) {
			Logger.println("字幕/弹幕文件，不计入下载记录");
			return;
		}
		if (!isInRepo(avRecord)) {
			Matcher matcher = standardAvPattern.matcher(avRecord);
			if (matcher.find()) {
				if (definitionStrictMode) {
					downRepo.add(avRecord);
				} else {
					downRepo.add(matcher.group(1) + matcher.group(3));
				}
				appendRecordToFile(avRecord);
			}
		}
	}

	static Thread convertThread;

	public static void convert() {
		if (convertThread == null) {
			convertThread = new Thread(new Runnable() {
				@Override
				public void run() {
					convertSync();
					System.out.println("转换结束");
					convertThread = null;
				}
			});
			convertThread.start();
		}
	}

	public static void stopConvert() {
		if (convertThread != null) {
			convertThread.interrupt();
			System.out.println("人为转换结束");
		}
	}

	static void convertSync() {
		Pattern avPattern = Pattern.compile("^av([0-9a-zA-Z]+)(-[0-9]+-p[0-9]+)$");
		File fRepo = ResourcesUtil.sourceOf("config/repo.config");
		File fRepoNew = ResourcesUtil.sourceOf("config/repo.new.config");

		HashMap<String, String> avBvMap = new HashMap<>();
		// 先初始化downRepo
		BufferedReader buReader = null;
		BufferedWriter buWriter = null;
//		HttpHeaders headers = new HttpHeaders();
//		HttpRequestUtil util = new HttpRequestUtil();

		int count = 0;
		try {
			buReader = new BufferedReader(new InputStreamReader(new FileInputStream(fRepoNew), "utf-8"));
			while (buReader.readLine() != null) {
				count++;
			}
		} catch (Exception e) {
		}
		try {
			buReader = new BufferedReader(new InputStreamReader(new FileInputStream(fRepo), "utf-8"));
			buWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fRepoNew, true), "utf-8"));
			String avRecord;
			int lineCnt = 0;
			while ((avRecord = buReader.readLine()) != null) {
				lineCnt++;
				if (lineCnt % 100 == 0) {
					System.out.println("当前转换进度： " + lineCnt);
				}
				if (lineCnt <= count) {
					continue;
				}
				Thread.sleep(0);
				Matcher matcher = avPattern.matcher(avRecord);
				String lineToAppend = null;
				if (matcher.find()) {
					String aid = matcher.group(1);
					String bvid = avBvMap.get(aid);
					if (bvid == null) {
						bvid = ConvertUtil.Av2Bv(Long.parseLong(aid));
						avBvMap.put(aid, bvid);
						lineToAppend = bvid + matcher.group(2);
//						String url = "https://api.bilibili.com/x/web-interface/view/detail?bvid=&jsonp=jsonp&callback=__jp0&aid="
//								+ aid;
//						HashMap<String, String> header = headers.getBiliJsonAPIHeaders(aid);
//						String callBack = util.getContent(url, header);
//						//Logger.println(callBack);
//						try {
//							JSONObject infoObj = new JSONObject(callBack.substring(6, callBack.length() - 2)).getJSONObject("data")
//									.getJSONObject("View");
//							bvid = infoObj.getString("bvid");
//							avBvMap.put(aid, bvid);
//							lineToAppend = bvid + matcher.group(2);
//						}catch (Exception e) {
//							lineToAppend = avRecord;
//						}
					} else {
						lineToAppend = bvid + matcher.group(2);
					}
				} else {
					lineToAppend = avRecord;
				}
				buWriter.write(lineToAppend);
				buWriter.newLine();
			}
			buReader.close();
			buWriter.close();

			File fRepoBackup = ResourcesUtil.sourceOf("config/repo.config.bk" + System.currentTimeMillis() / 1000);
			ResourcesUtil.copy(fRepo, fRepoBackup);
			fRepo.delete();
			ResourcesUtil.copy(fRepoNew, fRepo);
			fRepoNew.delete();
			JOptionPane.showMessageDialog(null, "转换完毕, 请重新加载");

		} catch (Exception e) {
		} finally {
			ResourcesUtil.closeQuietly(buReader);
			ResourcesUtil.closeQuietly(buWriter);
		}
	}

	synchronized static void appendRecordToFile(String line) {
		// System.out.println(Thread.currentThread().getName() + "开始记录");
		try {
			FileWriter fw = new FileWriter(fRepo, true);
			fw.write(line);
			fw.write("\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(Thread.currentThread().getName() + "记录完成");
	}

}
