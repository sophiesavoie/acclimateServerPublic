package hackqc18.Acclimate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 7508641260991175614L;

	public UserNotAuthorizedException(String uId) {
		super("L'utilisateur " + uId + " n'est pas autorisé à effectuer cette action");
	}
	
}
