package nicelee.test.junit;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.util.CmdUtil;

/*
	h265_big nvdia硬解耗时：555ms
	h265_big软解耗时：8131ms
	h265_big intel硬解耗时：11789ms
	h265_small nvdia硬解耗时：2004ms
	h265_small 软解耗时：2331ms
	h265_small intel硬解耗时：2646ms
 */
public class FFmpegTest {

	String FFMPEG_PATH;
	String h265_small_video;
	String h265_small_audio;
	String h265_big_video;
	String h265_big_audio;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		FFMPEG_PATH = "ffmpeg2.exe";
		h265_small_video = "download/测试杜比无损BV13L4y1K7th-126-p1_video.m4s";
		h265_small_audio = "download/测试杜比无损BV13L4y1K7th-126-p1_audio.m4s";
		h265_big_video = "download/测试杜比无损BV1tB4y1E7oT-125-p1_video.m4s";
		h265_big_audio = "download/测试杜比无损BV1tB4y1E7oT-125-p1_audio.m4s";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void h265_small_soft() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-i", h265_small_video, "-i", h265_small_audio, "-c", "copy",
				"h265_small_soft.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_small 软解耗时：" + (end - begin) + "ms");
	}

	@Test
	public void h265_small_qsv() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-hwaccel", "qsv", "-i", h265_small_video, "-i", h265_small_audio, "-c",
				"copy", "h265_small_qsv.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_small intel硬解耗时：" + (end - begin) + "ms");
	}

	@Test
	public void h265_small_cuda() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-hwaccel", "cuda", "-i", h265_small_video, "-i", h265_small_audio, "-c",
				"copy", "h265_small_cuda.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_small nvdia硬解耗时：" + (end - begin) + "ms");
	}

	@Test
	public void h265_big_soft() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-i", h265_big_video, "-i", h265_big_audio, "-c", "copy",
				"h265_big_soft.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_big软解耗时：" + (end - begin) + "ms");
	}

	@Test
	public void h265_big_qsv() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-hwaccel", "qsv", "-i", h265_big_video, "-i", h265_big_audio, "-c", "copy",
				"h265_big_qsv.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_big intel硬解耗时：" + (end - begin) + "ms");
	}

	@Test
	public void h265_big_cuda() {
		long begin = System.currentTimeMillis();
		String cmd[] = { FFMPEG_PATH, "-y", "-hwaccel", "cuda", "-i", h265_big_video, "-i", h265_big_audio, "-c",
				"copy", "h265_big_cuda.mp4" };
		CmdUtil.run(cmd);
		long end = System.currentTimeMillis();
		System.out.println("h265_big nvdia硬解耗时：" + (end - begin) + "ms");
		System.out.println(Arrays.toString(cmd));
	}

}
