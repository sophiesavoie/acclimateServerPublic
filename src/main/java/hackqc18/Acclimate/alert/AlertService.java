package hackqc18.Acclimate.alert;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.AcclimateApplication;
import hackqc18.Acclimate.alert.repository.HistoAlertRepository;
import hackqc18.Acclimate.alert.repository.LiveAlertRepository;
import hackqc18.Acclimate.alert.repository.OldUserAlertRepository;
import hackqc18.Acclimate.alert.repository.UserAlertRepository;
import hackqc18.Acclimate.exception.AlertNotFoundException;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.ResourceNotFoundException;
import hackqc18.Acclimate.exception.UnsupportedAlertTypeException;
import hackqc18.Acclimate.exception.UserNotAuthorizedException;
import hackqc18.Acclimate.monitoredZone.MoniZoneService;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;
import hackqc18.Acclimate.notifications.PushNotifServiceImpl;
import hackqc18.Acclimate.tile.TileService;
import hackqc18.Acclimate.user.User;
import hackqc18.Acclimate.user.UserService;


/**
 * Service class for User Alerts. This is the class that implements all the
 * dirty works to answer requests from the controller and connect them with the
 * model (Alert).
 * 
 * @author Normand Desmarais and Sophie Savoie
 */
// The @Service annotation informs Spring that this class must be treated
// as a Service class. In the background, Spring makes sure that this
// class is a Singleton (only has one single instance) and makes it possible
// to retrieve it with an @Autowired annotation (see the @Autowired in
// the UserAlertController class).
@Service
public class AlertService {

    private static final int rssfetchingDelay = 120;
    
    // These @Autowired creates a singleton instance of the four following
    // repositories classes.
	@Autowired
	private UserAlertRepository  userAlertRepository;
    @Autowired
    private LiveAlertRepository  liveAlertRepository;
    @Autowired
    private HistoAlertRepository histoAlertRepository;
    @Autowired
    private OldUserAlertRepository oldUserAlertRepository;
    
