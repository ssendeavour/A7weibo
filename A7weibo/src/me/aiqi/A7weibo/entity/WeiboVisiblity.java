package me.aiqi.A7weibo.entity;

public class WeiboVisiblity {
	public static final int NORMAL = 0; // 普通微博
	public static final int PRIVATE = 1; // 私密微博
	public static final int SELECTED_GROUP = 3; // 指定分组微博
	public static final int PRIVATE_FRIEND = 4; // 密友微博

	// 微博可见性
	private int type;
	// 指定的分组可见微博的分组信息
	private int list_id;

	public WeiboVisiblity(int type) {
		this.type = type;
	}

	public WeiboVisiblity(int type, int list_id) {
		super();
		this.type = type;
		this.list_id = list_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getList_id() {
		return list_id;
	}

	public void setList_id(int list_id) {
		this.list_id = list_id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WeiboVisiblity && ((WeiboVisiblity) o).getType() == this.type) {
			if (this.type == SELECTED_GROUP) {
				return ((WeiboVisiblity) o).getList_id() == this.list_id ? true : false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String result;
		switch (type) {
		case NORMAL:
			result = "普通微博";
			break;
		case PRIVATE:
			result = "私密微博";
			break;
		case SELECTED_GROUP:
			result = "指定分组微博, 可见分组id：" + list_id;
			break;
		case PRIVATE_FRIEND:
			result = "密友微博";
			break;
		default:
			result = "未知";
			break;
		}
		return result;
	}
}
