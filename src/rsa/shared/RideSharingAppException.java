package rsa.shared;

public class RideSharingAppException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5916264826815412970L;

	public RideSharingAppException() {
        super();
    }

    public RideSharingAppException(String message) {
        super(message);
    }

    public RideSharingAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public RideSharingAppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RideSharingAppException(Throwable cause) {
        super(cause);
    }

}
