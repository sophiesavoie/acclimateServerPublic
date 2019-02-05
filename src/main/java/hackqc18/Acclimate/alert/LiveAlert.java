package hackqc18.Acclimate.alert;

import javax.persistence.Entity;

/**
 * Live alert entity class
 * 
 * @author Sophie Savoie
 *
 */

@Entity
public class LiveAlert extends Alert{
	
	
	public LiveAlert() {
		
	}
	
	/**
     * Constructor used by live alert from the live RSS stream.
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
    public LiveAlert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCat,
            String dateDeMiseAJour, String urgence, String description,
            Geometry geometry) {
        super(id, nom, source, territoire, certitude, severite, type,
                sousCat, dateDeMiseAJour, urgence, description, geometry);
    }
    
    /**
     * Creates a clone of a live alert
     */
    @Override
    public LiveAlert clone() {
    	try {
    		return (LiveAlert) super.clone();
    	} catch (CloneNotSupportedException e) {
    		LiveAlert liveAlert = new LiveAlert(this.id, this.nom, this.source, this.territoire,
    	            this.certitude, this.severite, this.typeAlert, this.sousCategorie,
    	            this.dateDeMiseAJour, this.urgence, this.description,
    	            this.getGeometry());
    		return liveAlert;
    	}
    }
}
