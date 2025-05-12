package ma.formation.exceptions;

// Unauthorized Exception
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
