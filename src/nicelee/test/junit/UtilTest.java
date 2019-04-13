package nicelee.test.junit;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.util.CmdUtil;

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
	@Test
	public void testEpIdToAvId() {
		CmdUtil.deleteAllInactiveCmdTemp();
	}
	
}
