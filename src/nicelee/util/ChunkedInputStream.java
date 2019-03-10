package nicelee.util;

import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream{
	
	int length = 0;
	int[] len = new int[32];
	int pEnd = 0;
	InputStream in;
	
	public ChunkedInputStream(InputStream in) {
		this.in = in;
	}
	
	
	@Override
	public int read() throws IOException {
		if( length > 1) {
			length --;
			return in.read();
		}else if( length == 1) {
			int content = in.read();
			//读取换行符\r\n
			in.read();
			in.read();
			length --;
			return content;
		}else {
			//先读取长度,直到遇到\r
			int content = in.read();
			while(content != 0x0d && content != -1) {
				len[pEnd] = content;
				pEnd ++;
				content = in.read();
			}
			//读取\n
			in.read();
			if(pEnd == 0) {
				return -1;
			}
			//获取长度
			length = getLength();
			return read();
		}
	}
	
	final char[] HEX_NUMBER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	int getLength() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pEnd; i++) {
			//数字
			if( len[i] <= 0x39 ) {
				sb.append(HEX_NUMBER[len[i] - 0x30]);
			}else if ( len[i] <= 0x46 ) {
			//大写字母
				sb.append(HEX_NUMBER[len[i] - 0x41 + 10]);
			}else {
			//小写字母
				sb.append(HEX_NUMBER[len[i] - 0x61 + 10]);
			}
		}
		pEnd = 0;
		return Integer.valueOf(sb.toString(), 16);
	}
}
