package nicelee.test.junit;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.zip.DeflaterInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.net.stream.InflateWithHeaderInputStream;
import nicelee.bilibili.util.net.stream.TestStream;
import nicelee.ui.Global;

public class INeedAVTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试 下载弹幕 TODO 浏览器打开没问题，但是这里是乱码
	 */
	@Test
	public void testDownloadDanmuku() {
		ConfigUtil.initConfigs();
		HttpRequestUtil util = new HttpRequestUtil();
//		downloader.download("https://api.bilibili.com/x/v1/dm/list.so?oid=85464878", "av48804637", 80, 1);
		String danmuku = util.getContent("http://comment.bilibili.com/85464878.xml",
				new HttpHeaders().getDanmuHeaders(), null);
		System.out.println(danmuku);
	}

	// @Test
	public void testDeflate() {
		try {
			// byte[] test = {0x33, 0x34, 0x34, 0x04, 0x00}; //"111"
			// byte[] test = {0x33, 0x34, 0x02 0x00}; //"12"
			// byte[] test = {0x33, 0x34, 0x04 0x00}; //"11"
//			byte[] test = {0x33, 0x01, 0x00};  //"4"
			// byte[] test = {0x33, 0x06, 0x00}; //"3"
			// byte[] test = {0x33, 0x02, 0x00}; //"2"
			// byte[] test = {0x33, 0x04, 0x00}; //"1"
			byte[] test = { 0x03, 0x00 }; // ""
			InputStream in = new InflateWithHeaderInputStream(new TestStream(test));
			int value, cnt = 0;
			while ((value = in.read()) != -1) {
				// Logger.println(Integer.toHexString(value));
				String hexStr = Integer.toHexString(value);
				if (hexStr.length() == 1) {
					System.out.print(0);
				}
				System.out.print(hexStr);
				System.out.print(" ");
				if (cnt == 30) {
					System.out.print("\r\n");
					cnt = 0;
				}
				cnt++;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(" ");
		try {
			InputStream in = (new DeflaterInputStream(new TestStream("")));
			int value, cnt = 0;
			while ((value = in.read()) != -1) {
				// Logger.println(Integer.toHexString(value));
				String hexStr = Integer.toHexString(value);
				if (hexStr.length() == 1) {
					System.out.print(0);
				}
				System.out.print(hexStr);
				System.out.print(" ");
				if (cnt == 30) {
					System.out.print("\r\n");
					cnt = 0;
				}
				cnt++;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 测试根据av号获取信息
	 */
	// @Test
	public void testGetAvInfo() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("av35296336", Global.downloadFormat, true);
		if (!video.getAuthorId().equals("179497530")) {
			fail("AuthorId Not Expected");
		}
		if (!video.getVideoName().contains("【炮姐】某科学的超电磁炮op合集  【无字幕版】")) {
			fail("VideoName Not Expected");
		}
		if (!video.getVideoPreview()
				.equals("http://i1.hdslb.com/bfs/archive/b04c8df57dea36b283865fca73be6a198e21c94c.jpg")) {
			fail("VideoPreview Link Not Expected");
		}
		ClipInfo clip = video.getClips().get(61860101L);
		if (clip == null) {
			fail("clip is null");
		}
		if (!clip.getLinks().get(32).contains("/61860101/61860101-1-32.flv")
				&& !clip.getLinks().get(32).contains("/61860101/61860101-1-30032.m4s")) {
			fail("Video Link Not Expected");
		}
	}

	/**
	 * 测试根据ss号获取信息
	 */
	// @Test
	public void testGetSsInfo() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("ss25617", Global.downloadFormat, false);
		if (!video.getVideoName().contains("魔法禁书目录 第三季")) {
			fail("VideoName Not Expected");
		}
		if (!video.getVideoPreview()
				.equals("https://i0.hdslb.com/bfs/bangumi/a92892921f3209f7784a954c37467c9869a1d4c1.png")) {
			fail("VideoPreview Link Not Expected");
		}
		assertEquals(video.getClips().size(), 26);
		assertEquals(video.getClips().get(58979770L).getAvId(), "av33191369");
		assertEquals(video.getClips().get(59000148L).getAvId(), "av33695610");
		assertEquals(video.getClips().get(60228375L).getAvId(), "av34175504");
		assertEquals(video.getClips().get(67704785L).getAvId(), "av34655095");
		assertEquals(video.getClips().get(61573130L).getAvId(), "av35141965");
		assertEquals(video.getClips().get(84745928L).getAvId(), "av48384067");
	}

}
