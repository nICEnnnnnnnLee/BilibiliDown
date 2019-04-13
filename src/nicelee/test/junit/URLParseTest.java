package nicelee.test.junit;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.INeedAV;

public class URLParseTest {

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
	 * 测试解析Ep
	 */
	@Test
	public void testEpIdToAvId() {
		INeedAV util = new INeedAV();
		assertEquals(util.EpIdToAvId("ep250440"), "av36088061");
		assertEquals(util.EpIdToAvId("ep17342"), "av3997347");
	}
	
	/**
	 * 测试解析MdId --> SsId
	 */
	@Test
	public void testMdIdToSsId() {
		INeedAV util = new INeedAV();
		assertEquals(util.MdIdToSsId("md134912"), "ss25617");
		assertEquals(util.MdIdToSsId("md5867"), "ss5867");
	}
	
	/**
	 * 测试解析收藏夹 mlId --> 众多avId 
	 */
	@Test
	public void testAVList4FaviList() {
		INeedAV util = new INeedAV();
		assertEquals(util.getAVList4FaviList("229929928", 1), " av32424575 av32113696 av13017167 av23150912");
		assertEquals(util.getAVList4FaviList("101422828", 2), " av5685627 av6107466 av6544912 av6971831 av7189243");
	}
	
	/**
	 * 测试解析UP主 个人特定频道 
	 * https://space.bilibili.com/546195/channel/detail?cid=21838
	 */
	@Test
	public void testAVList4Channel() {
		INeedAV util = new INeedAV();
		System.out.println(util.getAVList4Channel("546195", "21838", 2));
		assertEquals(util.getAVList4Channel("546195", "21838", 1), " av12083728 av12388205 av12845082 av13480868");
	}
	
	/**
	 * 测试解析UP主 所有视频 
	 * https://space.bilibili.com/5276/video
	 */
	@Test
	public void testAVList4Space() {
		INeedAV util = new INeedAV();
		//最新的第6 ~10 个
		String results[] = util.getAVList4Space("5276", 2).split(" ");
		assertEquals(results.length, 6);
		for(int i =1; i <= 5; i++) {
			assert(results[i].matches("^av[0-9]+$"));
		}
	}

}
