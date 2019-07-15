package nicelee.bilibili.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	
	private static String appkey(String key, int add) {
		byte[] keyBytes = key.getBytes();
		for (int i = 0; i < keyBytes.length; i++) {
	        int index = 0;
	        int ch = keyBytes[i];
	        int num = keyBytes[i] + add; 
	        num = (num - 65) % 57 + 65; 
	        while (90 < num && 97 > num) {
	            add = (index * add) + add; index ++;
	            num = ch + add;
	            num = (num - 65) % 57 + 65;
	        }
	        keyBytes[i] = (byte) num;
	    }
	    return new String(keyBytes);
	}
	
	private static String encrypt(String param) {
		byte[] secretBytes = null;
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 对字符串进行加密
			md.update(param.getBytes());
			// 获得加密后的数据
			secretBytes = md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1,secretBytes).toString(16);// 16进制数字
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	public static String sign(String param, String appSecret) {
		return encrypt(param + appSecret);
	}
	// 主函数调用测试
	public static void main(String[] args) {
		System.out.println(sign("actionkey=appkey&aid=2478750&appkey=YvirImLGlLANCLvM&build=5423000&cid=3876154&device=android&expire=0&fnval=80&fnver=0&force_host=0&fourk=0&mid=0&mobi_app=android&npcybs=0&otype=json&platform=android&qn=80&quality=3&ts=1561814729",
				 "JNlZNgfNGKZEpaDTkCdPQVXntXhuiJEM"));
		
		System.out.println("4ae40cf1894d2c30bb0b364c599f09c7");
	}

}
