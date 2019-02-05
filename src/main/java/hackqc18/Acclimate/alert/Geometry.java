package hackqc18.Acclimate.alert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Embeddable;

/**
 * Geometry class
 * 
 * @author Normand Desmarais and Sophie Savoie
 */
@Embeddable
public class Geometry {
    public static final String TYPE_POINT = "Point";
    public static final String TYPE_POLYGON = "Polygon";
    
    private String type;
    private double[] coordinates;
    private double[][] polyCoordinates;

    public Geometry() {
        
    }
    
    /**
     * Constructor for Point type Geometry
     * 
     * @param lat latitudinal coordinate
     * @param lng longitudinal coordinate
     */
    public Geometry(double lat, double lng) {
        type = TYPE_POINT;
        coordinates = new double[]{lat, lng};
    }
    
    /**
     * Constructor for Polygon type Geometry
     * Prend en argument un ensemble de deux points [[nord, ouest],[sud, est]]
     * 
     * @param polygonCoordinates
     */
    public Geometry(double[][] polygonCoordinates) {
        type = TYPE_POLYGON;
        polyCoordinates = polygonCoordinates;
    }
    
    
    /**
     * Getters et setters
     */

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public double[][] getPolyCoordinates() {
		return polyCoordinates;
	}

	public void setPolyCoordinates(double[][] polyCoordinates) {
		this.polyCoordinates = polyCoordinates;
	}

	@Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "{}";
    }
}
