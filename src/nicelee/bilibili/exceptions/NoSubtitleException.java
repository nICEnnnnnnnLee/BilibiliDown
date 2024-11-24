package nicelee.bilibili.exceptions;

public class NoSubtitleException extends BilibiliError{

	private static final long serialVersionUID = -154799775624722132L;

	public NoSubtitleException(String message) {
        super(message);
    }
	
	public NoSubtitleException(String message, Throwable cause) {
        super(message, cause);
    }
}
