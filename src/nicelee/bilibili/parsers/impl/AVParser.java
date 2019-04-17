package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;


import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;

@Bilibili(name = "av")
public class AVParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("av[0-9]+");
	private String avId;

//	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
	public AVParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			avId = matcher.group();
			System.out.println("匹配AVParser: " + avId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return avId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("AVParser正在获取结果" + avId);
		return getAVDetail(avId, videoFormat, getVideoLink);
	}

}
