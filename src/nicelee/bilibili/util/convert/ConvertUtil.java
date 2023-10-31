package nicelee.bilibili.util.convert;

import java.util.HashMap;

/**
 * https://github.com/Colerar/abv
 * MIT License
 * 
 * Copyright (c) 2022 Colerar
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class ConvertUtil {

	private final static String Table = "FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf";
	private final static long XOR = 23442827791579L;
	private final static long MASK = 2251799813685247L;
	private final static long MAX_AID = 1L << 51;
	private final static HashMap<Character, Integer> Tr = new HashMap<Character, Integer>((int) (58 / 0.75 + 1));

	static {
		for (int i = 0; i < 58; i++) {
			Tr.put(Table.charAt(i), i);
		}
	}

	public static long Bv2Av(String bvId) {
		char[] bv = bvId.toCharArray();
		char swap1 = bv[3]; bv[3] = bv[9]; bv[9] = swap1;
		char swap2 = bv[4]; bv[4] = bv[7]; bv[7] = swap2;
		long tmp = 0;
		for (int bvIdx = 3; bvIdx < bv.length; bvIdx++) { // 前三个字母是 BV1
			int tableIdx = Tr.get(bv[bvIdx]);
			tmp = tmp * 58 + (long) tableIdx; // 字母表的长度就是58
		}
		long avId = (tmp & MASK) ^ XOR;
		return avId;
	}

	public static String Av2Bv(long avNum) {
		char[] bv = "BV1000000000".toCharArray();
		int bvIdx = 11;
		long tmp = (MAX_AID | avNum) ^ XOR;
		while (tmp != 0) {
			int tableIdx = (int) (tmp % 58); // 字母表的长度就是58
			char cc = Table.charAt(tableIdx);
			bv[bvIdx] = cc;
			tmp /= 58;
			bvIdx--;
		}
		char swap1 = bv[3]; bv[3] = bv[9]; bv[9] = swap1;
		char swap2 = bv[4]; bv[4] = bv[7]; bv[7] = swap2;
		return String.valueOf(bv);
	}

	public static String Av2Bv(String av) {
		return Av2Bv(Long.parseLong(av.replace("av", "")));
	}

	public static void main(String[] args) {
		System.out.println(Bv2Av("BV1L9Uoa9EUx"));
		System.out.println(Av2Bv(111298867365120L));
		System.out.println("------------");
		System.out.println(Bv2Av("BV1UM411d7LQ"));
		System.out.println(Av2Bv(536870911L));
		System.out.println("------------");
		System.out.println(Bv2Av("BV1Si4y1e72D"));
		System.out.println(Av2Bv(536870912L));
		System.out.println("------------");
		System.out.println(Bv2Av("BV1Yx411w777"));
		System.out.println(Av2Bv(250000L));
		System.out.println(Av2Bv("250000"));
		System.out.println("------------");
		System.out.println(Av2Bv(111298867365120L));
		System.out.println(Bv2Av("BV1L9Uoa9EUx"));
	}
}
