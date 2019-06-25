package parser;

public class ScannerException extends RuntimeException { // 예외처리
	private static final long serialVersionUID = -5564986423129197718L;

	public ScannerException() {
		super();
	}

	public ScannerException(String details) {
		super(details);
	}
}
