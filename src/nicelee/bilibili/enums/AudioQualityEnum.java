package nicelee.bilibili.enums;

public enum AudioQualityEnum {
	FLAC("无损FLAC", 3, "无损FLAC"),
	HIGH("高品质", 2, "高清320K"),
	STANDARD("标准", 1, "标准192K"),
	FLUENT("流畅", 0, "流畅128K");
	
	private String quality;
	private int qn;
	private String description;
	
	AudioQualityEnum(String quality, int qn, String description){
		this.quality = quality;
		this.qn = qn;
		this.description = description;
	}
	
	public static String getQualityDescript(int qn) {
		AudioQualityEnum[] enums = AudioQualityEnum.values();
		for(AudioQualityEnum item : enums) {
			if(item.getQn() == qn) {
				return item.getDescription();
			}
		}
		return null;
	}
	
	public static int getQN(String quality) {
		AudioQualityEnum[] enums = AudioQualityEnum.values();
		for(AudioQualityEnum item : enums) {
			if(item.getQuality().equals(quality)) {
				return item.getQn();
			}
		}
		return 0;
	}
	
	public static boolean contains(int quality) {
		AudioQualityEnum[] enums = AudioQualityEnum.values();
		for(AudioQualityEnum item : enums) {
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
