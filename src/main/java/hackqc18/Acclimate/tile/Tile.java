package hackqc18.Acclimate.tile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import hackqc18.Acclimate.monitoredZone.*;
import hackqc18.Acclimate.user.User;


/**
 * Entity class for tiles
 * 
 * @author Sophie Savoie and Olivier Lepage-Applin
 *
 */
@Entity
public class Tile {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Integer tileId;
	
	/**
	 * The linear number representing the index of the matrix
	 */
	int tileIndex;
	
	@ManyToOne(optional = false, cascade=CascadeType.REMOVE)
	@JoinColumn(name = "zoneId", nullable = false)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="zoneId")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("uId")
	private MonitoredZone monitoredZone;
	
	
	public Tile() {
		
	}
	
	
	/**
	 * Tile constructor
	 * 
	 * @param tileIndex
	 * @param monitoredZone
	 */
	public Tile(int tileIndex, MonitoredZone monitoredZone) {
		this.tileIndex = tileIndex;
		this.monitoredZone = monitoredZone;
	}
	
	/**
	 * Tile constructor used by the tile matrix
	 * 
	 * @param tileIndex
	 * @param monitoredZone
	 */
	public Tile(int tileIndex) {
		this.tileIndex = tileIndex;
	}

	
	/**
	 * Getters et setters
	 */
	public Integer getTileId() {
		return tileId;
	}


	public void setTileId(Integer tileId) {
		this.tileId = tileId;
	}


	public int getTileIndex() {
		return tileIndex;
	}


	public void setTileIndex(int tileIndex) {
		this.tileIndex = tileIndex;
	}


	public MonitoredZone getMonitoredZone() {
		return monitoredZone;
	}


	public void setMonitoredZone(MonitoredZone monitoredZone) {
		this.monitoredZone = monitoredZone;
	}
	
	
	
}
