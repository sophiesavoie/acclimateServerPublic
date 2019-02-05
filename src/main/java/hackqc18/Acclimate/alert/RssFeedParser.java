package hackqc18.Acclimate.alert;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hackqc18.Acclimate.alert.rss.ItemRSS;
import hackqc18.Acclimate.alert.rss.Rss;


@Component
public class RssFeedParser {
	//Alertes présentement contenues dans le répertoire
	private static HashMap<String, LiveAlert> actualAlerts = new HashMap<>();
	
	//Alertes parsées dans le flux Rss périodiquement
	private static HashMap<String, LiveAlert> liveAlerts = new HashMap<>();
	private ArrayList <LiveAlert> newAlerts = new ArrayList<>();
	private ArrayList <LiveAlert> oldAlerts = new ArrayList<>();
	
    private final String rssURL = "https://geoegl.msp.gouv.qc.ca/avp/rss/";
    private final XmlMapper xmlMapper  = new XmlMapper();
    private String lastFeed = "";

    /**
     * Utility method used to fetch alerts from the live RSS stream at periodic
     * intervals.
     * @param actualAlerts2 
     */
    public Pair<ArrayList<LiveAlert>, ArrayList<LiveAlert>> updateAlertsFromRssFeedTask
    		(HashMap<String, LiveAlert> actuAlerts) {
    	
    	String feed = getRssFeed();
    	
        if (!lastFeed.equals(feed)) {
        	newAlerts.clear();
        	oldAlerts.clear();
        	liveAlerts.clear();
        	actualAlerts = actuAlerts;
        	
        	parseFeed(feed);
    		updateAlertLists();
			lastFeed = feed;
			return Pair.createPair(newAlerts, oldAlerts);
        }
		return null;
    }


    /**
     * Utility method that actually fetch the RSS feed.
     *
     * @return the feed in XML format
     */
    private String getRssFeed() {
        try {
            String feed = "";
            URL url = new URL(rssURL);
            URLConnection rssSrc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    rssSrc.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                feed += inputLine;
            }

            in.close();
            return feed;
            
        } catch (MalformedURLException ex) {
            // We don't want the application to stop if the feed is not
            // available, so just log a warning
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            // We don't want the application to stop if the feed is not
            // available, so just log a warning
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        }
        return "";
    }

    
    /**
     * Parse the content of the feed using Jackson XmlMapper to automatically
     * map the feed on the class Rss and adds the alerts to the newAlerts ArrayList. 
     *
     * @param feed the content of the RSS feed
     */
    private void parseFeed(String feed) {
        try {

            String tmpStrs[];
            String alertId, nom, source, territoire, certitude, severite, type,
                    dateDeMiseAJour, urgence, description, sousCat;
            double lat, lng;
            Rss rssObject = xmlMapper.readValue(feed, Rss.class);
            
            if (rssObject.getChannel().getItem() != null) {
            	for (ItemRSS item : rssObject.getChannel().getItem()) {

                    /**
                     * The name is stored in item.title
                     */
                    nom = item.getTitle();

                    /**
                     * item.guid contains coordinates and alertId in the form of :
                     * "{url}?...&center={lng},{lat}&...#{alertId}"
                     * ex:"{url}/?context=avp&center=-73.6387202781213,45.6928705203507&zoom=10#MSP.SS.043208"
                     */
                    tmpStrs = item.getGuid().split("#");
                    alertId = tmpStrs[1].replaceAll("\\.", "-");
                    tmpStrs = tmpStrs[0].split("center=")[1].split("&")[0]
                            .split(",");
                    lng = Double.parseDouble(tmpStrs[0]);
                    lat = Double.parseDouble(tmpStrs[1]);

                    /**
                     * descriptions contains all other parameters in the form of key
                     * value pairs: <b>{key}</b> : {value} separated by "<br/>
                     * ".
                     */
                    tmpStrs = item.getDescription().split("<br/>");
                    source = tmpStrs[0].split(":")[1].trim();
                    sousCat = tmpStrs[1].split(":")[1].trim();
                    type = getShortType(sousCat);
                    dateDeMiseAJour = tmpStrs[2].split(":")[1].trim();
                    description = tmpStrs[3].split(":")[1].trim();
                    severite = tmpStrs[4].split(":")[1].trim();
                    territoire = tmpStrs[5].split(":")[1].trim();
                    certitude = tmpStrs[6].split(":")[1].trim();
                    urgence = tmpStrs[7].split(":")[1].trim();
                    
                    Geometry geometry = new Geometry(lat, lng);

                    LiveAlert alert =                   
                            new LiveAlert(alertId, nom, source, territoire, certitude,
                                    severite, type, sousCat, dateDeMiseAJour, urgence,
                                    description, geometry);
                    liveAlerts.put(alertId, alert);
                }
            }

        } catch (MismatchedInputException ex) {
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (JsonParseException ex) {
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RssFeedParser.class.getName())
                    .log(Level.WARNING, null, ex);
        }
    }
    
    /**
     * Updates the alert lists to identify the alerts that need to be added ou removed from the live alert repository
     */
    private void updateAlertLists() {
    	
    	// Si le répertoire d'alertes est vide, ajouter toutes les alertes
    	if (actualAlerts.size() == 0) {
    		newAlerts.addAll(liveAlerts.values());
    	}
    	
    	else {
    		//On parcourt les nouvelles alertes et on ne garde que celles qui ne sont pas dans déjà dans le répertoire
    		for (LiveAlert liveAlert: liveAlerts.values()) {
    			if (!actualAlerts.containsKey(liveAlert.id))   					
    				newAlerts.add(liveAlert);
    		}
    		// On parcourt les alertes actuelles et on retire celles qui ne sont plus à jour
    		for (LiveAlert actualAlert: actualAlerts.values()) {
    			if(!liveAlerts.containsKey(actualAlert.id))
    				oldAlerts.add(actualAlert);
    		}  		
    	}	
    }
    
    
    /**
     * Identifies the alert type based on its subcategory
     * 
     * @param sousCat 
     * @return the alert type
     */
    private String getShortType(String sousCat) {
        String result;
        switch (sousCat) {
            case "Feu de brousse":
            case "Feu de forêt":
                result = "Feu";
                break;
            case "Inondation":
            case "Inondation par ruissellement":
            case "Suivi des cours d'eau":
                result = "Eau";
                break;
            case "Avalanche":
            case "Géomorphologique (ex. érosion)":
            case "Mouvement de terrain":
            case "Tremblement de terre":
                result = "Terrain";
                break;
            case "Glace":
            case "Onde de tempête":
            case "Orage violent":
            case "Ouragan":
            case "Pluie":
            case "Pluie verglaçante":
            case "Tempête hivernale":
            case "Tornade":
            case "Vent de tempête":
                result = "Météo";
                break;
            default:
                result = "Météo";
        }
        return result;
    }
}
