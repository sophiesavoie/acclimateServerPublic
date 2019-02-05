package hackqc18.Acclimate.alert;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hackqc18.Acclimate.AcclimateApplication;

/**
 * Parses the alerts from the historical alert database stored in a CSV document
 * 
 * @author Sophie Savoie and Normand Desmarais
 *
 */

@Component
public class CsvAlertParser {
	
    protected static String filename = "historique_alertes.csv";
	private ArrayList <HistoAlert> newAlerts;
	private final static String url = "https://geoegl.msp.gouv.qc.ca/ws/igo_gouvouvert.fcgi?service=wfs&version=1.1.0&"
			+ "request=getfeature&typename=vg_observation_v_autre_wmst&outputformat=CSV";
	private final static String docPath = "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + filename;
			
	private final File fileDir = new File(docPath);
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(CsvAlertParser.class);
	
	/**
	 * Returns the historical alerts of the precedent week
	 * 
	 * @return the alert list
	 */
    public ArrayList <HistoAlert> getLastWeekAlerts() {
        String toBeParsed = "";
        
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileDir), "UTF8"));

            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("\r")) {
                    s.replace("\r", ",");
                }

                toBeParsed += s;
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(CsvAlertParser.class.getName()).log(Level.WARNING,
                    "Erreur à l’ouverture du fichier '" + filename + "'.", ex);
        }
        parseLastWeekAlerts(toBeParsed);
        
        return newAlerts;
    }
    
    /**
     * Downloads the historical alerts csv document and saves it in the resources each week before the 
     * parsing is scheduled
     * 
     * @param url
     * @param docPath
     * @throws IOException
     */
    @Scheduled(cron = "0 2 0 * * 0")
	public static void downloadDocument() throws IOException {
        try {

            FileUtils.copyURLToFile(new URL(url), new File(docPath), 10000, 10000);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}


    /**
     * Utility method that creates a unique id from the alert type, date,
     * longitude and latitude.
     *
     * @return the id
     */
    public String createId(String type, String date, double lat, double lng) {
        return (type.hashCode() + "-" + date.hashCode() + "-" + lat + "-" + lng)
                .replaceAll("\\.", "");
    }
    
    
    /**
     * Parses the alerts of the csv document and adds the alerts of the precedent week in the newAlerts ArrayList
     * 
     * @param toBeParsed
     */
    public void parseLastWeekAlerts(String toBeParsed) {
        String[] alertePrg = toBeParsed.split(",");
        newAlerts = new ArrayList<>();
		Calendar cal = Calendar.getInstance();			    
		Timestamp now = new Timestamp(System.currentTimeMillis());
		cal.setTimeInMillis(now.getTime());
		cal.add(Calendar.DATE, -7);
		Timestamp lastWeek = new Timestamp(cal.getTime().getTime());
        
        // String[] typesAlertes = {"Avalanche", "Feu de brousse", "Feu de
        // forêt",
        // "Géomorphologique (ex. érosion)", "Glace", "Inondation",
        // "Inondation par ruissellement", "Mouvement de terrain", "Onde de
        // tempête",
        // "Orage violent", "Ouragan", "Pluie", "Pluie verglaçante",
        // "Tempête hivernale", "Tornade", "Tremblement de terre",
        // "Vent de tempête"};
        String[] typesAlertes = { "Feu de forêt", "Inondation" };
        String nom = "", territoire = "", certitude = "", severite = "",
                type = "", sousCat = "";
        String dateDeMiseAJour = "", urgence = "", description = "",
                alertId = "";
        String source = "Ministère de la Sécurité publique du Québec";
        double lng = 0.0, lat = 0.0;

        for (int i = 10; i < alertePrg.length; i++) {
            int j = i % 10;

            switch (j) {
                case 0:
                    String[] temp = alertePrg[i].split("[a-z]+");
                    if (temp.length > 1) {
                        dateDeMiseAJour = temp[1];
                    }
                    break;
                case 1:
                    break;
                case 2:
                    territoire = alertePrg[i];
                    break;
                case 3:
                    lng = Double.parseDouble(alertePrg[i]);
                    break;
                case 4:
                    lat = Double.parseDouble(alertePrg[i]);
                    break;
                case 5:
                    urgence = alertePrg[i];
                    break;
                case 6:
                    certitude = alertePrg[i];
                    break;
                case 7:
                    nom = alertePrg[i];
                    sousCat = nom;
                    type = getShortType(nom);
                    break;
                case 8:
                    severite = alertePrg[i];
                    break;
                case 9:
                    for (int k = 0; k < typesAlertes.length; k++) {
                        if (nom.equals(typesAlertes[k])) {
                        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            			    Date parsedDate;
            			    Timestamp alertDate;
            			    int year = cal.get(Calendar.YEAR);
							try {
								if (dateDeMiseAJour.substring(0, 4).equals(year + "")) {
									parsedDate = dateFormat.parse(dateDeMiseAJour);
									alertDate = new java.sql.Timestamp(parsedDate.getTime());
									
									if (alertDate.after(lastWeek)) {
										alertId = createId(type, dateDeMiseAJour, lat, lng);
			                            Geometry geometry = new Geometry(lat, lng);
			                            HistoAlert alert = new HistoAlert(alertId, nom, source, territoire,
			                                    certitude, severite, type, sousCat, dateDeMiseAJour,
			                                    urgence, description, geometry);
			                            newAlerts.add(alert);
			                            break;
									}
								}
								
							} catch (ParseException e) {
					        	Logger.getLogger(CsvAlertParser.class.getName()).log(Level.WARNING,
					                    "Erreur lors du parse de la date '" + dateDeMiseAJour + "'.", e);
							}
                        }
                    }
                    break;
                default:
                    break;
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
