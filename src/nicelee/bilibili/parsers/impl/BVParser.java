package nicelee.bilibili.parsers.impl;

import java.util.regex.Pattern;
import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;

@Bilibili(name = "bv")
public class BVParser extends AbstractBaseParser {
	private static final Pattern pattern = Pattern.compile("BV[0-9A-Za-z]+");

	private String avId;

	public BVParser(Object... obj) {
		super(obj);
	}

	public boolean matches(String input) {
		this.matcher = pattern.matcher(input);
		boolean matches = this.matcher.find();
		if (matches) {
			this.avId = this.matcher.group();
			System.out.println("匹配BVParser: " + this.avId);
		}
		return matches;
	}

	public String validStr(String input) {
		return this.avId;
	}

	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("BVParser正在获取结果" + this.avId);
		return getAVDetail(this.avId, videoFormat, getVideoLink);
	}
}