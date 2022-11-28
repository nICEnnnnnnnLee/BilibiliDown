package nicelee.bilibili.enums;

public enum DownloadModeEnum {
	All(0), VideoOnly(1), AudioOnly(2);

	int mode;

	DownloadModeEnum(int mode) {
		this.mode = mode;
	}

	public static DownloadModeEnum getModeEnum(int mode) {
		DownloadModeEnum[] enums = DownloadModeEnum.values();
		for (DownloadModeEnum item : enums) {
			if (item.mode == mode) {
				return item;
			}
		}
		return All;
	}

	public int getMode() {
		return mode;
	}
	
}
