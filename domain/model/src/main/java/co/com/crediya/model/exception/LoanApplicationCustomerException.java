package co.com.crediya.model.exception;

public class LoanApplicationCustomerException extends Exception{

    private ErrorType type;

    public LoanApplicationCustomerException(String message, ErrorType type) {
        super(message);
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
