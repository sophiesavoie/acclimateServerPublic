package hackqc18.Acclimate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedAlertTypeException extends RuntimeException {

    private static final long serialVersionUID = -8389998719414039633L;

    public UnsupportedAlertTypeException(String alertType) {
        super("The alert type '" + alertType + "' is not supported.");
    }
}
