package hackqc18.Acclimate.user;

import javax.persistence.*;

import org.apache.commons.net.ntp.TimeStamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * User entity class
 * 
 * @author Sophie Savoie
 *
 */
@Entity
public class User {
	
	@Id
	@Column(unique = true, nullable = false)
	private String uId;
	
	@Column(length = 8000)
	private ArrayList <String> registrationToken = new ArrayList<>();
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Timestamp dateCreation;
	private Karma karma;

	//Obligatoires
	@Column(unique = true, nullable = false)
	private String userName;

	
	public User() {
		
	}
	
	
	/**
	 * User class constructor
	 * 
	 * @param date
	 * @param uId
	 * @param userName
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param karma
	 * @param registrationToken
	 */
	public User(Timestamp date, String uId, String userName,
			Karma karma, ArrayList <String> registrationToken) {
		super();
		this.dateCreation = date;
		this.uId = uId;
		this.userName = userName;
		this.karma = karma;
		this.registrationToken = registrationToken;
	}

	
	/**
	 * Getters and setters
	 */
	public String getuId() {
		return uId;
	}


	public void setuId(String uId) {
		this.uId = uId;
	}


	public ArrayList<String> getRegistrationToken() {
		return registrationToken;
	}


	public void setRegistrationToken(ArrayList<String> registrationToken) {
		this.registrationToken = registrationToken;
	}
	
	public void addRegistrationToken(String registrationToken) {
		this.registrationToken.add(registrationToken);
	}
	

	public Timestamp getDateCreation() {
		return dateCreation;
	}


	public void setDateCreation(Timestamp dateCreation) {
		this.dateCreation = dateCreation;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Karma getKarma() {
		return karma;
	}


	public void setKarma(Karma karma) {
		this.karma = karma;
	}
}
