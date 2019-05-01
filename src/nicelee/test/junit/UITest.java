package nicelee.test.junit;

import java.awt.FlowLayout;
import javax.swing.JFrame;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.ui.item.JOptionPaneManager;
import nicelee.ui.item.MJTitleBar;

public class UITest {

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

	//@Test
	public void test() {
		for (int i = 0; i < 100; i++) {
			JOptionPaneManager.showMsgWithNewThread("title", "msg" + i);
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("结束");
	}
	
	@Test
	public void testTitleBar() {
		JFrame frame = new JFrame();
		frame.setSize(450, 500);
		frame.setUndecorated(true);
		frame.setLayout(new FlowLayout());
		//this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		MJTitleBar titleBar = new MJTitleBar(frame);
		frame.add(titleBar);
		frame.setVisible(true);
		frame.show();
		int cnt=0;
		while(cnt <= 4) {
			cnt ++;
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
