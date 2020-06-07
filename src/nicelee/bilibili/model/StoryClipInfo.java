package nicelee.bilibili.model;

public class StoryClipInfo {

	long node_id;
	String option; // 跳入当前片段的选择
	long cid;
//	long choices[]; // 当前的选择
	
	public StoryClipInfo(long cid) {
		this.cid = cid;
	}
	public StoryClipInfo(long cid, long node_id, String option) {
		this.cid = cid;
		this.node_id = node_id;
		this.option = option;
	}
	public long getNode_id() {
		return node_id;
	}
	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}

	@Override
	public int hashCode() {
		return (int) cid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this != null && obj != null) {
			if (obj instanceof StoryClipInfo) {
				StoryClipInfo clip = (StoryClipInfo) obj;
				return (this.getCid() == clip.getCid());
			}
		}
		return false;
	}
}
