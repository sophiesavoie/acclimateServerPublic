package hackqc18.Acclimate.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hackqc18.Acclimate.alert.Alert;


/**
 * Repository interface for the users
 * 
 * @author Sophie Savoie
 *
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {

	User findByUId(String uId);
	
	boolean existsByUserName(String userName);

	boolean existsByUId(String uId);
	
}
