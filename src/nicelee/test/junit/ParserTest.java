package nicelee.test.junit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.parsers.IInputParser;
import nicelee.bilibili.parsers.InputParser;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.ui.Global;

public class ParserTest {

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
	 * 测试根据av号获取信息
	 */
	//@Test
	public void testAVParser() {
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
	//@Test
	public void testEPParser() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("https://www.bilibili.com/bangumi/play/ep250435/", 0, false);
		assertEquals("av33695610", video.getVideoId());
		assertEquals("928123", video.getAuthorId());
		assertEquals("http://i0.hdslb.com/bfs/archive/3fe329f522a85c92d142db623d3fc5787efdf8a2.jpg", video.getVideoPreview());
		assertEquals("av33695610", video.getClips().get(59000148L).getAvId());
	}
	
	/**
	 * 测试根据ss号获取信息
	 */
	//@Test
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
	
	//@Test
	public void testMdParser() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("md134912", 0, false);//ss25617
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

	/**
	 * 测试解析UP主 个人特定频道 
	 * https://space.bilibili.com/546195/channel/detail?cid=21838
	 */
	//@Test
	public void testAVList4Channel() {
		HttpRequestUtil util = new HttpRequestUtil();
		IInputParser parser = new InputParser(util, 20, "listAll");
		
		assert(parser.matches("https://space.bilibili.com/546195/channel/detail?cid=21838 p=1"));
		String result = (parser.validStr("https://space.bilibili.com/546195/channel/detail?cid=21838 p=1"));
		assertEquals(" av12083728 av12388205 av12845082 av13480868", result);
	}
	
	/**
	 * 测试解析UP主 所有视频 
	 * https://space.bilibili.com/5276/video
	 */
	//@Test
	public void testAVList4Space() {
		HttpRequestUtil util = new HttpRequestUtil();
		IInputParser parser = new InputParser(util, 5, "listAll");
		//parser.setPageSize(5);
		
		assert(parser.matches("https://space.bilibili.com/5276/video p=2"));
		String result = (parser.validStr("https://space.bilibili.com/5276/video p=2"));
		//最新的第6 ~10 个
		System.out.println(result);
		String results[] = result.split(" ");
		assertEquals(results.length, 6);
		for(int i =1; i <= 5; i++) {
			assert(results[i].matches("^av[0-9]+$"));
		}
	}
	
	/**
	 * https://h.bilibili.com/44254262
	 * URL4PictureParser
	 */
	@Test
	public void testURL4PictureParser() {
		HttpRequestUtil util = new HttpRequestUtil();
		IInputParser parser = new InputParser(util, 20, "listAll");
		
		System.out.println(parser.matches("https://h.bilibili.com/44254262"));
		System.out.println(parser.matches("44254262"));
		
		System.out.println("[/\\|:*?<>\"$]".replaceAll("[|:*?<>\"$]", ""));
		
		
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\h43603721-0-p2.jpg";
		String formattedTitle = "动漫基地投稿、Ⅴ\\\\动漫基地投稿、Ⅴ-pn1-第2张-0";
		System.out.println(path);
		System.out.println(formattedTitle);
		path = path.replaceAll("(?:av|h)[0-9]+-[0-9]+-p[0-9]+", formattedTitle);
		System.out.println(path);
		
		Pattern suffixPattern = Pattern.compile("\\.[^.]+$");
		String fileName = "hello.test.webp";
		Matcher suffixM = suffixPattern.matcher(fileName);
		suffixM.find();
		System.out.println(suffixM.group());;
	}

}
