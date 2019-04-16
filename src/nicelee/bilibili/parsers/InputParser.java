package nicelee.bilibili.parsers;

import java.util.ArrayList;
import java.util.List;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpRequestUtil;

public class InputParser implements IInputParser {

	// private HttpRequestUtil util;
	private List<IInputParser> parsers = null;
	private IInputParser parser = null;

	@Override
	public void init(HttpRequestUtil util) {
		parsers = new ArrayList<>();
		// this.util = util;
		try {
			for (Class<?> clazz : PackageScanLoader.validParserClasses) {
				IInputParser inputParser = (IInputParser) clazz.newInstance();
				inputParser.init(util);
				parsers.add(inputParser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectParser(String input) {
		for (IInputParser parser : parsers) {
			if (parser.matches(input)) {
				this.parser = parser;
			}
		}
	}

	@Override
	public boolean matches(String input) {
		return true;
	}

	@Override
	public String validStr(String input) {
		selectParser(input);
		if (parser != null) {
			return parser.validStr(input);
		}
		return "";
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
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
	public void setPageSize(int pageSize) {
		for (IInputParser parser : parsers) {
			parser.setPageSize(pageSize);
		}
		
	}

}
