package nicelee.bilibili.exceptions;

public class Status412Exception extends BilibiliError{
	private static final long serialVersionUID = -2163955775382719286L;

	public Status412Exception(String message) {
        super(message);
    }
	
	public Status412Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
