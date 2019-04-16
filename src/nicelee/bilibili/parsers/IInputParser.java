package nicelee.bilibili.parsers;

import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpRequestUtil;

public interface IInputParser {
	
	/**
	 * 初始化
	 * @param util
	 */
	public void init(HttpRequestUtil util);
	/**
	 * 设置分页参数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize);
	/**
	 * 该Parser类型是否可以解析
	 * @return
	 */
	public boolean matches(String input);
	
	/**
	 * 获取处理过后的字符串
	 */
	public String validStr(String input);
	
	/**
	 * 获取视频信息
	 */
	public VideoInfo result(String avId, int videoFormat, boolean getVideoLink);
	
	/**
	 * 获取视频链接
	 */
	public String getVideoLink(String avId, String cid, int qn, int downFormat);
	
	/**
	 * 获取上一次查询的视频链接的实际清晰度
	 */
	public int getVideoLinkQN();
	
}
