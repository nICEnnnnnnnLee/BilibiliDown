package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;

//@Bilibili(name = "test", note = "用于测试")
public class TestParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("nicelee\\.test");

	public TestParser(Object... obj) {
		super(obj);
	}
	
	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		return matcher.matches();
	}

	@Override
	public String validStr(String input) {
		return input;
	}
	
	
	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		this.paramSetter.setRealQN(80);
		return ".test";
	}
	
	
	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		VideoInfo vInfos = new VideoInfo();
		vInfos.setAuthor("test");
		vInfos.setAuthorId("12345");
		vInfos.setBrief("测试breief");
		vInfos.setVideoId("nicelee.test");
		vInfos.setVideoName("hhhhhhh");
		vInfos.setVideoPreview("http://i2.hdslb.com/bfs/archive/0975f9fe3ec5a65b983f43bc437b8f5698e4ea8a.jpg");
		LinkedHashMap<Long, ClipInfo> clips = new LinkedHashMap<>();
		ClipInfo clip = new ClipInfo();
		clip.setAvId("nicelee.test");
		clip.setcId(1234567L);
		clip.setAvTitle("hh");
		clip.setTitle("666");
		clip.setPage(1);
		HashMap<Integer, String> links = new HashMap<Integer, String>();
		links.put(80, ".test");
		clip.setLinks(links);
		
		
		clips.put(1234567L, clip);
		vInfos.setClips(clips);
		return vInfos;
	}

}
