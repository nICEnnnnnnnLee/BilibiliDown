package nicelee.bilibili.model;

import java.util.LinkedHashMap;

public class VideoInfo {
	String videoName;
	String videoId;
	String author;
	String authorId;
	String videoPreview;
	String brief;
	LinkedHashMap<Long, ClipInfo> clips;// 未使用 Integer 为 Page
	String videoLink;

	public void print() {
		System.out.print(toString());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------\r\n");
		sb.append("视频名称为 :").append(videoName).append(" \r\n");
		sb.append("视频ID为 :").append(videoId).append(" \r\n");
		sb.append("作者为 :").append(author).append(" \r\n");
		sb.append("作者ID为 :").append(authorId).append(" \r\n");
		sb.append("作者的话为 :").append(brief).append(" \r\n");
		sb.append("预览图路径为 :").append(videoPreview).append(" \r\n");
		sb.append("视频下载链接为 :").append(videoLink).append(" \r\n");
		if (clips != null) {
			sb.append("当前有小视频个数 :").append(clips.size()).append(" \r\n");
			for (ClipInfo clip : clips.values()) {
				sb.append(clip.toString());
			}
		}
		sb.append("------------------------------\r\n");
		return sb.toString();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getVideoPreview() {
		return videoPreview;
	}

	public void setVideoPreview(String videoPreview) {
		this.videoPreview = videoPreview;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public LinkedHashMap<Long, ClipInfo> getClips() {
		return clips;
	}

	public void setClips(LinkedHashMap<Long, ClipInfo> clips) {
		this.clips = clips;
	}

}
