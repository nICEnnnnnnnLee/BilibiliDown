package nicelee.test.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.util.RepoUtil;

public class RepoTest {

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

	// @Test
	public void testThreadRun() {
		System.out.println("初始化开始");
		RepoUtil.init(false);
		System.out.println("初始化完毕");
		for (int i = 0; i < 1000; i++) {
			final int cnt = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					RepoUtil.appendAndSave("av1234-32-p" + cnt);
				}
			}).start();
		}
	}

	// @Test
	public void renameBatToRepo() {
		Pattern standardAvPattern = Pattern.compile("(av[0-9]+)-([0-9]+)-(p[0-9]+)");
		File file = new File("download/rename.bat");
		HashSet<String> downRepo = new HashSet<String>();
		// 先初始化downRepo
		BufferedReader buReader = null;
		try {
			buReader = new BufferedReader(new FileReader(file));
			String avRecord;
			while ((avRecord = buReader.readLine()) != null) {
				Matcher matcher = standardAvPattern.matcher(avRecord);
				if (matcher.find()) {
					System.out.println(avRecord.toString());
					downRepo.add(matcher.group());
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File fRepo = new File("config/repo.config");
			FileWriter fw = new FileWriter(fRepo, true);
			for (Object avRecord : downRepo.toArray()) {
				System.out.println(avRecord.toString());
				fw.write(avRecord.toString());
				fw.write("\r\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 去除重复记录
	// @Test
	public void remakeRepo() {
		Pattern standardAvPattern = Pattern.compile("(av[0-9]+)-([0-9]+)-(p[0-9]+)");
		File file = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\config\\repo.config");
		HashSet<String> downRepo = new HashSet<String>();
		// 先初始化downRepo
		BufferedReader buReader = null;
		try {
			buReader = new BufferedReader(new FileReader(file));
			String avRecord;
			while ((avRecord = buReader.readLine()) != null) {
				Matcher matcher = standardAvPattern.matcher(avRecord);
				if (matcher.find()) {
					System.out.println(avRecord.toString());
					downRepo.add(matcher.group());
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File fRepo = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\config\\repo2.config");
			FileWriter fw = new FileWriter(fRepo, true);
			for (Object avRecord : downRepo.toArray()) {
				System.out.println(avRecord.toString());
				fw.write(avRecord.toString());
				fw.write("\r\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 将avID转换为BVID
	@Test
	public void convertRepo() {
		RepoUtil.convert();
	}
}
