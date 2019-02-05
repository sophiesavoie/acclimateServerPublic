package hackqc18.Acclimate.alert.repository;

import java.util.List;

import javax.transaction.Transactional;

import hackqc18.Acclimate.alert.OldUserAlert;
import hackqc18.Acclimate.alert.UserAlert;
import hackqc18.Acclimate.user.User;


/**
 * Interface used to define the old user alert repository. 
 * 
 * @author Sophie Savoie
 *
 */
@Transactional
public interface OldUserAlertRepository extends AlertBaseRepository<OldUserAlert> {

	void deleteById(String id);

	UserAlert findById(String id);

	boolean existsById(String id);
	
	List<UserAlert> findByUser (User user);
	
}