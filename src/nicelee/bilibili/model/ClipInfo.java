package nicelee.bilibili.model;

import java.util.HashMap;

public class ClipInfo {
	String avTitle;
	long cId;
	String avId;
	int page;
	String title;
	String picPreview;
	String listName; // 收藏夹名称 或其它集合名称（不一定存在）
	String listOwnerName; // 收藏夹主人 或其它集合的拥有者（不一定存在）
	// 解析器太多，不想一个一个再去改，只有收藏夹有下面俩
	long favTime;	// 收藏时间
	long cTime;		// 发布、更新时间
	String upName;
	String upId;
	HashMap<Integer, String> links;
	
	int remark = -1; // 用于ss番剧查询时显示顺序

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------\r\n");
		sb.append("--avTitle为 :").append(avTitle).append(" \r\n");
		sb.append("--page为 :").append(page).append(" \r\n");
		sb.append("--cId为 :").append(cId).append(" \r\n");
		sb.append("--title为 :").append(title).append(" \r\n");
		if (avId != null)
			sb.append("--avId为 :").append(avId).append(" \r\n");
		if (listName != null)
			sb.append("--listName为 :").append(listName).append(" \r\n");
		if (listOwnerName != null)
			sb.append("--listOwnerName为 :").append(listOwnerName).append(" \r\n");
		
//		if (links != null) {
//			for (String link : links.values()) {
//				sb.append("----下载链接 :").append(link).append(" \r\n");
//			}
//		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this != null && obj != null) {
			if (obj instanceof ClipInfo) {
				ClipInfo clip = (ClipInfo) obj;
				return (this.cId == clip.cId);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) cId;
	}

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getAvId() {
		return avId;
	}

	public void setAvId(String avId) {
		this.avId = avId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<Integer, String> getLinks() {
		return links;
	}

	public void setLinks(HashMap<Integer, String> links) {
		this.links = links;
	}

	public int getRemark() {
		int p = remark == -1 ? page : remark;
		return p;
	}

	public void setRemark(int remark) {
		this.remark = remark;
	}

	public String getAvTitle() {
		return avTitle;
	}

	public void setAvTitle(String avTitle) {
		this.avTitle = avTitle;
	}

	public String getPicPreview() {
		return picPreview;
	}

	public void setPicPreview(String picPreview) {
		this.picPreview = picPreview;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getListOwnerName() {
		return listOwnerName;
	}

	public void setListOwnerName(String listOwnerName) {
		this.listOwnerName = listOwnerName;
	}

	public String getUpName() {
		return upName;
	}

	public void setUpName(String upName) {
		this.upName = upName;
	}

	public String getUpId() {
		return upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public long getFavTime() {
		return favTime;
	}

	public void setFavTime(long favTime) {
		this.favTime = favTime;
	}

	public long getcTime() {
		return cTime;
	}

	public void setcTime(long cTime) {
		this.cTime = cTime;
	}

}
