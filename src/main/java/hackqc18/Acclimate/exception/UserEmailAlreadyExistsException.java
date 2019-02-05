package hackqc18.Acclimate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserEmailAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = -262338900979667773L;

	public UserEmailAlreadyExistsException(String email) {
		super("Le courriel '" + email + "' existe déjà.");
	}

}
