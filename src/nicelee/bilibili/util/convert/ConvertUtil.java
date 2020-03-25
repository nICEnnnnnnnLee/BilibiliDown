package nicelee.bilibili.util.convert;

import java.util.HashMap;

/**
 * https://github.com/CCRcmcpe/AV-BV-Convert 
 * MIT License
 * 
 * Copyright (c) 2020 CCRcmcpe
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

	private final static String Table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
	private final static int Xor = 177451812;
	private final static long Add = 100618342136696320L;
	private final static int[] S = { 11, 10, 3, 8, 4, 6, 2, 9, 5, 7 };
	private final static HashMap<Character, Integer> Tr = new HashMap<Character, Integer>((int) (58 / 0.75 + 1));

	static {
		for (int i = 0; i < 58; i++) {
			Tr.put(Table.charAt(i), i);
		}
	}

	public static long Bv2Av(String bv) {
		long r = 0;
		long pow = 1;
		for (int i = 0; i < 10; i++) {
			r += Tr.get(bv.charAt(S[i])) * pow;
			pow *= 58;
		}
		return (r - Add) ^ Xor;
	}

	public static String Av2Bv(long avNum) {
		long x1 = (avNum ^ Xor) + Add;
		long pow = 1;
		char[] r = "BV          ".toCharArray();
		for (int i = 0; i < 10; i++) {
			int index = (int) (x1 / pow % 58);
			pow *= 58;
			r[S[i]] = Table.charAt(index);
		}
		return String.valueOf(r);
	}

	public static String Av2Bv(String av) {
		return Av2Bv(Long.parseLong(av.replace("av", "")));
	}

	public static void main(String[] args) {
		System.out.println(Bv2Av("BV1Yx411w777"));
		System.out.println(Av2Bv(250000L));
		System.out.println(Av2Bv("250000"));
	}
}
