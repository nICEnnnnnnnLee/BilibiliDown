package nicelee.bilibili.model;

public class StoryClipInfo {

	String node_id;
	String option;
	long cid;
	
	public StoryClipInfo(long cid) {
		this.cid = cid;
	}
	public StoryClipInfo(long cid, String node_id, String option) {
		this.cid = cid;
		this.node_id = node_id;
		this.option = option;
	}
	public String getNode_id() {
		return node_id;
	}
	public void setNode_id(String node_id) {
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
}
