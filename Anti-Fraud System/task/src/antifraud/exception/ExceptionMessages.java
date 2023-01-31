package antifraud.exception;

public class ExceptionMessages {
    public static final String presentRole = "This user role is already present!";
    public static final String forbiddenRoles = "You can't set ADMINISTRATOR or ANONYMOUS role";
    public static final String invalidRequest  ="Invalid request!";
    public static final String userNotFound  = "User not found!";
    public static final String uniqueUsername  ="User with that username already exist!";
    public static final String transactionNotFound  = "Transaction not found!";

    private ExceptionMessages() {
    }
}
