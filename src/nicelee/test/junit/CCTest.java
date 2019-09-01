package nicelee.test.junit;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.ccaption.ClosedCaptionDealer;
import nicelee.ui.item.JOptionPaneManager;
import nicelee.ui.item.MJTitleBar;

public class CCTest {

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
	public void test() throws IOException {
		ClosedCaptionDealer ccDealer = new ClosedCaptionDealer();
		ccDealer.getCC("av54694376", ".", "test");
//		ccDealer.save2srt("https://i0.hdslb.com/bfs/subtitle/a426a23a315d990261d74f713956c04e8449b961.json", new File("test.srt"));
	}

}
