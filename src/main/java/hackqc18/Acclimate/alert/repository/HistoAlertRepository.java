package hackqc18.Acclimate.alert.repository;
import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.HistoAlert;
import javax.transaction.Transactional;


/**
 * Interface used to define the historical alert repository. 
 * 
 * @author Sophie Savoie
 *
 */
@Transactional
public interface HistoAlertRepository extends AlertBaseRepository<HistoAlert>{

	Alert findById(String id);

	boolean existsById(String id);

}

