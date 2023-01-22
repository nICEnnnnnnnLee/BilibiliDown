package nicelee.bilibili.exceptions;

public class BilibiliError extends RuntimeException{
	private static final long serialVersionUID = 8445773757756877239L;

	public BilibiliError(String message) {
        super(message);
    }
	
	public BilibiliError(String message, Throwable cause) {
        super(message, cause);
    }
}
