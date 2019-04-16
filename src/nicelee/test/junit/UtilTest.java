package nicelee.test.junit;


import static org.junit.Assert.assertEquals;

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
	 * 测试 根据格式生成文件名
	 */
	@Test
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
	
}
