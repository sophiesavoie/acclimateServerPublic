package hackqc18.Acclimate.monitoredZone;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.annotation.Nullable;
import javax.persistence.*;

import org.apache.commons.net.ntp.TimeStamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.firebase.database.annotations.NotNull;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.alert.LiveAlert;
import hackqc18.Acclimate.tile.TileCalculator;
import hackqc18.Acclimate.user.User;


/**
 * Monitored zone entity class
 * 
 * @author Sophie Savoie
 *
 */
@Entity
public class MonitoredZone implements Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer zoneId;
	
	private String name;
	private Geometry geometry;
	private int radius; //en mètres
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "uId", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("uId")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JsonIgnore
	private User user;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Timestamp dateCreation;
	

	public MonitoredZone() {
		
	}
	
	/**
	 * MonitoredZones constructor
	 * 
	 * @param lng
	 * @param lat
	 * @param radius
	 */
	public MonitoredZone(String name, Geometry geometry, int radius, User user, Timestamp date) {
		this.name = name;
		this.geometry = geometry;
		this.radius = radius;
		this.user = user;
		this.dateCreation = date;
	}
	
	
	/**
	 * Tells if the monitoredZone contains the given alert
	 * 
	 * @param alert
	 * @return
	 */
	public boolean containsAlert(Alert alert) {
		
		double alertLat = alert.getLat();
		double alertLng = alert.getLng();
		
		if (this.getGeometry().getType().equals("Polygon")) {
			
			// Rectangle boundingBox
			double[][] box = this.geometry.getPolyCoordinates();
			double north, south, east, west;
			north = box[0][0];
			south = box[1][0];
			west = box[0][1];
			east = box[1][1];
			
			return alert.overlapWithBox(north, south, east, west);
			
		} else {
			
			double[] center = this.geometry.getCoordinates();
			double tolerance = 0.000001d;
			// Circle geometry
			
			// 1. calculate distance between alert and zone center
			double deltaX, deltaY;
			deltaY = alertLat - center[0];
			deltaX = alertLng - center[1];
			double distance = Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
			
			// 2. return true if distance smaller than radius converted in radius
			double degRad = (double)radius / 111139;	// un degré est équivalent à 111139m
			return distance <= Math.pow(degRad, 2);
		}
	}
	
	/**
	 * Creates a clone of the monitored zone
	 */
	@Override
    public MonitoredZone clone() {
    	try {
    		return (MonitoredZone)super.clone();
    	} catch (CloneNotSupportedException e) {
    		MonitoredZone mz = new MonitoredZone(this.name, this.geometry, this.radius, this.user, this.dateCreation);
    		return mz;
    	}
    }

	
	/**
	 * Getters et setters
	 */
	public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserId() {
		return this.user.getuId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Timestamp dateCreation) {
		this.dateCreation = dateCreation;
	}
	
}
