package nicelee.test.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.util.HttpHeaders;
import nicelee.util.HttpRequestUtil;

public class M4sTest {

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
	public void testDownloadSeg() {
		System.out.println(new File("download/seg.flv").length());
		System.out.println(new File("download/seg-1.flv").length());
		HttpRequestUtil util = new HttpRequestUtil();
		HttpHeaders headers = new HttpHeaders();
		HashMap<String, String> header = headers.getBiliWwwHeaders("av44883097");
		//header.put("range", "bytes=949828-2296536");
//		util.download("https://cn-jszj-dx-v-09.acgvideo.com/upgcxcode/42/19/78601942/78601942-1-80.flv?expires=1551418800&platform=pc&ssig=mp6LJJhWQ7Wbg7w8oBk4KQ&oi=3728813686&trid=58b985e70747459a83ff439dfab3bb53&nfb=maPYqpoel5MI3qOUX6YpRA==&nfc=1"
//				, "seg.flv", header);
				
	}
	
	//@Test
	public void test() {
//		RandomAccessFile range = new RandomAccessFile("download/range.m4s", "rw");
//		RandomAccessFile mp4 = new RandomAccessFile("download/mp4.m4s", "rw");
		String source = "download/78601942-1-30080.m4s";
		String init = "download/init.m4s";
		String range = "download/range.m4s";
		String file = "download/file.m4s";
		long off = 0;
		long end = 0;
		// 0 ~ 975, 976-1211 , 1212 ~
		copyFile(source, init, 0, 975);
		copyFile(source, range, 976, 1211);
		copyFile(source, file, 1212, Long.MAX_VALUE);
	}

	/**
	 * @param source
	 * @param dst
	 */
	private void copyFile(String source, String dst, long off, long end) {
		RandomAccessFile raf = null;
		RandomAccessFile rafDst = null;
		try {
			raf = new RandomAccessFile(source, "r");
			rafDst = new RandomAccessFile(dst, "rw");
			
			byte buffer[] = new byte[1024];
			long cnt = 0;
			int size = raf.read(buffer);
			raf.seek(off);
			while(size > 0 && cnt <= end - off + 1) {
				if(cnt + size <= end - off + 1) {
					rafDst.write(buffer);
					cnt += size;
				}else {
					rafDst.write(buffer, 0, (int)(end - off + 1 - cnt));
					cnt += end - off + 1 - cnt;
				}
				size = raf.read(buffer);
			}
			System.out.println(cnt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rafDst.close();
			} catch (Exception e) {
			}
			try {
				raf.close();
			} catch (Exception e) {
			}
		}
	}

}
