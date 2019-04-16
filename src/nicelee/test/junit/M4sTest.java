package nicelee.test.junit;

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

import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

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
		HashMap<String, String> header = headers.getBiliWwwFLVHeaders("av44883097");
		//header.put("range", "bytes=949828-2296536");
//		util.download("https://upos-hz-mirrorkodou.acgvideo.com/upgcxcode/42/19/78601942/78601942-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEVEuxTEto8i8o859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_&deadline=1551500670&gen=playurl&nbs=1&oi=3728813686&os=kodou&platform=pc&trid=4c9dcc087d7941c98b7daddcd70fe09f&uipk=5&upsig=47e79da13fdde0c8e79ce1075a266385"
//				, "78601942-1-30080.m4s", header);
		util.download("https://upos-hz-mirrorks3u.acgvideo.com/upgcxcode/42/19/78601942/78601942-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEVEuxTEto8i8o859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_&deadline=1551500670&gen=playurl&nbs=1&oi=3728813686&os=ks3u&platform=pc&trid=4c9dcc087d7941c98b7daddcd70fe09f&uipk=5&upsig=756b7f1e6d2e84068ff20daf0998d34f"
				, "78601942-1-30216.m4s", header);
				
	}
	
	//@Test
	public void test() {
//		RandomAccessFile range = new RandomAccessFile("download/range.m4s", "rw");
//		RandomAccessFile mp4 = new RandomAccessFile("download/mp4.m4s", "rw");
		String source = "download/78601942-1-30080.m4s";
		String init = "download/init.m4s";
		String range = "download/range.m4s";
		String file = "download/file.m4s";
		//long off = 0;
		//long end = 0;
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
