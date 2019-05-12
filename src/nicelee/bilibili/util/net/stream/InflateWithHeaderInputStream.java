package nicelee.bilibili.util.net.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;

import nicelee.bilibili.util.Logger;

//未使用
public class InflateWithHeaderInputStream extends InputStream {

	private InputStream in;
	private boolean isHeaderRead;
	private int[] header = {0x78, 0x9c};//{0x01, 0x78};//{0x01, 0x78};//{0x78, 0x01};// {0x78, 0x9c}
	private int pHeader = 0;
	
	private boolean isTailToRead;
	private int[] tail = {1, 0};
	private int pTail = 0;
	Adler32 ad;
	long adler = 1;
	public InflateWithHeaderInputStream(InputStream in) {
		this.in = in;
		isHeaderRead = false;
		isTailToRead = false;
		ad = new Adler32();
	}

	@Override
	public int read() throws IOException {
		int value;
		value =  resultAddHeadAndTail();
		System.out.println(Integer.toHexString(value));
		if(isTailToRead) {
			System.out.println("----------" + Integer.toHexString(value));
		}
		return value;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private int resultAddHeadAndTail() throws IOException {
		int value;
		if(!isHeaderRead) {
			value = header[pHeader];
			pHeader++;
			if(pHeader > header.length - 1) {
				isHeaderRead = true;
			}
			return value;
		}else if(!isTailToRead){
			value = in.read();
//			value = -1;
			if(value == -1) {
				return -1;
//				isTailToRead = true;
//				pTail ++;
//				return ((tail[1] & 0xff00) >> 8);
////				return (tail[1] & 0xff);
			}else {
				adler_32Check(value);
				return value;
			}
		}else {
			if(pTail > 3) {
				return -1;
			}else if(pTail == 1){
				pTail ++;
				return (tail[1] & 0xff);
//				return ((tail[1] & 0xff00) >> 8);
			}else if(pTail == 2){
				pTail ++;
				return ((tail[0] & 0xff00) >> 8);
//				return (tail[0] & 0xff);
			}else{
				pTail ++;
				return (tail[0] & 0xff);
//				return ((tail[0] & 0xff00) >> 8);
			}
		}
	}

	/**
	 * @param value
	 */
	private void adler_32Check(int value) {
//		long s1 = adler & 0xffff;
//        long s2 = (adler >> 16) & 0xffff;
//        s1 = (s1 + value) % 65521;
//        s2 = (s2 + s1)     % 65521;
//        adler = (s2 << 16) + s1;
//		tail[0] = (int) (adler & 0xffff);
//		tail[1] = (int) ((adler >> 16) & 0xffff);
		ad.update(value);
		tail[0] = (int) (ad.getValue() & 0xffff);
		tail[1] = (int) ((ad.getValue() >> 16) & 0xffff);
	}

}
