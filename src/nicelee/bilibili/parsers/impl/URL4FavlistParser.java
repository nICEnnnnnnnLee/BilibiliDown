package nicelee.bilibili.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.util.HttpRequestUtil;

@Bilibili(name = "URL-favlist-parser", note = "收藏夹解析器， 基于MLParser")
public class URL4FavlistParser extends MLParser {

	private final static Pattern pattern = Pattern.compile("space\\.bilibili\\.com/([0-9]+)/favlist\\?fid=([0-9]+)");// 个人收藏夹
	
	@Override
	public void init(HttpRequestUtil util ) {
		super.init(util);
		this.util = util;
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			mlIdNumber = matcher.group(2);
			//获取参数
			Matcher paramMatcher = paramPattern.matcher(input);
			if(paramMatcher.find()) {
				page = Integer.parseInt(paramMatcher.group(1));
			}
			return true;
		}
		return false;
	}

}
