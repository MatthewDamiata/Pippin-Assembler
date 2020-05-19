package project;

public class DivideByZeroException extends RuntimeException{

	public DivideByZeroException() {
		
	}
	public DivideByZeroException(String str) {
		super(str);
	}
}
