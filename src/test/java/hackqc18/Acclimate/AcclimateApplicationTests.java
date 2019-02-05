package hackqc18.Acclimate;

import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.AlertService;
import hackqc18.Acclimate.alert.CsvAlertParser;
import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.alert.HistoAlert;
import hackqc18.Acclimate.alert.LiveAlert;
import hackqc18.Acclimate.alert.SopfeuAlertScraper;
import hackqc18.Acclimate.alert.UserAlert;
import hackqc18.Acclimate.alert.repository.HistoAlertRepository;
import hackqc18.Acclimate.alert.repository.LiveAlertRepository;
import hackqc18.Acclimate.alert.repository.UserAlertRepository;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.monitoredZone.MoniZoneRepository;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;
import hackqc18.Acclimate.notifications.PushNotifServiceImpl;
import hackqc18.Acclimate.tile.Tile;
import hackqc18.Acclimate.tile.TileCalculator;
import hackqc18.Acclimate.tile.TileRepository;
import hackqc18.Acclimate.tile.TileService;
import hackqc18.Acclimate.user.Karma;
import hackqc18.Acclimate.user.User;
import hackqc18.Acclimate.user.UserRepository;
import hackqc18.Acclimate.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AcclimateApplicationTests {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MoniZoneRepository moniZoneRepository;
	@Autowired
	UserAlertRepository userAlertRepository;
	@Autowired
	SopfeuAlertScraper sopfeuAlertScraper;
	@Autowired
	TileService tileService;
	@Autowired
	LiveAlertRepository liveAlertRepository;
	@Autowired
	AlertService alertService;
	@Autowired
	TileRepository tileRepository;
	@Autowired
	HistoAlertRepository histoAlertRepository;
	@Autowired
	UserService userService;
	@Autowired
	CsvAlertParser csvAlertParser;

	
	private static final Logger log = LoggerFactory.getLogger(AcclimateApplicationTests.class);

	@Before
    public void authenticateServer() {
        try {
            // [ Obtenir l'instance de Firebase App pour utiliser Admin SDK ]
            FileInputStream serviceAccount = new FileInputStream("acclimate-c79ea-firebase-adminsdk-fn6dq-9264e4b010.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://acclimate-c79ea.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);
            // [ Fin de la validation du Admin SDK API ]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Test
	public void contextLoads() throws ParseException, OperationNotSupportedException, BadRequestException {
		
		
		
		/**
		ZoneId zoneId = ZoneId.of( "America/Montreal" );  	
    	TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    	Timestamp now = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();		    
		cal.setTimeInMillis(now.getTime());
		cal.setTimeZone(timeZone);
		Timestamp date = new Timestamp(cal.getTime().getTime());
		log.info(date + "");
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date parsedTimeStamp = null;
		try {
			parsedTimeStamp = dateFormat.parse("2018-09-02 17:42:10");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
		String date = timestamp + "";
		
		Geometry pointSO = new Geometry(40.0, -84.0);
		
		User user = userRepository.findByUId("x123");
		
		ArrayList<String> plusOneUser = new ArrayList<>();
		ArrayList<String> minusOneUser = new ArrayList<>();
		
		UserAlert userAlert = new UserAlert("nom", "source", "territoire",
				"certitude", "severite", "type", "sousCategorie",
				date, "urgence", "description", pointSO, user, 0, plusOneUser, minusOneUser);
		
		alertService.createUserAlert(userAlert);
		
		
		TileCalculator tileCalculator = new TileCalculator();
		
		Geometry pointSO = new Geometry(40.0, -84.0);
		log.info("SO: " + tileCalculator.findTileIndexOfPoint(40.0, -84.0) + "");
		log.info(tileCalculator.findTileMatrixIndex(40.0, -84.0)[0] + " ," + tileCalculator.findTileMatrixIndex(40.0, -84.0)[1] );
		log.info(tileCalculator.yxToIndex(tileCalculator.findTileMatrixIndex(40.0, -84.0)[0] , tileCalculator.findTileMatrixIndex(40.0, -84.0)[1]) + "");
		Geometry pointNO = new Geometry(62.0, -84.0);
		log.info("NO: " + tileCalculator.findTileIndexOfPoint(62.0, -84.0) + "");
		log.info(tileCalculator.findTileMatrixIndex(62.0, -84.0)[0] + " ," + tileCalculator.findTileMatrixIndex(62.0, -84.0)[1] );
		log.info(tileCalculator.yxToIndex(tileCalculator.findTileMatrixIndex(62.0, -84.0)[0], tileCalculator.findTileMatrixIndex(62.0, -84.0)[1]) + "");
		Geometry pointSE = new Geometry(40.0, -58.0);
		log.info("SE: " + tileCalculator.findTileIndexOfPoint(40.0, -58.0) + "");
		log.info(tileCalculator.findTileMatrixIndex(40.0, -58.0)[0] + " ," + tileCalculator.findTileMatrixIndex(40.0, -58.0)[1] );
		log.info(tileCalculator.yxToIndex(tileCalculator.findTileMatrixIndex(40.0, -58.0)[0], tileCalculator.findTileMatrixIndex(40.0, -58.0)[1]) + "");
		Geometry pointNE = new Geometry(62.0, -58.0);
		log.info("NE: " + tileCalculator.findTileIndexOfPoint(62.0, -58.0) + "");
		log.info(tileCalculator.findTileMatrixIndex(62.0, -58.0)[0] + " ," + tileCalculator.findTileMatrixIndex(62.0, -58.0)[1] );
		log.info(tileCalculator.yxToIndex(tileCalculator.findTileMatrixIndex(62.0, -58.0)[0], tileCalculator.findTileMatrixIndex(62.0, -58.0)[1]) + "" );
		
		Geometry geo = new Geometry(75.0, -45.0);
		User user = new User(timestamp, "x123", "sophiess", "s@gmail.com", "Sophie",
				"Savoie", karma, registrationToken);
		
		userRepository.save(user);


		
		ArrayList<String> registrationToken = new ArrayList<>();
		registrationToken.add("cMKS2VqWNRY:APA91bF0GOUdKTa2ZcCUIhzDXYnewGovbrqDTJPpPVfW4nbBWRKjLHg9inz6ahIhg56s2tQ7ZFefho93iXa_XngE4raWMMJwef8xLfCUIzFy0QnkY");
		Karma karma = new Karma (0);
		Timestamp date = new Timestamp(System.currentTimeMillis());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(
	            "yyyy-MM-dd hh:mm:ss");
		java.util.Date parsedTimeStamp = dateFormat.parse("2014-08-22 15:02:51");
	    Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
		
		
		
		//-----------------------------------------------------------------------
		
		double[][] southPoly = {{40.1, -84.0},{40.9, -80.0}};
		Geometry southPolygon = new Geometry(southPoly);
		
		MonitoredZone moniZone1 = new MonitoredZone("southPoly", southPolygon, 50, user, date);
		MonitoredZone mz = moniZoneRepository.save(moniZone1);
		ArrayList<Integer> tileIndexes = tileService.findMonitoredZoneTiles(mz);
		
		
		tileIndexes.forEach(index -> {
			Tile tile = new Tile (index, mz);
			tileService.createTile(tile);
		});
		
		/**
		//---------------------------------------------------------
		
		double[][] westPoly = {{62.0, -84.0},{40.0, -84.0}};
		Geometry westPolygon = new Geometry(westPoly);
		
		MonitoredZone moniZone2 = new MonitoredZone("westPoly", westPolygon, 50, user, date);
		MonitoredZone mz2 = moniZoneRepository.save(moniZone2);
		ArrayList<Integer> tileIndexes2 = tileService.findMonitoredZoneTiles(mz2);
		
		tileIndexes2.forEach(index -> {
			Tile tile = new Tile (index, mz2);
			tileService.createTile(tile);
		});
		
		//---------------------------------------------------------
		
		double[][] northPoly = {{62.0, -84.0},{62.0, -58.0}};
		Geometry northPolygon = new Geometry(northPoly);
		
		MonitoredZone moniZone3 = new MonitoredZone("northPoly", northPolygon, 50, user, date);
		MonitoredZone mz3 = moniZoneRepository.save(moniZone3);
		ArrayList<Integer> tileIndexes3 = tileService.findMonitoredZoneTiles(mz3);
		
		tileIndexes3.forEach(index -> {
			Tile tile = new Tile (index, mz3);
			tileService.createTile(tile);
		});
		
		//---------------------------------------------------------
		
		double[][] eastPoly = {{62.0, -60.0},{60.0, -58.0}};
		Geometry eastPolygon = new Geometry(eastPoly);
		
		MonitoredZone moniZone4 = new MonitoredZone("eastPoly", eastPolygon, 50, user, date);
		MonitoredZone mz4 = moniZoneRepository.save(moniZone4);
		ArrayList<Integer> tileIndexes4 = tileService.findMonitoredZoneTiles(mz4);
		
		tileIndexes4.forEach(index -> {
			Tile tile = new Tile (index, mz4);
			tileService.createTile(tile);
		});

		//---------------------------------------------------------
		
		Geometry point = new Geometry(40.0, -84.0);
		MonitoredZone moniZone5 = new MonitoredZone("chalet", point, 111131, user, date);
		MonitoredZone mz5 = moniZoneRepository.save(moniZone5);

		ArrayList<Integer> tileIndexes5 = tileService.findMonitoredZoneTiles(mz5);
		
		tileIndexes5.forEach(index -> {
			Tile tile = new Tile (index, mz5);
			tileService.createTile(tile);
		});
		
		//--------------------------------------------------------------
		Geometry geoAlert = new Geometry(40.9, -80.0);
		LiveAlert lAlert = new LiveAlert("1", "Veille d'orages violents en vigueur","Environnement Canada",
				"Gatineau, Simcoe - Delhi - Norfolk, Dunnville","Probable","Modérée","Météo","Orage violent",
				"lundi 06 août 2018", "Prévue", "Les conditions sont propices à la formation d'orages",
				geoAlert);
		liveAlertRepository.save(lAlert);
		
		ArrayList<LiveAlert> newAlerts = new ArrayList<>();
		newAlerts.add(lAlert);
		
    	log.info("Notifications----------------------------------------------");
    	
    	/**
		List<MonitoredZone> mz1 = new ArrayList<>();
		int index = tileCalculator.findTileIndexOfPoint(lAlert.getLat(), lAlert.getLng());
		log.info("Index : " + index);
		
		mz1 = tileService.getZonesByTileIndex(index);
		log.info("mz1.Size" + mz1.size());
		ArrayList<MonitoredZone> zoneWithAlert = new ArrayList<>();
		
		for (MonitoredZone zone : mz1) {
			log.info("Zone1: " + zone.getZoneId());
			if (zone.containsAlert(lAlert)) {
				zoneWithAlert.add(zone);
				log.info("Zone ajoutée: " + zone.getZoneId());
			}
		}

		log.info("zonesAvecAlertes: " + zoneWithAlert.size());

		
		
    	//--------------------------------------------------------------------

    	HashMap<MonitoredZone, Integer> zoneToNotify = new HashMap<MonitoredZone, Integer>();
    	List<MonitoredZone> mnOfAlert = new ArrayList<>();
    	
    	for(LiveAlert liveAlert: newAlerts) {
    		int index1 = tileService.findTileIndexOfPoint(liveAlert.getLat(), liveAlert.getLng());
    		log.info("Index : " + index1);
    		mnOfAlert = tileService.getZonesByTileIndex(index1);
    		log.info("mz1.Size" + mnOfAlert.size());
    		
    		mnOfAlert.forEach(mn -> {
    			if (mn.containsAlert(liveAlert)) {
    				if (!zoneToNotify.containsKey(mn)) {
    					zoneToNotify.put(mn, 1);
    					log.info("Zone ajoutée: " + mn.getZoneId());
    				}
        			else {
        				zoneToNotify.put(mn, zoneToNotify.get(mn) + 1);
        				log.info("Zone +1: " + mn.getZoneId());
        			}	
    			}
    		});    		
    		
    		for (MonitoredZone zone : zoneToNotify.keySet()) {
    			ArrayList<String> registrationTokens = new ArrayList<>();
    			registrationTokens = tileService.getUserToNotifRegTokens(zone);
    			
    			PushNotifServiceImpl.sendMzLiveAlertNotif(registrationTokens, zone.getName(), 
    					zoneToNotify.get(zone).intValue());
    		}
    	}
    	
    	//###################################  NOTIFICATION  ##################################
    	ArrayList<String> listOfDevices = new ArrayList<>(); // liste des appareils qui vont recevoir les tests
        String sophie = "cqmDyN0c6UE:APA91bGzWf2FLxx7-atzIu6CSVQAszCqXUUhz-tFKTFCzGeuQLb4_hfksM-rIvPkid2ZRl3I9ZIy-qyQqysifwrGyaTyPKJ5dNAcE9b1CVTJLI3Fe6G5yR5uUWNtkjL8UMvCjrGGLWsq";
        String jeremi = "dgVFOBkJyXg:APA91bEauGfHnRK8kHxrTDXn-U2DSXT756j0Y0_MtgVkBe42QcK1LjxPWWCwGE83xb2bJvIFMJ7DuubUJ7fyi91RDA3spuTE7uN8RikPNjbZ2wQu84cb84S6Db5sLiFjCZ4VkrPgmWLO";
        listOfDevices.add(sophie);

        // [ Exemple d'envoie d'une notif lorsqu'une MZ contient des alertes ]
        String mzName = mz5.getName();
        int nbrAlertes = 1;
        PushNotifServiceImpl.sendMzLiveAlertNotif(listOfDevices, mzName, nbrAlertes);
    	
        //###################################  NOTIFICATION  ##################################
		**/
	}
}
