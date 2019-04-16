package nicelee.bilibili.enums;

public enum StatusEnum {
	NONE(9, "尚未开始"), 
	DOWNLOADING(0, "正在下载"), 
	PROCESSING(0, "转码中"), 
	STOP(-2, "人工停止"),
	FAIL(-1, "因异常造成的停止"), 
	SUCCESS(1, "成功");
	
	private int code;
	private String description;
	StatusEnum(int code, String description){
		this.code = code;
		this.description = description;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
}
