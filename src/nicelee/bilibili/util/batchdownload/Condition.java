package nicelee.bilibili.util.batchdownload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.regex.Pattern;

import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.model.ClipInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.RepoUtil;
import nicelee.ui.Global;

public class Condition {

	String left;
	String operator;
	String right;

	final static HashSet<String> validsOfLeft;
	final static HashSet<String> validsOfOperator;

	static {
		validsOfLeft = new HashSet<>();
		validsOfLeft.add("_");
		validsOfLeft.add("page");
		validsOfLeft.add("bv");
		validsOfLeft.add("favTime");
		validsOfLeft.add("cTime");
		validsOfLeft.add("avTitle");
		validsOfLeft.add("clipTitle");
		validsOfOperator = new HashSet<>();
		validsOfOperator.add(":");
		validsOfOperator.add("!");
		validsOfOperator.add("<");
		validsOfOperator.add(">");
	}

	public Condition(String left, String operator, String right) {
		if (!validsOfLeft.contains(left)) {
			throw new RuntimeException("批量下载配置不对, 判断表达式左值有误: " + left);
		}
		if (!validsOfOperator.contains(operator)) {
			throw new RuntimeException("批量下载配置不对, 判断表达式操作符有误: " + operator);
		}
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public String toString() {
		return left + operator + right;
	}

	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public boolean match(ClipInfo clip, int page) {
		switch (left) {
		case "_":
			if (operator.equals(":")) {
				return matchUnderlineColon(clip, page);
			} else if (operator.equals("!")) {
				return !matchUnderlineColon(clip, page);
			} else {
				throw new RuntimeException("不合法的表达式   " + left + operator + right);
			}

		case "page":
			int expectedPage = Integer.parseInt(right);
			if (operator.equals(":")) {
				return expectedPage == page;
			} else if (operator.equals(">")) {
				return expectedPage > page;
			} else if (operator.equals("<")) {
				return expectedPage < page;
			} else {
				throw new RuntimeException("不合法的表达式   " + left + operator + right);
			}
		case "bv":
			if (operator.equals(":")) {
				return clip.getAvId().equals(right);
			} else {
				throw new RuntimeException("不合法的表达式   " + left + operator + right);
			}
		case "favTime":
			if (clip.getFavTime() == 0)
				throw new RuntimeException("该url类型解析结果不存在favTime字段的信息 ");

			if (operator.equals(">")) {
				try {
					return clip.getFavTime() > sdf.parse(right).getTime();
				} catch (ParseException e) {
				}
			} else if (operator.equals("<")) {
				try {
					return clip.getFavTime() < sdf.parse(right).getTime();
				} catch (ParseException e) {
				}
			}
			throw new RuntimeException("不合法的表达式   " + left + operator + right);
		case "cTime":
			if (clip.getcTime() == 0)
				throw new RuntimeException("该url类型解析结果不存在cTime字段的信息 ");
			if (operator.equals(">")) {
				try {
					return clip.getcTime() > sdf.parse(right).getTime();
				} catch (ParseException e) {
				}
			} else if (operator.equals("<")) {
				try {
					return clip.getcTime() < sdf.parse(right).getTime();
				} catch (ParseException e) {
				}
			}
			throw new RuntimeException("不合法的表达式   " + left + operator + right);
		case "avTitle":
			boolean eq0 = operator.equals(":");
			if (!eq0 && !operator.equals("!")) {
				throw new RuntimeException("avTitle操作符只能为 : 或者 !, 不能是: " + operator);
			}
			boolean m0 = Pattern.matches(right, clip.getAvTitle());
			Logger.printf("标题正则匹配: %s\nPattern: %s\nContent: %s", m0, right, clip.getAvTitle());
			return eq0? m0 : !m0;
		case "clipTitle":
			boolean eq1 = operator.equals(":");
			if (!eq1 && !operator.equals("!")) {
				throw new RuntimeException("clipTitle操作符只能为 : 或者 !, 不能是: " + operator);
			}
			boolean m1 = Pattern.matches(right, clip.getTitle());
			Logger.printf("小标题正则匹配: %s\nPattern: %s\nContent: %s", m1, right, clip.getTitle());
			return eq1? m1 : !m1;
		}

		return true;
	}

	/**
	 * <p>表达式为 _:{xx} 的情况</p>
	 */
	public boolean matchUnderlineColon(ClipInfo clip, int page) {
		if (right.equals("_")) {
			return true;
		} else if (right.equals("downloaded")) {
			String avRecord = new StringBuilder(clip.getAvId()).append("-")
					.append(VideoQualityEnum.getQN(Global.menu_qn)).append("-p").append(clip.getPage()).toString();
			return RepoUtil.isInRepo(avRecord);
		} else {
			throw new RuntimeException("不合法的表达式   " + left + operator + right);
		}
	}

	public String getKey() {
		return left;
	}

	public void setKey(String key) {
		this.left = key;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return right;
	}

	public void setValue(String value) {
		this.right = value;
	}

}
