package ma.formation.exceptions;

// Business Logic Exception
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
