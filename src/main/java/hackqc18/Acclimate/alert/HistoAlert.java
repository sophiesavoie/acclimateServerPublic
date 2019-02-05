package hackqc18.Acclimate.alert;

import javax.persistence.Entity;

/**
 * Historical alert entity class
 * 
 * @author Sophie Savoie
 *
 */
@Entity
public class HistoAlert extends Alert{
	
	public HistoAlert() {
		
	}
	
	/**
	 * Constructor used by historical alerts
	 * 
	 * @param id
	 * @param nom
	 * @param source
	 * @param territoire
	 * @param certitude
	 * @param severite
	 * @param type
	 * @param dateDeMiseAJour
	 * @param urgence
	 * @param description
	 */
	public HistoAlert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCat,
            String dateDeMiseAJour, String urgence, String description,
            Geometry geometry) {
        super(id, nom, source, territoire, certitude, severite, type, sousCat,
                dateDeMiseAJour, urgence, description, geometry);
    }

}
