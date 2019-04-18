package nicelee.bilibili.parsers.impl;

import nicelee.bilibili.util.Logger;

public abstract class AbstractPageQueryParser<T> extends AbstractBaseParser{

	protected int API_PMAX = 20; // API 实际每次分页查询个数
	protected T pageQueryResult;
	
	public AbstractPageQueryParser(Object... obj) {
		super(obj);
	}

	/**
	 * 初始化 result 设置每次分页实际查询的个数
	 */
	public abstract void initPageQueryParam();

	/**
	 * 
	 * <p>
	 * 查询第p 页的结果，将第min 到 第max 的数据加入 result
	 * </p>
	 * (此处每页大小为固定设置，与配置文件不一定相符)
	 * 
	 * @param begin
	 * @param end
	 * @param obj
	 * @return 查询成功/ 失败
	 */
	protected abstract boolean query(int p, int min, int max, Object... obj);

	/**
	 * 分页查询
	 * 
	 * @param pageSize
	 * @param page
	 * @param obj
	 * @return 以pageSize 进行分页查询，获取第page页的结果
	 */
	public T result(int pageSize, int page, Object... obj) {
		initPageQueryParam();
		// 获取第 begin 个到第 end 个视频
		int begin = (page - 1) * pageSize + 1;
		int end = page * pageSize;
		Logger.printf("当前 pageSize: %d, 查询第 %d页。 即获取第 %d 到第%d个视频",
				pageSize, page, begin, end);
		query(begin, end, obj);
		return pageQueryResult;
	}

	/**
	 * 查询第begin 到 第end 的结果, 加入 result
	 * 
	 * @param begin
	 * @param end
	 * @param obj
	 * @return 查询成功/ 失败
	 */
	private boolean query(int begin, int end, Object... obj) {
		// begin 属于第 (begin-1)/FAV_PMAX + 1 页
		// end 属于第(end-1)/FAV_PMAX + 1 页
		int pageBegin = (begin - 1) / API_PMAX + 1;
		int minPointerinBegin = (begin - 1) % API_PMAX + 1;
		int pageEnd = (end - 1) / API_PMAX + 1;
		int maxPointerinEnd = (end - 1) % API_PMAX + 1;
		// 如果一次请求可以搞定
		if (pageBegin == pageEnd) {
			return query(pageBegin, minPointerinBegin, maxPointerinEnd, obj);
		}
		// 如果需要两次以上， 先请求一次，
		boolean listNotEmpty = query(pageBegin, minPointerinBegin, API_PMAX, obj);
		if (!listNotEmpty) {
			return false;
		}
		for (int i = pageBegin + 1; i <= pageEnd; i++) {
			if (i < pageEnd) {
				listNotEmpty = query(i, 1, API_PMAX, obj);
			} else {
				listNotEmpty = query(i, 1, maxPointerinEnd, obj);
			}
			if (!listNotEmpty) {
				break;
			}
		}
		return true;
	}

}
