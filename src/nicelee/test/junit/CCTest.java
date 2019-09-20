package nicelee.test.junit;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.ccaption.ClosedCaptionDealer;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

public class CCTest {

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

	//@Test
	public void test() throws IOException {
		ClosedCaptionDealer ccDealer = new ClosedCaptionDealer();
		ccDealer.getCC("av54694376", ".", "test");
//		ccDealer.save2srt("https://i0.hdslb.com/bfs/subtitle/a426a23a315d990261d74f713956c04e8449b961.json", new File("test.srt"));
	}
	
	@Test
	public void testDanmuku() throws IOException {
		HttpRequestUtil util = new HttpRequestUtil();
//		HashMap<String, String> headers = new HashMap<String, String>();
		HashMap<String, String> headers = new HttpHeaders().getBiliJsonAPIHeaders("av67589226");
		headers.put("Accept-Encoding", "none");
		headers.remove("Referer");
		String result = util.getContent("http://comment.bilibili.com/85464878.xml", headers);
		System.out.println(result);
	}

}
