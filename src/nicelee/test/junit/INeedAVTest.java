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

	@Test
	// getVideoInfo
	public void testGetVideoInfo() {
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
		if (!clip.getLinks().get(32).contains("/61860101/61860101-1-32.flv")) {
			fail("Video Link Not Expected");
		}
	}
	
	@Test
	public void testEpIdToAvId() {
		INeedAV avs = new INeedAV();
		assertEquals(avs.EpIdToAvId("ep250435"), "av33695610");
	}

}
