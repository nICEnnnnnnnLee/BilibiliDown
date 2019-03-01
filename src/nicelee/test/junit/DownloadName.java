package nicelee.test.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;

public class DownloadName {

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

	@Test
	public void test() {
		HttpRequestUtil util = new HttpRequestUtil();
		HttpHeaders headers = new HttpHeaders();
		String html = util.getContent("https://cidian.911cha.com/cixing_mingci.html", headers.getCommonHeaders("cidian.911cha.com"));
		System.out.println(html);
	}

}
