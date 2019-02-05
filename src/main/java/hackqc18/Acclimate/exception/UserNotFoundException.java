package hackqc18.Acclimate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3532519130900684187L;

	public UserNotFoundException(String uId) {
        super("L'identifiant '" + uId + "' n'est pas valide.");
    }
}