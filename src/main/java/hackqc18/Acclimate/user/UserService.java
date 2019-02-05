package hackqc18.Acclimate.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.alert.AlertService;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.UserEmailAlreadyExistsException;
import hackqc18.Acclimate.exception.UserNameAlreadyExistsException;
import hackqc18.Acclimate.exception.UserNotFoundException;
import hackqc18.Acclimate.monitoredZone.MoniZoneService;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;


/**
 * Service class for the monitored zones
 * 
 * @author Sophie Savoie
 *
 */
@Service
public class UserService {
	
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private MoniZoneService moniZoneService;
	@Autowired
	private AlertService alertService;
	
	
	/**
	 * Returns all the users found in the user repository
	 * 
	 * @return list of users
	 */
	public ArrayList<User> findAllUsers() {
		ArrayList<User> users = new ArrayList<>();
		userRepository.findAll().forEach(user -> {
			users.add(user);
		});
		return users;
	}
	
	/**
	 * Returns the user related to the given uId
	 * 
	 * @param uId
	 * @return the user
	 * @throws UserNotFoundException
	 */
	public User findUserByUId(String uId){
		if (!userRepository.existsByUId(uId))
			throw new UserNotFoundException(uId);
		
		return userRepository.findByUId(uId);
	}
	
	/**
	 * Checks if the user exists in the database 
	 * 
	 * @param uId the uId of the user
	 * @return true of false
	 */
	public boolean existsByUId(String uId) {
		
		return userRepository.existsByUId(uId);
	}
	
	/**
	 *Creates a new user in the database 
	 * 
	 * @param user sent in the request
	 * @return the user saved in the database
	 * @throws BadRequestException 
	 */
	public User createUser(User user) throws BadRequestException {

		if (userRepository.existsByUserName(user.getUserName()))
			throw new UserNameAlreadyExistsException(user.getUserName());
		
		try {
			return userRepository.save(user);
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la création du user");
		}
		
	}
	
	
	/**
	 * Updates the given fields of the user corresponding to the uId
	 * 
	 * @param uId
	 * @param fields
	 * @return the updated user
	 * @throws OperationNotSupportedException
	 * @throws UserNotFoundException
	 * @throws BadRequestException 
	 */
	public User updateUser(String uId, Map<String, Object> fields)  
			throws OperationNotSupportedException, UserNotFoundException, BadRequestException{
		
		if (!userRepository.existsByUId(uId))
			throw new UserNotFoundException(uId);
		
		User user = userRepository.findByUId(uId);
		
        fields.forEach((k, v) -> {
    		Field uField = ReflectionUtils.findRequiredField(User.class, k);
        	ReflectionUtils.setField(uField, user, v);       		
        });

        try {
        	userRepository.save(user);
        	return user;
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la création du user");
		}	
	}
	
	/**
	 * Updates the karma of the of the user corresponding to the uId
	 * 
	 * @param uId
	 * @param fields
	 * @return the updated user
	 * @throws OperationNotSupportedException
	 * @throws BadRequestException 
	 */
	public User updateUserKarma(String uId, Map<String, Object> fields) 
			throws OperationNotSupportedException, UserNotFoundException, BadRequestException{
		
		if (!userRepository.existsByUId(uId))
			throw new UserNotFoundException(uId);
		
		User user = userRepository.findByUId(uId);
		Karma karma = user.getKarma();
		
        fields.forEach((k, v) -> {
        	Field kField = ReflectionUtils.findRequiredField(Karma.class, k);
    		ReflectionUtils.setField(kField, karma, v);
        });
        
        try {
        	userRepository.save(user);
        	return user;
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la mise à jour du user");
		}
	}

	
	/**
	 * Deletes the user corresponding to the given uId
	 * 
	 * @param uId the user uId
	 * @return 
	 * @throws OperationNotSupportedException
	 * @throws UserNotFoundException
	 */
	public ResponseEntity<?> deleteUser(String uId) 
			throws OperationNotSupportedException {
		
		moniZoneService.deleteAllMzOfUser(uId);
		alertService.deleteAllUserAlertOfUid(uId);
		userRepository.delete(userRepository.findByUId(uId));
		
        return ResponseEntity.ok().build();
    }
	
	
	/**
	 * Returns the user and the monitored zones corresponding to the given uId
	 * 
	 * @param uId
	 * @return the user and the monitored zones
	 */
	public HashMap<User, List<MonitoredZone>> findUserAndMzByUId(String uId) {
		HashMap<User, List<MonitoredZone>> map = new HashMap<User, List<MonitoredZone>>();
		
		User user = userRepository.findById(uId).orElseThrow(() -> 
				new UserNotFoundException(uId));
		
		List<MonitoredZone> mz = moniZoneService.findMoniZonesByUId(uId);
		
		map.put(user, mz);
		
		return map;
	}
	
	/**
	 * Creates a new user in the database
	 * 
	 * @param user
	 */
	public void saveUser(User user) {
		
		userRepository.save(user);
	}

	/**
	 * Returns the list of users corresponding to the given list of uId
	 * 
	 * @param uId
	 * @return the list of users
	 */
	public List<User> findAllByUid(List<String> uId) {
    	ArrayList<User> users = new ArrayList<>();
    	
    	uId.forEach(userId ->{
    		users.add(userRepository.findByUId(userId));
    	});
		
    	return users;
	}

}
