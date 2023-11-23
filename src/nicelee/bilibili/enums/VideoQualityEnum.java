package nicelee.bilibili.enums;

public enum VideoQualityEnum {
	QDanmu("弹幕", 801, "弹幕"),
	QCC("CC字幕", 800, "字幕"),
	Q8K("8K", 127, "超高清 8K"),
	QHDR("HDR", 125, "真彩 HDR"),
	Q4K("4K", 120, "超清4K"),
	Q1080P60("1080P60", 116, "高清1080P60"),
	Q1080PPlus("1080P+", 112, "高清1080P+"),
	Q1080P("1080P", 80, "高清1080P"),
	Q720P60("720P60", 74, "高清720P60"),
	Q720P("720P", 64, "高清720P"),
	Q480P("480P", 32, "清晰480P"),
	Q320P("320P", 16, "流畅320P");
	
	private String quality;
	private int qn;
	private String description;
	
	VideoQualityEnum(String quality, int qn, String description){
		this.quality = quality;
		this.qn = qn;
		this.description = description;
	}
	
	public static String getQualityDescript(int qn) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for(VideoQualityEnum item : enums) {
			if(item.getQn() == qn) {
				return item.getDescription();
			}
		}
		return null;
	}
	
	public static int getQN(String quality) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for(VideoQualityEnum item : enums) {
			if(item.getQuality().equals(quality)) {
				return item.getQn();
			}
		}
		return 0;
	}
	
	public static boolean contains(int quality) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for(VideoQualityEnum item : enums) {
			if(item.getQn() == quality) {
				return true;
			}
		}
		return false;
	}

	public String getQuality() {
		return quality;
	}

	public int getQn() {
		return qn;
	}

	String getDescription() {
		return description;
	}
}
