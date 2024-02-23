package nicelee.bilibili.model;

public class TaskInfo {
	int orderNum;
	ClipInfo clip;
	String fileName;	// 文件名称
	String fileSize;	// 文件大小
	String qn;			// 清晰度
	String status;		// 开始为null，中间会有个过渡状态"just put in download panel"，最终会落到 "success" 或者 "fail" 或者 "stop" 或者 其它失败原因
	
	public TaskInfo(ClipInfo clip, int orderNum) {
		this.clip = clip;
		this.orderNum = orderNum;
	}
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this != null && obj != null) {
			if (obj instanceof TaskInfo) {
				TaskInfo c = (TaskInfo) obj;
				return (this.clip.equals(c.clip));
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) clip.hashCode();
	}

	public ClipInfo getClip() {
		return clip;
	}

	public void setClip(ClipInfo clip) {
		this.clip = clip;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getQn() {
		return qn;
	}

	public void setQn(String qn) {
		this.qn = qn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

}
