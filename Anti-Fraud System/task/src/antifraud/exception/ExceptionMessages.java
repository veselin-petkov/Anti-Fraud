package antifraud.exception;

public class ExceptionMessages {
    public static final String PRESENT_ROLE = "This user role is already present!";
    public static final String FORBIDDEN_ROLE = "You can't set ADMINISTRATOR or ANONYMOUS role";
    public static final String INVALID_REQUEST ="Invalid request!";
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String UNIQUE_USERNAME ="User with that username already exist!";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found!";
    private ExceptionMessages() {
    }
}
