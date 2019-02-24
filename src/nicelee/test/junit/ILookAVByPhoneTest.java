package nicelee.test.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.INeedAVbPhone;
import nicelee.model.VideoInfo;

public class ILookAVByPhoneTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// getVideoInfo
	public void testGetVideoInfo() {
		INeedAVbPhone util = new INeedAVbPhone();
		VideoInfo video = util.getVideoFromMobileById("av35296336");
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
		if (!video.getVideoLink().contains(".mp4")) {
			fail("VideoLink Not Valid");
		}
	}

}
