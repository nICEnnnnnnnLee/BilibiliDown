package nicelee.test.junit;

import static org.junit.Assert.assertEquals;

import java.net.HttpCookie;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.util.HttpCookies;

public class INeedLoginTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// 测试Cookie保存和与String之间的转换
	public void testSaveCookie() {
		INeedLogin inl = new INeedLogin();
		String origin = "[sid=123, bili_jct=ff44, SESSDATA=grgrerer, DedeUserID__ckMd5=ggg, DedeUserID=hhh]";
		System.out.println(origin);
		List<HttpCookie> oringinSet = HttpCookies.convertCookies(origin);

		System.out.println(oringinSet.toString());
		inl.iCookies = oringinSet;
		//inl.saveCookies(oringinSet.toString());
		String lastSet = (String) inl.readCookies();
		System.out.println(lastSet.toString());

		List<HttpCookie> str0 = HttpCookies.convertCookies(lastSet);
		System.out.println(str0);

		assertEquals(oringinSet.toString(), str0.toString());
	}

	@Test
	public void testGetLoginStatus() {
		INeedLogin inl = new INeedLogin();
		// 应该为有效cookie, 已经换掉了
		String cookies = "[sid=123, bili_jct=ff44, SESSDATA=grgrerer, DedeUserID__ckMd5=ggg, DedeUserID=hhh]";
		boolean succ = inl.getLoginStatus(HttpCookies.convertCookies(cookies));
		assertEquals(false, succ);
	}

}
