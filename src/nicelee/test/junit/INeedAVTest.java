package nicelee.test.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.ui.Global;

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
	@Test
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
