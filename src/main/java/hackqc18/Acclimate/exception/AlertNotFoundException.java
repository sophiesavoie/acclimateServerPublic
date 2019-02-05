package hackqc18.Acclimate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlertNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4962644189434991162L;


    public AlertNotFoundException(String alertId) {
        super("L'identifiant '" + alertId + "' n'est pas valide.");
    }
}
