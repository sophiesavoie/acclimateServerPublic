package hackqc18.Acclimate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNameAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 5737634652537057962L;

	public UserNameAlreadyExistsException(String userName) {
		super("Le nom d'utilisateur '" + userName + "' existe déjà.");
	}

}