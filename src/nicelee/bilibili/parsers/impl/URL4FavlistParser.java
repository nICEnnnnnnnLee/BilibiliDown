package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;


import nicelee.bilibili.annotations.Bilibili;

@Bilibili(name = "URL-favlist-parser", ifLoad = "listAll", note = "收藏夹解析器， 基于MLParser")
public class URL4FavlistParser extends MLParser {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=([0-9]+)");// 个人收藏夹
	
	public URL4FavlistParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			mlIdNumber = matcher.group(2);
			return true;
		}
		return false;
	}

}
