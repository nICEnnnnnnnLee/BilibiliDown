package nicelee.bilibili.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.VideoInfo;
import nicelee.ui.Global;
import nicelee.ui.thread.DownloadRunnable;

public class VersionManagerUtil {

	static HttpRequestUtil util = new HttpRequestUtil();
	static HttpHeaders headers = new HttpHeaders();
	public static String downUrl;
	public static String downName;
	public static String versionTag;
	public static String versionName;

	/**
	 * 获取当前最新版本, 并返回与当前版本是否匹配
	 * 
	 * @return
	 */
	public static boolean queryLatestVersion() {
		if (downUrl != null) {
			//return versionTag.equals(Global.version);
			return isLatestVer(versionTag, Global.version);
		}
		String json = util.getContent("https://api.github.com/repos/nICEnnnnnnnLee/BilibiliDown/releases?per_page=1",
				headers.getHeaders());
		Logger.println(json);
		JSONObject jObj = new JSONArray(json).getJSONObject(0);
		versionTag = jObj.getString("tag_name").trim().toLowerCase();
		versionName = jObj.getString("name");

		JSONArray assets = jObj.getJSONArray("assets");
		for (int i = 0; i < assets.length(); i++) {
			JSONObject asset = assets.getJSONObject(i);
			String assetName = asset.getString("name");
			if (assetName.startsWith("BilibiliDown") && assetName.endsWith(".zip")) {
				downName = assetName;
				downUrl = asset.getString("browser_download_url");
				break;
			}
		}
		//return versionTag.equals(Global.version);
		return isLatestVer(versionTag, Global.version);
	}
	
	final private static Pattern versionPattern = Pattern.compile("v([0-9]+)\\.([0-9]+)");
	private static boolean isLatestVer(String latestTag, String localTag) {
		Matcher m1 = versionPattern.matcher(latestTag);
		m1.find();
		int platest1 = Integer.parseInt(m1.group(1));
		int platest2 = Integer.parseInt(m1.group(2));
		
		Matcher m2 = versionPattern.matcher(localTag);
		m2.find();
		int plocal1 = Integer.parseInt(m2.group(1));
		int plocal2 = Integer.parseInt(m2.group(2));
		
		if(plocal1 > platest1 || (plocal1 == platest1 && plocal2 >= platest2)) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 下载最新版本
	 * 
	 * @throws IOException
	 */
	public static void downloadLatestVersion() throws Exception {
		if (!queryLatestVersion()) {
			VideoInfo avInfo = new INeedAV().getVideoDetail(downName, 0, false);
			DownloadRunnable downThread = new DownloadRunnable(avInfo, avInfo.getClips().get(1234L), 0);
			Global.queryThreadPool.execute(downThread);
//			util.setSavePath("update/");
//			if (util.download(downUrl, downName, headers.getHeaders())) {
//				unzipTargetJar();
//				Object[] options = { "是", "否" };
//				int m = JOptionPane.showOptionDialog(null, "已经下载成功，需要关闭程序才能更新，现在是否重启?", "成功！",
//						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
//				Logger.println(m);
//				if (m == 0) {
//					RunCmdAndCloseApp("1");
//				}
//			} else {
//				throw new Exception("下载失败");
//			}
		} else {
			System.out.print("当前版本已是最新，无需更新");
		}
	}

	private static void unzipTargetJar() throws IOException {
		unzipTargetJar(downName);
	}
	
	/**
	 * 解压出包中的"INeedBiliAV.jar"
	 */
	public static void unzipTargetJar(String downName) throws IOException {
		File targetfolder = new File("update/");
		ZipInputStream zi = new ZipInputStream(new FileInputStream("update/" + downName));
		ZipEntry ze = null;
		FileOutputStream fo = null;
		byte[] buff = new byte[1024];
		int len;

		// 如果有 INeedBiliAV.update.jar，先删除
		File dstFile = new File(targetfolder, "INeedBiliAV.update.jar");
		if (dstFile.exists()) {
			dstFile.delete();
		}
		while ((ze = zi.getNextEntry()) != null) {
			if ("INeedBiliAV.jar".equals(ze.getName())) {
				fo = new FileOutputStream(dstFile);
				while ((len = zi.read(buff)) > 0)
					fo.write(buff, 0, len);
				fo.close();
				zi.closeEntry();
				break;
			}
			zi.closeEntry();
		}
		zi.close();
	}

	/**
	 * 运行bat文件，并关闭当前程序 (bat文件删除旧的jar文件, 替换新的jar文件，替换完成后重新打开程序)
	 * <p>
	 * code : 0 更新后不再打开程序，1 更新后打开程序
	 * </p>
	 */
	public static void RunCmdAndCloseApp(String code) {
		try {
			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
				String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
				String cmd[] = { "cmd", "/c", "start", "update.bat", code, pid };
				CmdUtil.run(cmd);
			}else {
				//TODO
			}
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 1. shell脚本以当前类为入口运行jar
	 * **2. 下载最新的安装包update/BilibiliDown.vX.X.release.zip
	 * **3. 解压update/INeedBiliAV.update.jar
	 * 4. shell脚本将之复制并覆盖 update/INeedBiliAV.update.jar --> INeedBiliAV.jar
	 * 5. shell脚本打开程序
	 */
	public static void main(String args[]) {
//		Global.init();
		ConfigUtil.initConfigs();
		if (!queryLatestVersion()) {
			System.out.println("正在下载: " + downName);
			try {
				util.setSavePath("update/");
				if (util.download(downUrl, downName, headers.getHeaders())) {
					unzipTargetJar();
					System.exit(1);
				} else {
					System.out.printf("下载 %s 失败\n", downName);
				}
			}catch (Exception e) {
				e.printStackTrace();
				System.out.printf("下载 %s 失败\n", downName);
			}
		}else {
			System.out.printf("当前版本%s已是最新，无需更新", Global.version);
			System.exit(0);
		}
		System.exit(-1);
	}
}
