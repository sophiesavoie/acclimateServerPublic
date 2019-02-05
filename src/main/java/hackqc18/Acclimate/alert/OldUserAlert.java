package hackqc18.Acclimate.alert;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import hackqc18.Acclimate.user.User;

/**
 * OldUserAlert entity class
 * 
 * @author Sophie Savoie
 *
 */
@Entity
public class OldUserAlert extends Alert{
	
	private int score;
    @ManyToOne
	@JoinColumn(name = "uId")
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("uId")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JsonIgnore
	private User user;
    
    protected ArrayList <String> plusOneUsers = new ArrayList<>();
    private ArrayList <String> minusOneUsers = new ArrayList<>();
    private String  photoPath;
	
	public OldUserAlert() {
		
	}
	
	/**
     * Constructor used by old user Alerts
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
     * @param plusOneUsers
     * @param minusOneUsers
     * @param uId
     */
    public OldUserAlert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCategorie,
            String dateDeMiseAJour, String urgence, String description,
            Geometry geometry, User user, int score, ArrayList <String> plusOneUsers, 
    		ArrayList <String> minusOneUsers) {
    	super(id, nom, source, territoire, certitude, severite, type, sousCategorie,
                dateDeMiseAJour, urgence, description, geometry);
    	this.score = score;
        this.user = user;
        this.plusOneUsers = plusOneUsers;
        this.minusOneUsers = minusOneUsers;
    }
	
	/**
     * Constructor for old user alert with a new user alert
     * 
     * @param userAlert
     */
    public OldUserAlert(UserAlert userAlert) {
    	super(userAlert.getId(), userAlert.nom, userAlert.source, userAlert.territoire, userAlert.certitude, userAlert.severite, 
    			userAlert.getType(), userAlert.sousCategorie, userAlert.dateDeMiseAJour, userAlert.urgence, 
    			userAlert.description, userAlert.geometry);
    	this.score = userAlert.getScore();
        this.user = userAlert.getUser();
        this.plusOneUsers = userAlert.getPlusOneUsers();
        this.minusOneUsers = userAlert.getMinusOneUsers();
    }

}
