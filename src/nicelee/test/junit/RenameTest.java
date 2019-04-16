package nicelee.test.junit;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RenameTest {

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
	 * 测试 重命名
	 */
	@Test
	public void testEpIdToAvId() {
		File folder = new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\");
		Pattern pattern = Pattern.compile("^([^-]*)(-.*-p([0-9]+))\\.flv");
		for (File file : folder.listFiles()) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				File nf = new File(folder, matcher.group(1) + matcher.group(3) + matcher.group(2) + ".flv");
				System.out.println(nf.getName());
				file.renameTo(nf);
			}
		}
	}

}
