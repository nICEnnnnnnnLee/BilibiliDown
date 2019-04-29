package nicelee.test.junit;



import java.lang.management.ManagementFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.VersionManagerUtil;

public class VersionManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	/**
	 * 测试 获取最新版本的tag
	 */
	//@Test
	public void testQueryLatestVersion() {
		// 打开并选中
		try {
			VersionManagerUtil.queryLatestVersion();
			System.out.println(VersionManagerUtil.downName);
			System.out.println(VersionManagerUtil.downUrl);
			System.out.println(VersionManagerUtil.versionName);
			System.out.println(VersionManagerUtil.versionTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试 获取程序pid
	 */
	@Test
	public void testGetPID() {
		try {
			Logger.println(ManagementFactory.getRuntimeMXBean().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试 下载最新版本
	 */
	//@Test
	public void testDownloadLatestVersion() {
		// 打开并选中
		try {
			VersionManagerUtil.downloadLatestVersion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试 打开bat
	 */
	//@Test
	public void testOpenBat() {
		// 打开并选中
		try {
//			Desktop desktop = Desktop.getDesktop();
//			desktop.open(new File("update.bat"));
			String cmd[] ={"cmd", "/c","start", "update.bat", "0"};
			CmdUtil.run(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
