package hackqc18.Acclimate.alert.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.UserAlert;
import hackqc18.Acclimate.user.User;

/**
 * Interface used to define the user alert repository. This interface will
 * automatically be instantiated as a Singleton by Spring at compile time by
 * inserting the following declaration in classes that needs this repository:
 *
 * @Autowired private UserAlertRepository userAlertRepository;
 */
@Transactional
public interface UserAlertRepository extends AlertBaseRepository<UserAlert> {

	void deleteById(String id);

	UserAlert findById(String id);

	boolean existsById(String id);
	
	List<UserAlert> findByUser (User user);
	
}
