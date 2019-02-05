package hackqc18.Acclimate.monitoredZone;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hackqc18.Acclimate.user.User;


/**
 * Repository interface for the monitored zones
 * 
 * @author Sophie Savoie
 *
 */
@Repository
public interface MoniZoneRepository extends CrudRepository<MonitoredZone, Integer> {
	
	List<MonitoredZone> findByUser(User user);

	boolean existsByUser(User user);
	
}
