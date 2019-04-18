package nicelee.test.junit;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.util.CmdUtil;
import nicelee.ui.Global;

public class UtilTest {

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
	 * 测试 删除已经生效过的临时cmd 命令文件
	 */
	//@Test
	public void testDeleteAllInactiveCmdTemp() {
		CmdUtil.deleteAllInactiveCmdTemp();
	}
	
	/**
	 * 测试 打开文件夹并选择文件
	 */
	@Test
	public void testOpenFolder() {
		// 打开并选中
		//String cmd[] = { "explorer", "/e,/select,"+"D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\ffmpeg.exe" };
		try {
			String cmd = "explorer /e,/select," +"D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\翎霜ヾ的视频列表-pn10-【翎霜】运动会雪初音倾情献跳b with u【单录版】-b with u 操场-64.mp4";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试 根据格式生成文件名
	 */
	//@Test
	public void testEpIdToAvId() {
		Global.formatStr = "avTitle-pDisplay-clipTitle-qn";
		String formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题");
		System.out.println(formatedName);
		assertEquals("av的标题-pn2-片段的标题-80", formatedName);
		
		Global.formatStr = "开头avTitle-pDisplay-clipTitle-qn-pAvkkk666";
		formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题");
		System.out.println(formatedName);
		assertEquals("开头av的标题-pn2-片段的标题-80-p1kkk666", formatedName);
	}
	
	/**
	 * 测试 根据文件名
	 */
	//@Test
	public void testRename() {
		String str = "test$]WHAT ";
		str = str.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		System.out.println(str);
		str = "av21449435-16-p1.flv".replaceFirst("av[0-9]+-[0-9]+-p[0-9]+", str);
	}
	
}
