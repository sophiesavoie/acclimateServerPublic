package hackqc18.Acclimate.alert.repository;

import java.util.Optional;

import javax.transaction.Transactional;
import hackqc18.Acclimate.alert.Alert;

@Transactional
public interface AlertRepository extends AlertBaseRepository<Alert>{

	void deleteById(String id);

	Optional<Alert> findById(String id);

}
