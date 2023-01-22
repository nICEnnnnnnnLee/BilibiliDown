package nicelee.bilibili.exceptions;

public class ApiLinkQueryParseError extends BilibiliError{
	private static final long serialVersionUID = 8445773757756877239L;

	public ApiLinkQueryParseError(String message) {
        super(message);
    }
	
	public ApiLinkQueryParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
