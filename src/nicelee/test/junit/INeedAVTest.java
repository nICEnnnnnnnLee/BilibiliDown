package nicelee.test.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.INeedAV;
import nicelee.model.ClipInfo;
import nicelee.model.VideoInfo;

public class INeedAVTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试根据av号获取信息
	 */
	@Test
	public void testGetAvInfo() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("av35296336", true);
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
		ClipInfo clip = video.getClips().get(1);
		if (clip == null) {
			fail("clip is null");
		}
		if (clip.getcId() != 61860101) {
			fail("Clip cId Not Expected");
		}
		if (!clip.getLinks().get(32).contains("/61860101/61860101-1-32.flv") && !clip.getLinks().get(32).contains("/61860101/61860101-1-30032.m4s")) {
			fail("Video Link Not Expected");
		}
	}
	
	/**
	 * 测试根据ss号获取信息
	 */
	@Test
	public void testGetSsInfo() {
		INeedAV avs = new INeedAV();
		VideoInfo video = null;
		video = avs.getVideoDetail("ss25617", false);
		if (!video.getVideoName().contains("魔法禁书目录 第三季")) {
			fail("VideoName Not Expected");
		}
		if (!video.getVideoPreview()
				.equals("https://i0.hdslb.com/bfs/bangumi/a92892921f3209f7784a954c37467c9869a1d4c1.png")) {
			fail("VideoPreview Link Not Expected");
		}
		assertEquals(video.getClips().size(), 26);
		assertEquals(video.getClips().get(1).getcId(), 58979770);
		assertEquals(video.getClips().get(1).getAvId(), "av33191369");
		assertEquals(video.getClips().get(2).getcId(), 59000148);
		assertEquals(video.getClips().get(2).getAvId(), "av33695610");
		assertEquals(video.getClips().get(3).getcId(), 60228375);
		assertEquals(video.getClips().get(3).getAvId(), "av34175504");
		assertEquals(video.getClips().get(4).getcId(), 67704785);
		assertEquals(video.getClips().get(4).getAvId(), "av34655095");
		assertEquals(video.getClips().get(5).getcId(), 61573130);
		assertEquals(video.getClips().get(5).getAvId(), "av35141965");
		assertEquals(video.getClips().get(26).getcId(), 84745928);
		assertEquals(video.getClips().get(26).getAvId(), "av48384067");
	}
	
	@Test
	public void testEpIdToAvId() {
		INeedAV avs = new INeedAV();
		assertEquals(avs.EpIdToAvId("ep250435"), "av33695610");
	}

}
