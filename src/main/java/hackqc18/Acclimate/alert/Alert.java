package hackqc18.Acclimate.alert;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.validation.constraints.Size;

import hackqc18.Acclimate.user.User;

/**
 * Abstract class for alerts
 * 
 * @author Normand Desmarais and Sophie Savoie
 */

@Entity
@Inheritance
public abstract class Alert implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer autoId;
    
    @Column(unique = true, nullable = false)
    protected String id;
    protected String nom;
    protected String source;
    @Column(length = 4000)
    protected String territoire;
    protected String certitude;
    protected String severite;
    // we have to use typeAlert instead of type only, because the database
    // flattens geometry properties which already has a property named type
    protected String typeAlert;
    protected String sousCategorie;
    protected String dateDeMiseAJour;
    protected String urgence;
    @Column(length = 4000)
    protected String description;
    protected Geometry geometry;


    public Alert() {

    }

    
    /**
     * Constructor used by a LiveAlert and a HistoricalAlert
     * 
     * @param id
     * @param nom
     * @param source
     * @param territoire
     * @param certitude
     * @param severite
     * @param type
     * @param sousCategorie
     * @param dateDeMiseAJour
     * @param urgence
     * @param description
     * @param geometry
     */
    public Alert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCategorie,
            String dateDeMiseAJour, String urgence, String description, Geometry geometry) {
        super();
        this.id = id;
        this.nom = nom;
        this.source = source;
        this.territoire = territoire;
        this.certitude = certitude;
        this.severite = severite;
        this.typeAlert = type;
        this.sousCategorie = sousCategorie;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.urgence = urgence;
        this.description = description;
        this.geometry = geometry;
    }
    
    /**
     * Constructor used by a userAlert
     * 
     * @param id
     * @param nom
     * @param source
     * @param territoire
     * @param certitude
     * @param severite
     * @param type
     * @param sousCategorie
     * @param dateDeMiseAJour
     * @param urgence
     * @param description
     * @param count
     * @param geometry
     */
    public Alert(String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCategorie,
            String dateDeMiseAJour, String urgence, String description, Geometry geometry) {
        super();
        this.nom = nom;
        this.source = source;
        this.territoire = territoire;
        this.certitude = certitude;
        this.severite = severite;
        this.typeAlert = type;
        this.sousCategorie = sousCategorie;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.urgence = urgence;
        this.description = description;
        this.geometry = geometry;
    }

	/**
     * This method only supports Point type Alert for now. Returns true if the alert is within the box 
     * defined by the north south, east and west parameters
     *
     * @param nord northern most latitude
     * @param sud southern most latitude
     * @param est eastern most longitude
     * @param ouest western most longitude
     * @return true if the point is within the box defined by the north south,
     *         east and west parameters
     */
    public boolean overlapWithBox(double north, double south, double east,
            double west) {
        
        double coordLat = this.getLat();
        double coordLng = this.getLng();

        return (coordLat <= north && coordLat >= south && 
        		coordLng >= west && coordLng <= east);

    }
    
    
    /**
     * Getters and setters
     */
    public String getSousCategorie() {
		return sousCategorie;
	}


	public void setSousCategorie(String sousCategorie) {
		this.sousCategorie = sousCategorie;
	}


	public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getNom() {
        return nom;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getSource() {
        return source;
    }


    public void setSource(String source) {
        this.source = source;
    }


    public String getTerritoire() {
        return territoire;
    }


    public void setTerritoire(String territoire) {
        this.territoire = territoire;
    }


    public String getCertitude() {
        return certitude;
    }


    public void setCertitude(String certitude) {
        this.certitude = certitude;
    }


    public String getSeverite() {
        return severite;
    }


    public void setSeverite(String severite) {
        this.severite = severite;
    }


    public String getType() {
        return typeAlert;
    }


    public void setType(String type) {
        this.typeAlert = type;
    }


    public String getDateDeMiseAJour() {
        return dateDeMiseAJour;
    }


    public void setDateDeMiseAJour(String dateDeMiseAJour) {
        this.dateDeMiseAJour = dateDeMiseAJour;
    }


    public String getUrgence() {
        return urgence;
    }


    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Geometry getGeometry() {
        return geometry;
    }


    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    
    public double getLng() {
    	return geometry.getCoordinates()[1];
    }
    
    public double getLat() {
    	return geometry.getCoordinates()[0];
    }
}
