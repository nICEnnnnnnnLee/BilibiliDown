package nicelee.bilibili.parsers.impl;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import nicelee.bilibili.annotations.Bilibili;

@Bilibili(name = "B23Parser", note = "B23短网址解析")
public class B23Parser extends BVParser {

	public B23Parser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		System.out.println(input);
		if(input.matches("^https?://b23\\.tv/.*")) {
			try {
				URL url = new URL(input);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.connect();
				
				String locatin = conn.getHeaderField("Location");
				System.out.println(locatin);
				if(locatin == null)
					return false;
				return super.matches(locatin);
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

}
