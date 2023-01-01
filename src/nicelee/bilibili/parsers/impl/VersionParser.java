package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;

@Bilibili(name = "Version", note = "用于最新的版本下载")
public class VersionParser extends AbstractBaseParser {

	//BilibiliDown.v5.3.release.zip
	//https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V5.3/BilibiliDown.v5.3.release.zip
	private static final Pattern pattern = Pattern.compile("BilibiliDown\\.v([0-9]+\\.[0-9]+).*\\.zip");
	private static final String PRE_RELEASE_FLAG = "BilibiliDown.PreRelease";
	String downName;
	String downUrl;
	
	public VersionParser(Object... obj) {
		super(obj);
	}
	
	@Override
	public boolean matches(String input) {
		if(PRE_RELEASE_FLAG.equals(input)) {
			downName = downUrl = PRE_RELEASE_FLAG;
			return true;
		}
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			downName = matcher.group();
			String tagName = matcher.group(1);
			downUrl = getDownUrl(tagName, downName);
			System.out.println("匹配VersionParser: " + tagName);
		}
		return matches;
	}

	private String getDownUrl(String version, String file) {
		Logger.println("当前使用的更新源为： " + Global.updateSourceActive);
		String key = "bilibili.download.update.patterns." + Global.updateSourceActive;
		String pattern = Global.settings.getOrDefault(key, "https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V{version}/{file}");
		return pattern.replace("{version}", version).replace("{file}", file);
	}
	@Override
	public String validStr(String input) {
		return downName;
	}
	
	
	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		this.paramSetter.setRealQN(1001);
		Logger.println(downUrl);
		return downUrl;
	}
	
	
	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		VideoInfo vInfos = new VideoInfo();
		vInfos.setAuthor("nICEnnnnnnnLee");
		vInfos.setAuthorId("nICEnnnnnnnLee");
		vInfos.setBrief("最新的程序版本");
		vInfos.setVideoId(downName);
		vInfos.setVideoName(downName);
		vInfos.setVideoPreview("http://i2.hdslb.com/bfs/archive/0975f9fe3ec5a65b983f43bc437b8f5698e4ea8a.jpg");
		LinkedHashMap<Long, ClipInfo> clips = new LinkedHashMap<>();
		ClipInfo clip = new ClipInfo();
		clip.setAvId(downName);
		clip.setcId(1234L);
		clip.setAvTitle(downName);
		clip.setTitle(downName);
		clip.setPage(1);
		HashMap<Integer, String> links = new HashMap<Integer, String>();
		links.put(1001, "");
		clip.setUpId("nICEnnnnnnnLee");
		clip.setUpName("nICEnnnnnnnLee");
		clip.setLinks(links);
		
		clips.put(1234L, clip);
		vInfos.setClips(clips);
		return vInfos;
	}

}
