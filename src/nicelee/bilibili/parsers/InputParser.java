package nicelee.bilibili.parsers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

// 糟糕的设计，改都不好改
public class InputParser implements IInputParser, IParamSetter {

	protected final static Pattern paramPattern = Pattern.compile("^(.*)p=([0-9]+)$");// 自定义参数, 目前只匹配个人主页视频的页码
	private static List<IInputParser> parsers = null;
	private IInputParser parser = null;
	private int page = 1;
	private int realQN = 1;
	private HttpRequestUtil util;
	private int pageSize;

	@SuppressWarnings("unchecked")
	public InputParser(HttpRequestUtil util, int pageSize, String loadContition) {
		this.util = util;
		this.pageSize = pageSize;
		if(parsers == null) {
			synchronized (InputParser.class) {
				if(parsers == null) {
					parsers = new ArrayList<>();
					try {
						for (Class<?> clazz : PackageScanLoader.validParserClasses) {
							// 判断是否需要载入
							Bilibili bili = clazz.getAnnotation(Bilibili.class);
							if (bili.ifLoad().isEmpty() || bili.ifLoad().equals("listAll")) {
								// 实例化并加入parser列表
								// IInputParser inputParser = (IInputParser) clazz.newInstance();
								// 获取构造函数
								// Constructor<IInputParser> con = (Constructor<IInputParser>)
								// clazz.getConstructor(Object[].class);
								Constructor<IInputParser> con = (Constructor<IInputParser>) clazz.getConstructors()[0];
								IInputParser inputParser = con.newInstance(new Object[] { new Object[] { null, null, 0 } });
								parsers.add(inputParser);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public IInputParser selectParser(String input) {
		if(parser != null && parser.matches(input)) {
			return parser;
		}
		parser = null;
		for (IInputParser parser : parsers) {
			if (parser.matches(input)) {
				try {
					Logger.println(input);
					Object[] param = new Object[] { new Object[] { util, this, pageSize } };
					this.parser = (IInputParser) parser.getClass().getConstructors()[0].newInstance(param);
					this.parser.matches(input);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | SecurityException e) {
				}
				break;
			}
		}
		return this.parser;
	}

	@Override
	public boolean matches(String input) {
		return true;
	}

	@Override
	public String validStr(String input) {
		// 获取参数, 并去掉参数字符串
		Matcher paramMatcher = paramPattern.matcher(input);
		if (paramMatcher.find()) {
			int page = Integer.parseInt(paramMatcher.group(2));
			this.page = page;
			input = paramMatcher.group(1);
		}
		selectParser(input);
		if (parser != null) {
			return parser.validStr(input);
		}
		Logger.println("当前没有parser匹配");
		return "";
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		// 获取参数, 并去掉参数字符串
		Matcher paramMatcher = paramPattern.matcher(input);
		if (paramMatcher.find()) {
			int page = Integer.parseInt(paramMatcher.group(2));
			this.page = page;
			input = paramMatcher.group(1);
		}
		selectParser(input);
		if (parser != null) {
			return parser.result(input, videoFormat, getVideoLink);
		}
		return null;
	}

	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		selectParser(avId);
		if (parser != null) {
			return parser.getVideoLink(avId, cid, qn, downFormat);
		}
		return null;
	}

	@Override
	public int getVideoLinkQN() {
		if (parser != null) {
			return parser.getVideoLinkQN();
		}
		return 0;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setRealQN(int qn) {
		realQN = qn;

	}

	@Override
	public int getRealQN() {
		return realQN;
	}

	public IInputParser getParser() {
		return parser;
	}
	
}
