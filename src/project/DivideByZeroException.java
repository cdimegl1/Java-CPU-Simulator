package project;

public class DivideByZeroException extends RuntimeException {
    private static final long serialVersionUID = -8273461871444428769L;

    public DivideByZeroException(String arg0) {
        super(arg0);
    }

    public DivideByZeroException() {
        super();
    }
}