    @Autowired
    private RssFeedParser rssFeedParser;
    @Autowired
    private CsvAlertParser csvAlertParser;
    @Autowired
    private TileService tileService;
    @Autowired
    private UserService userService;
    @Autowired
    private MoniZoneService moniZoneService;
    @Autowired
    private SopfeuAlertScraper sopfeuAlertScraper;
    
    
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);


    // TODO: call a method that would aggregate alerts depending
    // on the zooming size. It thus imply to update the Alert class
    // to include the number of alerts aggregated under this
    // alert umbrella and to automatically calculate the average
    // coordinate weighted by the alert "certitude", "severity" and
    // "urgence" as well as maybe its "dateDeMiseAJour".
    /**
     * Returns the list of alerts corresponding to the alertType and bounded by the north, south, east and west
     * coordinates.
     *
     * @param north the northern latitude (max: 90)
     * @param south the southern latitude (min: -90)
     * @param east the eastern longitude (max: 180)
     * @param west the western longitude (min: -180)
     * 
     * @return the list of alerts
     */
    public ArrayList<Alert> findAllAlerts(String alertType, double north,
            double south, double east, double west) {
    	ArrayList<Alert> alerts = new ArrayList<>();
    	
    	switch (alertType) {
        case "user":
        	userAlertRepository.findAll().forEach(alert -> {
                if (((Alert) alert).overlapWithBox(north, south, east, west)) {
                    alerts.add(alert);
                }
    		});
        	return alerts;       	
		case "live":
        	liveAlertRepository.findAll().forEach(alert -> {
                if (((Alert) alert).overlapWithBox(north, south, east, west)) {
                    alerts.add(alert);
                }
    		});
        	return alerts;
        case "historical":
        	histoAlertRepository.findAll().forEach(alert -> {
                if (((Alert) alert).overlapWithBox(north, south, east, west)) {
                    alerts.add(alert);
                }
    		});
        	return alerts;
        default:
        	throw new UnsupportedAlertTypeException(alertType);
        }
    }


    /**
     * Returns the alert for the provided id and alertType.
     *
     * @param id the id that uniquely identify the alert
     * @return the alert
     * @throws AlertNotFoundException, OperationNotSupportedException
     */
    public Alert findAlertById(String alertType, String id)
            throws AlertNotFoundException, OperationNotSupportedException {

    	switch (alertType) {
        case "user":
        	if(!userAlertRepository.existsById(id))
        		throw new AlertNotFoundException(id);
            return userAlertRepository.findById(id);
            
        case "live":
        	if(!liveAlertRepository.existsById(id))
        		throw new AlertNotFoundException(id);
        	return liveAlertRepository.findById(id);
        	
        case "historical":
        	if(!histoAlertRepository.existsById(id))
        		throw new AlertNotFoundException(id);
            return histoAlertRepository.findById(id);
            
        default:
            throw new UnsupportedAlertTypeException(alertType);
    	}
    }


    /**
     * Creates a userAlert in the database from the userAlert provided. 
     * 
     * @param alertType the type of the alert
     * @param userAlert the userAlert provided
     * @return the newly created or modified alert
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    public UserAlert createUserAlert(UserAlert userAlert)
            throws OperationNotSupportedException, BadRequestException {
    	
    	ZoneId zoneId = ZoneId.of("America/Montreal");  	
    	TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    	Timestamp now = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();		    
		cal.setTimeInMillis(now.getTime());
		cal.setTimeZone(timeZone);
		cal.add(Calendar.HOUR, -6);
		Timestamp date = new Timestamp(cal.getTime().getTime());
		userAlert.setDateDeMiseAJour(date + "");
		
    	if (userAlert.getUser() == null)
    		throw new BadRequestException("Le user de l'alerte ne doit pas être null");   	

    	try {
    		userAlert.setId(userAlert.createId());
    		
    		if (userAlert.getPhotoPath() != null) {
    			String path = userAlert.getPhotoPath();
    			userAlert.setPhotoPath(String.format(path, userAlert.getId()));
    		}
    		return userAlertRepository.save(userAlert);
    		
    	} catch (Exception e) {
    		throw new BadRequestException("Erreur lors de la création de l'alerte user");
    	}
    }

    
    /**
     * Updates the given fields of the user alert corresponding to the provided id
     *
     * @param id the id of the alert to be updated
     * @param fields the fields to be updated
     * @return the updated alert
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    public UserAlert updateUserAlert(String id, Map<String, Object> fields)
            throws OperationNotSupportedException, AlertNotFoundException, BadRequestException {
    	
    	ArrayList<Boolean> notifToSend = new ArrayList<>();
    	UserAlert userAlert;
    		
    	try {
    		userAlert = userAlertRepository.findById(id);
    	} catch(Exception e) {
    		throw new AlertNotFoundException(id);
    	}
    	
    	fields.forEach((k, v) -> {
    		Field field = ReflectionUtils.findRequiredField(UserAlert.class, k);
    		
    		if (field.getName().equals("plusOneUsers")) {
    			ArrayList <String> plusOnes = userAlert.getPlusOneUsers();
    			
    			if (userAlert.getPlusOneUsers().contains((String) v)) {
    				userAlert.getPlusOneUsers().remove((String) v);
    				userAlert.decreaseCount();
    			}
    			else {
    				plusOnes.add((String) v);
    				userAlert.setPlusOneUsers(plusOnes);
        			userAlert.increaseCount();
    			}

    			if (userAlert.getMinusOneUsers().contains((String) v)) {
    				userAlert.getMinusOneUsers().remove((String) v);
    				userAlert.increaseCount();
    			}
            	//if (userAlert.getScore() == 10) {
    			if (userAlert.getScore() == 2) {
            		userAlert.setCertitude("Observé");
            		notifToSend.add(true);
            		
            		User user = userAlert.getUser();
        			int pointsToAdd = userAlert.getPhotoPath() != null ? 6: 5;
        			user.getKarma().setPoints(pointsToAdd);
            		userService.saveUser(user);
            		
            		userAlert.getPlusOneUsers().forEach(userId -> {
            			User u = userService.findUserByUId(userId);
            			u.getKarma().increasePoints();
            			userService.saveUser(u);
            		});
            	}
            } else if (field.getName().equals("minusOneUsers")) {
    			ArrayList <String> minusOnes = userAlert.getMinusOneUsers();
    			
    			if (userAlert.getMinusOneUsers().contains((String) v)) {
    				userAlert.getMinusOneUsers().remove((String) v);
    				userAlert.increaseCount();
    			}
    			else {
    				minusOnes.add((String) v);
	    			userAlert.setMinusOneUsers(minusOnes);
	    			userAlert.decreaseCount();
    			}
    			
    			if (userAlert.getPlusOneUsers().contains((String) v)) {
    				userAlert.getPlusOneUsers().remove((String) v);
    				userAlert.decreaseCount();
    			}
    		} else {
    			ReflectionUtils.setField(field, userAlert, v);
    		}
        });
    	
    	try {
    		UserAlert ua = userAlertRepository.save(userAlert);
    		
    		// Si l'alerte devient confirmée, envoyer des notifications aux users concernés
    		if (!notifToSend.isEmpty()) {
    			sendUserAlertNotifications(ua);
    		}
    		return ua;
    		
    	} catch (Exception e) {
    		throw new BadRequestException("Erreur lors de la mise à jour de l'alerte user");
    	}
    	
    }
    
    /**
     * Modifies the geometry of the user alert corresponding to the provided id
     * 
     * @param zoneId
     * @param geometry
     * @return the updated user alert
     * @throws BadRequestException 
     */
	public UserAlert updateAlertGeometry(String id, Geometry geometry) 
			throws OperationNotSupportedException, AlertNotFoundException, BadRequestException {
		
		if(!userAlertRepository.existsById(id))
    		throw new AlertNotFoundException(id);
		
		UserAlert userAlert = userAlertRepository.findById(id);
		userAlert.setGeometry(geometry);
		
		try {
			return userAlertRepository.save(userAlert);
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la mise à jour de la géométrie de l'alerte user");
		}
	}


    /**
     * Deletes the user alert corresponding to the provided id from the database. 
     *
     * @param alertId the alert id
     * @return the deleted alert or null if it doesn't exist
     * @throws OperationNotSupportedException
     * @throws AlertNotFoundException
     */
    public ResponseEntity<?> deleteUserAlert(String alertId, String uId)
            throws OperationNotSupportedException, AlertNotFoundException {
    	
    	UserAlert alert;
    		
    	try {
    		alert = userAlertRepository.findById(alertId);
    	} catch (Exception e) {
    		throw new AlertNotFoundException(alertId);
    	}
    	
    	if (!alert.getUser().getuId().equals(uId))
    		throw new UserNotAuthorizedException(uId);
    	
		userAlertRepository.deleteById(alertId);
		
		return ResponseEntity.ok().build();
    }
    
    /**
     * Deletes all the userAlerts related to the user of the given uId
     * 
     * @param id
     * @return
     * @throws OperationNotSupportedException
     * @throws AlertNotFoundException
     */
    public ResponseEntity<?> deleteAllUserAlertOfUid(String uId) {	
    	ArrayList<UserAlert> userAlerts = new ArrayList<>();
    	User user = userService.findUserByUId(uId);
    	userAlerts = (ArrayList<UserAlert>) userAlertRepository.findByUser(user);

    	if (userAlerts != null) {
    		userAlerts.forEach(alert -> {
    			try {
    				userAlertRepository.delete(alert);
    			} catch (Exception e) {
    				new BadRequestException("Erreur lors de la suppression des alertes de: " + uId);
    			}
    		});
    	}
		
		return ResponseEntity.ok().build();
    }
    
    /**
     * Sends a notification to all the users concerned by the given userAlert
     */
    public void sendUserAlertNotifications(UserAlert userAlert) {
		int index = tileService.findTileIndexOfPoint(userAlert.getLat(), userAlert.getLng());
		
		ArrayList<MonitoredZone> mnOfAlert = tileService.getZonesByTileIndex(index);
		
		mnOfAlert.forEach(zone -> {
			if (zone.containsAlert(userAlert)) {
				ArrayList<String> registrationTokens = tileService.getUserToNotifRegTokens(zone);
				PushNotifServiceImpl.sendMzUserAlertNotif(registrationTokens, zone.getName(), 1);
			}
		});    		
    }
    
    /**
     * Sends a notification to all the users concerned by the given new live alerts
     * 
     * @param newAlerts
     */
    public void sendLiveAlertNotifications(ArrayList<LiveAlert> newAlerts){    	
    	HashMap<Integer, Integer> zoneToNotify = new HashMap<Integer, Integer>();
    	ArrayList<MonitoredZone> mnOfAlert = new ArrayList<>();
    	
    	for(LiveAlert liveAlert: newAlerts) {
    		int index = tileService.findTileIndexOfPoint(liveAlert.getLat(), liveAlert.getLng());
    		mnOfAlert = tileService.getZonesByTileIndex(index);
    		
    		mnOfAlert.forEach(mn -> {
    			if (mn.containsAlert(liveAlert)) {
    				
    				if (!zoneToNotify.containsKey(mn.getZoneId())) {
    					zoneToNotify.put(mn.getZoneId(), 1);
    				}
        			else {
        				zoneToNotify.put(mn.getZoneId(), zoneToNotify.get(mn.getZoneId()) + 1);
        			}	
    			}
    		});    		
    	}
    	
    	for (int zoneId : zoneToNotify.keySet()) {
			MonitoredZone zone = moniZoneService.findMoniZoneById(zoneId);
    		ArrayList<String> registrationTokens = tileService.getUserToNotifRegTokens(zone);
			
			PushNotifServiceImpl.sendMzLiveAlertNotif(registrationTokens, zone.getName(), 
					zoneToNotify.get(zoneId).intValue());
		}
    }
    
    /**
     * Fetches the live alerts from the rss feed and the SOPFEU website every two minutes
     * and then updates the liveAlert repository
     */
    // The @Scheduled annotation informs Spring to create a
    // task with the annotated method and to run it in a separate
    // thread at the given fixed rate. @Schedule only works if the
    // @EnableScheduling annotation has been set in the application class.
    
    @Scheduled(fixedRate = 1000 * rssfetchingDelay)
    public void upDateLiveAlerts() {
    	HashMap<String, LiveAlert> actualMSPAlerts = new HashMap<>();
    	Pair<ArrayList<LiveAlert>, ArrayList<LiveAlert>> parsedMSPAlerts;
    	
    	HashMap<String, LiveAlert> actualSopfeuAlerts = new HashMap<>();
    	Pair<ArrayList<LiveAlert>, ArrayList<LiveAlert>> parsedSopfeuAlerts;
    	
    	try {
    		liveAlertRepository.findAll().forEach(alert -> {
    			if (alert.getSource().equals("SOPFEU"))
    				actualSopfeuAlerts.put(alert.id, alert);
    				
    			else
    				actualMSPAlerts.put(alert.id, alert);
    				
    		});
    		log.info("actualMSPAlerts" + actualMSPAlerts.size());
    		log.info("actualSopfeuAlerts" + actualSopfeuAlerts.size());
    		
    		parsedMSPAlerts = rssFeedParser.updateAlertsFromRssFeedTask(actualMSPAlerts);
    		updateRepositories(parsedMSPAlerts);
    		
    		parsedSopfeuAlerts = sopfeuAlertScraper.updateSopfeuAlerts(actualSopfeuAlerts);
    		updateRepositories(parsedSopfeuAlerts);

    	} catch(NullPointerException e){
    		
    	}
    }
    
    
    /**
     * Updates the liveAlert repository
     * 
     * @param parsedAlerts
     */
    private void updateRepositories(Pair<ArrayList<LiveAlert>, ArrayList<LiveAlert>> parsedAlerts) {
    	ArrayList <LiveAlert> newAlerts = new ArrayList<>();
    	ArrayList <LiveAlert> oldAlerts = new ArrayList<>();
    	
    	newAlerts = parsedAlerts.getElement0();
    	liveAlertRepository.saveAll(newAlerts);
    	
    	if (!newAlerts.isEmpty())
    		sendLiveAlertNotifications(newAlerts);
    	
    	oldAlerts = parsedAlerts.getElement1();
    	
    	oldAlerts.forEach(alert -> {
    		if (!histoAlertRepository.existsById(alert.id)) {
    			HistoAlert histoAlert = new HistoAlert(alert.getId(), alert.getNom(), alert.getSource(), alert.getTerritoire(),
        				alert.getCertitude(), alert.getSeverite(), alert.getType(), alert.getSousCategorie(),
        				alert.getDateDeMiseAJour(), alert.getUrgence(), alert.getDescription(), alert.getGeometry());
        		histoAlertRepository.save(histoAlert);
			}
		});
    	liveAlertRepository.deleteAll(oldAlerts);
    }
    

    /**
     * Parses the historical alert database and updates the historicalAlerts repository each week
     */
    // mm (minutes) hh (heure) jj (numéro du jour) MMM (mois) JJJ (jour)
    @Scheduled(cron = "0 10 0 * * 0")
    public void updateHistoAlerts() {
    	ArrayList <HistoAlert> histoAlerts = new ArrayList<>();
    	
    	try {
    		histoAlerts = csvAlertParser.getLastWeekAlerts();
    		
    		histoAlerts.forEach(alert -> {
    			if (!histoAlertRepository.existsById(alert.getId()))
    				histoAlertRepository.save(alert);
    		});
    	} catch(NullPointerException e){
    		
    	}
    }
    
    /**
     * Updates the user alerts each hour. If an alert exists for more than 24 hours, it is transfered to the 
     * historialAlert ou the oldUserAlert repository, depending on the level of certitude of the alert.
     */
    @Scheduled(fixedRate = 30000 * rssfetchingDelay) // À chaque heure
    public void updateUserAlerts() {
    	Timestamp now = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
    	
    	userAlertRepository.findAll().forEach(alert -> {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			    Date parsedDate = dateFormat.parse(alert.getDateDeMiseAJour());			    
			    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
				cal.setTimeInMillis(timestamp.getTime());
				cal.add(Calendar.HOUR, 24);
				Timestamp tomorrow = new Timestamp(cal.getTime().getTime());
				
				if (tomorrow.before(now)) {
					if (alert.getCertitude().equals("Observé")) {
						HistoAlert histoAlert = new HistoAlert(alert.getId(), alert.getNom(), alert.getSource(), alert.getTerritoire(),
	            				alert.getCertitude(), alert.getSeverite(), alert.getType(), alert.getSousCategorie(),
	            				alert.getDateDeMiseAJour(), alert.getUrgence(), alert.getDescription(), alert.getGeometry());
						histoAlertRepository.save(histoAlert);
					}
					else {
						OldUserAlert oldUserAlert = new OldUserAlert(alert);
						oldUserAlertRepository.save(oldUserAlert);
					}
					userAlertRepository.delete(alert);
				}
					
			} catch (ParseException e) {
				
			}
    	});
    }
}
