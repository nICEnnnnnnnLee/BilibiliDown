package nicelee.bilibili.parsers.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.SysUtil;
import nicelee.ui.Global;

@Bilibili(name = "FFmpeg", note = "用于FFmpeg下载")
public class FFmpegParser extends AbstractBaseParser {

	public FFmpegParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		return "ffmpeg".equals(input.trim().toLowerCase());
	}

	@Override
	public String validStr(String input) {
		return input;
	}

	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		this.paramSetter.setRealQN(1000);
		return getDownUrl();
	}

	private String getDownUrl() {
		Logger.println("当前使用的更新源为： " + Global.ffmpegSourceActive);
		String key = "bilibili.download.ffmpeg.url." + Global.ffmpegSourceActive;
		String url = Global.settings.get(key);
		url = url.replace("{os}", SysUtil.getOS()).replace("{arch}", SysUtil.getARCH()).replace("{exeSuffix}",
				SysUtil.getEXE_SUFFIX());
		return url;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		VideoInfo vInfos = new VideoInfo();
		vInfos.setAuthor("nICEnnnnnnnLee");
		vInfos.setAuthorId("nICEnnnnnnnLee");
		vInfos.setBrief("自编译的FFmpeg版本");
		vInfos.setVideoId("FFmpeg");
		vInfos.setVideoName("FFmpeg");
		vInfos.setVideoPreview("http://i2.hdslb.com/bfs/archive/0975f9fe3ec5a65b983f43bc437b8f5698e4ea8a.jpg");
		LinkedHashMap<Long, ClipInfo> clips = new LinkedHashMap<>();
		ClipInfo clip = new ClipInfo();
		clip.setAvId("FFmpeg");
		clip.setcId(1234L);
		clip.setAvTitle("FFmpeg");
		clip.setTitle("FFmpeg");
		clip.setPage(1);
		HashMap<Integer, String> links = new HashMap<Integer, String>();
		links.put(799, "");
		clip.setUpId("nICEnnnnnnnLee");
		clip.setUpName("nICEnnnnnnnLee");
		clip.setLinks(links);

		clips.put(1234L, clip);
		vInfos.setClips(clips);
		return vInfos;
	}

}
