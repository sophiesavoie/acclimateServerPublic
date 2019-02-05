package hackqc18.Acclimate.user;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import hackqc18.Acclimate.authentication.VerifyToken;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.UserNotAuthorizedException;
import hackqc18.Acclimate.exception.UserNotFoundException;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;


/**
 * Controller class for users
 * 
 * @author Sophie Savoie
 *
 */
@RestController
@RequestMapping("api/users")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	
	/**
     * The GET method associated with the URL
     * "api/users/{uId}", where uId is variable. It returns
     * the associated user if it exists or an empty body otherwise.
     *
     * @param uId the id of the user of interest
     * @return the user or empty if not found
     * @throws OperationNotSupportedException
	 * @throws BadRequestException 
     * @throws UserNotFoundException
     */
    @GetMapping
    public ArrayList<User> getAllUsers(
    		/**@RequestHeader("Authorization") String token**/) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	//String userId = VerifyToken.verifyIdToken(token);
    	//if (!userService.existsByUId(userId))
    		//throw new UserNotAuthorizedException(userId);
    	
        return userService.findAllUsers();
    }	
	
	/**
     * The GET method associated with the URL
     * "api/users/{uId}", where uId is variable. It returns
     * the associated user if it exists or an empty body otherwise.
     *
     * @param uId the id of the user of interest
     * @return the user or empty if not found
     * @throws OperationNotSupportedException
     * @throws UserNotFoundException
     */
    @GetMapping("/{uId}")
    public User getUser(
            @PathVariable String uId,
    		@RequestHeader("Authorization") String token) 
            throws UserNotFoundException, OperationNotSupportedException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!uId.equals(userId))
    		throw new UserNotAuthorizedException(userId);
    	if (!userService.existsByUId(userId))
    		throw new UserNotFoundException(userId);
    	
        return userService.findUserByUId(userId);
    }
    
    /**
     * The GET method associated with the URL
     * "api/users/{uId}", where uId is variable. It returns
     * the associated user if it exists or an empty body otherwise.
     *
     * @param uId the id of the user of interest
     * @return the user or empty if not found
     * @throws OperationNotSupportedException
     * @throws UserNotFoundException
     */
    @GetMapping("/mz/{uId}")
    public HashMap<User, List<MonitoredZone>> getUserAndMonitoredZones(
            @PathVariable String uId,
            @RequestHeader("Authorization") String token) 
            throws UserNotFoundException, OperationNotSupportedException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !(uId.equals(userId)))
    		throw new UserNotAuthorizedException(userId);
    	
        return userService.findUserAndMzByUId(uId);
    }
    
    /**
     * The GET method associated with the URL "api/users/uids". 
     * It returns the associated users if they exist or an empty body otherwise.
     * 
     * @param uId
     * @return
     */
    @PostMapping("/uids")
    public @ResponseBody List<User> getAllUsersByUid(
    		@RequestBody List<String> uId) {
    			
    	return userService.findAllByUid(uId);
    }
    
    /**
     * The POST method associated with the URL "api/users". It creates a new user.
     *
     * @param User the object of the user
     * @return the user created and saved in the repository
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    @PostMapping
    public ResponseEntity<User> addUser(
    		@RequestBody User user)
            throws OperationNotSupportedException, BadRequestException {
    	
    	User u = userService.createUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{uId}").buildAndExpand(u.getuId()).toUri();
        
        return ResponseEntity.created(location).body(u);
    }
    
    /**
     * The PATCH method associated with the URL
     * "api/users/{uId}", where uId is variable. If the
     * user with the corresponding uId exists, it modifies the values of
     * the given fields. It returns the modified user of an empty body if
     * it doesn't exist.
     *
     * @param uId the id of the user of interest
     * @param fields the fields of the user that need to be modified
     * @return the modified user or an empty body if the user was not found
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     * @throws UserNotFoundException 
     */
    @PatchMapping("/{uId}")
    public User updateUser(
    		@PathVariable String uId, 
    		@RequestBody Map<String, Object> fields,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, UserNotFoundException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !(uId.equals(userId)))
    		throw new UserNotAuthorizedException(userId);
        
    	return userService.updateUser(uId, fields);
    }
    
    
    /**
     * The PATCH method associated with the URL
     * "api/users/{uId}", where uId is variable. If the
     * user with the corresponding uId exists, it modifies the values of
     * the given fields. It returns the modified user of an empty body if
     * it doesn't exist.
     *
     * @param uId the id of the user of interest
     * @param fields the fields of the user that need to be modified
     * @return the modified user or an empty body if the user was not found
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     * @throws UserNotFoundException 
     */
    @PatchMapping("/karma/{uId}")
    public User updateUserKarma(
    		@PathVariable String uId, 
    		@RequestBody Map<String, Object> fields,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, UserNotFoundException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !(uId.equals(userId)))
    		throw new UserNotAuthorizedException(userId);
        
    	return userService.updateUserKarma(uId, fields);
    }
    
    
    /**
     * The DELETE method associated with the URL
     * "api/users/{uId}", where uId is variable. It deletes
     * the corresponding user from the database if it is found.
     *
     * @param alertId the id of the alert of interest
     * @return the deleted alert or an empty body if it wasn't found
     * @throws OperationNotSupportedException
     */
    @DeleteMapping("/{uId}")
    public ResponseEntity<?> removeUser(
    		@PathVariable String uId,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !(uId.equals(userId)))
    		throw new UserNotAuthorizedException(userId);
    	
    	return userService.deleteUser(uId);
    }
}
