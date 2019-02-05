package hackqc18.Acclimate.alert;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * SopfeuAlertScraper class that retrieves the alerts from the Sopfeu website
 * 
 * @author Sophie Savoie
 *
 */

@Component
public class SopfeuAlertScraper {
	
	//Alertes présentement contenues dans le répertoire
	private static HashMap<String, LiveAlert> actualAlerts = new HashMap<>();
	
	//Alertes parsées dans le flux Rss périodiquement
	private static HashMap<String, LiveAlert> liveAlerts = new HashMap<>();
	private ArrayList <LiveAlert> newAlerts = new ArrayList<>();
	private ArrayList <LiveAlert> oldAlerts = new ArrayList<>();
	
	private final String START_URL = "https://sopfeu.qc.ca/";
	
	private static final Logger log = LoggerFactory.getLogger(SopfeuAlertScraper.class);
	
	private String source = "SOPFEU";
	private String type = "Feu";
	private String sousCat = "Feu de forêt";
	private Timestamp date = new Timestamp(System.currentTimeMillis());
	private String dateDeMiseAJour = date + "";
	private String certitude = "observée";
	private String urgence = "inconnue";
	
	
	/**
	 * Recueille les alertes sur le site web de la Sopfeu, les compare avec les alertes de la base de données
	 * fournies en paramètres et retourne une paire composée des nouvelles alertes et des anciennes alertes
	 * 
	 * @param actuAlerts les alertes dans la base de données
	 * @return la paire d'alertes nouvelles et anciennes
	 */
	public Pair<ArrayList<LiveAlert>, ArrayList<LiveAlert>> updateSopfeuAlerts
			(HashMap<String, LiveAlert> actuAlerts) {
		
		newAlerts.clear();
    	oldAlerts.clear();
    	liveAlerts.clear();
    	actualAlerts = actuAlerts;
		
    	scrapeSopfeuWebsite();
		updateAlertLists();
		
		return Pair.createPair(newAlerts, oldAlerts);
	}
	
	
	/**
	 * Recueille les alertes sur le de la Sopfeu et les ajoute à l'arrayList liveAlerts
	 */
	private void scrapeSopfeuWebsite(){
		String basePath = new File("").getAbsolutePath();
		System.setProperty("webdriver.gecko.driver", basePath + "\\selenium-java-3.14.0\\geckodriver.exe");

		FirefoxBinary firefoxBinary = new FirefoxBinary();
	    firefoxBinary.addCommandLineOptions("--headless");
	    FirefoxOptions firefoxOptions = new FirefoxOptions();
	    firefoxOptions.setBinary(firefoxBinary);
	    FirefoxDriver driver = new FirefoxDriver(firefoxOptions);


		
		try {
			/**
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
            HtmlPage driver = webClient.getPage(START_URL);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.waitForBackgroundJavaScript(3000);
            
            WebElement incendiesIntensifs = driver.getFirstByXPath("//div[@class='bloc incendies']/div[1]");
			log.info(incendiesIntensifs.getText());
			if(!incendiesIntensifs.getText().equals("0")) {
				// zone intensive
				DomElement btn = driver.getElementById("detail-1");
				btn.click();
				Thread.sleep(2000);
				WebElement tableIntensive = driver.getElementByName("statistiques-table");
				List<WebElement> rowsIntensive = tableIntensive.findElements(By.cssSelector("table.statistiques-table tr"));
				log.info("rowsIntensive " + rowsIntensive.size());
				createAlerts(rowsIntensive);
				WebElement closeBtn = driver.getElementByName("close");   
				closeBtn.click();
			}
			
			// zone nordique
			HtmlAnchor btnNordique = driver.getAnchorByHref("#tab-2");
			log.info(btnNordique.asText());
			btnNordique.click();	
			List<DomElement> elements1 = driver.getElementsByName("bloc incendies");
			log.info(elements1.size() + "");
			DomElement btn2 = driver.getAnchorByHref("javascript:;");
			log.info(btn2.asText());
			btn2.click();
			Thread.sleep(2000);
			log.info(driver.asXml());
			WebElement tableNordique = driver.getElementByName("statistiques-table");
			List<WebElement> rowsNordique = tableNordique.findElements(By.cssSelector("table.statistiques-table tr"));
			log.info("rowsNordique " + rowsNordique.size());
			createAlerts(rowsNordique);
			**/
			
			driver.get(START_URL);
			
			WebElement incendiesIntensifs = driver.findElement(By.xpath("//div[@class='bloc incendies']/div[1]"));
			if(!incendiesIntensifs.getText().equals("0")) {
				// zone intensive
				WebElement btn = driver.findElement(By.id("detail-1"));
				btn.click();
				Thread.sleep(2000);
				WebElement tableIntensive = driver.findElement(By.className("statistiques-table"));
				List<WebElement> rowsIntensive = tableIntensive.findElements(By.cssSelector("table.statistiques-table tr"));
				createAlerts(rowsIntensive);
				WebElement closeBtn = driver.findElement(By.className("close"));   
				closeBtn.click();
			}
			
			// zone nordique
			WebElement btnNordique = driver.findElement(By.cssSelector("a[href*='#tab-2']"));
			btnNordique.click();	
			List<WebElement> elements1 = driver.findElements(By.className("bloc incendies"));
			WebElement btn2 = driver.findElement(By.id("detail-2"));
			btn2.click();
			Thread.sleep(2000);
			WebElement tableNordique = driver.findElement(By.className("statistiques-table"));
			List<WebElement> rowsNordique = tableNordique.findElements(By.cssSelector("table.statistiques-table tr"));
			createAlerts(rowsNordique);
			driver.close();
			
		} catch (Exception e) {
			
		}
		     
	}
	
	
	/**
	 * Crée les alertes à partir des rangées de tableau html fournies en paramètre
	 * 
	 * @param rows
	 */
	private void createAlerts(List<WebElement> rows) {
		rows.forEach(row -> {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			if(cells.size() > 0) {
				String alertId = cells.get(0).getText();
				String nom = "feu numéro " + alertId;
				String territoire = "MRC " + cells.get(1).getText();
				String severite = cells.get(2).getText();
				String description = "Feu d'une superficie estimée (Hectares): " + cells.get(3).getText();
				double lat = Double.parseDouble(cells.get(4).getText());
				double lng = Double.parseDouble(cells.get(5).getText());
				Geometry geometry = new Geometry(lat, lng);
				
				LiveAlert alert =                   
		                new LiveAlert(alertId, nom, source, territoire, certitude,
		                        severite, type, sousCat, dateDeMiseAJour, urgence,
		                        description, geometry);
				
				liveAlerts.put(alertId, alert);
			}
		}); 
	}
	
	 /**
     * Met à jour les listes d'alertes
     */
    private void updateAlertLists() {
    	
    	// Si le répertoire d'alertes est vide, ajouter toutes les alertes
    	if (actualAlerts.size() == 0) {
    		newAlerts.addAll(liveAlerts.values());
    	}
    	
    	else {
    		//On parcourt les nouvelles alertes et on ne garde que celles qui ne sont pas dans déjà dans le répertoire
    		for (LiveAlert liveAlert: liveAlerts.values()) {
    			if (!actualAlerts.containsKey(liveAlert.id)) {
    				newAlerts.add(liveAlert);
    			}	
    		}
    		// On parcourt les alertes actuelles et on retire celles qui ne sont plus à jour
    		for (LiveAlert actualAlert: actualAlerts.values()) {
    			if(!liveAlerts.containsKey(actualAlert.id)) {
    				oldAlerts.add(actualAlert);
    		}  	}
    	}	
    }
}
