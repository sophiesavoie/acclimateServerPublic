package hackqc18.Acclimate.user;
import javax.persistence.Embeddable;


/**
 * Karma class 
 * 
 * @author Sophie Savoie
 *
 */
@Embeddable
public class Karma {
	int points;
	
	public Karma() {
		
	}
	
	
	/**
	 * Constructor of the Karma class
	 * 
	 * @param points the karma points of the user
	 */
	public Karma(int points) {
		this.points = points;
	}

	/**
	 * Increase the number of karma points
	 */
	public void increasePoints() {
		this.points++;
		
	}
	
	/**
	 * Setters and getters
	 */
	public int getPoints() {
		return points;
	}


	public void setPoints(int points) {
		this.points = points;
	}


}
