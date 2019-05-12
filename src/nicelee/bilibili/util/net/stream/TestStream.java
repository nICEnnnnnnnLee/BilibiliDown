package nicelee.bilibili.util.net.stream;

import java.io.IOException;
import java.io.InputStream;

//未使用
public class TestStream extends InputStream{

	byte[] content;
	int pointer;
	public TestStream(String testStr) {
		content = testStr.getBytes();
		pointer = 0;
	}
	
	public TestStream(byte[] test) {
		content = test;
		pointer = 0;
	}
	
	
	@Override
	public int read() throws IOException {
		if(pointer > content.length -1) {
			return -1;
		}else {
			int value = content[pointer];
			pointer ++;
			return value;
		}
	}
}
