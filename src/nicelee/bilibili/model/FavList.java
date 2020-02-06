package nicelee.bilibili.model;

public class FavList {

	long ownerId;// 收藏夹用户id
	long fId; // 收藏夹id
	int size; // 收藏夹内视频个数
	String title = "111"; // 收藏夹名称
	
	public FavList(long ownerId, long fId, int size, String title) {
		this.ownerId = ownerId;
		this.fId = fId;
		this.size = size;
		this.title = title;
	}
	
	@Override
	public String toString() {
		return title + "(" + size +")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this != null && obj != null) {
			if (obj instanceof FavList) {
				FavList fav = (FavList) obj;
				return (this.fId == fav.fId);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) fId;
	}


	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public long getfId() {
		return fId;
	}

	public void setfId(long fId) {
		this.fId = fId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
