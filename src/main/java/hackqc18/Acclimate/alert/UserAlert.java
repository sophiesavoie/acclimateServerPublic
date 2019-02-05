package hackqc18.Acclimate.alert;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
 * UserAlert entity class
 * 
 * @author Sophie Savoie
 *
 */
@Entity
public class UserAlert extends Alert{
	
	// observed and probable "certitude" count are triggers used
    // to update the "certitude" status accordingly
    //private static final int OBSERVED_CERTITUDE_COUNT = 10;
    //private static final int PROBABLE_CERTITUDE_COUNT = 5;
	
	private static final int OBSERVED_CERTITUDE_COUNT = 2;

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
    
    
    public UserAlert() {

    }
    

    /**
     * Constructor used by user Alerts
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
    public UserAlert(String nom, String source, String territoire,
            String certitude, String severite, String type, String sousCategorie,
            String dateDeMiseAJour, String urgence, String description,
            Geometry geometry, User user, int score, ArrayList <String> plusOneUsers, 
    		ArrayList <String> minusOneUsers) {
    	super(nom, source, territoire, certitude, severite, type, sousCategorie,
                dateDeMiseAJour, urgence, description, geometry);
    	this.id = createId();
    	this.score = score;
        this.user = user;
        this.plusOneUsers = plusOneUsers;
        this.minusOneUsers = minusOneUsers;
    }
   
    
    /**
     * Increases the count of an alert and changes its "certitude" status
     * accordingly. It also updates the "dateDeMiseAJour" property to the
     * current date and time.
     *
     * @param alert the alert
     */
    public void increaseCount() {
        score++;
        this.setDateDeMiseAJour(LocalDateTime.now().toString());
        //if (score == PROBABLE_CERTITUDE_COUNT) {
            //this.setCertitude("Probable");
        if (score == OBSERVED_CERTITUDE_COUNT) {
        	this.setCertitude("Observé");
        }
    }
    
    /**
     * Decreases the count of an alert and changes its "certitude" status
     * accordingly. It also updates the "dateDeMiseAJour" property to the
     * current date and time.
     *
     * @param alert the alert
     */
    public void decreaseCount() {
    	score--;
        this.setDateDeMiseAJour(LocalDateTime.now().toString());
        //if (score == PROBABLE_CERTITUDE_COUNT - 1) {
            //this.setCertitude("Inconnu");
        //} else if (score == OBSERVED_CERTITUDE_COUNT - 1) {
        	//this.setCertitude("Probable");
        //}
        if (score == OBSERVED_CERTITUDE_COUNT - 1)
        	this.setCertitude("Inconnu");
        
	}
    
    /**
     * Utility method that creates a unique id from the alert type, longitude
     * and latitude. 
     *
     * @return the id
     */
    public String createId() {
        return this.getType().hashCode() + "-"
                + distanceFromZeroInKM(this.getLng()) + "-"
                + distanceFromZeroInKM(this.getLat());
    }


    /**
     * Utility method that computes the distance in KM of a longitude or
     * latitude coordinate from the zero value of this coordinate.
     *
     * @param coordinate the longitude or latitude coordinate
     * @return the distance in KM from the zero value of this coordinate
     */
    private int distanceFromZeroInKM(double coordinate) {
        double R = 6378.137; // Radius of earth in KM
        double a = Math.abs(Math.sin(coordinate * Math.PI / 180 / 2));
        double a2 = a * a;
        double d = 2 * R * Math.atan2(a, Math.sqrt(1 - a2));
        return (int) Math.round(d); // KM
    }
    
    
    /**
     * Getters et setters
     */

	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.setDateDeMiseAJour(LocalDateTime.now().toString());
        //if (score == PROBABLE_CERTITUDE_COUNT) {
            //this.setCertitude("Probable");
        if (score == OBSERVED_CERTITUDE_COUNT) {
        	this.setCertitude("Observé");
        }
		this.score = score;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public ArrayList<String> getPlusOneUsers() {
		return plusOneUsers;
	}


	public void setPlusOneUsers(ArrayList<String> plusOneUsers) {
		this.plusOneUsers = plusOneUsers;
	}


	public ArrayList<String> getMinusOneUsers() {
		return minusOneUsers;
	}


	public void setMinusOneUsers(ArrayList<String> minusOneUsers) {
		this.minusOneUsers = minusOneUsers;
	}


	public String getPhotoPath() {
		return photoPath;
	}


	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

}
