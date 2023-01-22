package nicelee.bilibili.exceptions;

public class QualityTooLowException extends BilibiliError{
	private static final long serialVersionUID = -6643066495256859753L;

	public QualityTooLowException(String message) {
        super(message);
    }
	
	public QualityTooLowException(String message, Throwable cause) {
        super(message, cause);
    }
}
