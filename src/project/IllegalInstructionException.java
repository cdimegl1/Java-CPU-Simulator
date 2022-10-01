package project;

public class IllegalInstructionException extends RuntimeException {
    private static final long serialVersionUID = 8897896486106949673L;

    public IllegalInstructionException(String arg0) {
        super(arg0);
    }

    public IllegalInstructionException() {
        super();
    }
}
