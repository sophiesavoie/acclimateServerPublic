package hackqc18.Acclimate.alert.repository;

import javax.transaction.Transactional;

import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.alert.LiveAlert;


/**
 * Interface used to define the live alert repository. 
 * 
 * @author Sophie Savoie
 *
 */
@Transactional
public interface LiveAlertRepository extends AlertBaseRepository<LiveAlert> {
	
	LiveAlert findById(String id);

	boolean existsById(String alertId);

	boolean existsByGeometry(Geometry geometry);

	boolean findByGeometry(Geometry geometry);
	
}